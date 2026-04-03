package com.demo.ltud_n10.presentation.ui.dashboard;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.demo.ltud_n10.R;

import java.util.List;

public class DashboardAdapter extends RecyclerView.Adapter<DashboardAdapter.ViewHolder> {

    private final List<DashboardMenuItem> items;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(DashboardMenuItem item);
    }

    public DashboardAdapter(List<DashboardMenuItem> items, OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dashboard_menu, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DashboardMenuItem item = items.get(position);
        holder.tvTitle.setText(item.getTitle());
        // holder.ivIcon.setImageResource(item.getIconRes());
        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        ImageView ivIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            ivIcon = itemView.findViewById(R.id.ivIcon);
        }
    }
}
