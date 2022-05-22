package com.safshekan.parkban.repositories;

import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;
import android.util.Log;

import com.safshekan.parkban.helper.DateTimeHelper;
import com.safshekan.parkban.persistence.ParkbanDatabase;
import com.safshekan.parkban.persistence.models.Car;
import com.safshekan.parkban.persistence.models.CarPlate;
import com.safshekan.parkban.persistence.models.ParkingSpace;
import com.safshekan.parkban.persistence.models.ResponseResultType;
import com.safshekan.parkban.persistence.models.SendStatus;
import com.safshekan.parkban.persistence.models.model;
import com.safshekan.parkban.services.dto.BooleanResultDto;
import com.safshekan.parkban.services.dto.CarRecordsDto;
import com.safshekan.parkban.services.dto.CashBillDto;
import com.safshekan.parkban.services.dto.CashDetailsResultDto;
import com.safshekan.parkban.services.dto.CurrentShiftDto;
import com.safshekan.parkban.services.dto.DriverDebtResultDto;
import com.safshekan.parkban.services.dto.ExitBillDto;
import com.safshekan.parkban.services.dto.FailedFactorsRequestDto;
import com.safshekan.parkban.services.dto.FailedFactorsResultDto;
import com.safshekan.parkban.services.dto.FirstElectronicPaymentDto;
import com.safshekan.parkban.services.dto.ForthElectronicPaymentDto;
import com.safshekan.parkban.services.dto.FunctionalityResultDto;
import com.safshekan.parkban.services.dto.IncreaseDriverWalletCardResultDto;
import com.safshekan.parkban.services.dto.IncreaseDriverWalletResultDto;
import com.safshekan.parkban.services.dto.IntResultDto;
import com.safshekan.parkban.services.dto.LoginResultDto;
import com.safshekan.parkban.services.dto.LongResultDto;
import com.safshekan.parkban.services.dto.ParkAmountResultDto;
import com.safshekan.parkban.services.dto.ParkbanShiftResultDto;
import com.safshekan.parkban.services.dto.ReportDetailDto;
import com.safshekan.parkban.services.dto.ReportResultDto;
import com.safshekan.parkban.services.dto.SendRecordAndPayResultDto;
import com.safshekan.parkban.services.dto.SendRecordResultDto;
import com.safshekan.parkban.services.ParkbanServiceProvider;
import com.safshekan.parkban.services.dto.StringResultDto;
import com.safshekan.parkban.services.dto.ThirdElectronicPaymentRequestDto;
import com.safshekan.parkban.services.dto.ThirdElectronicPaymentResponseDto;
import com.safshekan.parkban.services.dto.ThirdPartDto;
import com.safshekan.parkban.services.dto.ThirdPartResponseDto;

import java.util.Collections;
import java.util.Date;
import java.util.List;

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
