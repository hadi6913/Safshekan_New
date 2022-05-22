package com.safshekan.parkban.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.safshekan.parkban.R;
import com.safshekan.parkban.databinding.FragmentCurrentCarsBinding;
import com.safshekan.parkban.viewmodels.PlateListViewModel;

public class CurrentCarsFragment extends Fragment {

    PlateListViewModel viewModel;
    FragmentCurrentCarsBinding binding;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewModel = ViewModelProviders.of(getActivity()).get(PlateListViewModel.class);
        binding.setViewModel(viewModel);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_current_cars, container, false);
        return binding.getRoot();
    }
}
