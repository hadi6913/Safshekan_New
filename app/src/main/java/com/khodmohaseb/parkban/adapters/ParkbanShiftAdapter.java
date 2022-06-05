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
import com.khodmohaseb.parkban.databinding.AdapterParkbanShiftBinding;
import com.khodmohaseb.parkban.services.dto.ParkbanShiftResultDto;
import com.khodmohaseb.parkban.viewmodels.ShiftViewModel;

import java.util.ArrayList;
import java.util.List;

public class ParkbanShiftAdapter extends RecyclerView.Adapter<ParkbanShiftAdapter.ShiftViewHolder> {

    private List<ParkbanShiftResultDto.ParkbanShiftDto> shiftList = new ArrayList<>();

    public ParkbanShiftAdapter() {
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public ShiftViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_parkban_shift, parent, false);
        return new ShiftViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShiftViewHolder holder, int position) {
        holder.setShift(shiftList.get(position));
    }

    public void setShiftList(List<ParkbanShiftResultDto.ParkbanShiftDto> items) {
        this.shiftList.clear();
        shiftList.addAll(items);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return shiftList.size();
    }

    public class ShiftViewHolder extends RecyclerView.ViewHolder {

        AdapterParkbanShiftBinding binding;

        public ShiftViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);

            ShiftViewModel viewModel = ViewModelProviders.of((BaseActivity) itemView.getContext()).get(ShiftViewModel.class);
            binding.setViewModel(viewModel);
        }

        public void setShift(ParkbanShiftResultDto.ParkbanShiftDto shift) {
            if (shift != null)
                binding.setShiftDto(shift);
        }
    }
}
