package com.safshekan.parkban.viewmodels;

import android.app.Activity;
import android.app.Dialog;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;

import com.safshekan.parkban.BaseActivity;
import com.safshekan.parkban.ListPlatesActivity;
import com.safshekan.parkban.R;
import com.safshekan.parkban.adapters.PlateListAdapter;
import com.safshekan.parkban.dialogs.CarTimesDialog;
import com.safshekan.parkban.dialogs.RecordsStatusDialog;
import com.safshekan.parkban.fragments.CurrentCarsFragment;
import com.safshekan.parkban.fragments.PreviousCarsFragment;
import com.safshekan.parkban.helper.FontHelper;
import com.safshekan.parkban.helper.ImageLoadHelper;
import com.safshekan.parkban.helper.ShowToast;
import com.safshekan.parkban.persistence.ParkbanDatabase;
import com.safshekan.parkban.persistence.models.Car;
import com.safshekan.parkban.persistence.models.CarItems;
import com.safshekan.parkban.persistence.models.CarPlate;
import com.safshekan.parkban.persistence.models.CarPlateHistory;
import com.safshekan.parkban.persistence.models.ResponseResultType;
import com.safshekan.parkban.persistence.models.SendStatus;
import com.safshekan.parkban.services.dto.CarRecordsDto;
import com.safshekan.parkban.services.dto.SendRecordResultDto;
import com.safshekan.parkban.repositories.ParkbanRepository;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.ProgressDialog;

import static android.content.Context.CONNECTIVITY_SERVICE;

public class PlateListViewModel extends ViewModel {

    private MutableLiveData<Car> carLiveData;
    private List<Car> cars;
    private LiveData<List<Car>> currentCars;
    private LiveData<List<Car>> previousCars;
    private ParkbanRepository parkbanRepository;
    private int successCount, failedCount;
    private RecordsStatusDialog recordDialog;
    private Context context;
    private ProgressDialog progressDialog;
    private ParkbanRepository carRepository;
    private int sendingRecordCount = 0;
    private boolean cancelSending = false;
    private View currentView, previousView;
    private MutableLiveData<Boolean> previous = new MutableLiveData<>();
    private MutableLiveData<Boolean> current = new MutableLiveData<>();
    private List<Car> sendCars;
    private MutableLiveData<Boolean> emptyList;
    private Fragment fragment;

    public PlateListAdapter plateListAdapter = new PlateListAdapter();

    public PlateListAdapter getPlateListAdapter() {
        if (plateListAdapter == null)
            plateListAdapter = new PlateListAdapter();
        return plateListAdapter;
    }

    public MutableLiveData<Boolean> getEmptyList() {
        return emptyList;
    }

    public MutableLiveData<Integer> progress = new MutableLiveData<>();
    private Dialog progressBar;

    public LiveData<Integer> getProgress() {
        return progress;
    }

    public LiveData<Boolean> getCurrent() {
        return current;
    }

    public LiveData<Boolean> getPrevious() {
        return previous;
    }

    public LiveData<Car> getCarLiveDate() {
        if (carLiveData == null) {
            carLiveData = new MutableLiveData<>();
        }
        return carLiveData;
    }

    public void init(final Context context) {

        fragment = new CurrentCarsFragment();
        ((ListPlatesActivity) (context)).setFragmentCars(fragment);

        if (currentCars == null)
            currentCars = new MutableLiveData<>();

        if (previousCars == null)
            previousCars = new MutableLiveData<>();

        if (emptyList == null)
            emptyList = new MutableLiveData<>();

        cars = new ArrayList<>();

        previous.setValue(false);
        current.setValue(true);

        if (parkbanRepository == null) {
            ParkbanDatabase.getInstance(context.getApplicationContext(), new ParkbanDatabase.DatabaseReadyCallback() {
                @Override
                public void onReady(ParkbanDatabase instance) {
                    parkbanRepository = new ParkbanRepository(instance);
                    progress.setValue(10);
                    getPreviousCarFromDB(context);
                    getCurrentCarFromDB(context);
                }
            });
        }
    }

    public void onResume() {
        if (previous == null)
            previous = new MutableLiveData<>();
        if (current == null)
            current = new MutableLiveData<>();
        previous.setValue(false);
        current.setValue(true);
    }

    public void getCurrentCarFromDB(final Context context) {
        this.context = context;
        progress.setValue(10);

        currentCars = parkbanRepository.getCars();

        currentCars.observe((BaseActivity) context, new Observer<List<Car>>() {
            @Override
            public void onChanged(@Nullable final List<Car> currentCars) {

                if (currentCars.size() == 0)
                    progress.setValue(0);

                for (int i = 0; i < currentCars.size(); i++) {
                    final Car car = currentCars.get(i);
                    final int finalI = i;

                    LiveData<List<CarPlate>> carPlates = parkbanRepository.getCarPlates(car.getId());
                    carPlates.observe((BaseActivity) context, new Observer<List<CarPlate>>() {
                        @Override
                        public void onChanged(@Nullable List<CarPlate> carPlates) {

                            if (inSendRecordState)
                                return;
                            car.getCarPlates().clear();
                            car.getCarPlates().addAll(carPlates);

                            if (finalI == currentCars.size() - 1) {
                                progress.setValue(0);
                            }
                            plateListAdapter.setCarList(currentCars);
                        }
                    });

                }
                plateListAdapter.setCarList(currentCars);
            }
        });

    }

    public void currentDayClick(View view) {

        current.setValue(true);
        previous.setValue(false);

        getCurrentCarFromDB(view.getContext());

        fragment = new CurrentCarsFragment();
        ((ListPlatesActivity) (context)).setFragmentCars(fragment);

    }

    public void previousClick(View view) {
        previous.setValue(true);
        current.setValue(false);

        getPreviousCarFromDB(view.getContext());

        fragment = new PreviousCarsFragment();
        ((ListPlatesActivity) (context)).setFragmentCars(fragment);
    }

    private void getPreviousCarFromDB(final Context context) {
        this.context = context;
        progress.setValue(10);

        previousCars = parkbanRepository.getPreviousCarsOld();
        previousCars.observe((BaseActivity) context, new Observer<List<Car>>() {
            @Override
            public void onChanged(@Nullable final List<Car> previousCars) {

                if (previousCars.size() == 0)
                    progress.setValue(0);

                for (int i = 0; i < previousCars.size(); i++) {
                    final Car car = previousCars.get(i);
                    final int finalI = i;

                    LiveData<List<CarPlate>> carPlates = parkbanRepository.getCarPlates(car.getId());
                    carPlates.observe((BaseActivity) context, new Observer<List<CarPlate>>() {
                        @Override
                        public void onChanged(@Nullable List<CarPlate> carPlates) {
                            if (inSendRecordState)
                                return;

                            if (carPlates.size() > 0) {
                                car.getCarPlates().clear();
                                car.getCarPlates().addAll(carPlates);
                            }

                            if (previous.getValue())
                                plateListAdapter.setCarList(previousCars);

                            if (finalI == previousCars.size() - 1) {
                                progress.setValue(0);
                            }
                        }
                    });
                }
                if (previous.getValue())
                    plateListAdapter.setCarList(previousCars);
            }
        });

    }


    public void onCarItemSelected(View view, CarItems item) {
        Car car = findCarById(item.getCarId());
        if (car == null)
            return;

        CarTimesDialog dialog = new CarTimesDialog();
        ArrayList<CarPlateHistory> carPlateHistories = new ArrayList<>();

        for (int i = 0; i < car.getCarPlates().size(); i++) {
            CarPlateHistory carPlateHistory = new CarPlateHistory();
            Date date = new Date(car.getCarPlates().get(i).getRecordDate().getTime());
            String time = FontHelper.IntegerFormat(date.getHours()) + ":" + FontHelper.IntegerFormat(date.getMinutes()) + ":" + FontHelper.IntegerFormat(date.getSeconds());
            carPlateHistory.setTime(time);
            carPlateHistory.setStatus(car.getCarPlates().get(i).getStatus());
            carPlateHistories.add(carPlateHistory);
        }

        //set car image
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File outFile = new File(storageDir, car.getCarPlates().get(car.getCarPlates().size() - 1).getPlateFileName());
        String absolutePath = outFile.getAbsolutePath();
        Bitmap imageFile = ImageLoadHelper.getInstance().loadImage(context, absolutePath);
        if (imageFile != null)
            imageFile = Bitmap.createScaledBitmap(imageFile, 130, 130, false);

        dialog.setItem(carPlateHistories, imageFile);
        dialog.show(((Activity) context).getFragmentManager(), "");
    }

    private Car findCarById(long carId) {
        cars.clear();
        if (current.getValue() != null && current.getValue() && currentCars != null)
            cars.addAll(currentCars.getValue());

        if (previous.getValue() != null && previous.getValue() && previousCars != null)
            cars.addAll(previousCars.getValue());

        for (int i = 0; i < cars.size(); i++) {
            if (cars.get(i).getId() == carId)
                return cars.get(i);
        }
        return null;
    }

    private List<Car> carsToSend = new ArrayList<>();
    private int carIndex;
    private int plateIndex;
    private int responesCount;
    private boolean inSendRecordState;
    private int platesCount;
    private int sentCount, failCount;

    public void sendRecords(final View view) {

        if (!hasNetConnection(view.getContext())){
            ShowToast.getInstance().showWarning(view.getContext() , R.string.no_net_connection);
            return;
        }

        carsToSend.clear();

        carsToSend.addAll(previousCars.getValue());
        carsToSend.addAll(currentCars.getValue());

        cancelSending = false;
        platesCount = 0;
        responesCount = 0;
        sentCount = 0;
        failCount = 0;

        for (Car car : carsToSend) {
            for (CarPlate carPlate : car.getCarPlates()) {
                if (carPlate.getStatus() != SendStatus.SENT.ordinal())
                    platesCount++;
            }
        }

        if (platesCount > 0) {
            inSendRecordState = true;

            progressDialog = new ProgressDialog(context, R.style.MyDialogTheme);
            progressDialog.setMessage("sending records");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setMax(platesCount);
            progressDialog.setButton(DialogInterface.BUTTON_POSITIVE, "", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "لغو ارسال", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    cancelSending = true;
                    dialog.dismiss();
                }
            });
            progressDialog.show();

            sendPlates(view.getContext());
        } else {
            //show message there is no plate for send
            ShowToast.getInstance().showWarning(context, R.string.no_record_for_send);
        }

    }

    private void sendPlates(final Context context) {
        if (!cancelSending && findPlateForSend()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    sendCarPlate(context);
                }
            }).start();
        } else {
            inSendRecordState = false;
            if (cancelSending) {
                Log.d("TEST", "sent complete");
                //show count of sent plates

            }
            showResultDialog(platesCount, sentCount);
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    private boolean findPlateForSend() {
        int idx = 0;
        for (int i = 0; i < carsToSend.size(); i++) {
            for (int j = 0; j < carsToSend.get(i).getCarPlates().size(); j++) {
                if (carsToSend.get(i).getCarPlates().get(j).getStatus() != SendStatus.SENT.ordinal()) {
                    idx++;
                    if (idx > responesCount) {
                        carIndex = i;
                        plateIndex = j;
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void sendCarPlate(final Context context) {
        Car car = carsToSend.get(carIndex);
        final CarPlate carPlate = car.getCarPlates().get(plateIndex);

        final CarRecordsDto carRecordsDto = new CarRecordsDto();

        carRecordsDto.setLatitude(car.getLatitude());
        carRecordsDto.setLongitude(car.getLongitude());
        carRecordsDto.setPlateNo(car.getPlateNo());
        carRecordsDto.setUserId(BaseActivity.CurrentUserId);
        carRecordsDto.setFirstParkDate(new Date(car.getCarPlates().get(0).getRecordDate().getTime()));
        carRecordsDto.setImageIntArray(ImageLoadHelper.getInstance().convertImageFileToIntArray(context, carPlate.getPlateFileName()));

        carRecordsDto.setDateTime(new Date(carPlate.getRecordDate().getTime()));
        carRecordsDto.setExit(carPlate.isExit());
        carRecordsDto.setParkId(carPlate.getId());
        carRecordsDto.setParkingSpaceId(car.getParkingSpaceId());
        carRecordsDto.setVerificationStatus(carPlate.getEditedPlate());

        parkbanRepository.sendRecord(carRecordsDto,
                new ParkbanRepository.ServiceResultCallBack<SendRecordResultDto.SendRecordStatus>() {
                    @Override
                    public void onSuccess(final SendRecordResultDto.SendRecordStatus result) {
                        parkbanRepository.updateCarPlateRecordStatus(result.getParkId(), result.isStatus(),
                                new ParkbanRepository.DataBaseCarPlateUpdateCallBack() {
                                    @Override
                                    public void onSuccess() {

                                        if (result.isStatus())
                                            sentCount++;
                                        else
                                            failCount++;

                                        responesCount++;
                                        progressDialog.setProgress(responesCount);
                                        sendPlates(context);

                                    }

                                    @Override
                                    public void onFailed() {
                                        // show message error
                                        inSendRecordState = false;
                                        progressDialog.dismiss();
                                        progressDialog = null;
                                    }
                                });
                    }

                    @Override
                    public void onFailed(ResponseResultType resultType, String message, int errorCode) {
                        // show message error
                        // showResultDialog(sentRecordCount);
                        inSendRecordState = false;
                        progressDialog.dismiss();
                        progressDialog = null;

                        switch (resultType) {
                            case RetrofitError:
                                ShowToast.getInstance().showError(context, R.string.exception_msg);
                                break;
                            case ServerError:
                                if (errorCode != 0)
                                    ShowToast.getInstance().showError(context, errorCode);
                                else
                                    ShowToast.getInstance().showError(context, R.string.connection_failed);
                                break;
                            default:
                                ShowToast.getInstance().showError(context, resultType.ordinal());
                        }

                        getCurrentCarFromDB(context);
                        getPreviousCarFromDB(context);
                    }
                });
    }

    private void showResultDialog(int allRecord, int sentCount) {

        recordDialog = new RecordsStatusDialog();
        failedCount = allRecord - sentCount;

        recordDialog.setItem(String.valueOf(allRecord), String.valueOf(sentCount), String.valueOf(failCount));
        recordDialog.show(((Activity) context).getFragmentManager(), "");

        getCurrentCarFromDB(context);
        getPreviousCarFromDB(context);
    }

    private boolean hasNetConnection(Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);

        NetworkInfo mobileInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        boolean mobileConnected = mobileInfo.getState() == NetworkInfo.State.CONNECTED;

        NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        boolean wifiConnected = wifiInfo.getState() == NetworkInfo.State.CONNECTED;

        if (mobileConnected || wifiConnected) {
            return true;
        } else
            return false;
    }

}