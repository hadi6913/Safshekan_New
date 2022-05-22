package com.safshekan.parkban.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.view.View;

import com.safshekan.parkban.BaseActivity;
import com.safshekan.parkban.R;
import com.safshekan.parkban.ShiftActivity;
import com.safshekan.parkban.adapters.ParkbanShiftAdapter;
import com.safshekan.parkban.helper.DateTimeHelper;
import com.safshekan.parkban.helper.ShowToast;
import com.safshekan.parkban.persistence.ParkbanDatabase;
import com.safshekan.parkban.persistence.models.ResponseResultType;
import com.safshekan.parkban.repositories.ParkbanRepository;
import com.safshekan.parkban.services.dto.ParkbanShiftResultDto;
import com.mohamadamin.persianmaterialdatetimepicker.utils.PersianCalendar;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ShiftViewModel extends ViewModel {

    private ParkbanShiftAdapter shiftAdapter = new ParkbanShiftAdapter();
    private ParkbanRepository parkbanRepository;
    private Date BeginDate, EndDate;
    private String fromDate, toDate;

    public MutableLiveData<Integer> progress = new MutableLiveData<>();

    public LiveData<Integer> getProgress() {
        return progress;
    }

    public String getFromDate() {
        if (BeginDate == null)
            BeginDate = DateTimeHelper.getBeginCurrentMonth();

        return setDisplayDate(BeginDate);
        //return fromDate;
    }

    public String getToDate() {
        if (EndDate == null)
            EndDate = DateTimeHelper.getEndCurrentMonth();

        toDate = setDisplayDate(EndDate);

        return toDate;
    }

    public ParkbanShiftAdapter getShiftAdapter() {
        return shiftAdapter;
    }

    public void init(final Context context) {

        if (BeginDate == null)
            BeginDate = DateTimeHelper.getBeginCurrentMonth();
        if (EndDate == null)
            EndDate = DateTimeHelper.getEndCurrentMonth();

        if (parkbanRepository == null) {
            ParkbanDatabase.getInstance(context.getApplicationContext(), new ParkbanDatabase.DatabaseReadyCallback() {
                @Override
                public void onReady(ParkbanDatabase instance) {
                    parkbanRepository = new ParkbanRepository(instance);
                    if (BeginDate != null && EndDate != null) {
                        String start = DateTimeHelper.DateToString(BeginDate);
                        String end = DateTimeHelper.DateToString(EndDate);
                        getParkbanShift(context,start, end);
                    }
                }
            });
        }
    }

    private void getParkbanShift(final Context context, String startDate, String endDate) {

        progress.setValue(10);

        parkbanRepository.getParkbanShift(BaseActivity.CurrentUserId, startDate, endDate, new ParkbanRepository.ServiceResultCallBack<ParkbanShiftResultDto>() {
            @Override
            public void onSuccess(ParkbanShiftResultDto result) {
                progress.setValue(0);
                List<ParkbanShiftResultDto.ParkbanShiftDto> shiftList = new ArrayList<>();
                shiftList = result.getValue();
                shiftAdapter.setShiftList(shiftList);
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

    public void showShifts(View view) {

        String startDate = ((ShiftActivity) view.getContext()).getStartDate();
        String endDate = ((ShiftActivity) view.getContext()).getEndDate();

        if (endDate.compareTo(startDate) < 0){
            ShowToast.getInstance().showError(view.getContext() , R.string.begin_date_smaller_than_end_date);
            return;
        }

        getParkbanShift(view.getContext(),startDate, endDate);

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
}
