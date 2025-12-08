package graduate.itdreams.android.ui.main.achievement;

import static com.facebook.FacebookSdk.getCacheDir;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import graduate.itdreams.android.BR;
import graduate.itdreams.android.R;
import graduate.itdreams.android.data.model.api.request.achievement.UpdateCertificateRequest;
import graduate.itdreams.android.data.model.api.request.achievement.UploadCertificateRequest;
import graduate.itdreams.android.databinding.FragmentAchievementBinding;
import graduate.itdreams.android.di.component.FragmentComponent;
import graduate.itdreams.android.ui.base.fragment.BaseFragment;
import graduate.itdreams.android.ui.main.home.SimulationAdapter;
import graduate.itdreams.android.ui.main.login.LoginActivity;
import graduate.itdreams.android.ui.main.simulation.SimulationOverviewActivity;
import graduate.itdreams.android.ui.main.taskdetail.PdfActivity;

public class AchievementFragment extends BaseFragment<FragmentAchievementBinding, AchievementViewModel> {
    @Override
    protected void performDataBinding() {
        binding.setF(this);
        binding.setVm(viewModel);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        performDataBinding();

        customBtnSearch();

        loadJobs();
        viewModel.forceLogout.observe(this, isLogout -> {
            if (Boolean.TRUE.equals(isLogout)) {
                viewModel.logout();
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
            }
        });
        binding.swipeRefresh.setOnRefreshListener(() -> {
            viewModel.fetchAchievementList(); // gọi lại API
        });

        return binding.getRoot();
    }
    private void loadJobs() {
        viewModel.fetchAchievementList();
        viewModel.getPostList().observe(getViewLifecycleOwner(), postList -> {
            if (postList == null || postList.isEmpty()) return;

            AchievementAdapter adapter = new AchievementAdapter(viewModel, item -> {
                String titleSimulation = "Thành tựu " + item.getSimulation().getTitle();

                if(item.getFilePath() == null){
                    UploadCertificateRequest request = new UploadCertificateRequest();
                    request.setSimulationName(item.getSimulation().getTitle());
                    request.setUsername(item.getStudentName());
                    viewModel.uploadCertificate(request);
                    viewModel.getCertificateUrl().observe(getViewLifecycleOwner(), url ->{
                        UpdateCertificateRequest updateCertificateRequest = new UpdateCertificateRequest();
                        updateCertificateRequest.setId(item.getId());
                        updateCertificateRequest.setFilePath(url);
                        viewModel.updateAchievement(updateCertificateRequest);

                        loadCertificate(url, titleSimulation);
                    });
                }else {
                    loadCertificate(item.getFilePath(), titleSimulation);
                }

            });
            adapter.setData(postList);
            binding.recycleview.setLayoutManager(new LinearLayoutManager(getContext()));
            binding.recycleview.setAdapter(adapter);
            binding.swipeRefresh.setRefreshing(false);
        });

    }

    private void loadCertificate(String pdfUrl, String title){
        if (pdfUrl != null) {
            Intent intent = new Intent(getContext(), PdfActivity.class);
            intent.putExtra("url_pdf", pdfUrl );
            intent.putExtra("title_pdf", title );
            startActivity(intent);
        }

    }
    private void customBtnSearch() {
        View searchPlate = binding.searchView.findViewById(androidx.appcompat.R.id.search_plate);
        if (searchPlate != null) {
            searchPlate.setBackground(null);
        }
        ImageView searchIcon = binding.searchView.findViewById(androidx.appcompat.R.id.search_mag_icon);
        searchIcon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.bg_btn));

    }

    @Override
    public int getBindingVariable() {
        return BR.vm;
    }
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_achievement;
    }
    @Override
    protected void performDependencyInjection(FragmentComponent buildComponent) {
        buildComponent.inject(this);
    }
}
