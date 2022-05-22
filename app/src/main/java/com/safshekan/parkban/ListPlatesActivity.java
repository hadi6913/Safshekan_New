package com.safshekan.parkban;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.safshekan.parkban.databinding.ActivityListPlatesBinding;
import com.safshekan.parkban.viewmodels.PlateListViewModel;

public class ListPlatesActivity extends BaseActivity {

    private ActivityListPlatesBinding binding;
    private PlateListViewModel plateListViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_list_plates);
        plateListViewModel = ViewModelProviders.of(this).get(PlateListViewModel.class);

        binding.setViewModel(plateListViewModel);
        plateListViewModel.init(this);

        binding.setLifecycleOwner(this);

        plateListViewModel.getProgress().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer value) {
                if (value > 0) {
                    showProgress(true);
                } else {
                    showProgress(false);
                }
            }
        });

        plateListViewModel.getCurrent().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean isSelected) {

                binding.currentLayout.setBackgroundColor(isSelected
                        ? ListPlatesActivity.this.getResources().getColor(R.color.colorPrimary)
                        : ListPlatesActivity.this.getResources().getColor(R.color.light_grey));
            }
        });

        plateListViewModel.getPrevious().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean isSelected) {

                binding.previousLayout.setBackgroundColor(isSelected
                        ? ListPlatesActivity.this.getResources().getColor(R.color.colorPrimary)
                        : ListPlatesActivity.this.getResources().getColor(R.color.light_grey));
            }
        });

    }
    public void setFragmentCars(Fragment fragment){
        getSupportFragmentManager()
                .beginTransaction()
                .replace(binding.frameLayout.getId(),fragment)
                .commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        plateListViewModel.onResume();
        plateListViewModel.getCurrentCarFromDB(this);
    }
}
