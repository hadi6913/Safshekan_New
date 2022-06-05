package com.khodmohaseb.parkban.adapters;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.khodmohaseb.parkban.BaseActivity;
import com.khodmohaseb.parkban.R;
import com.khodmohaseb.parkban.databinding.AdapterReportItemBinding;
import com.khodmohaseb.parkban.services.dto.ReportDetailDto;
import com.khodmohaseb.parkban.viewmodels.ReportViewModel;

import java.util.ArrayList;
import java.util.List;

public class ReportListAdapter extends RecyclerView.Adapter<ReportListAdapter.ReportListViewHolder> {

    private List<ReportDetailDto> functList = new ArrayList<>();

    public ReportListAdapter() {
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public ReportListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_report_item, parent, false);
        return new ReportListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportListViewHolder holder, int position) {
        holder.setFunctionality(functList.get(position));
        if (functList.get(position).getMemberCode()==null||functList.get(position).getMemberCode().equals("")){
            holder.binding.linearLayoutMembercodeContainer.setVisibility(View.GONE);
        }
    }

    public void setFuncList(List<ReportDetailDto> items) {
        this.functList.clear();
        if (items != null)
            functList.addAll(items);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return functList.size();
    }





    public class ReportListViewHolder extends RecyclerView.ViewHolder {

        AdapterReportItemBinding binding;

        public ReportListViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);

            ReportViewModel viewModel = ViewModelProviders.of((BaseActivity) itemView.getContext()).get(ReportViewModel.class);

            binding.setViewModel(viewModel);
        }

        public void setFunctionality(ReportDetailDto func) {
            if (func != null)
                binding.setFuncDto(func);
        }
    }
}
