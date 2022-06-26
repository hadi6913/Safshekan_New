package com.khodmohaseb.parkban.repositories;

import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;
import android.util.Log;

import com.khodmohaseb.parkban.helper.DateTimeHelper;
import com.khodmohaseb.parkban.persistence.ParkbanDatabase;
import com.khodmohaseb.parkban.persistence.models.Car;
import com.khodmohaseb.parkban.persistence.models.CarPlate;
import com.khodmohaseb.parkban.persistence.models.ParkingSpace;
import com.khodmohaseb.parkban.persistence.models.ResponseResultType;
import com.khodmohaseb.parkban.persistence.models.SendStatus;
import com.khodmohaseb.parkban.persistence.models.khodmohaseb.EntranceRecord;
import com.khodmohaseb.parkban.persistence.models.khodmohaseb.ExitRecord;
import com.khodmohaseb.parkban.persistence.models.khodmohaseb.TraffikRecord;
import com.khodmohaseb.parkban.persistence.models.model;
import com.khodmohaseb.parkban.services.dto.BooleanResultDto;
import com.khodmohaseb.parkban.services.dto.CarRecordsDto;
import com.khodmohaseb.parkban.services.dto.CashBillDto;
import com.khodmohaseb.parkban.services.dto.CashDetailsResultDto;
import com.khodmohaseb.parkban.services.dto.CurrentShiftDto;
import com.khodmohaseb.parkban.services.dto.DriverDebtResultDto;
import com.khodmohaseb.parkban.services.dto.ExitBillDto;
import com.khodmohaseb.parkban.services.dto.FailedFactorsRequestDto;
import com.khodmohaseb.parkban.services.dto.FailedFactorsResultDto;
import com.khodmohaseb.parkban.services.dto.FirstElectronicPaymentDto;
import com.khodmohaseb.parkban.services.dto.ForthElectronicPaymentDto;
import com.khodmohaseb.parkban.services.dto.FunctionalityResultDto;
import com.khodmohaseb.parkban.services.dto.IncreaseDriverWalletCardResultDto;
import com.khodmohaseb.parkban.services.dto.IncreaseDriverWalletResultDto;
import com.khodmohaseb.parkban.services.dto.IntResultDto;
import com.khodmohaseb.parkban.services.dto.LoginResultDto;
import com.khodmohaseb.parkban.services.dto.LongResultDto;
import com.khodmohaseb.parkban.services.dto.ParkAmountResultDto;
import com.khodmohaseb.parkban.services.dto.ParkbanShiftResultDto;
import com.khodmohaseb.parkban.services.dto.ReportResultDto;
import com.khodmohaseb.parkban.services.dto.SendRecordAndPayResultDto;
import com.khodmohaseb.parkban.services.dto.SendRecordResultDto;
import com.khodmohaseb.parkban.services.ParkbanServiceProvider;
import com.khodmohaseb.parkban.services.dto.StringResultDto;
import com.khodmohaseb.parkban.services.dto.ThirdElectronicPaymentRequestDto;
import com.khodmohaseb.parkban.services.dto.ThirdElectronicPaymentResponseDto;
import com.khodmohaseb.parkban.services.dto.ThirdPartDto;
import com.khodmohaseb.parkban.services.dto.ThirdPartResponseDto;
import com.khodmohaseb.parkban.services.dto.khodmohaseb.forgotrecord.ForgotEntranceRequest;
import com.khodmohaseb.parkban.services.dto.khodmohaseb.forgotrecord.ForgotRecordResponse;
import com.khodmohaseb.parkban.services.dto.khodmohaseb.parkinginfo.GetParkingInfoResponse;
import com.khodmohaseb.parkban.services.dto.khodmohaseb.sendiorecord.SendIoRecordRequest;
import com.khodmohaseb.parkban.services.dto.khodmohaseb.sendtraffikrecord.SendTraffikRecordRequest;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ParkbanRepository {

    private static final String TAG = "ParkbanRepository";
    private final ParkbanDatabase database;

    public interface DataBaseResultCallBack {
        void onSuccess(long id);

        void onFailed();
    }


    public interface DataBaseInsertedCarPelakCallBack {
        void onSuccess(EntranceRecord entranceRecord);

        void onFailed();
    }


    public interface DataBaseEntranceRecordResultCallBack {
        void onSuccess(List<EntranceRecord> entranceRecordList);

        void onFailed();
    }

    public interface DataBaseExitRecordResultCallBack {
        void onSuccess(List<ExitRecord> exitRecordList);

        void onFailed();
    }

    public interface DataBaseTraffikRecordResultCallBack {
        void onSuccess(List<TraffikRecord> traffikRecordList);

        void onFailed();
    }


    //**********************************************************************************************
    //**********************************************************************************************
    //**********************************************************************************************

    public interface DataBaseResultCustomCallBack {
        void onSuccess(model result);

        void onFailed(model result);
    }

    public interface DataBaseBooleanCallBack {
        void onSuccess(boolean exit);

        void onFailed();
    }

    public interface DataBaseCarsResultCallBack {
        void onSuccess(List<Car> cars);

        void onFailed();
    }


    public interface DataBaseCarPlatesResultCallBack {
        void onSuccess(List<CarPlate> carPlates);

        void onFailed();
    }

    public interface DataBaseCarPlateUpdateCallBack {
        void onSuccess();

        void onFailed();
    }

    public interface DataBaseCarResultCallBack {
        void onSuccess(Car cars);

        void onFailed();
    }

    public interface DataBaseParkSpaceCallBack {
        void onSuccess(List<Long> parkFull);

        void onFailed();
    }

    public interface DataBaseRecordDateCallBack {
        void onSuccess(Date recordDate);

        void onFailed();
    }

    public interface ServiceResultCallBack<T> {
        void onSuccess(T result);

        void onFailed(ResponseResultType resultType, String message, int errorCode);
    }

    public ParkbanRepository(ParkbanDatabase database) {
        this.database = database;

    }


    //)))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))
    //)))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))
    //)))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))
    //)))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))
    //)))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))
    //)))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))

    /**** Khodmohaseb Server Repository ****/


    public void getDeviceToken(String imei, final ServiceResultCallBack<String> callBack) {


        RequestBody body =
                RequestBody.create(MediaType.parse("application/json"), imei.trim());


        ParkbanServiceProvider.getInstance().getDeviceToken(body).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    if (response.isSuccessful()) {

                        callBack.onSuccess(response.body());


                    } else {
                        callBack.onFailed(ResponseResultType.ServerError, response.message(), response.code());
                    }
                } catch (Exception e) {
                    callBack.onFailed(ResponseResultType.RetrofitError, "", 0);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                callBack.onFailed(ResponseResultType.ServerError, t.getMessage(), 0);

            }
        });
    }


    public void getParkingInformation(String imei, final ServiceResultCallBack<GetParkingInfoResponse> callBack) {


        RequestBody body =
                RequestBody.create(MediaType.parse("application/json"), imei.trim());


        ParkbanServiceProvider.getInstance().getParkingInfoFromServer(body).enqueue(new Callback<GetParkingInfoResponse>() {
            @Override
            public void onResponse(Call<GetParkingInfoResponse> call, Response<GetParkingInfoResponse> response) {
                try {
                    if (response.isSuccessful()) {

                        callBack.onSuccess(response.body());


                    } else {
                        callBack.onFailed(ResponseResultType.ServerError, response.message(), response.code());
                    }
                } catch (Exception e) {
                    callBack.onFailed(ResponseResultType.RetrofitError, "", 0);
                }
            }

            @Override
            public void onFailure(Call<GetParkingInfoResponse> call, Throwable t) {
                callBack.onFailed(ResponseResultType.ServerError, t.getMessage(), 0);

            }
        });
    }


    public void sendIo(SendIoRecordRequest sendIoRecordRequest, final ServiceResultCallBack<String> callBack) {


        ParkbanServiceProvider.getInstance().sendIoRecord(sendIoRecordRequest).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    if (response.isSuccessful()) {

                        if (response.body().trim().equals("Ok")) {
                            callBack.onSuccess(response.body());
                        } else {
                            callBack.onFailed(ResponseResultType.ServerError, response.message(), response.code());
                        }


                    } else {
                        callBack.onFailed(ResponseResultType.ServerError, response.message(), response.code());
                    }
                } catch (Exception e) {
                    callBack.onFailed(ResponseResultType.RetrofitError, "", 0);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                callBack.onFailed(ResponseResultType.ServerError, t.getMessage(), 0);
            }
        });


    }

    public void sendTraffik(SendTraffikRecordRequest sendTraffikRecordRequest, final ServiceResultCallBack<String> callBack) {


        ParkbanServiceProvider.getInstance().sendTraffikRecord(sendTraffikRecordRequest).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    if (response.isSuccessful()) {

                        if (response.body().trim().equals("Ok")) {
                            callBack.onSuccess(response.body());
                        } else {
                            callBack.onFailed(ResponseResultType.ServerError, response.message(), response.code());
                        }


                    } else {
                        callBack.onFailed(ResponseResultType.ServerError, response.message(), response.code());
                    }
                } catch (Exception e) {
                    callBack.onFailed(ResponseResultType.RetrofitError, "", 0);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                callBack.onFailed(ResponseResultType.ServerError, t.getMessage(), 0);
            }
        });


    }




    public void forgotEntrance(ForgotEntranceRequest forgotEntranceRequest, final ServiceResultCallBack<ForgotRecordResponse> callBack) {


        ParkbanServiceProvider.getInstance().findEntranceWithoutExit(forgotEntranceRequest).enqueue(new Callback<ForgotRecordResponse>() {
            @Override
            public void onResponse(Call<ForgotRecordResponse> call, Response<ForgotRecordResponse> response) {
                try {
                    if (response.isSuccessful()) {

                        callBack.onSuccess(response.body());


                    } else {
                        callBack.onFailed(ResponseResultType.ServerError, response.message(), response.code());
                    }
                } catch (Exception e) {
                    callBack.onFailed(ResponseResultType.RetrofitError, "", 0);
                }
            }

            @Override
            public void onFailure(Call<ForgotRecordResponse> call, Throwable t) {
                callBack.onFailed(ResponseResultType.ServerError, t.getMessage(), 0);
            }
        });


    }





    //)))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))
    //)))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))
    //)))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))
    //)))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))
    //)))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))


    //)))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))
    //)))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))
    //)))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))
    //)))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))
    //)))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))
    //)))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))

    /**** Server Repository ****/
    public void login(String userName, String password, final ServiceResultCallBack<LoginResultDto.Parkban> callBack) {

        ParkbanServiceProvider.getInstance().login(userName, password, 1).enqueue(new Callback<LoginResultDto>() {
            @Override
            public void onResponse(Call<LoginResultDto> call, Response<LoginResultDto> response) {
                try {
                    if (response.isSuccessful()) {
                        if (response.body().getResponseResultType() != ResponseResultType.Ok.getValue()) {
                            callBack.onFailed(ResponseResultType.ServerError, "", response.body().getResponseResultType());

                        } else {
                            callBack.onSuccess(response.body().getValue());

                        }
                    } else {
                        callBack.onFailed(ResponseResultType.ServerError, response.message(), response.code());

                    }
                } catch (Exception e) {
                    callBack.onFailed(ResponseResultType.RetrofitError, "", 0);

                }
            }

            @Override
            public void onFailure(Call<LoginResultDto> call, Throwable t) {
                callBack.onFailed(ResponseResultType.ServerError, t.getMessage(), 0);

            }
        });
    }

    public void sendBasicInformation(String plate, String memberCode, String cardNumber, final ServiceResultCallBack<ExitBillDto.ExitBillDtoResponse> callBack) {

        ParkbanServiceProvider.getInstance().sendBaseInformation(plate, memberCode, cardNumber).enqueue(new Callback<ExitBillDto>() {
            @Override
            public void onResponse(Call<ExitBillDto> call, Response<ExitBillDto> response) {
                try {
                    if (response.isSuccessful()) {
                        if (response.body().getResponseResultType() != ResponseResultType.Ok.getValue()) {
                            callBack.onFailed(ResponseResultType.NotFoundEntry, response.body().getMessage(), response.body().getResponseResultType());
                        } else {
                            callBack.onSuccess(response.body().getValue());
                        }
                    } else {
                        callBack.onFailed(ResponseResultType.ServerError, response.message(), response.code());
                    }
                } catch (Exception e) {
                    callBack.onFailed(ResponseResultType.RetrofitError, "", 0);
                }

            }

            @Override
            public void onFailure(Call<ExitBillDto> call, Throwable t) {
                callBack.onFailed(ResponseResultType.ServerError, t.getMessage(), 0);
            }
        });

    }

    public void cashePayment(long dumpId, long amount, final ServiceResultCallBack<CashBillDto.RecipeCash> callBack) {

        ParkbanServiceProvider.getInstance().CashPayment(dumpId, amount).enqueue(new Callback<CashBillDto>() {
            @Override
            public void onResponse(Call<CashBillDto> call, Response<CashBillDto> response) {
                try {
                    if (response.isSuccessful()) {
                        if (response.body().getResponseResultType() != ResponseResultType.Ok.getValue())
                            callBack.onFailed(ResponseResultType.ServerError, "", response.body().getResponseResultType());
                        else {
                            callBack.onSuccess(response.body().getValue());
                        }
                    } else {
                        callBack.onFailed(ResponseResultType.ServerError, response.message(), response.code());
                    }
                } catch (Exception e) {
                    callBack.onFailed(ResponseResultType.RetrofitError, "", 0);
                }

            }

            @Override
            public void onFailure(Call<CashBillDto> call, Throwable t) {
                callBack.onFailed(ResponseResultType.ServerError, t.getMessage(), 0);
            }
        });

    }

    public void firstElectronicPayment(long dumpId, long amount, final ServiceResultCallBack<FirstElectronicPaymentDto.FirstElectronicPaymentResponse> callBack) {

        ParkbanServiceProvider.getInstance().firstElectronicPayment(dumpId, amount).enqueue(new Callback<FirstElectronicPaymentDto>() {
            @Override
            public void onResponse(Call<FirstElectronicPaymentDto> call, Response<FirstElectronicPaymentDto> response) {

                try {
                    if (response.isSuccessful()) {
                        if (response.body().getResponseResultType() != ResponseResultType.Ok.getValue())
                            callBack.onFailed(ResponseResultType.ServerError, "", response.body().getResponseResultType());
                        else {
                            callBack.onSuccess(response.body().getValue());
                        }
                    } else {
                        callBack.onFailed(ResponseResultType.ServerError, response.message(), response.code());
                    }
                } catch (Exception e) {
                    callBack.onFailed(ResponseResultType.RetrofitError, "", 0);
                }

            }

            @Override
            public void onFailure(Call<FirstElectronicPaymentDto> call, Throwable t) {
                callBack.onFailed(ResponseResultType.ServerError, t.getMessage(), 0);
            }
        });
    }

    public void thirdElectronicPayment(ThirdElectronicPaymentRequestDto.ThirdPart requestObj, final ServiceResultCallBack<ThirdElectronicPaymentResponseDto> callBack) {
        ParkbanServiceProvider.getInstance().thirdElectronicPayment(requestObj).enqueue(new Callback<ThirdElectronicPaymentResponseDto>() {
            @Override
            public void onResponse(Call<ThirdElectronicPaymentResponseDto> call, Response<ThirdElectronicPaymentResponseDto> response) {

                try {
                    if (response.isSuccessful()) {
                        if (response.body().getResponseResultType() != ResponseResultType.Ok.getValue())
                            callBack.onFailed(ResponseResultType.ServerError, "", response.body().getResponseResultType());
                        else {
                            callBack.onSuccess(response.body());
                        }
                    } else {
                        callBack.onFailed(ResponseResultType.ServerError, response.message(), response.code());
                    }

                } catch (Exception e) {
                    callBack.onFailed(ResponseResultType.RetrofitError, "", 0);
                }

            }

            @Override
            public void onFailure(Call<ThirdElectronicPaymentResponseDto> call, Throwable t) {
                callBack.onFailed(ResponseResultType.ServerError, t.getMessage(), 0);
            }
        });
    }

    public void forthElectronicPayment(long factorId, final ServiceResultCallBack<ForthElectronicPaymentDto> callBack) {
        ParkbanServiceProvider.getInstance().forthElectronicPayment(factorId).enqueue(new Callback<ForthElectronicPaymentDto>() {
            @Override
            public void onResponse(Call<ForthElectronicPaymentDto> call, Response<ForthElectronicPaymentDto> response) {
                try {
                    if (response.isSuccessful()) {
                        if (response.body().getResponseResultType() != ResponseResultType.Ok.getValue())
                            callBack.onFailed(ResponseResultType.ServerError, "", response.body().getResponseResultType());
                        else {
                            callBack.onSuccess(response.body());
                        }
                    } else {
                        callBack.onFailed(ResponseResultType.ServerError, response.message(), response.code());
                    }
                } catch (Exception e) {
                    callBack.onFailed(ResponseResultType.RetrofitError, "", 0);
                }
            }

            @Override
            public void onFailure(Call<ForthElectronicPaymentDto> call, Throwable t) {
                callBack.onFailed(ResponseResultType.ServerError, t.getMessage(), 0);
            }
        });
    }

    public void getParkbanReportList(String startDate, String endDate, final ServiceResultCallBack<ReportResultDto> callBack) {

        ParkbanServiceProvider.getInstance().getParkbanReportList(startDate, endDate).enqueue(new Callback<ReportResultDto>() {
            @Override
            public void onResponse(Call<ReportResultDto> call, Response<ReportResultDto> response) {
                try {
                    if (response.isSuccessful()) {

                        if (response.body().getResponseResultType() != ResponseResultType.Ok.getValue()) {

                            callBack.onFailed(ResponseResultType.ServerError, "", response.body().getResponseResultType());


                        } else {

                            callBack.onSuccess(response.body());


                        }
                    } else {

                        callBack.onFailed(ResponseResultType.ServerError, response.message(), response.code());


                    }
                } catch (Exception e) {
                    callBack.onFailed(ResponseResultType.RetrofitError, "", 0);

                }
            }

            @Override
            public void onFailure(Call<ReportResultDto> call, Throwable t) {
                callBack.onFailed(ResponseResultType.ServerError, t.getMessage(), 0);
            }
        });


    }


    //)))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))
    //)))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))
    //)))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))
    //)))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))
    //)))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))


    /**** Server Repository ****/
    public void sendRecord(CarRecordsDto carRecords, final ServiceResultCallBack<SendRecordResultDto.SendRecordStatus> callBack) {
        ParkbanServiceProvider.getInstance().sendRecord(carRecords)
                .enqueue(new Callback<SendRecordResultDto>() {
                    @Override
                    public void onResponse(Call<SendRecordResultDto> call, Response<SendRecordResultDto> response) {
                        try {
                            if (response.isSuccessful()) {
                                if (response.body().getResponseResultType() != ResponseResultType.Ok.getValue())
                                    callBack.onFailed(ResponseResultType.ServerError, "", response.body().getResponseResultType());
                                else {
                                    callBack.onSuccess(response.body().getValue());
                                }
                            } else {
                                callBack.onFailed(ResponseResultType.ServerError, response.message(), response.code());
                            }
                        } catch (Exception e) {
                            callBack.onFailed(ResponseResultType.RetrofitError, "", 0);
                        }

                    }

                    @Override
                    public void onFailure(Call<SendRecordResultDto> call, Throwable t) {
                        callBack.onFailed(ResponseResultType.ServerError, t.getMessage(), 0);
                    }
                });
    }

    public void getParkMarginLimit(final ServiceResultCallBack<IntResultDto> callBack) {
        ParkbanServiceProvider.getInstance().getParkMarginLimit().enqueue(new Callback<IntResultDto>() {
            @Override
            public void onResponse(Call<IntResultDto> call, Response<IntResultDto> response) {
                try {
                    if (response.isSuccessful()) {
                        if (response.body().getResponseResultType() != ResponseResultType.Ok.getValue())
                            callBack.onFailed(ResponseResultType.ServerError, "", response.body().getResponseResultType());
                        else {
                            callBack.onSuccess(response.body());
                        }
                    } else {
                        callBack.onFailed(ResponseResultType.ServerError, response.message(), response.code());
                    }
                } catch (Exception e) {
                    callBack.onFailed(ResponseResultType.RetrofitError, e.getMessage(), 0);
                }
            }

            @Override
            public void onFailure(Call<IntResultDto> call, Throwable t) {
                callBack.onFailed(ResponseResultType.ServerError, t.getMessage(), 0);
            }
        });
    }

    public void getLastVersion(final ServiceResultCallBack<StringResultDto> callBack) {
        ParkbanServiceProvider.getInstance().getLastVersion().enqueue(new Callback<StringResultDto>() {
            @Override
            public void onResponse(Call<StringResultDto> call, Response<StringResultDto> response) {
                try {
                    if (response.isSuccessful()) {
                        if (response.body().getResponseResultType() != ResponseResultType.Ok.getValue())
                            callBack.onFailed(ResponseResultType.ServerError, "", response.body().getResponseResultType());
                        else {
                            callBack.onSuccess(response.body());
                        }
                    } else {
                        callBack.onFailed(ResponseResultType.ServerError, response.message(), response.code());
                    }
                } catch (Exception e) {
                    callBack.onFailed(ResponseResultType.RetrofitError, "", 0);
                }
            }

            @Override
            public void onFailure(Call<StringResultDto> call, Throwable t) {
                callBack.onFailed(ResponseResultType.ServerError, t.getMessage(), 0);
            }
        });
    }

    public void getCurrentShift(long parkbanUserId, final ServiceResultCallBack<CurrentShiftDto> callBack) {
        ParkbanServiceProvider.getInstance().getCurrentShift(parkbanUserId).enqueue(new Callback<CurrentShiftDto>() {
            @Override
            public void onResponse(Call<CurrentShiftDto> call, Response<CurrentShiftDto> response) {
                try {
                    if (response.isSuccessful()) {
                        if (response.body().getResponseResultType() != ResponseResultType.Ok.getValue())
                            callBack.onFailed(ResponseResultType.ServerError, "", response.body().getResponseResultType());
                        else {
                            callBack.onSuccess(response.body());
                        }
                    } else {
                        callBack.onFailed(ResponseResultType.ServerError, response.message(), response.code());
                    }
                } catch (Exception e) {
                    callBack.onFailed(ResponseResultType.RetrofitError, "", 0);
                }
            }

            @Override
            public void onFailure(Call<CurrentShiftDto> call, Throwable t) {
                callBack.onFailed(ResponseResultType.ServerError, t.getMessage(), 0);
            }
        });
    }

    public void getParkbanShift(long parkbanUserId, String startDate, String endDate, final ServiceResultCallBack<ParkbanShiftResultDto> callBack) {
        ParkbanServiceProvider.getInstance().getParkbanShift(parkbanUserId, startDate, endDate).enqueue(new Callback<ParkbanShiftResultDto>() {
            @Override
            public void onResponse(Call<ParkbanShiftResultDto> call, Response<ParkbanShiftResultDto> response) {
                try {
                    if (response.isSuccessful()) {
                        if (response.body().getResponseResultType() != ResponseResultType.Ok.getValue())
                            callBack.onFailed(ResponseResultType.ServerError, "", response.body().getResponseResultType());
                        else {
                            callBack.onSuccess(response.body());
                        }
                    } else {
                        callBack.onFailed(ResponseResultType.ServerError, response.message(), response.code());
                    }
                } catch (Exception e) {
                    callBack.onFailed(ResponseResultType.RetrofitError, "", 0);
                }
            }

            @Override
            public void onFailure(Call<ParkbanShiftResultDto> call, Throwable t) {
                callBack.onFailed(ResponseResultType.ServerError, t.getMessage(), 0);
            }
        });
    }

    public void getParkbanFunctionality(long parkbanUserId, String startDate, String endDate, int workShiftType, final ServiceResultCallBack<FunctionalityResultDto> callBack) {
        ParkbanServiceProvider.getInstance().getParkbanFunctionality(parkbanUserId, startDate, endDate, workShiftType).enqueue(new Callback<FunctionalityResultDto>() {
            @Override
            public void onResponse(Call<FunctionalityResultDto> call, Response<FunctionalityResultDto> response) {
                try {
                    if (response.isSuccessful()) {
                        if (response.body().getResponseResultType() != ResponseResultType.Ok.getValue())
                            callBack.onFailed(ResponseResultType.ServerError, "", response.body().getResponseResultType());
                        else {
                            callBack.onSuccess(response.body());
                        }
                    } else {
                        callBack.onFailed(ResponseResultType.ServerError, response.message(), response.code());
                    }
                } catch (Exception e) {
                    callBack.onFailed(ResponseResultType.RetrofitError, "", 0);
                }
            }

            @Override
            public void onFailure(Call<FunctionalityResultDto> call, Throwable t) {
                callBack.onFailed(ResponseResultType.ServerError, t.getMessage(), 0);
            }
        });
    }

    public void getParkAmount(long parkSpaceId, String dateTime, String firstTime, String lastTime, final ServiceResultCallBack<ParkAmountResultDto> callBack) {
        ParkbanServiceProvider.getInstance().getParkAmount(parkSpaceId, dateTime, firstTime, lastTime).enqueue(new Callback<ParkAmountResultDto>() {
            @Override
            public void onResponse(Call<ParkAmountResultDto> call, Response<ParkAmountResultDto> response) {
                try {
                    if (response.isSuccessful()) {
                        if (response.body().getResponseResultType() != ResponseResultType.Ok.getValue())
                            callBack.onFailed(ResponseResultType.ServerError, "", response.body().getResponseResultType());
                        else {
                            callBack.onSuccess(response.body());
                        }
                    } else {
                        callBack.onFailed(ResponseResultType.ServerError, response.message(), response.code());
                    }
                } catch (Exception e) {
                    callBack.onFailed(ResponseResultType.RetrofitError, "", 0);
                }
            }

            @Override
            public void onFailure(Call<ParkAmountResultDto> call, Throwable t) {
                callBack.onFailed(ResponseResultType.ServerError, t.getMessage(), 0);
            }
        });
    }

    public void sendRecordAndPay(CarRecordsDto record, final ServiceResultCallBack<SendRecordAndPayResultDto> callBack) {
        ParkbanServiceProvider.getInstance().sendRecordAndPay(record).enqueue(new Callback<SendRecordAndPayResultDto>() {
            @Override
            public void onResponse(Call<SendRecordAndPayResultDto> call, Response<SendRecordAndPayResultDto> response) {
                try {
                    if (response.isSuccessful()) {
                        if (response.body().getResponseResultType() != ResponseResultType.Ok.getValue())
                            callBack.onFailed(ResponseResultType.ServerError, "", response.body().getResponseResultType());
                        else {
                            callBack.onSuccess(response.body());
                        }
                    } else {
                        callBack.onFailed(ResponseResultType.ServerError, response.message(), response.code());
                    }
                } catch (Exception e) {
                    callBack.onFailed(ResponseResultType.RetrofitError, "", 0);
                }
            }

            @Override
            public void onFailure(Call<SendRecordAndPayResultDto> call, Throwable t) {
                callBack.onFailed(ResponseResultType.ServerError, t.getMessage(), 0);
            }
        });
    }

    public void hasParkbanCredit(long amount, final ServiceResultCallBack<BooleanResultDto> callBack) {
        ParkbanServiceProvider.getInstance().hasParkbanCredit(amount).enqueue(new Callback<BooleanResultDto>() {
            @Override
            public void onResponse(Call<BooleanResultDto> call, Response<BooleanResultDto> response) {
                try {
                    if (response.isSuccessful()) {
                        if (response.body().getResponseResultType() != ResponseResultType.Ok.getValue())
                            callBack.onFailed(ResponseResultType.ServerError, "", response.body().getResponseResultType());
                        else {
                            callBack.onSuccess(response.body());
                        }
                    } else {
                        callBack.onFailed(ResponseResultType.ServerError, response.message(), response.code());
                    }
                } catch (Exception e) {
                    callBack.onFailed(ResponseResultType.RetrofitError, "", 0);
                }
            }

            @Override
            public void onFailure(Call<BooleanResultDto> call, Throwable t) {
                callBack.onFailed(ResponseResultType.ServerError, t.getMessage(), 0);
            }
        });
    }

    public void increaseDiverWallet(IncreaseDriverWalletResultDto.IncreaseDriverWalletDto wallet, final ServiceResultCallBack<IncreaseDriverWalletResultDto> callBack) {
        ParkbanServiceProvider.getInstance().increaseDriverWallet(wallet).enqueue(new Callback<IncreaseDriverWalletResultDto>() {
            @Override
            public void onResponse(Call<IncreaseDriverWalletResultDto> call, Response<IncreaseDriverWalletResultDto> response) {
                try {
                    if (response.isSuccessful()) {
                        if (response.body().getResponseResultType() != ResponseResultType.Ok.getValue())
                            callBack.onFailed(ResponseResultType.ServerError, "", response.body().getResponseResultType());
                        else {
                            callBack.onSuccess(response.body());
                        }
                    } else {
                        callBack.onFailed(ResponseResultType.ServerError, response.message(), response.code());
                    }
                } catch (Exception e) {
                    callBack.onFailed(ResponseResultType.RetrofitError, "", 0);
                }
            }

            @Override
            public void onFailure(Call<IncreaseDriverWalletResultDto> call, Throwable t) {
                callBack.onFailed(ResponseResultType.ServerError, t.getMessage(), 0);
            }
        });
    }

    //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    public void increaseDiverWalletCard(IncreaseDriverWalletCardResultDto.IncreaseDriverWalletDto wallet, final ServiceResultCallBack<IncreaseDriverWalletCardResultDto> callBack) {
        ParkbanServiceProvider.getInstance().increaseDriverWalletCard(wallet).enqueue(new Callback<IncreaseDriverWalletCardResultDto>() {
            @Override
            public void onResponse(Call<IncreaseDriverWalletCardResultDto> call, Response<IncreaseDriverWalletCardResultDto> response) {
                try {
                    if (response.isSuccessful()) {
                        if (response.body().getResponseResultType() != ResponseResultType.Ok.getValue())
                            callBack.onFailed(ResponseResultType.ServerError, "", response.body().getResponseResultType());
                        else {
                            callBack.onSuccess(response.body());
                        }
                    } else {
                        callBack.onFailed(ResponseResultType.ServerError, response.message(), response.code());
                    }

                } catch (Exception e) {
                    callBack.onFailed(ResponseResultType.RetrofitError, "", 0);
                }
            }

            @Override
            public void onFailure(Call<IncreaseDriverWalletCardResultDto> call, Throwable t) {
                callBack.onFailed(ResponseResultType.ServerError, t.getMessage(), 0);
            }
        });
    }


    public void thirdPartRepoAction(ThirdPartDto.ThirdPart requestObj, final ServiceResultCallBack<ThirdPartResponseDto> callBack) {
        ParkbanServiceProvider.getInstance().thirdPartAction(requestObj).enqueue(new Callback<ThirdPartResponseDto>() {
            @Override
            public void onResponse(Call<ThirdPartResponseDto> call, Response<ThirdPartResponseDto> response) {
                try {
                    if (response.isSuccessful()) {
                        if (response.body().getResponseResultType() != ResponseResultType.Ok.getValue())
                            callBack.onFailed(ResponseResultType.ServerError, "", response.body().getResponseResultType());
                        else {
                            callBack.onSuccess(response.body());
                        }
                    } else {
                        callBack.onFailed(ResponseResultType.ServerError, response.message(), response.code());
                    }

                } catch (Exception e) {
                    callBack.onFailed(ResponseResultType.RetrofitError, "", 0);
                }
            }

            @Override
            public void onFailure(Call<ThirdPartResponseDto> call, Throwable t) {
                callBack.onFailed(ResponseResultType.ServerError, t.getMessage(), 0);
            }
        });
    }


    public void forthPartRepoAction(long factorId, final ServiceResultCallBack<IncreaseDriverWalletCardResultDto> callBack) {
        ParkbanServiceProvider.getInstance().forthPartAction(factorId).enqueue(new Callback<IncreaseDriverWalletCardResultDto>() {
            @Override
            public void onResponse(Call<IncreaseDriverWalletCardResultDto> call, Response<IncreaseDriverWalletCardResultDto> response) {
                try {
                    if (response.isSuccessful()) {
                        if (response.body().getResponseResultType() != ResponseResultType.Ok.getValue())
                            callBack.onFailed(ResponseResultType.ServerError, "", response.body().getResponseResultType());
                        else {
                            callBack.onSuccess(response.body());
                        }
                    } else {
                        callBack.onFailed(ResponseResultType.ServerError, response.message(), response.code());
                    }
                } catch (Exception e) {
                    callBack.onFailed(ResponseResultType.RetrofitError, "", 0);
                }
            }

            @Override
            public void onFailure(Call<IncreaseDriverWalletCardResultDto> call, Throwable t) {
                callBack.onFailed(ResponseResultType.ServerError, t.getMessage(), 0);
            }
        });
    }


    public void solveFailedFactorsRepoAction(FailedFactorsRequestDto.RequestFailedFactor requestObject, final ServiceResultCallBack<FailedFactorsResultDto> callBack) {
        ParkbanServiceProvider.getInstance().solveFailedFactors(requestObject).enqueue(new Callback<FailedFactorsResultDto>() {
            @Override
            public void onResponse(Call<FailedFactorsResultDto> call, Response<FailedFactorsResultDto> response) {
                try {
                    if (response.isSuccessful()) {
                        if (response.body().getResponseResultType() != ResponseResultType.Ok.getValue())
                            callBack.onFailed(ResponseResultType.ServerError, "", response.body().getResponseResultType());
                        else {
                            callBack.onSuccess(response.body());
                        }
                    } else {
                        callBack.onFailed(ResponseResultType.ServerError, response.message(), response.code());
                    }
                } catch (Exception e) {
                    callBack.onFailed(ResponseResultType.RetrofitError, "", 0);
                }
            }

            @Override
            public void onFailure(Call<FailedFactorsResultDto> call, Throwable t) {
                callBack.onFailed(ResponseResultType.ServerError, t.getMessage(), 0);
            }
        });
    }


    //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

    public void getChargeReport(long parkbanId, String startDate, String endDate, final ServiceResultCallBack<CashDetailsResultDto> callBack) {
        ParkbanServiceProvider.getInstance().getChargeReport(parkbanId, startDate, endDate).enqueue(new Callback<CashDetailsResultDto>() {
            @Override
            public void onResponse(Call<CashDetailsResultDto> call, Response<CashDetailsResultDto> response) {
                try {
                    if (response.isSuccessful()) {
                        if (response.body().getResponseResultType() != ResponseResultType.Ok.getValue())
                            callBack.onFailed(ResponseResultType.ServerError, "", response.body().getResponseResultType());
                        else {
                            callBack.onSuccess(response.body());
                        }
                    } else {
                        callBack.onFailed(ResponseResultType.ServerError, response.message(), response.code());
                    }
                } catch (Exception e) {
                    callBack.onFailed(ResponseResultType.RetrofitError, "", 0);
                }
            }

            @Override
            public void onFailure(Call<CashDetailsResultDto> call, Throwable t) {
                callBack.onFailed(ResponseResultType.ServerError, t.getMessage(), 0);
            }
        });
    }

    public void getUnregisteredCarDebt(String plate, final ServiceResultCallBack<LongResultDto> callBack) {
        ParkbanServiceProvider.getInstance().getUnregisteredCarDebt(plate).enqueue(new Callback<LongResultDto>() {
            @Override
            public void onResponse(Call<LongResultDto> call, Response<LongResultDto> response) {
                try {
                    if (response.isSuccessful()) {
                        if (response.body().getResponseResultType() != ResponseResultType.Ok.getValue())
                            callBack.onFailed(ResponseResultType.ServerError, "", response.body().getResponseResultType());
                        else {
                            callBack.onSuccess(response.body());
                        }
                    } else {
                        callBack.onFailed(ResponseResultType.ServerError, response.message(), response.code());
                    }
                } catch (Exception e) {
                    callBack.onFailed(ResponseResultType.RetrofitError, "", 0);
                }
            }

            @Override
            public void onFailure(Call<LongResultDto> call, Throwable t) {
                callBack.onFailed(ResponseResultType.ServerError, t.getMessage(), 0);
            }
        });
    }

    public void getDriverFullDebtAndWallet(long phoneNumber, final ServiceResultCallBack<DriverDebtResultDto> callBack) {
        ParkbanServiceProvider.getInstance().getDriverFullDebtAndWallet(phoneNumber).enqueue(new Callback<DriverDebtResultDto>() {
            @Override
            public void onResponse(Call<DriverDebtResultDto> call, Response<DriverDebtResultDto> response) {
                try {
                    if (response.isSuccessful()) {
                        if (response.body().getResponseResultType() != ResponseResultType.Ok.getValue())
                            callBack.onFailed(ResponseResultType.ServerError, "", response.body().getResponseResultType());
                        else {
                            callBack.onSuccess(response.body());
                        }
                    } else {
                        callBack.onFailed(ResponseResultType.ServerError, response.message(), response.code());
                    }
                } catch (Exception e) {
                    callBack.onFailed(ResponseResultType.RetrofitError, "", 0);
                }
            }

            @Override
            public void onFailure(Call<DriverDebtResultDto> call, Throwable t) {
                callBack.onFailed(ResponseResultType.ServerError, t.getMessage(), 0);
            }
        });
    }


    /**** DataBase Repository ****/


    //)))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))
    //)))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))
    //)))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))
    //)))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))
    //)))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))
    //)))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))

    /**** Khodmohaseb Datebase Repository ****/

    public void saveEntranceRecord(
            long deviceID,
            String plate,
            String entranceDate,
            int tariffId,
            long paidAmount,
            int payType,
            String electronicPaymentCode,
            long entranceDoorId,
            long entranceOperatorId,
            int sourceKind
            , final DataBaseResultCallBack callBack) {
        new SaveEntranceRecordAsyncTask(callBack)
                .execute(database, deviceID, plate, entranceDate, tariffId, paidAmount, payType, electronicPaymentCode, entranceDoorId, entranceOperatorId, sourceKind);
    }

    private static class SaveEntranceRecordAsyncTask extends AsyncTask<Object, Void, Long> {

        private final DataBaseResultCallBack callBack;

        public SaveEntranceRecordAsyncTask(DataBaseResultCallBack callBack) {
            this.callBack = callBack;
        }

        @Override
        protected Long doInBackground(Object... params) {

            ParkbanDatabase database = (ParkbanDatabase) params[0];
            long deviceId = (long) params[1];
            String plate = (String) params[2];
            String entranceDate = (String) params[3];
            int tariffId = (int) params[4];
            long paidAmount = (long) params[5];
            int payType = (int) params[6];
            String electronicPaymentCode = (String) params[7];
            long entranceDoorId = (long) params[8];
            long entranceOperatorId = (long) params[9];
            int sourceKind = (int) params[10];

            EntranceRecord entranceRecord = new EntranceRecord();
            entranceRecord.setDeviceID(deviceId);
            entranceRecord.setPlate(plate);
            entranceRecord.setEntranceDate(entranceDate);
            entranceRecord.setTariffId(tariffId);
            entranceRecord.setPaidAmount(paidAmount);
            entranceRecord.setPayType(payType);
            entranceRecord.setElectronicPaymentCode(electronicPaymentCode);
            entranceRecord.setEntranceDoorId(entranceDoorId);
            entranceRecord.setEntranceOperatorId(entranceOperatorId);
            entranceRecord.setSourceKind(sourceKind);


            try {
                return database.getEntranceRecordDao().saveEntranceRecord(entranceRecord);
            } catch (Exception e) {
                Log.e(TAG, "Error in saveCarPlate", e);
                return 0L;
            }
        }

        @Override
        protected void onPostExecute(Long result) {
            super.onPostExecute(result);
            if (result > 0) {
                callBack.onSuccess(result);
            } else {
                callBack.onFailed();
            }
        }
    }


    public void getFromEntranceTable(final DataBaseEntranceRecordResultCallBack callBack) {
        new GetFromEntranceTableAsyncTask(callBack).execute(database);
    }

    private static class GetFromEntranceTableAsyncTask extends AsyncTask<Object, Void, List<EntranceRecord>> {
        private final DataBaseEntranceRecordResultCallBack callBack;

        public GetFromEntranceTableAsyncTask(DataBaseEntranceRecordResultCallBack callBack) {
            this.callBack = callBack;
        }

        @Override
        protected List<EntranceRecord> doInBackground(Object... params) {
            ParkbanDatabase database = (ParkbanDatabase) params[0];
            List<EntranceRecord> entranceRecordList;
            try {
                return database.getEntranceRecordDao().getFiveEntranceRecord();

            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<EntranceRecord> entranceRecordList) {
            super.onPostExecute(entranceRecordList);
            callBack.onSuccess(entranceRecordList);
            if (entranceRecordList == null)
                callBack.onFailed();
        }
    }


    public void isCarInserted(
            String plate
            , final DataBaseInsertedCarPelakCallBack callBack) {
        new IsCarInsertedAsyncTask(callBack)
                .execute(database, plate);
    }

    private static class IsCarInsertedAsyncTask extends AsyncTask<Object, Void, EntranceRecord> {

        private final DataBaseInsertedCarPelakCallBack callBack;

        public IsCarInsertedAsyncTask(DataBaseInsertedCarPelakCallBack callBack) {
            this.callBack = callBack;
        }

        @Override
        protected EntranceRecord doInBackground(Object... params) {

            ParkbanDatabase database = (ParkbanDatabase) params[0];
            String plate = (String) params[1];


            try {
                return database.getEntranceRecordDao().getEntranceRecordByPlate(plate);
            } catch (Exception e) {
                Log.e(TAG, "Error in search entrance record", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(EntranceRecord result) {
            super.onPostExecute(result);
            if (result != null) {
                callBack.onSuccess(result);
            } else {
                callBack.onFailed();
            }
        }
    }


    public void deleteEntranceRecord(String plate, final DataBaseResultCallBack callBack) {
        new DeleteEntranceRecordAsyncTask(callBack).execute(database, plate);
    }

    private static class DeleteEntranceRecordAsyncTask extends AsyncTask<Object, Void, Boolean> {
        private final DataBaseResultCallBack callBack;

        public DeleteEntranceRecordAsyncTask(DataBaseResultCallBack callBack) {
            this.callBack = callBack;
        }

        @Override
        protected Boolean doInBackground(Object... params) {
            ParkbanDatabase database = (ParkbanDatabase) params[0];
            String plate = (String) params[1];

            try {
                database.getEntranceRecordDao().deleteOneEntranceRecordByPlate(plate);
                return true;

            } catch (Exception e) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (result)
                callBack.onSuccess(1);
            else
                callBack.onFailed();
        }
    }


    public void setExitEntranceRecord(String plate, final DataBaseResultCallBack callBack) {
        new SetExitEntranceRecordAsyncTask(callBack).execute(database, plate);
    }

    private static class SetExitEntranceRecordAsyncTask extends AsyncTask<Object, Void, Boolean> {
        private final DataBaseResultCallBack callBack;

        public SetExitEntranceRecordAsyncTask(DataBaseResultCallBack callBack) {
            this.callBack = callBack;
        }

        @Override
        protected Boolean doInBackground(Object... params) {
            ParkbanDatabase database = (ParkbanDatabase) params[0];
            String plate = (String) params[1];

            try {
                database.getEntranceRecordDao().setExitEntranceRecordByPlate(plate);
                return true;

            } catch (Exception e) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (result)
                callBack.onSuccess(1);
            else
                callBack.onFailed();
        }
    }


    public void deleteFromEntranceRecord(List<String> plateList, final DataBaseBooleanCallBack callBack) {
        new DeleteFromEntranceRecordAsyncTask(callBack).execute(database, plateList);
    }

    private static class DeleteFromEntranceRecordAsyncTask extends AsyncTask<Object, Void, Boolean> {
        private final DataBaseBooleanCallBack callBack;

        public DeleteFromEntranceRecordAsyncTask(DataBaseBooleanCallBack callBack) {
            this.callBack = callBack;
        }

        @Override
        protected Boolean doInBackground(Object... params) {
            ParkbanDatabase database = (ParkbanDatabase) params[0];
            List<String> plateList = (List<String>) params[1];

            try {
                database.getEntranceRecordDao().deleteEntranceRecordByPlate(plateList);
                return true;

            } catch (Exception e) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (result)
                callBack.onSuccess(true);
            else
                callBack.onFailed();
        }
    }


    public void saveExitRecord(
            long deviceID,
            String plate,
            String exitDate,
            int tariffId,
            long paidAmount,
            int payType,
            String electronicPaymentCode,
            long exitDoorId,
            long exitOperatorId,
            int sourceKind
            , final DataBaseResultCallBack callBack) {
        new SaveExitRecordAsyncTask(callBack)
                .execute(database, deviceID, plate, exitDate, tariffId, paidAmount, payType, electronicPaymentCode, exitDoorId, exitOperatorId, sourceKind);
    }

    private static class SaveExitRecordAsyncTask extends AsyncTask<Object, Void, Long> {

        private final DataBaseResultCallBack callBack;

        public SaveExitRecordAsyncTask(DataBaseResultCallBack callBack) {
            this.callBack = callBack;
        }

        @Override
        protected Long doInBackground(Object... params) {

            ParkbanDatabase database = (ParkbanDatabase) params[0];
            long deviceID = (long) params[1];
            String plate = (String) params[2];
            String exitDate = (String) params[3];
            int tariffId = (int) params[4];
            long paidAmount = (long) params[5];
            int payType = (int) params[6];
            String electronicPaymentCode = (String) params[7];
            long exitDoorId = (long) params[8];
            long exitOperatorId = (long) params[9];
            int sourceKind = (int) params[10];

            ExitRecord exitRecord = new ExitRecord();
            exitRecord.setDeviceID(deviceID);
            exitRecord.setPlate(plate);
            exitRecord.setExitDate(exitDate);
            exitRecord.setTariffId(tariffId);
            exitRecord.setPaidAmount(paidAmount);
            exitRecord.setPayType(payType);
            exitRecord.setElectronicPaymentCode(electronicPaymentCode);
            exitRecord.setExitDoorId(exitDoorId);
            exitRecord.setExitOperatorId(exitOperatorId);
            exitRecord.setSourceKind(sourceKind);


            try {
                return database.getExitRecordDao().saveExitRecord(exitRecord);
            } catch (Exception e) {
                Log.e(TAG, "Error in save exit record", e);
                return 0L;
            }
        }

        @Override
        protected void onPostExecute(Long result) {
            super.onPostExecute(result);
            if (result > 0) {
                callBack.onSuccess(result);
            } else {
                callBack.onFailed();
            }
        }
    }


    public void deleteExitRecord(String plate, final DataBaseResultCallBack callBack) {
        new DeleteExitRecordAsyncTask(callBack).execute(database, plate);
    }

    private static class DeleteExitRecordAsyncTask extends AsyncTask<Object, Void, Boolean> {
        private final DataBaseResultCallBack callBack;

        public DeleteExitRecordAsyncTask(DataBaseResultCallBack callBack) {
            this.callBack = callBack;
        }

        @Override
        protected Boolean doInBackground(Object... params) {
            ParkbanDatabase database = (ParkbanDatabase) params[0];
            String plate = (String) params[1];

            try {
                database.getExitRecordDao().deleteOneExitRecordByPlate(plate);
                return true;

            } catch (Exception e) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (result)
                callBack.onSuccess(1);
            else
                callBack.onFailed();
        }
    }


    public void getFromExitTable(final DataBaseExitRecordResultCallBack callBack) {
        new GetFromExitTableAsyncTask(callBack).execute(database);
    }

    private static class GetFromExitTableAsyncTask extends AsyncTask<Object, Void, List<ExitRecord>> {
        private final DataBaseExitRecordResultCallBack callBack;

        public GetFromExitTableAsyncTask(DataBaseExitRecordResultCallBack callBack) {
            this.callBack = callBack;
        }

        @Override
        protected List<ExitRecord> doInBackground(Object... params) {
            ParkbanDatabase database = (ParkbanDatabase) params[0];
            List<ExitRecord> exitRecordList;
            try {
                return database.getExitRecordDao().getFiveExitRecord();

            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<ExitRecord> exitRecordList) {
            super.onPostExecute(exitRecordList);
            callBack.onSuccess(exitRecordList);
            if (exitRecordList == null)
                callBack.onFailed();
        }
    }


    public void deleteFromExitRecord(List<String> plateList, final DataBaseBooleanCallBack callBack) {
        new DeleteFromExitRecordAsyncTask(callBack).execute(database, plateList);
    }

    private static class DeleteFromExitRecordAsyncTask extends AsyncTask<Object, Void, Boolean> {
        private final DataBaseBooleanCallBack callBack;

        public DeleteFromExitRecordAsyncTask(DataBaseBooleanCallBack callBack) {
            this.callBack = callBack;
        }

        @Override
        protected Boolean doInBackground(Object... params) {
            ParkbanDatabase database = (ParkbanDatabase) params[0];
            List<String> plateList = (List<String>) params[1];

            try {
                database.getExitRecordDao().deleteExitRecordByPlate(plateList);
                return true;

            } catch (Exception e) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (result)
                callBack.onSuccess(true);
            else
                callBack.onFailed();
        }
    }


    public void saveTraffikRecord(
            long deviceID,
            String plate,
            String entranceDate,
            String exitDate,
            int tariffId,
            long paidAmount,
            int payType,
            String electronicPaymentCode,
            long entranceDoorId,
            long exitDoorId,
            long entranceOperatorId,
            long exitOperatorId,
            String vehicleName
            , final DataBaseResultCallBack callBack) {
        new SaveTraffikRecordAsyncTask(callBack)
                .execute(
                        database,
                        deviceID,
                        plate,
                        entranceDate,
                        exitDate,
                        tariffId,
                        paidAmount,
                        payType,
                        electronicPaymentCode,
                        entranceDoorId,
                        exitDoorId,
                        entranceOperatorId,
                        exitOperatorId,
                        vehicleName

                );
    }

    private static class SaveTraffikRecordAsyncTask extends AsyncTask<Object, Void, Long> {

        private final DataBaseResultCallBack callBack;

        public SaveTraffikRecordAsyncTask(DataBaseResultCallBack callBack) {
            this.callBack = callBack;
        }

        @Override
        protected Long doInBackground(Object... params) {

            ParkbanDatabase database = (ParkbanDatabase) params[0];
            long deviceID = (long) params[1];
            String plate = (String) params[2];
            String entranceDate = (String) params[3];
            String exitDate = (String) params[4];
            int tariffId = (int) params[5];
            long paidAmount = (long) params[6];
            int payType = (int) params[7];
            String electronicPaymentCode = (String) params[8];
            long entranceDoorId = (long) params[9];
            long exitDoorId = (long) params[10];
            long entranceOperatorId = (long) params[11];
            long exitOperatorId = (long) params[12];
            String vehicleName = (String) params[13];

            TraffikRecord traffikRecord = new TraffikRecord();
            traffikRecord.setDevice_id(deviceID);
            traffikRecord.setPlate(plate);
            traffikRecord.setEntranceDate(entranceDate);
            traffikRecord.setExitDate(exitDate);
            traffikRecord.setTariffId(tariffId);
            traffikRecord.setPaidAmount(paidAmount);
            traffikRecord.setPayType(payType);
            traffikRecord.setElectronicPaymentCode(electronicPaymentCode);
            traffikRecord.setEntranceDoorId(entranceDoorId);
            traffikRecord.setExitDoorId(exitDoorId);
            traffikRecord.setEntranceOperatorId(entranceOperatorId);
            traffikRecord.setExitOperatorId(exitOperatorId);
            traffikRecord.setVehicleName(vehicleName);


            try {
                return database.getTraffikRecordDao().saveTraffikRecord(traffikRecord);
            } catch (Exception e) {
                Log.e(TAG, "Error in save traffik record", e);
                return 0L;
            }
        }

        @Override
        protected void onPostExecute(Long result) {
            super.onPostExecute(result);
            if (result > 0) {
                callBack.onSuccess(result);
            } else {
                callBack.onFailed();
            }
        }
    }


    public void getFromTraffikTable(final DataBaseTraffikRecordResultCallBack callBack) {
        new GetFromTraffikTableAsyncTask(callBack).execute(database);
    }

    private static class GetFromTraffikTableAsyncTask extends AsyncTask<Object, Void, List<TraffikRecord>> {
        private final DataBaseTraffikRecordResultCallBack callBack;

        public GetFromTraffikTableAsyncTask(DataBaseTraffikRecordResultCallBack callBack) {
            this.callBack = callBack;
        }

        @Override
        protected List<TraffikRecord> doInBackground(Object... params) {
            ParkbanDatabase database = (ParkbanDatabase) params[0];
            List<TraffikRecord> traffikRecordList;
            try {
                return database.getTraffikRecordDao().getFiveTraffikRecord();

            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<TraffikRecord> traffikRecordList) {
            super.onPostExecute(traffikRecordList);
            callBack.onSuccess(traffikRecordList);
            if (traffikRecordList == null)
                callBack.onFailed();
        }
    }


    public void deleteFromTraffikRecord(List<String> plateList, final DataBaseBooleanCallBack callBack) {
        new DeleteFromTraffikRecordAsyncTask(callBack).execute(database, plateList);
    }

    private static class DeleteFromTraffikRecordAsyncTask extends AsyncTask<Object, Void, Boolean> {
        private final DataBaseBooleanCallBack callBack;

        public DeleteFromTraffikRecordAsyncTask(DataBaseBooleanCallBack callBack) {
            this.callBack = callBack;
        }

        @Override
        protected Boolean doInBackground(Object... params) {
            ParkbanDatabase database = (ParkbanDatabase) params[0];
            List<String> plateList = (List<String>) params[1];

            try {
                database.getTraffikRecordDao().deleteTraffikRecordByPlate(plateList);
                return true;

            } catch (Exception e) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (result)
                callBack.onSuccess(true);
            else
                callBack.onFailed();
        }
    }


    //)))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))
    //)))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))
    //)))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))
    //)))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))
    //)))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))


    //)))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))
    //)))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))
    //)))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))
    //)))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))
    //)))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))
    //)))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))


    public void saveCarPlate(boolean isNewCar, int carId, CarPlate carPlate, boolean pay, final DataBaseResultCallBack callBack) {
        new SaveCarPlateAsyncTask(callBack)
                .execute(database, carPlate, isNewCar, carId, pay);
    }

    private static class SaveCarPlateAsyncTask extends AsyncTask<Object, Void, Long> {

        private final DataBaseResultCallBack callBack;

        public SaveCarPlateAsyncTask(DataBaseResultCallBack callBack) {
            this.callBack = callBack;
        }

        @Override
        protected Long doInBackground(Object... params) {

            ParkbanDatabase database = (ParkbanDatabase) params[0];
            CarPlate carPlate = (CarPlate) params[1];
            boolean isNewCar = (boolean) params[2];
            int carId = (int) params[3];
            boolean pay = (boolean) params[4];

            try {

                if (isNewCar) {
                    Car car = new Car(carPlate.getPlateNumber(), carPlate.getLatitude(), carPlate.getLongitude(), carPlate.getParkingSpaceId());
                    car.setStatus(SendStatus.PENDING.ordinal());
                    car.setId((int) database.getCarDao().saveCar(car));
                    carId = car.getId();
                }

                if (carId <= 0) {
                    return 0L;
                }

                carPlate.setCarId(carId);
                carPlate.setStatus(pay ? SendStatus.IsSENDING.ordinal() : SendStatus.PENDING.ordinal());
                return database.getCarPlateDao().saveCarPlate(carPlate);


            } catch (Exception e) {
                Log.e(TAG, "Error in saveCarPlate", e);
                return 0L;
            }
        }

        @Override
        protected void onPostExecute(Long result) {
            super.onPostExecute(result);
            if (result > 0) {
                callBack.onSuccess(result);
            } else {
                callBack.onFailed();
            }
        }
    }

    public LiveData<Car> getCarByPlateNo(String plateNo) {
        return database.getCarDao().getCarByPlateNo(plateNo);
    }

    public LiveData<List<Car>> getCars() {

        return database.getCarDao().getCurrentCarsOld(DateTimeHelper.getCurrentTimeForDB());
    }

    public LiveData<List<Car>> getPreviousCarsOld() {

        return database.getCarDao().getPreviousCarsOld(DateTimeHelper.getCurrentTimeForDB());
    }

//    public LiveData<List<Car>> getAllCars() {
//
//        return database.getCarDao().getAllCars();
//    }

    public void getAllCars(final DataBaseCarsResultCallBack callBack) {
        new GetAllCars(callBack).execute(database);
    }

    private static class GetAllCars extends AsyncTask<Object, Void, List<Car>> {
        private final DataBaseCarsResultCallBack callBack;

        public GetAllCars(DataBaseCarsResultCallBack callBack) {
            this.callBack = callBack;
        }

        @Override
        protected List<Car> doInBackground(Object... params) {
            ParkbanDatabase database = (ParkbanDatabase) params[0];
            List<Car> cars;
            try {
                return database.getCarDao().getAllCars();

            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Car> cars) {
            super.onPostExecute(cars);
            callBack.onSuccess(cars);
            if (cars == null)
                callBack.onFailed();
        }
    }

    public void getAllCarPlates(int carId, final DataBaseCarPlatesResultCallBack callBack) {
        new GetAllCarPlates(callBack).execute(database, carId);
    }

    private static class GetAllCarPlates extends AsyncTask<Object, Void, List<CarPlate>> {
        private final DataBaseCarPlatesResultCallBack callBack;

        public GetAllCarPlates(DataBaseCarPlatesResultCallBack callBack) {
            this.callBack = callBack;
        }

        @Override
        protected List<CarPlate> doInBackground(Object... params) {
            ParkbanDatabase database = (ParkbanDatabase) params[0];
            int carId = (int) params[1];
            try {
                return database.getCarPlateDao().getAllCarPlates(carId);
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<CarPlate> carPlates) {
            super.onPostExecute(carPlates);
            callBack.onSuccess(carPlates);
            if (carPlates == null)
                callBack.onFailed();
        }
    }

    public LiveData<List<CarPlate>> getCarPlates(int carId) {
        return database.getCarPlateDao().getCarPlates(carId);
    }

    public void getCarByPlateNoAndDate(String plateNo, final DataBaseCarResultCallBack callBack) {
        new GetCarByPlateNoAndDate(callBack).execute(database, plateNo);
    }

    private static class GetCarByPlateNoAndDate extends AsyncTask<Object, Void, Car> {
        private final DataBaseCarResultCallBack callBack;

        public GetCarByPlateNoAndDate(DataBaseCarResultCallBack callBack) {
            this.callBack = callBack;
        }

        @Override
        protected Car doInBackground(Object... params) {
            ParkbanDatabase database = (ParkbanDatabase) params[0];
            String plateNo = (String) params[1];
            Car car = null;

            try {

                car = database.getCarDao().getCarByPlateNoAndDate(plateNo, DateTimeHelper.getCurrentTimeForDB());

            } catch (Exception e) {
                Log.e(TAG, "Error in getAndUpdateCarPlate", e);
            }
            return car;
        }

        @Override
        protected void onPostExecute(Car car) {
            super.onPostExecute(car);
            callBack.onSuccess(car);
            callBack.onFailed();
        }
    }

    public void checkIsExitOfCar(long carId, final DataBaseBooleanCallBack callBack) {
        new CheckIsExitOfCar(callBack).execute(database, carId);
    }

    private static class CheckIsExitOfCar extends AsyncTask<Object, Void, Boolean> {
        private final DataBaseBooleanCallBack callBack;

        public CheckIsExitOfCar(DataBaseBooleanCallBack callBack) {
            this.callBack = callBack;
        }

        @Override
        protected Boolean doInBackground(Object... params) {
            ParkbanDatabase database = (ParkbanDatabase) params[0];
            long carId = (long) params[1];

            boolean result;
            try {
                result = database.getCarPlateDao().checkIsExitOfCar(carId);
            } catch (Exception e) {
                Log.e(TAG, "Error in checkIsExitOfCar", e);
                return false;
            }

            return result;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            callBack.onSuccess(aBoolean);
            callBack.onFailed();

        }
    }

    public void getCarByPlaceId(long parkingSpaceId, final DataBaseBooleanCallBack callBack) {
        new GetCarByPlaceId(callBack).execute(database, parkingSpaceId);
    }

    private static class GetCarByPlaceId extends AsyncTask<Object, Void, Boolean> {
        private final DataBaseBooleanCallBack callBack;

        public GetCarByPlaceId(DataBaseBooleanCallBack callBack) {
            this.callBack = callBack;
        }

        @Override
        protected Boolean doInBackground(Object... params) {
            ParkbanDatabase database = (ParkbanDatabase) params[0];
            long parkingSpaceId = (long) params[1];

            boolean result;
            try {
                result = database.getCarDao().getCarByPlaceId(parkingSpaceId);
            } catch (Exception e) {
                Log.e(TAG, "Error in getCarByPlaceId", e);
                return false;
            }

            return result;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            callBack.onSuccess(aBoolean);
            callBack.onFailed();
        }
    }

    public void getAndUpdateCarPlate(long carPlateId, boolean status, final DataBaseCarPlateUpdateCallBack callBack) {
        new GetAndUpdateCarPlateAsyncTask(callBack)
                .execute(database, carPlateId, status);
    }

    private static class GetAndUpdateCarPlateAsyncTask extends AsyncTask<Object, Void, Boolean> {
        private final DataBaseCarPlateUpdateCallBack callBack;

        public GetAndUpdateCarPlateAsyncTask(DataBaseCarPlateUpdateCallBack callBack) {
            this.callBack = callBack;
        }

        @Override
        protected Boolean doInBackground(Object... params) {
            ParkbanDatabase database = (ParkbanDatabase) params[0];
            long id = (long) params[1];
            boolean status = (boolean) params[2];

            try {
                CarPlate carPlate = database.getCarPlateDao().getCarPlate(id);

                if (status)
                    carPlate.setStatus(SendStatus.SENT.ordinal());
                else
                    carPlate.setStatus(SendStatus.FAILED.ordinal());

                database.getCarPlateDao().updateCarPlate(carPlate);

                return true;

            } catch (Exception e) {
                Log.e(TAG, "Error in getAndUpdateCarPlate", e);
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (result)
                callBack.onSuccess();
            else
                callBack.onFailed();
        }
    }

    public void getLastAndUpdateCarPlate(int carId, final DataBaseCarPlateUpdateCallBack callBack) {
        new GetAndUpdateLastCarPlateAsyncTask(callBack).execute(database, carId);
    }

    private static class GetAndUpdateLastCarPlateAsyncTask extends AsyncTask<Object, Void, Boolean> {

        private final DataBaseCarPlateUpdateCallBack callBack;

        public GetAndUpdateLastCarPlateAsyncTask(DataBaseCarPlateUpdateCallBack callBack) {
            this.callBack = callBack;
        }

        @Override
        protected Boolean doInBackground(Object... params) {
            ParkbanDatabase database = (ParkbanDatabase) params[0];
            int carId = (int) params[1];

            try {

                CarPlate carPlate = database.getCarPlateDao().getLastCarPlates(carId);
                carPlate.setExitBySystem(true);
                database.getCarPlateDao().updateCarPlate(carPlate);
                return true;

            } catch (Exception e) {
                Log.e(TAG, "Error in updateCar", e);
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (result)
                callBack.onSuccess();
            else
                callBack.onFailed();
        }
    }

    public void updateCar(Car car, final DataBaseCarPlateUpdateCallBack callBack) {
        new updateCarAsyncTask(callBack).execute(database, car);
    }

    private static class updateCarAsyncTask extends AsyncTask<Object, Void, Boolean> {

        private final DataBaseCarPlateUpdateCallBack callBack;

        private updateCarAsyncTask(DataBaseCarPlateUpdateCallBack callBack) {
            this.callBack = callBack;
        }

        @Override
        protected Boolean doInBackground(Object... params) {
            ParkbanDatabase database = (ParkbanDatabase) params[0];
            Car car = (Car) params[1];

            try {
                database.getCarDao().updateCar(car);

                return true;
            } catch (Exception e) {
                Log.e(TAG, "Error in updateCar", e);
                return false;
            }

        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (result)
                callBack.onSuccess();
            else
                callBack.onFailed();
        }
    }

    public void saveParkSpace(ParkingSpace parkingSpace, final DataBaseResultCallBack callBack) {
        new SaveParkSpaceAsyncTask(callBack)
                .execute(database, parkingSpace);
    }

    private static class SaveParkSpaceAsyncTask extends AsyncTask<Object, Void, Long> {

        private final DataBaseResultCallBack callBack;

        public SaveParkSpaceAsyncTask(DataBaseResultCallBack callBack) {
            this.callBack = callBack;
        }

        @Override
        protected Long doInBackground(Object... params) {
            ParkbanDatabase database = (ParkbanDatabase) params[0];
            ParkingSpace parkSpace = (ParkingSpace) params[1];

            try {
                return database.getParkingSpaceDao().saveParkingSpace(parkSpace);

            } catch (Exception e) {
                Log.e(TAG, "Error in saveParkSpace", e);
                return 0L;
            }
        }

        @Override
        protected void onPostExecute(Long result) {
            super.onPostExecute(result);
            if (result > 0) {
                callBack.onSuccess(result);
            } else {
                callBack.onFailed();
            }
        }
    }

    public void getParkSpaceFull(Long diff, final DataBaseParkSpaceCallBack callBack) {
        new getParkSpaceFullAsyncTask(callBack)
                .execute(database, diff);
    }

    private static class getParkSpaceFullAsyncTask extends AsyncTask<Object, Void, List<Long>> {

        private final DataBaseParkSpaceCallBack callBack;

        public getParkSpaceFullAsyncTask(DataBaseParkSpaceCallBack callBack) {
            this.callBack = callBack;
        }

        @Override
        protected List<Long> doInBackground(Object... params) {
            ParkbanDatabase database = (ParkbanDatabase) params[0];
            Long diff = (Long) params[1];

            try {
                return database.getCarDao().getParkSpaceFull(diff);

            } catch (Exception e) {
                Log.e(TAG, "Error in saveParkSpace", e);
                return Collections.singletonList(0L);
            }
        }

        @Override
        protected void onPostExecute(List<Long> result) {
            super.onPostExecute(result);

            callBack.onSuccess(result);

            callBack.onFailed();

        }
    }

    public void updateCarPlateRecordStatus(long id, boolean status, DataBaseCarPlateUpdateCallBack callBack) {
        new UpdateCarPlateRecordStatusAsyncTask(callBack)
                .execute(database, id, status);
    }

    private static class UpdateCarPlateRecordStatusAsyncTask extends AsyncTask<Object, Integer, Boolean> {
        private final DataBaseCarPlateUpdateCallBack callBack;

        public UpdateCarPlateRecordStatusAsyncTask(DataBaseCarPlateUpdateCallBack callBack) {
            this.callBack = callBack;
        }

        @Override
        protected Boolean doInBackground(Object... params) {
            ParkbanDatabase database = (ParkbanDatabase) params[0];
            long id = (long) params[1];
            boolean status = (boolean) params[2];

            try {
                database.getCarPlateDao().updateCarPlateStatus(id,
                        status ? SendStatus.SENT.ordinal() : SendStatus.FAILED.ordinal());

                return true;

            } catch (Exception e) {
                Log.e(TAG, "Error in getAndUpdateCarPlate", e);
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (result)
                callBack.onSuccess();
            else
                callBack.onFailed();
        }
    }

    public void getCarplateForDel(long date, final DataBaseCarPlatesResultCallBack callBack) {
        new GetCarplateForDel(callBack).execute(database, date);
    }

    private static class GetCarplateForDel extends AsyncTask<Object, Void, List<CarPlate>> {
        private final DataBaseCarPlatesResultCallBack callBack;

        public GetCarplateForDel(DataBaseCarPlatesResultCallBack callBack) {
            this.callBack = callBack;
        }

        @Override
        protected List<CarPlate> doInBackground(Object... params) {
            ParkbanDatabase database = (ParkbanDatabase) params[0];
            long date = (long) params[1];

            try {
                return database.getCarPlateDao().getCarPlateSent(date);
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<CarPlate> carPlates) {
            super.onPostExecute(carPlates);
            callBack.onSuccess(carPlates);
            callBack.onFailed();
        }
    }

    public void deleteCarPlatesSent(CarPlate plate, final DataBaseCarPlateUpdateCallBack callBack) {
        new DeleteCarPlates(callBack).execute(database, plate);
    }

    private static class DeleteCarPlates extends AsyncTask<Object, Void, Boolean> {
        private final DataBaseCarPlateUpdateCallBack callBack;

        public DeleteCarPlates(DataBaseCarPlateUpdateCallBack callBack) {
            this.callBack = callBack;
        }

        @Override
        protected Boolean doInBackground(Object... params) {
            ParkbanDatabase database = (ParkbanDatabase) params[0];
            CarPlate plate = (CarPlate) params[1];

            try {
                database.getCarPlateDao().deleteCarPlate(plate);
                return true;

            } catch (Exception e) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (result)
                callBack.onSuccess();
            else
                callBack.onFailed();
        }
    }

    ///
    public void getCarForDel(final DataBaseCarsResultCallBack callBack) {
        new GetCarForDel(callBack).execute(database);
    }

    private static class GetCarForDel extends AsyncTask<Object, Void, List<Car>> {
        private final DataBaseCarsResultCallBack callBack;

        public GetCarForDel(DataBaseCarsResultCallBack callBack) {
            this.callBack = callBack;
        }

        @Override
        protected List<Car> doInBackground(Object... params) {
            ParkbanDatabase database = (ParkbanDatabase) params[0];

            try {
                return database.getCarDao().getCarsNotHaveCarPlate();
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Car> cars) {
            super.onPostExecute(cars);
            callBack.onSuccess(cars);
            callBack.onFailed();
        }
    }

    public void deleteCars(final DataBaseCarPlateUpdateCallBack callBack) {
        new DeleteCars(callBack).execute(database);
    }

    private static class DeleteCars extends AsyncTask<Object, Void, Boolean> {
        private final DataBaseCarPlateUpdateCallBack callBack;

        public DeleteCars(DataBaseCarPlateUpdateCallBack callBack) {
            this.callBack = callBack;
        }

        @Override
        protected Boolean doInBackground(Object... params) {
            ParkbanDatabase database = (ParkbanDatabase) params[0];

            try {
                database.getCarDao().deleteCarsNotHaveCarPlate();
                return true;

            } catch (Exception e) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (result)
                callBack.onSuccess();
            else
                callBack.onFailed();
        }
    }

    public void getFirstRecordDate(long carId, final DataBaseRecordDateCallBack callBack) {
        new GetFirstRecordDate(callBack).execute(database, carId);
    }

    private static class GetFirstRecordDate extends AsyncTask<Object, Void, Date> {
        private final DataBaseRecordDateCallBack callBack;

        public GetFirstRecordDate(DataBaseRecordDateCallBack callBack) {
            this.callBack = callBack;
        }

        @Override
        protected Date doInBackground(Object... params) {
            ParkbanDatabase database = (ParkbanDatabase) params[0];
            long carId = (long) params[1];

            try {
                return database.getCarPlateDao().getFirstCarPlateTime(carId);

            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(Date result) {
            super.onPostExecute(result);
            if (result != null)
                callBack.onSuccess(result);
            else
                callBack.onFailed();
        }
    }

    public void updateAllCarPlateStatus(DataBaseCarPlateUpdateCallBack callBack) {
        new UpdateAllCarPlateStatusAsyncTask(callBack)
                .execute(database);
    }

    private static class UpdateAllCarPlateStatusAsyncTask extends AsyncTask<Object, Integer, Boolean> {
        private final DataBaseCarPlateUpdateCallBack callBack;

        public UpdateAllCarPlateStatusAsyncTask(DataBaseCarPlateUpdateCallBack callBack) {
            this.callBack = callBack;
        }

        @Override
        protected Boolean doInBackground(Object... params) {
            ParkbanDatabase database = (ParkbanDatabase) params[0];

            try {
                database.getCarPlateDao().updateAllCarPlateStatus();

                return true;

            } catch (Exception e) {
                Log.e(TAG, "Error in getAndUpdateCarPlate", e);
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (result)
                callBack.onSuccess();
            else
                callBack.onFailed();
        }
    }

    public void getCurrentCars(final DataBaseCarsResultCallBack callBack) {
        new GetCurrentCars(callBack).execute(database);
    }

    private static class GetCurrentCars extends AsyncTask<Object, Void, List<Car>> {
        private final DataBaseCarsResultCallBack callBack;

        public GetCurrentCars(DataBaseCarsResultCallBack callBack) {
            this.callBack = callBack;
        }

        @Override
        protected List<Car> doInBackground(Object... params) {
            ParkbanDatabase database = (ParkbanDatabase) params[0];
            try {
                return database.getCarDao().getCurrentCars(DateTimeHelper.getCurrentTimeForDB());

            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Car> cars) {
            super.onPostExecute(cars);
            callBack.onSuccess(cars);
            if (cars == null)
                callBack.onFailed();
        }
    }

    public void getPreviousCars(final DataBaseCarsResultCallBack callBack) {
        new GetPreviousCars(callBack).execute(database);
    }

    private static class GetPreviousCars extends AsyncTask<Object, Void, List<Car>> {
        private final DataBaseCarsResultCallBack callBack;

        public GetPreviousCars(DataBaseCarsResultCallBack callBack) {
            this.callBack = callBack;
        }

        @Override
        protected List<Car> doInBackground(Object... params) {
            ParkbanDatabase database = (ParkbanDatabase) params[0];
            try {
                return database.getCarDao().getPreviousCars(DateTimeHelper.getCurrentTimeForDB());

            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Car> cars) {
            super.onPostExecute(cars);
            callBack.onSuccess(cars);
            if (cars == null)
                callBack.onFailed();
        }
    }
}
