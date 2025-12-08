package graduate.itdreams.android.ui.main.simulation.overview;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import eu.davidea.flexibleadapter.databinding.BR;
import graduate.itdreams.android.BuildConfig;
import graduate.itdreams.android.R;
import graduate.itdreams.android.data.model.api.response.ItemTitleContentResponse;
import graduate.itdreams.android.databinding.FragmentOverviewBinding;
import graduate.itdreams.android.di.component.FragmentComponent;
import graduate.itdreams.android.ui.base.fragment.BaseFragment;
import graduate.itdreams.android.ui.main.simulation.SimulationOverviewViewModel;


public class OverviewFragment extends BaseFragment<FragmentOverviewBinding, OverviewViewModel> {
    private ExoPlayer player;
    private boolean isFullscreen = false;
    private ViewGroup originalParent;
    private int originalIndex;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SimulationOverviewViewModel activityViewModel = new ViewModelProvider(requireActivity()).get(SimulationOverviewViewModel.class);

        activityViewModel.getSimulationDetail().observe(getViewLifecycleOwner(), response -> {
            if (response != null) {
                String overviewJson = response.getOverview();
                String descriptionJson = response.getDescription();
                Gson gson = new Gson();

                ItemTitleContentResponse itemDescription = gson.fromJson(descriptionJson, ItemTitleContentResponse.class);
                binding.tvTitleDescription.setText(itemDescription.getTitle());
                binding.description.setText(itemDescription.getContent());

                Type listType = new TypeToken<List<ItemTitleContentResponse>>(){}.getType();
                List<ItemTitleContentResponse> overviewList = gson.fromJson(overviewJson, listType);

                OverviewAdapter overviewAdapter = new OverviewAdapter(overviewList);
                binding.rcvOverview.setLayoutManager(new LinearLayoutManager(requireContext()));
                binding.rcvOverview.setAdapter(overviewAdapter);

                if(response.getVideoPath() != null){
                    binding.playerView.setVisibility(View.VISIBLE);
                    loadVideo(response.getVideoPath());
                }

            }
        });
    }

    private void loadVideo(String videoUrl) {
        player = new ExoPlayer.Builder(getContext()).build();
        binding.playerView.setPlayer(player);

        String fullUrl = BuildConfig.URL_LOAD_VIDEO + videoUrl;
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

    @Override
    public int getBindingVariable() {
        return BR.vm;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_overview;
    }

    @Override
    protected void performDataBinding() {
        binding.setF(this);
        binding.setVm(viewModel);
    }

    @Override
    protected void performDependencyInjection(FragmentComponent buildComponent) {
        buildComponent.inject(this);
    }
}