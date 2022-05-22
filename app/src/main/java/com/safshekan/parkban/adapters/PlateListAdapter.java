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
import com.safshekan.parkban.databinding.AdapterPlateItemLayoutBinding;
import com.safshekan.parkban.persistence.models.Car;
import com.safshekan.parkban.persistence.models.CarItems;
import com.safshekan.parkban.viewmodels.PlateListViewModel;

import java.util.ArrayList;
import java.util.List;

public class PlateListAdapter extends RecyclerView.Adapter<PlateListAdapter.PlateViewHolder> {

    private List<Car> carList = new ArrayList<>();

    public PlateListAdapter() {
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public PlateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_plate_item_layout, parent, false);
        return new PlateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlateViewHolder holder, int position) {
        holder.setCar(carList.get(position));
    }

    public void setCarList(List<Car> items) {
        this.carList.clear();
        if (items != null)
            carList.addAll(items);

        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return carList.size();
    }

    @Override
    public long getItemId(int position) {
        return carList.get(position).getId();
    }

    public class PlateViewHolder extends RecyclerView.ViewHolder {

        AdapterPlateItemLayoutBinding binding;

        public PlateViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);

            PlateListViewModel plateListViewModel = ViewModelProviders.of((BaseActivity) itemView.getContext()).get(PlateListViewModel.class);

            binding.setViewModel(plateListViewModel);

        }

        public void setCar(Car car) {
            if (car.getPlateNo() != null)
                binding.setCarItem(new CarItems(itemView.getContext(), car));
        }

    }
}
