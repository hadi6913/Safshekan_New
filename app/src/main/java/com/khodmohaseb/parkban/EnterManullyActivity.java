package com.khodmohaseb.parkban;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.res.AssetManager;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.khodmohaseb.parkban.databinding.ActivityEnterManuallyBinding;
import com.khodmohaseb.parkban.databinding.ActivityEnterQrBinding;
import com.khodmohaseb.parkban.helper.ShowToast;
import com.khodmohaseb.parkban.utils.Animation_Constant;
import com.khodmohaseb.parkban.utils.MyBounceInterpolator;
import com.khodmohaseb.parkban.utils.PelakUtility;
import com.khodmohaseb.parkban.viewmodels.EnterManuallyViewModel;
import com.khodmohaseb.parkban.viewmodels.EnterQrViewModel;
import com.mohamadamin.persianmaterialdatetimepicker.date.DatePickerDialog;
import com.mohamadamin.persianmaterialdatetimepicker.time.RadialPickerLayout;
import com.mohamadamin.persianmaterialdatetimepicker.time.TimePickerDialog;
import com.mohamadamin.persianmaterialdatetimepicker.utils.PersianCalendar;

import org.opencv.android.OpenCVLoader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static ir.shahaabco.ANPRNDK.anpr_create;


public class EnterManullyActivity extends BaseActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private static final String TAG = "EnterManullyActivity";
    private int failedLoadLib = 0;
    private EnterManuallyViewModel enterManuallyViewModel;
    private ActivityEnterManuallyBinding binding;

    int year_;
    int month_;
    int day_;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_enter_manually);
        enterManuallyViewModel = ViewModelProviders.of(this).get(EnterManuallyViewModel.class);
        binding.setViewModel(enterManuallyViewModel);
        EditText etxtCar = findViewById(R.id.etxt_car_plate_first_cell_enter_manually);
        EditText etxtMotor = findViewById(R.id.etxt_motor_plate_first_cell_enter_manually);
        RelativeLayout datePickerRelativeLayout = findViewById(R.id.fucckingDatePicker);
        String givenPelak = getIntent().getStringExtra("sendedpelak");


        enterManuallyViewModel.init(this, etxtCar, etxtMotor);
        enterManuallyViewModel.getHasPelak().setValue(true);
        binding.setLifecycleOwner(this);
        enterManuallyViewModel.mSpinner = binding.spinnerEnterManually;
        enterManuallyViewModel.mCarTypeSpinner = binding.spinnerCarTypeEnterManually;


        datePickerRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Animation myAnim = AnimationUtils.loadAnimation(EnterManullyActivity.this, R.anim.btn_bubble_animation);
                MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
                myAnim.setInterpolator(interpolator);
                v.startAnimation(myAnim);
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        PersianCalendar persianCalendar = new PersianCalendar();
                        DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(
                                EnterManullyActivity.this,
                                persianCalendar.getPersianYear(),
                                persianCalendar.getPersianMonth(),
                                persianCalendar.getPersianDay()
                        );
                        datePickerDialog.show(getFragmentManager(), "Datepickerdialog");


                    }
                }, Animation_Constant.ANIMATION_VALUE);


            }
        });


        binding.spinnerCarTypeEnterManually.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                enterManuallyViewModel.getSelectedCarTypeName().setValue(String.valueOf(binding.spinnerCarTypeEnterManually.getSelectedItem().toString().trim()));


                if (String.valueOf(binding.spinnerCarTypeEnterManually.getSelectedItem().toString().trim()).equals(enterManuallyViewModel.getParkingInfoResponse.getTariffs().getVehicleTariff1().getVehicleName().trim())) {
                    enterManuallyViewModel.getSelectedTarrifId().setValue(1);
                    enterManuallyViewModel.getSelectedTarrifEntranceFee().setValue(enterManuallyViewModel.getParkingInfoResponse.getTariffs().getVehicleTariff1().getEntranceCost().toString());

                }

                if (enterManuallyViewModel.getParkingInfoResponse.getTariffs().getVehicleTariff2() != null) {
                    if (String.valueOf(binding.spinnerCarTypeEnterManually.getSelectedItem().toString().trim()).equals(enterManuallyViewModel.getParkingInfoResponse.getTariffs().getVehicleTariff2().getVehicleName().trim())) {
                        enterManuallyViewModel.getSelectedTarrifId().setValue(2);
                        enterManuallyViewModel.getSelectedTarrifEntranceFee().setValue(enterManuallyViewModel.getParkingInfoResponse.getTariffs().getVehicleTariff2().getEntranceCost().toString());


                    }
                }


                if (enterManuallyViewModel.getParkingInfoResponse.getTariffs().getVehicleTariff3() != null) {
                    if (String.valueOf(binding.spinnerCarTypeEnterManually.getSelectedItem().toString().trim()).equals(enterManuallyViewModel.getParkingInfoResponse.getTariffs().getVehicleTariff3().getVehicleName().trim())) {
                        enterManuallyViewModel.getSelectedTarrifId().setValue(3);
                        enterManuallyViewModel.getSelectedTarrifEntranceFee().setValue(enterManuallyViewModel.getParkingInfoResponse.getTariffs().getVehicleTariff3().getEntranceCost().toString());


                    }
                }

                if (enterManuallyViewModel.getParkingInfoResponse.getTariffs().getVehicleTariff4() != null) {
                    if (String.valueOf(binding.spinnerCarTypeEnterManually.getSelectedItem().toString().trim()).equals(enterManuallyViewModel.getParkingInfoResponse.getTariffs().getVehicleTariff4().getVehicleName().trim())) {
                        enterManuallyViewModel.getSelectedTarrifId().setValue(4);
                        enterManuallyViewModel.getSelectedTarrifEntranceFee().setValue(enterManuallyViewModel.getParkingInfoResponse.getTariffs().getVehicleTariff4().getEntranceCost().toString());


                    }
                }


                if (enterManuallyViewModel.getParkingInfoResponse.getTariffs().getVehicleTariff5() != null) {
                    if (String.valueOf(binding.spinnerCarTypeEnterManually.getSelectedItem().toString().trim()).equals(enterManuallyViewModel.getParkingInfoResponse.getTariffs().getVehicleTariff5().getVehicleName().trim())) {
                        enterManuallyViewModel.getSelectedTarrifId().setValue(5);
                        enterManuallyViewModel.getSelectedTarrifEntranceFee().setValue(enterManuallyViewModel.getParkingInfoResponse.getTariffs().getVehicleTariff5().getEntranceCost().toString());


                    }
                }


                if (enterManuallyViewModel.getParkingInfoResponse.getTariffs().getVehicleTariff6() != null) {
                    if (String.valueOf(binding.spinnerCarTypeEnterManually.getSelectedItem().toString().trim()).equals(enterManuallyViewModel.getParkingInfoResponse.getTariffs().getVehicleTariff6().getVehicleName().trim())) {
                        enterManuallyViewModel.getSelectedTarrifId().setValue(6);
                        enterManuallyViewModel.getSelectedTarrifEntranceFee().setValue(enterManuallyViewModel.getParkingInfoResponse.getTariffs().getVehicleTariff6().getEntranceCost().toString());


                    }
                }


                if (enterManuallyViewModel.getParkingInfoResponse.getTariffs().getVehicleTariff7() != null) {
                    if (String.valueOf(binding.spinnerCarTypeEnterManually.getSelectedItem().toString().trim()).equals(enterManuallyViewModel.getParkingInfoResponse.getTariffs().getVehicleTariff7().getVehicleName().trim())) {
                        enterManuallyViewModel.getSelectedTarrifId().setValue(7);
                        enterManuallyViewModel.getSelectedTarrifEntranceFee().setValue(enterManuallyViewModel.getParkingInfoResponse.getTariffs().getVehicleTariff7().getEntranceCost().toString());


                    }
                }

                if (enterManuallyViewModel.getParkingInfoResponse.getTariffs().getVehicleTariff8() != null) {
                    if (String.valueOf(binding.spinnerCarTypeEnterManually.getSelectedItem().toString().trim()).equals(enterManuallyViewModel.getParkingInfoResponse.getTariffs().getVehicleTariff8().getVehicleName().trim())) {
                        enterManuallyViewModel.getSelectedTarrifId().setValue(8);
                        enterManuallyViewModel.getSelectedTarrifEntranceFee().setValue(enterManuallyViewModel.getParkingInfoResponse.getTariffs().getVehicleTariff8().getEntranceCost().toString());


                    }
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                binding.spinnerCarTypeEnterManually.setSelection(0);
                enterManuallyViewModel.getSelectedCarTypeName().setValue(String.valueOf(binding.spinnerCarTypeEnterManually.getSelectedItem().toString().trim()));
                enterManuallyViewModel.getSelectedTarrifId().setValue(1);
                enterManuallyViewModel.getSelectedTarrifEntranceFee().setValue(enterManuallyViewModel.getParkingInfoResponse.getTariffs().getVehicleTariff1().getEntranceCost().toString());


            }
        });


        binding.spinnerEnterManually.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                enterManuallyViewModel.getPlate__1().setValue(String.valueOf(binding.spinnerEnterManually.getSelectedItem()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                binding.spinnerEnterManually.setSelection(0);
                enterManuallyViewModel.getPlate__1().setValue(String.valueOf(binding.spinnerEnterManually.getSelectedItem()));
            }
        });
        binding.etxtCarPlateThirdCellEnterManually.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 3) {
                    binding.etxtCarPlateThirdCellEnterManually.clearFocus();
                    binding.etxtCarPlateForthCellEnterManually.requestFocus();
                    binding.etxtCarPlateForthCellEnterManually.setCursorVisible(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        binding.etxtMotorPlateFirstCellEnterManually.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 3) {
                    binding.etxtMotorPlateFirstCellEnterManually.clearFocus();
                    binding.etxtMotorPlateSecondCellEnterManually.requestFocus();
                    binding.etxtMotorPlateSecondCellEnterManually.setCursorVisible(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        binding.etxtCarPlateForthCellEnterManually.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    binding.etxtCarPlateForthCellEnterManually.clearFocus();
                    binding.etxtCarPlateThirdCellEnterManually.requestFocus();
                    binding.etxtCarPlateThirdCellEnterManually.setCursorVisible(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        binding.etxtMotorPlateSecondCellEnterManually.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    binding.etxtMotorPlateSecondCellEnterManually.clearFocus();
                    binding.etxtMotorPlateFirstCellEnterManually.requestFocus();
                    binding.etxtMotorPlateFirstCellEnterManually.setCursorVisible(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        if (givenPelak.length() == 8) {
            //motor
            enterManuallyViewModel.getMotor().setValue(true);
            enterManuallyViewModel.getCar().setValue(false);
            enterManuallyViewModel.getMplate__0().setValue(givenPelak.substring(0, 3));
            enterManuallyViewModel.getMplate__1().setValue(givenPelak.substring(3, 8));

        } else {
            //car
            enterManuallyViewModel.getMotor().setValue(false);
            enterManuallyViewModel.getCar().setValue(true);
            enterManuallyViewModel.getPlate__0().setValue(givenPelak.substring(0, 2));
            enterManuallyViewModel.getPlate__2().setValue(givenPelak.substring(4, 7));
            enterManuallyViewModel.getPlate__3().setValue(givenPelak.substring(7, 9));
            binding.spinnerEnterManually.setSelection(PelakUtility.convert00To0(givenPelak.substring(2, 4)) - 1);
            enterManuallyViewModel.getPlate__1().setValue(String.valueOf(binding.spinnerEnterManually.getSelectedItem()));


        }


        enterManuallyViewModel.getProgress().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer value) {
                if (value > 0) {
                    showProgress(true);
                } else {
                    showProgress(false);
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        enterManuallyViewModel.processActivityResult(this, requestCode, resultCode, data);
    }


    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        Log.d(TAG, "onDateSet: year:" + year + " month:" + monthOfYear + "day:" + dayOfMonth);
        year_ = year;
        month_ = monthOfYear;
        day_ = dayOfMonth;

        TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(
                EnterManullyActivity.this,
                12,
                15,
                true
        );
        timePickerDialog.show(getFragmentManager(), "timepickerdialog");

    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {


        String year = Integer.toString(year_);
        String month = String.format("%02d", month_);
        String day = String.format("%02d", day_);
        String hour = String.format("%02d", hourOfDay);
        String min = String.format("%02d", minute);

        enterManuallyViewModel.getEnteredDate().setValue(year + "/" + month + "/" + day + "   " + hour + " : " + min);


        Log.d(TAG, "" + year_ + month_ + day_ + hourOfDay + minute);


    }
}
