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
import java.util.ArrayList;
import java.util.List;

import graduate.itdreams.android.BR;
import graduate.itdreams.android.R;
import graduate.itdreams.android.data.model.api.request.achievement.UpdateCertificateRequest;
import graduate.itdreams.android.data.model.api.request.achievement.UploadCertificateRequest;
import graduate.itdreams.android.data.model.api.response.simulation.AchievementResponse;
import graduate.itdreams.android.data.model.api.response.simulation.SimulationResponse;
import graduate.itdreams.android.databinding.FragmentAchievementBinding;
import graduate.itdreams.android.di.component.FragmentComponent;
import graduate.itdreams.android.ui.base.fragment.BaseFragment;
import graduate.itdreams.android.ui.main.home.SimulationAdapter;
import graduate.itdreams.android.ui.main.login.LoginActivity;
import graduate.itdreams.android.ui.main.simulation.SimulationOverviewActivity;
import graduate.itdreams.android.ui.main.taskdetail.PdfActivity;

public class AchievementFragment extends BaseFragment<FragmentAchievementBinding, AchievementViewModel> {
    private List<AchievementResponse> fullList = new ArrayList<>();
    private AchievementAdapter adapter;

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
        setupSearch();

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

            adapter = new AchievementAdapter(viewModel, item -> {
                if(item.getSimulation() == null){
                    Log.d("Achivement", "Chứng chỉ bạn chưa được lưu");
                }else {
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
                }
            });
            fullList.clear();
            for (AchievementResponse item : postList) {
                if (item != null) {
                    fullList.add(item);
                }
            }

            adapter.setData(fullList);
            binding.recycleview.setLayoutManager(new LinearLayoutManager(getContext()));
            binding.recycleview.setAdapter(adapter);
            binding.swipeRefresh.setRefreshing(false);
        });

    }
    private void setupSearch() {
        binding.searchView.setOnQueryTextListener(
            new androidx.appcompat.widget.SearchView.OnQueryTextListener() {

                @Override
                public boolean onQueryTextSubmit(String query) {
                    filterList(query);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    filterList(newText);
                    return true;
                }
            }
        );
    }
    private void filterList(String keyword) {
        if (adapter == null) return;

        // Nếu chưa load data
        if (fullList.isEmpty()) {
            adapter.setData(fullList);
            return;
        }

        // Clear search
        if (keyword == null || keyword.trim().isEmpty()) {
            adapter.setData(fullList);
            return;
        }

        List<AchievementResponse> filteredList = new ArrayList<>();
        String key = keyword.toLowerCase();

        for (AchievementResponse item : fullList) {
            if (item.getSimulation().getTitle() != null &&
                    item.getSimulation().getTitle().toLowerCase().contains(key)) {
                filteredList.add(item);
            }
        }

        adapter.setData(filteredList);
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
