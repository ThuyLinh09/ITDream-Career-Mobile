package graduate.itdreams.android.ui.main.taskdetail;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import eu.davidea.flexibleadapter.databinding.BR;
import graduate.itdreams.android.BuildConfig;
import graduate.itdreams.android.R;
import graduate.itdreams.android.data.model.api.request.task.CompleteTaskRequest;
import graduate.itdreams.android.data.model.api.request.task.RestartTaskRequest;
import graduate.itdreams.android.data.model.api.request.task.TaskQuestionProgressRequest;
import graduate.itdreams.android.data.model.api.response.ItemTitleContentResponse;
import graduate.itdreams.android.data.model.api.response.question.TaskQuestionResponse;
import graduate.itdreams.android.data.model.api.response.task.ListAnswerResponse;
import graduate.itdreams.android.data.model.api.response.task.SubTaskProgressResponse;
import graduate.itdreams.android.data.model.api.response.task.SubTaskResponse;
import graduate.itdreams.android.databinding.FragmentSubTaskBinding;
import graduate.itdreams.android.di.component.FragmentComponent;
import graduate.itdreams.android.ui.base.fragment.BaseFragment;
import graduate.itdreams.android.ui.main.simulation.overview.OverviewAdapter;
import graduate.itdreams.android.ui.main.taskdetail.question.QuestionItemAdapter;
import graduate.itdreams.android.ui.main.taskdetail.question.QuestionQuizAdapter;

public class SubTaskFragment extends BaseFragment<FragmentSubTaskBinding,SubTaskViewModel> {
    private ExoPlayer player;
    private boolean isFullscreen = false;
    private ViewGroup originalParent;
    private int originalIndex;
    private int currentIndex = 0;
    private SubTaskProgressResponse subTaskProgress;
    private File file;
    private QuestionItemAdapter.UploadFileCallback currentUploadCallback;
    private List<TaskQuestionResponse> cachedQuestions;
    private List<ListAnswerResponse> cachedAnswers;
    private boolean questionLoaded = false;
    private boolean answerLoaded = false;
    private long simulationId;
    private long subTaskId;
    public static SubTaskFragment newInstance(Long simulationId,SubTaskResponse subTask) {
        SubTaskFragment fragment = new SubTaskFragment();
        Bundle args = new Bundle();
        args.putLong("simulation_id", simulationId);
        args.putLong("subtask_id", subTask.getId());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    
        Bundle args = getArguments();
        if (args == null) {
            throw new IllegalStateException("SubTaskFragment requires arguments but found null.");
        }

        simulationId = args.getLong("simulation_id", -1);
        subTaskId = args.getLong("subtask_id", -1);

        viewModel.fetchSubtaskDetail(subTaskId);
        viewModel.getSubTaskDetail().observe(getViewLifecycleOwner(), responseSubtaskDetail -> {
            if (responseSubtaskDetail != null) {
                String introductionJson = responseSubtaskDetail.getIntroduction();
                Gson gson = new Gson();

                Type listType = new TypeToken<List<ItemTitleContentResponse>>(){}.getType();
                List<ItemTitleContentResponse> overviewList = gson.fromJson(introductionJson, listType);

                OverviewAdapter overviewAdapter = new OverviewAdapter(overviewList);
                binding.rcvContent.setLayoutManager(new LinearLayoutManager(requireContext()));
                binding.rcvContent.setAdapter(overviewAdapter);
                if(responseSubtaskDetail.getFilePath() == null){
                    binding.documentButton.setVisibility(View.GONE);
                }else {
                    binding.documentButton.setVisibility(View.VISIBLE);
                }
                if(responseSubtaskDetail.getVideoPath() != null){
                    binding.playerView.setVisibility(View.VISIBLE);
                    loadVideo(responseSubtaskDetail.getVideoPath());
                }
            }
        });
        viewModel.createSubtaskProgress(subTaskId);
        viewModel.getSubTaskProgress().observe(getViewLifecycleOwner(), responseSubtaskProgress -> {
            subTaskProgress = responseSubtaskProgress;
            viewModel.fetchListTaskQuestion(simulationId, subTaskId);

            viewModel.fetchListAnswer(responseSubtaskProgress.getId(), subTaskId);
        });

        viewModel.getTaskQuestion().observe(getViewLifecycleOwner(), questions -> {
            questionLoaded = true;
            cachedQuestions = questions;
            tryLoadUI();
        });

        viewModel.getAnswerList().observe(getViewLifecycleOwner(), answers -> {
            answerLoaded = true;
            cachedAnswers = answers;
            tryLoadUI();
        });

        binding.btnComplete.setOnClickListener(v -> {
            CompleteTaskRequest request = new CompleteTaskRequest();
            request.setTaskId(subTaskId);
            viewModel.completeTask(request);
        });
        binding.btnRestart.setOnClickListener(v -> {
            RestartTaskRequest request = new RestartTaskRequest();
            request.setTaskId(subTaskId);
            viewModel.restartTask(request);
        });

    }
    private void loadVideo(String videoUrl) {
        player = new ExoPlayer.Builder(getContext()).build();
        binding.playerView.setPlayer(player);

        String fullUrl;
        if (videoUrl.startsWith("http://") || videoUrl.startsWith("https://")) {
            fullUrl = videoUrl; // dùng trực tiếp
        } else {
            fullUrl = BuildConfig.URL_LOAD_VIDEO + videoUrl; // ghép với server
        }
        MediaItem mediaItem = MediaItem.fromUri(Uri.parse(fullUrl));

        player.setMediaItem(mediaItem);
        player.prepare();
        //player.play();

        ImageButton fullscreenButton = binding.playerView.findViewById(R.id.exo_fullscreen);
        fullscreenButton.setOnClickListener(v -> {
            toggleFullscreen();
            if (isFullscreen) {
                fullscreenButton.setImageResource(R.drawable.ic_fullscreen_exit);
            } else {
                fullscreenButton.setImageResource(R.drawable.ic_fullscreen);
            }
        });

    }
    private void toggleFullscreen() {
        Activity activity = getActivity();
        if (activity == null) return;

        FrameLayout fullscreenContainer = activity.findViewById(R.id.fullscreenContainer);
        View appBar = activity.findViewById(R.id.appbar);
        View nestedScroll = activity.findViewById(R.id.nestedScrollView);

        if (fullscreenContainer == null || appBar == null || nestedScroll == null) return;

        if (!isFullscreen) {
            // Lưu parent cũ và index cũ
            originalParent = (ViewGroup) binding.playerView.getParent();
            originalIndex = originalParent.indexOfChild(binding.playerView);

            // Gỡ PlayerView khỏi fragment
            originalParent.removeView(binding.playerView);

            // Thêm PlayerView vào container fullscreen
            fullscreenContainer.addView(binding.playerView,
                    new FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT));
            fullscreenContainer.setVisibility(View.VISIBLE);
            fullscreenContainer.bringToFront();

            // Xoay ngang màn hình
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

            // Ẩn các phần khác
            appBar.setVisibility(View.GONE);
            nestedScroll.setVisibility(View.GONE);

            isFullscreen = true;
        } else {
            // Thoát fullscreen
            fullscreenContainer.removeView(binding.playerView);
            fullscreenContainer.setVisibility(View.GONE);

            // Xoay dọc màn hình
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            // Hiện lại các phần khác
            appBar.setVisibility(View.VISIBLE);
            nestedScroll.setVisibility(View.VISIBLE);

            // Trả PlayerView về vị trí cũ
            originalParent.addView(binding.playerView, originalIndex);

            isFullscreen = false;
        }
    }
    private void tryLoadUI() {
        if (!questionLoaded || !answerLoaded) return;

        if (cachedQuestions == null || cachedQuestions.isEmpty()) {
            Log.e("SubTask", "TaskQuestion rỗng, không thể load UI");
            return;
        }

        if (cachedAnswers == null) cachedAnswers = new ArrayList<>();

        loadQuestion(cachedQuestions, cachedAnswers);
    }


    private void loadQuestion(List<TaskQuestionResponse> responseTaskQuestion, List<ListAnswerResponse> listAnswerResponses){
        if(responseTaskQuestion.get(0).getQuestionType() != 3){
            binding.layoutQuestionFileAndText.setVisibility(View.VISIBLE);
            binding.btnComplete.setVisibility(View.VISIBLE);
            binding.layoutQuestionQuiz.setVisibility(View.GONE);
            QuestionItemAdapter questionItemAdapter = new QuestionItemAdapter(responseTaskQuestion, listAnswerResponses, (item, position, callback) -> {
                currentUploadCallback = callback;
                // Mở file picker ở đây
                filePickerLauncher.launch("application/pdf");
            }, (item, position, file) -> {
                if(item.getQuestionType() == 1){
                    submitFileClick(responseTaskQuestion.get(position).getId(), file);
                }
            }, (item, position, answer) -> {
                if(item.getQuestionType() == 2){
                    submitTextClick(responseTaskQuestion.get(position).getId(), answer);
                }
            });
            binding.rcvQuestionFileAndText.setLayoutManager(new LinearLayoutManager(requireContext()){
                @Override
                public boolean canScrollVertically() {
                    return false;
                }
            });
            binding.rcvQuestionFileAndText.setAdapter(questionItemAdapter);
        }else {
            binding.layoutQuestionQuiz.setVisibility(View.VISIBLE);
            binding.btnComplete.setVisibility(View.VISIBLE);
            binding.layoutQuestionFileAndText.setVisibility(View.GONE);
            QuestionQuizAdapter adapter = new QuestionQuizAdapter(responseTaskQuestion, currentIndex, isCorrect -> {
                if(currentIndex + 1 == responseTaskQuestion.size()){
                    binding.btnNext.setEnabled(false);
                }else {
                    binding.btnNext.setEnabled(true);
                }
                submitQuizClick(responseTaskQuestion.get(currentIndex).getId(), isCorrect);
            });
            binding.rcvQuestionQuiz.setAdapter(adapter);
            binding.rcvQuestionQuiz.setLayoutManager(new LinearLayoutManager(requireContext()));
            binding.btnNext.setOnClickListener(v -> {
                if (currentIndex < responseTaskQuestion.size() - 1) {
                    currentIndex++;
                    adapter.setCurrentIndex(currentIndex);
                    adapter.notifyDataSetChanged();
                }
            });

        }
    }
    private void submitQuizClick(Long taskQuestionId, Boolean isCorrect){
        TaskQuestionProgressRequest request = new TaskQuestionProgressRequest();
        request.setAnswer("câu trả lời trắc nghiệm");
        request.setStudentSubTaskProgressId(subTaskProgress.getId());
        request.setTaskQuestionId(taskQuestionId);
        request.setIsCorrect(isCorrect);
        viewModel.submitQuestion(request);
    }
    private void submitTextClick(Long taskQuestionId, String answer){
        TaskQuestionProgressRequest request = new TaskQuestionProgressRequest();
        request.setAnswer(answer);
        request.setStudentSubTaskProgressId(subTaskProgress.getId());
        request.setTaskQuestionId(taskQuestionId);
        request.setIsCorrect(true);
        viewModel.submitQuestion(request);
    }
    private void submitFileClick(Long taskQuestionId, File file){
        TaskQuestionProgressRequest request = new TaskQuestionProgressRequest();
        viewModel.uploadFile(file);
        viewModel.getFilePath().observe(getViewLifecycleOwner(), filePath -> {
            request.setAnswer(filePath);
            request.setStudentSubTaskProgressId(subTaskProgress.getId());
            request.setTaskQuestionId(taskQuestionId);
            request.setIsCorrect(true);
            viewModel.submitQuestion(request);
        });

    }
    private final ActivityResultLauncher<String> filePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    file = uriToFile(uri);
                    currentUploadCallback.onFileSelected(file); // ← gọi callback ở đây
                    currentUploadCallback = null;
                }
            });
    private File uriToFile(Uri uri) {
        try {
            InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
            File tempFile = new File(requireContext().getCacheDir(), "upload_" + System.currentTimeMillis());

            FileOutputStream out = new FileOutputStream(tempFile);
            byte[] buffer = new byte[1024];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            out.flush();
            out.close();
            inputStream.close();

            return tempFile; // File đã convert thành công
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void viewFileClick(){
        String url = viewModel.getSubTaskDetail().getValue().getFilePath();
        Intent intent = new Intent(getContext(), PdfActivity.class);
        intent.putExtra("url_pdf", url );
        intent.putExtra("title_pdf", viewModel.getSubTaskDetail().getValue().getTitle() );
        startActivity(intent);
    }
    @Override
    public int getBindingVariable() {
        return BR.vm;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_sub_task;
    }

    @Override
    protected void performDataBinding() {
        binding.setLifecycleOwner(getViewLifecycleOwner());
        binding.setF(this);
        binding.setVm(viewModel);
    }

    @Override
    protected void performDependencyInjection(FragmentComponent buildComponent) {
        buildComponent.inject(this);
    }
}