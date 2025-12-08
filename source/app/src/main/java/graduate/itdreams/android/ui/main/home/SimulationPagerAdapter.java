package graduate.itdreams.android.ui.main.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import graduate.itdreams.android.R;
import graduate.itdreams.android.data.model.api.response.simulation.SimulationResponse;

public class SimulationPagerAdapter extends RecyclerView.Adapter<SimulationPagerAdapter.PageViewHolder>{
    private final List<List<SimulationResponse>> pages;
    private final HomeViewModel viewModel;
    private final SimulationAdapter.OnPostClickListener listener;

    public SimulationPagerAdapter(List<List<SimulationResponse>> pages, HomeViewModel viewModel, SimulationAdapter.OnPostClickListener listener) {
        this.pages = pages;
        this.viewModel = viewModel;
        this.listener = listener;
    }

    static class PageViewHolder extends RecyclerView.ViewHolder {
        RecyclerView recyclerView;

        public PageViewHolder(@NonNull View itemView) {
            super(itemView);
            recyclerView = itemView.findViewById(R.id.recyclerViewInPage); // bạn cần layout cho 1 page
        }
    }

    @NonNull
    @Override
    public PageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_page_simulation, parent, false);
        return new PageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PageViewHolder holder, int position) {
        SimulationAdapter adapter = new SimulationAdapter(viewModel, listener);
        adapter.setData(pages.get(position));
        holder.recyclerView.setLayoutManager(new LinearLayoutManager(holder.recyclerView.getContext()));
        holder.recyclerView.setAdapter(adapter);
    }

    @Override
    public int getItemCount() {
        return pages.size();
    }
}
