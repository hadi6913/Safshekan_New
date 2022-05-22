package com.safshekan.parkban.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.safshekan.parkban.R;
import com.safshekan.parkban.ReportActivity;
import com.safshekan.parkban.adapters.ReportListAdapter;
import com.safshekan.parkban.helper.DateTimeHelper;
import com.safshekan.parkban.helper.ShowToast;
import com.safshekan.parkban.persistence.ParkbanDatabase;
import com.safshekan.parkban.persistence.models.ResponseResultType;
import com.safshekan.parkban.repositories.ParkbanRepository;
import com.safshekan.parkban.services.dto.ReportResultDto;
import com.safshekan.parkban.utils.Animation_Constant;
import com.safshekan.parkban.utils.MyBounceInterpolator;

import java.util.Date;

public class ReportViewModel extends ViewModel {

    private ReportListAdapter funcAdapter = new ReportListAdapter();
    private Date BeginDate, EndDate;
    private ParkbanRepository parkbanRepository;
    private MutableLiveData<Boolean> showList;
    private MutableLiveData<Boolean> showTotalAmountLayout;
    public MutableLiveData<Integer> progress = new MutableLiveData<>();
    private MutableLiveData<String> totalCacheAmount;

    public MutableLiveData<Integer> getProgress() {
        return progress;
    }


    public MutableLiveData<Boolean> getShowList() {
        if (showList == null)
            showList = new MutableLiveData<>();
        return showList;
    }


    public MutableLiveData<Boolean> getShowTotalAmountLayout() {
        if (showTotalAmountLayout == null)
            showTotalAmountLayout = new MutableLiveData<>();
        return showTotalAmountLayout;
    }


    public MutableLiveData<String> getTotalCacheAmount() {
        if (totalCacheAmount == null)
            totalCacheAmount = new MutableLiveData<String>();
        return totalCacheAmount;
    }


    public ReportListAdapter getFuncAdapter() {
        return funcAdapter;
    }


    public void init(final Context context) {

        if (showList == null)
            showList = new MutableLiveData<>();
        showList.setValue(false);


        if (showTotalAmountLayout == null)
            showTotalAmountLayout = new MutableLiveData<>();
        showTotalAmountLayout.setValue(false);


        if (BeginDate == null)
            BeginDate = DateTimeHelper.getBeginCurrentMonth();
        if (EndDate == null)
            EndDate = DateTimeHelper.getEndCurrentMonth();


        if (parkbanRepository == null) {
            ParkbanDatabase.getInstance(context.getApplicationContext(), new ParkbanDatabase.DatabaseReadyCallback() {
                @Override
                public void onReady(ParkbanDatabase instance) {
                    parkbanRepository = new ParkbanRepository(instance);
//                    if (BeginDate != null && EndDate != null) {
//                        String start = DateTimeHelper.DateToString(BeginDate);
//                        String end = DateTimeHelper.DateToString(EndDate);
////                        getParkbanFunctionality(context, start, end);
//                    }
                }
            });
        }

    }


    private void getParkbanReportList(final Context context, String startDate, String endDate) {

        progress.setValue(10);
        parkbanRepository.getParkbanReportList(startDate, endDate, new ParkbanRepository.ServiceResultCallBack<ReportResultDto>() {
            @Override
            public void onSuccess(ReportResultDto result) {
                progress.setValue(0);


//                totalCacheAmount.setValue(String.valueOf(result.getValue().getTotalAmount()));
                getTotalCacheAmount().setValue(String.valueOf(result.getValue().getTotalAmount()));

                if (result.getValue().getReports().size() > 0) {
                    showTotalAmountLayout.setValue(true);
                    showList.setValue(true);
                    funcAdapter.setFuncList(result.getValue().getReports());
                } else {
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

    public void showReportList(final View view) {

        final String startDate = ((ReportActivity) view.getContext()).getStartDate();
        final String endDate = ((ReportActivity) view.getContext()).getEndDate();


        final Animation myAnim = AnimationUtils.loadAnimation(view.getContext(), R.anim.btn_bubble_animation);
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
        myAnim.setInterpolator(interpolator);
        view.startAnimation(myAnim);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (endDate.compareTo(startDate) < 0) {
                    ShowToast.getInstance().showError(view.getContext(), R.string.begin_date_smaller_than_end_date);
                } else {
                    getParkbanReportList(view.getContext(), startDate, endDate);
                }

            }
        }, Animation_Constant.ANIMATION_VALUE);


    }


}
