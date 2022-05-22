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
import com.safshekan.parkban.databinding.AdapterChargeReportBinding;
import com.safshekan.parkban.services.dto.CashDetailsResultDto;
import com.safshekan.parkban.viewmodels.ChargeReportViewModel;
import java.util.ArrayList;
import java.util.List;

public class ChargeReportAdapter extends RecyclerView.Adapter<ChargeReportAdapter.ChargeReportViewHolder> {

    private List<CashDetailsResultDto.CashDetailsDto> cashList = new ArrayList<>();

    public ChargeReportAdapter() {
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public ChargeReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_charge_report, parent, false);
        return new ChargeReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChargeReportViewHolder holder, int position) {
        holder.setCashDetails(cashList.get(position));
    }

    public void setCashList(List<CashDetailsResultDto.CashDetailsDto> items) {
        this.cashList.clear();
        if (items != null)
            cashList.addAll(items);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return cashList.size();
    }

    public class ChargeReportViewHolder extends RecyclerView.ViewHolder {

        AdapterChargeReportBinding binding;

        public ChargeReportViewHolder(View itemView) {
            super(itemView);

            binding = DataBindingUtil.bind(itemView);
            ChargeReportViewModel viewModel = ViewModelProviders.of((BaseActivity) itemView.getContext()).get(ChargeReportViewModel.class);

            binding.setViewModel(viewModel);
        }

        public void setCashDetails(CashDetailsResultDto.CashDetailsDto cash) {
            if (cash != null)
                binding.setCashDetailDto(cash);

        }

    }
}
