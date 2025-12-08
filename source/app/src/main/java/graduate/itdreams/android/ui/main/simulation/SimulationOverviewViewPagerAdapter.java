package graduate.itdreams.android.ui.main.simulation;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import graduate.itdreams.android.ui.main.simulation.overview.OverviewFragment;
import graduate.itdreams.android.ui.main.simulation.rate.RateFragment;
import graduate.itdreams.android.ui.main.simulation.task.TaskFragment;

public class SimulationOverviewViewPagerAdapter extends FragmentStateAdapter {
    public SimulationOverviewViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) return new OverviewFragment();
        else if (position == 1) return new TaskFragment();
        else return new RateFragment();
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
