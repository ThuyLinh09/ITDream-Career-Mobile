package graduate.itdreams.android.ui.main.taskdetail.question;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import graduate.itdreams.android.data.model.api.response.question.TaskQuestionResponse;
import graduate.itdreams.android.data.model.api.response.task.ListAnswerResponse;
import graduate.itdreams.android.databinding.ItemQuestionFileBinding;
import graduate.itdreams.android.databinding.ItemQuestionTextBinding;
import graduate.itdreams.android.ui.main.taskdetail.PdfActivity;

public class QuestionItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_FILE = 1;
    private static final int VIEW_TYPE_TEXT = 2;

    private List<TaskQuestionResponse> questionList = new ArrayList<>();
    private List<ListAnswerResponse> answerList = new ArrayList<>();

    public interface OnUploadFileClickListener {
        void onUploadFileClick(TaskQuestionResponse item, int position, UploadFileCallback callback);
    }
    public interface UploadFileCallback {
        void onFileSelected(File file);
    }
    private OnUploadFileClickListener listener;
    public interface OnSubmitFileClickListener {
        void onSubmitFileClick(TaskQuestionResponse item, int position, File selectedFile);
    }
    private OnSubmitFileClickListener submitFileListener;
    public interface OnSubmitTextClickListener {
        void onSubmitTextClick(TaskQuestionResponse item, int position, String answer);
    }
    private OnSubmitTextClickListener submitTextListener;
    public interface OnPdfClickListener {
        void onPdfClick();
    }
    private OnPdfClickListener pdfListener;
    public QuestionItemAdapter(List<TaskQuestionResponse> questionList, List<ListAnswerResponse> answerList, OnUploadFileClickListener listener, OnSubmitFileClickListener submitFileListener, OnSubmitTextClickListener submitTextListener) {
        this.questionList = questionList;
        this.answerList = answerList;
        this.listener = listener;
        this.submitFileListener = submitFileListener;
        this.submitTextListener = submitTextListener;
    }

    @Override
    public int getItemViewType(int position) {
        TaskQuestionResponse item = questionList.get(position);
        return item.getQuestionType();
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (viewType == VIEW_TYPE_TEXT) {
            ItemQuestionTextBinding binding =
                    ItemQuestionTextBinding.inflate(inflater, parent, false);
            return new TextViewHolder(binding, submitTextListener, this);
        } else {
            ItemQuestionFileBinding binding =
                    ItemQuestionFileBinding.inflate(inflater, parent, false);
            return new FileViewHolder(binding, submitFileListener, listener, this);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        TaskQuestionResponse item = questionList.get(position);

        if (holder instanceof TextViewHolder) {
            ((TextViewHolder) holder).bind(item, position);
        } else if (holder instanceof FileViewHolder) {
            ((FileViewHolder) holder).bind(item, position);
        }
    }

    @Override
    public int getItemCount() {
        return questionList != null ? questionList.size() : 0;
    }
    static class TextViewHolder extends RecyclerView.ViewHolder {
        ItemQuestionTextBinding binding;
        OnSubmitTextClickListener submitClickListener;
        QuestionItemAdapter adapter;
        public TextViewHolder(ItemQuestionTextBinding binding, OnSubmitTextClickListener submitClickListener, QuestionItemAdapter adapter) {
            super(binding.getRoot());
            this.binding = binding;
            this.submitClickListener = submitClickListener;
            this.adapter = adapter;
        }

        void bind(TaskQuestionResponse item, int position) {
            binding.tvQuestion.setText(item.getQuestion());

            ListAnswerResponse existingAnswer = adapter.findAnswer(item.getId());

            if (existingAnswer != null) {
                // Đã trả lời
                binding.textAnswer.setText(existingAnswer.getAnswer());
                binding.textAnswer.setEnabled(false);
                binding.btnSubmit.setVisibility(View.GONE);
            } else {
                // Chưa trả lời
                binding.textAnswer.setEnabled(true);
                binding.btnSubmit.setVisibility(View.VISIBLE);
                binding.btnSubmit.setOnClickListener(v ->{
                    String answer = binding.textAnswer.getText().toString().trim();
                    if (submitClickListener != null)
                        submitClickListener.onSubmitTextClick(item, position, answer);
                });
            }
        }
    }

    static class FileViewHolder extends RecyclerView.ViewHolder {
        ItemQuestionFileBinding binding;
        OnUploadFileClickListener listener;
        OnSubmitFileClickListener submitClickListener;
        File selectedFile = null;
        QuestionItemAdapter adapter;
        public FileViewHolder(ItemQuestionFileBinding binding,OnSubmitFileClickListener submitClickListener, OnUploadFileClickListener listener, QuestionItemAdapter adapter) {
            super(binding.getRoot());
            this.binding = binding;
            this.submitClickListener = submitClickListener;
            this.listener = listener;
            this.adapter = adapter;
        }

        void bind(TaskQuestionResponse item, int position) {
            binding.tvQuestion.setText(item.getQuestion());

            ListAnswerResponse existingAnswer = adapter.findAnswer(item.getId());
            if (existingAnswer != null) {
                // Hiện file đã nộp
                binding.answerPdf.setVisibility(View.VISIBLE);

                // Ẩn nút upload
                binding.btnUploadFile.setVisibility(View.GONE);
                binding.btnSubmit.setVisibility(View.GONE);
                binding.tvFileName.setVisibility(View.GONE);

                binding.answerPdf.setOnClickListener(v -> {
                    Intent intent = new Intent(binding.getRoot().getContext(), PdfActivity.class);
                    intent.putExtra("url_pdf", existingAnswer.getAnswer());
                    intent.putExtra("title_pdf", existingAnswer.getAnswer());
                    binding.getRoot().getContext().startActivity(intent);
                });
            } else {
                binding.answerPdf.setVisibility(View.GONE);

                binding.btnUploadFile.setVisibility(View.VISIBLE);
                binding.btnSubmit.setVisibility(View.VISIBLE);

                binding.btnUploadFile.setOnClickListener(v -> {
                    if (listener != null){
                        listener.onUploadFileClick(item, position, file -> {
                            selectedFile = file;
                            binding.tvFileName.setText(file.getName());
                        });
                    }
                });

                binding.btnSubmit.setOnClickListener(v ->{
                    if (submitClickListener != null)
                        submitClickListener.onSubmitFileClick(item, position, selectedFile);
                });
            }
        }
    }
    private ListAnswerResponse findAnswer(long questionId) {
        for (ListAnswerResponse ans : answerList) {
            if (ans.getTaskQuestion().getId() == questionId) return ans;
        }
        return null;
    }

}

