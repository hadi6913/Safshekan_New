package com.khodmohaseb.parkban.viewmodels;

import android.app.Activity;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.view.View;

import com.khodmohaseb.parkban.BaseActivity;
import com.khodmohaseb.parkban.FunctionalityReportActivity;
import com.khodmohaseb.parkban.R;
import com.khodmohaseb.parkban.adapters.FunctionalityAdapter;
import com.khodmohaseb.parkban.dialogs.ItemListDialog;
import com.khodmohaseb.parkban.helper.DateTimeHelper;
import com.khodmohaseb.parkban.helper.ShowToast;
import com.khodmohaseb.parkban.persistence.ParkbanDatabase;
import com.khodmohaseb.parkban.persistence.models.ResponseResultType;
import com.khodmohaseb.parkban.repositories.ParkbanRepository;
import com.khodmohaseb.parkban.services.dto.FunctionalityResultDto;
import com.khodmohaseb.parkban.services.dto.WorkShiftTypes;
import com.mohamadamin.persianmaterialdatetimepicker.utils.PersianCalendar;

import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;

public class FunctionalityReportViewModel extends ViewModel {

    private FunctionalityAdapter funcAdapter = new FunctionalityAdapter();
    private ParkbanRepository parkbanRepository;
    private Date BeginDate, EndDate;
    private MutableLiveData<String> totalImageCount;
    private MutableLiveData<String> totalParkCount;
    private List<WorkShiftTypes> shiftList;
    private ArrayList<WorkShiftTypes> shiftArrayList;
    private MutableLiveData<String> shiftName;
    private WorkShiftTypes shiftSelected;
    private MutableLiveData<Boolean> showList;

    public MutableLiveData<Integer> progress = new MutableLiveData<>();
    public LiveData<Integer> getProgress() {
        return progress;
    }

    public LiveData<String> getTotalImageCount() {
        if (totalImageCount == null)
            totalImageCount = new MutableLiveData<>();
        return totalImageCount;
    }

    public LiveData<String> getTotalParkCount() {
        if (totalParkCount == null)
            totalParkCount = new MutableLiveData<>();
        return totalParkCount;
    }

    public LiveData<String> getShiftName() {
        if (shiftName == null)
            shiftName = new MutableLiveData<>();
        return shiftName;
    }

    public LiveData<Boolean> getShowList() {
        if (showList == null)
            showList = new MutableLiveData<>();
        return showList;
    }

    public FunctionalityAdapter getFuncAdapter() {
        return funcAdapter;
    }

    public void init(final Context context) {

        if (showList == null)
            showList = new MutableLiveData<>();
        showList.setValue(false);

        if (shiftName == null)
            shiftName = new MutableLiveData<>();

        if (totalImageCount == null)
            totalImageCount = new MutableLiveData<>();

        if (totalParkCount == null)
            totalParkCount = new MutableLiveData<>();

        if (BeginDate == null)
            BeginDate = DateTimeHelper.getBeginCurrentMonth();
        if (EndDate == null)
            EndDate = DateTimeHelper.getEndCurrentMonth();

        //put value of VacationRequest Enum in a List
        shiftList = new ArrayList<WorkShiftTypes>(EnumSet.allOf(WorkShiftTypes.class));
        //convert List to ArrayList
        shiftArrayList = new ArrayList<>(shiftList.size());
        shiftArrayList.addAll(shiftList);

        shiftSelected = WorkShiftTypes.Shift4;
        shiftName.setValue(shiftSelected.getDescription());

        if (parkbanRepository == null) {
            ParkbanDatabase.getInstance(context.getApplicationContext(), new ParkbanDatabase.DatabaseReadyCallback() {
                @Override
                public void onReady(ParkbanDatabase instance) {
                    parkbanRepository = new ParkbanRepository(instance);
                    if (BeginDate != null && EndDate != null) {
                        String start = DateTimeHelper.DateToString(BeginDate);
                        String end = DateTimeHelper.DateToString(EndDate);
                        getParkbanFunctionality(context, start, end);
                    }
                }
            });
        }

    }

    private void getParkbanFunctionality(final Context context, String startDate, String endDate) {

        progress.setValue(10);

        parkbanRepository.getParkbanFunctionality(BaseActivity.CurrentUserId, startDate, endDate, shiftSelected.getValue(), new ParkbanRepository.ServiceResultCallBack<FunctionalityResultDto>() {
            @Override
            public void onSuccess(FunctionalityResultDto result) {
                ((FunctionalityReportActivity) context).hideResultLayout(false);

                progress.setValue(0);
                FunctionalityResultDto.FunctionalityDto funcResult = new FunctionalityResultDto.FunctionalityDto();
                funcResult = result.getValue();

                totalImageCount.setValue(String.valueOf(funcResult.getTotalImageCount()));
                totalParkCount.setValue(String.valueOf(funcResult.getTotalParkCount()));

                if (funcResult.getDetails().size() > 0) {
                    showList.setValue(true);
                    funcAdapter.setFuncList(funcResult.getDetails());
                }else {
                    showList.setValue(false);
                    ShowToast.getInstance().showWarning(context, R.string.no_result_found);
                }
            }

            @Override
            public void onFailed(ResponseResultType resultType, String message, int errorCode) {
                progress.setValue(0);
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
            }
        });
    }

    public void showFunctionality(View view){

        String startDate = ((FunctionalityReportActivity) view.getContext()).getStartDate();
        String endDate = ((FunctionalityReportActivity) view.getContext()).getEndDate();

        if (endDate.compareTo(startDate) < 0){
            ShowToast.getInstance().showError(view.getContext() , R.string.begin_date_smaller_than_end_date);
            return;
        }

        getParkbanFunctionality(view.getContext(),startDate, endDate);
    }

    public String setDisplayDate(Date date) {
        PersianCalendar persianCalendar = new PersianCalendar();
        if (date == null)
            return null;
        if (DateTimeHelper.getHour(date) <= 4)
            date = DateTimeHelper.setHour(date, 5);
        persianCalendar.setTime(date);
        return persianCalendar.getPersianShortDate();
    }

    public void shiftClick(final View view){
        ItemListDialog dialog = new ItemListDialog();
        dialog.setItems(shiftArrayList, R.string.shifts, new ItemListDialog.DialogOnItemSelectedListener() {
            @Override
            public void OnItemSelected(Object selectedItem) {
                setShifts((WorkShiftTypes) selectedItem, view.getContext());
            }
        });
        dialog.show(((Activity) view.getContext()).getFragmentManager() , "");
    }

    private void setShifts(WorkShiftTypes selectedItem,Context context){
        shiftSelected = selectedItem;
        shiftName.setValue(selectedItem.getDescription());

        funcAdapter.setFuncList(null);

        ((FunctionalityReportActivity) context).hideResultLayout(true);

    }
}
