package com.safshekan.parkban.adapters;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.safshekan.parkban.BaseActivity;
import com.safshekan.parkban.R;
import com.safshekan.parkban.databinding.AdapterFunctionalityBinding;
import com.safshekan.parkban.services.dto.FunctionalityDetailDto;
import com.safshekan.parkban.viewmodels.FunctionalityReportViewModel;

import java.util.ArrayList;
import java.util.List;

public class FunctionalityAdapter extends RecyclerView.Adapter<FunctionalityAdapter.FunctionalityViewHolder> {

    private List<FunctionalityDetailDto> functList = new ArrayList<>();

    public FunctionalityAdapter() {
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public FunctionalityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_functionality, parent, false);
        return new FunctionalityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FunctionalityViewHolder holder, int position) {
        holder.setFunctionality(functList.get(position));
    }

    public void setFuncList(List<FunctionalityDetailDto> items) {
        this.functList.clear();
        if (items != null)
            functList.addAll(items);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return functList.size();
    }





    public class FunctionalityViewHolder extends RecyclerView.ViewHolder {

        AdapterFunctionalityBinding binding;

        public FunctionalityViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);

            FunctionalityReportViewModel viewModel = ViewModelProviders.of((BaseActivity) itemView.getContext()).get(FunctionalityReportViewModel.class);

            binding.setViewModel(viewModel);
        }

        public void setFunctionality(FunctionalityDetailDto func) {
            if (func != null)
                binding.setFuncDto(func);
        }
    }
}
