package graduate.itdreams.android.ui.main.simulation;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.widget.PopupMenu;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;

import eu.davidea.flexibleadapter.databinding.BR;
import graduate.itdreams.android.R;
import graduate.itdreams.android.data.socket.dto.Message;
import graduate.itdreams.android.databinding.ActivitySimulationOverviewBinding;
import graduate.itdreams.android.di.component.ActivityComponent;
import graduate.itdreams.android.ui.base.activity.BaseActivity;
import graduate.itdreams.android.ui.main.feedback.FeedbackViewDialogFragment;
import graduate.itdreams.android.ui.main.taskdetail.TaskDetailActivity;


public class SimulationOverviewActivity extends BaseActivity<ActivitySimulationOverviewBinding, SimulationOverviewViewModel> {

    private long itemId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewBinding.setA(this);
        viewBinding.setVm(viewModel);
        viewBinding.setLifecycleOwner(this);
        viewBinding.btnBack.setOnClickListener(v -> finish());
        viewBinding.btnBackCollapsed.setOnClickListener(v -> finish());

        customTabLayout();
        setupToolbar();
        itemId = getIntent().getLongExtra("item_id", -1L);
        viewModel.fetchSimulationDetail(itemId);

        viewModel.imageLiveData.observe(this, bitmap -> {
            if (bitmap != null) {
                Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                viewBinding.toolbar.setBackground(drawable);
            }
        });
        viewModel.getFeedback(itemId);
        viewModel.getFeedback().observe(this, feedback -> {
            if (feedback != null) {
                viewBinding.iconRight.setImageResource(R.drawable.ic_chat_dot);
                viewBinding.btnMoreCollapsed.setImageResource(R.drawable.ic_chat_dot);
            }
        });
        viewBinding.iconRight.setOnClickListener(v -> {
            viewModel.getFeedback(itemId);
            viewModel.getFeedback().observe(this, feedback -> {
                if (feedback != null) {
                    FeedbackViewDialogFragment
                            .newInstance(feedback.getContent(), feedback.getSimulation().getEducator().getProfileAccountDto().getFullName(), feedback.getModifiedDate())
                            .show(getSupportFragmentManager(), "FeedbackDialog");
                }else {
                    FeedbackViewDialogFragment
                            .newInstance(null, null, null)
                            .show(getSupportFragmentManager(), "FeedbackDialog");
                }
            });
        });
        viewBinding.btnMoreCollapsed.setOnClickListener(v -> {
            viewModel.getFeedback(itemId);
            viewModel.getFeedback().observe(this, feedback -> {
                if (feedback != null) {
                    FeedbackViewDialogFragment
                            .newInstance(feedback.getContent(), feedback.getSimulation().getEducator().getProfileAccountDto().getFullName(), feedback.getModifiedDate())
                            .show(getSupportFragmentManager(), "FeedbackDialog");
                }else {
                    FeedbackViewDialogFragment
                            .newInstance(null, null, null)
                            .show(getSupportFragmentManager(), "FeedbackDialog");
                }
            });
        });

    }

    private void setupToolbar() {
        viewBinding.appbar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShown = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }

                if (scrollRange + verticalOffset == 0) {
                    viewBinding.toolbarCollapse.setVisibility(View.VISIBLE);
                    isShown = true;
                } else if (isShown) {
                    viewBinding.toolbarCollapse.setVisibility(View.GONE);
                    isShown = false;
                }
            }
        });
    }
    @Override
    public int getLayoutId() {
        return R.layout.activity_simulation_overview;
    }

    @Override
    public int getBindingVariable() {
        return BR.vm;
    }

    @Override
    public void performDependencyInjection(ActivityComponent buildComponent) {
        buildComponent.inject(this);
    }

    private void customTabLayout() {
        TabLayout tabLayout = viewBinding.tabLayout;
        ViewPager2 viewPager = viewBinding.viewPager;

        SimulationOverviewViewPagerAdapter simulationOverviewViewPagerAdapter = new SimulationOverviewViewPagerAdapter(this);
        viewPager.setAdapter(simulationOverviewViewPagerAdapter);
        viewPager.setUserInputEnabled(false);
        tabLayout.addTab(tabLayout.newTab().setText("Tổng quan"));
        tabLayout.addTab(tabLayout.newTab().setText("Nhiệm vụ"));
        tabLayout.addTab(tabLayout.newTab().setText("Đánh giá"));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition(), false);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

    }
    public void onStartClick(){
        Intent intent = new Intent(this, TaskDetailActivity.class);
        intent.putExtra("simulation_id", itemId );
        startActivity(intent);
    }

    @Override
    public void onMessageReceived(Message message) {

    }

    @Override
    public void onConnectionClosed() {

    }

    @Override
    public void onConnectionClosing() {

    }
}