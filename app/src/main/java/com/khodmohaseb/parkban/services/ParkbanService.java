package com.khodmohaseb.parkban.services;

import com.khodmohaseb.parkban.services.dto.BooleanResultDto;
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
import com.khodmohaseb.parkban.services.dto.LongResultDto;
import com.khodmohaseb.parkban.services.dto.ParkAmountResultDto;
import com.khodmohaseb.parkban.services.dto.ParkbanShiftResultDto;
import com.khodmohaseb.parkban.services.dto.ReportResultDto;
import com.khodmohaseb.parkban.services.dto.SendRecordAndPayResultDto;
import com.khodmohaseb.parkban.services.dto.SendRecordResultDto;
import com.khodmohaseb.parkban.services.dto.CarRecordsDto;
import com.khodmohaseb.parkban.services.dto.LoginResultDto;
import com.khodmohaseb.parkban.services.dto.StringResultDto;
import com.khodmohaseb.parkban.services.dto.ThirdElectronicPaymentRequestDto;
import com.khodmohaseb.parkban.services.dto.ThirdElectronicPaymentResponseDto;
import com.khodmohaseb.parkban.services.dto.ThirdPartDto;
import com.khodmohaseb.parkban.services.dto.ThirdPartResponseDto;
import com.khodmohaseb.parkban.services.dto.khodmohaseb.parkinginfo.GetParkingInfoResponse;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ParkbanService {



    //**********************************************************************************************
    //**********************************************************************************************
    //**********************************************************************************************
    //khod-mohaseb

    @POST("DeviceLogin")
    Call<String> getDeviceToken(@Body RequestBody body);

    @POST("GetParkingInfo")
    Call<GetParkingInfoResponse> getParkingInfoFromServer(@Body RequestBody body);






    //**********************************************************************************************
    //**********************************************************************************************
    //**********************************************************************************************

















    @POST("User/ParkbanLogin")
    @FormUrlEncoded
    Call<LoginResultDto> login(@Field("UserName") String UserName
            , @Field("UserPass") String Password, @Field("UserType") int userType);


    @GET("Traffic/GetExitCarInfoByParkban")
    Call<ExitBillDto> sendBaseInformation(@Query("plate") String plate, @Query("memberCode") String memberCode, @Query("cardNumber") String cardNumber);


    @GET("Traffic/PayDumpCashByParkban")
    Call<CashBillDto> CashPayment(@Query("dumpId") long dumpId, @Query("amount") long amount);

    @GET("Traffic/CreateFactorByParkban")
    Call<FirstElectronicPaymentDto> firstElectronicPayment(@Query("dumpId") long dumpId, @Query("amount") long amount);

    @POST("Traffic/CompleteFactor")
    Call<ThirdElectronicPaymentResponseDto> thirdElectronicPayment(@Body ThirdElectronicPaymentRequestDto.ThirdPart requestObj);

    @GET("Traffic/ApprovedFactor")
    Call<ForthElectronicPaymentDto> forthElectronicPayment(@Query("factorId") long factorId);

    @GET("report/GetUserActivityWithTotal")
    Call<ReportResultDto> getParkbanReportList(@Query("startDateTime")String startDateTime,@Query("endDateTime")String endDateTime);






    //**************************************************************************************************************
    //**************************************************************************************************************
    //**************************************************************************************************************
    //**************************************************************************************************************


    @POST("CarPark/SaveCarPark")
    Call<SendRecordResultDto> sendRecord(@Body CarRecordsDto records);

    @GET("Parkban/GetParkMarginLimit")
    Call<IntResultDto> getParkMarginLimit();

    @GET("Parkban/GetAppLatestVersion")
    Call<StringResultDto> getLastVersion();

    @GET("Parkban/GetCurrentParkbanShift")
    Call<CurrentShiftDto> getCurrentShift(@Query("ParkbanUserId") long parkbanUserId);

    @GET("Parkban/GetParkbanShift")
    Call<ParkbanShiftResultDto> getParkbanShift(@Query("ParkbanUserId") long parkbanUserId ,@Query("startDate") String startDate
    ,@Query("endDate") String endDate);

    @GET("Parkban/ParkbanFunctionalityOnShift")
    Call<FunctionalityResultDto> getParkbanFunctionality(@Query("ParkbanUserId") long parkbanUserId , @Query("beginDateTime") String startDate
            , @Query("endDateTime") String endDate,@Query("workShiftType") int workShiftType);

    @GET("CarPark/GetParkAmount")
    Call<ParkAmountResultDto> getParkAmount(@Query("parkSpaceId") long parkSpaceId , @Query("parkDateTime") String parkDateTime
            , @Query("startTime") String startTime, @Query("endTime") String endTime);

    @POST("CarPark/SaveCarParkAndPayByParkban")
    Call<SendRecordAndPayResultDto> sendRecordAndPay( @Body CarRecordsDto records);

    @POST("Parkban/IncreaseDriverWallet")
    Call<IncreaseDriverWalletResultDto> increaseDriverWallet(@Body IncreaseDriverWalletResultDto.IncreaseDriverWalletDto wallet);

    //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    @POST("Parkban/CreateFactor")
    Call<IncreaseDriverWalletCardResultDto> increaseDriverWalletCard(@Body IncreaseDriverWalletCardResultDto.IncreaseDriverWalletDto wallet);


    @POST("Parkban/CompletFactor")
    Call<ThirdPartResponseDto> thirdPartAction(@Body ThirdPartDto.ThirdPart requestObj);


    @GET("Parkban/ApprovalPosFactor")
    Call<IncreaseDriverWalletCardResultDto> forthPartAction(@Query("factorId") long factorId);

    @POST("Parkban/ApprovalPosListFactor")
    Call<FailedFactorsResultDto> solveFailedFactors(@Body FailedFactorsRequestDto.RequestFailedFactor requestObj);



    //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

    @GET("parkban/HasParkbanCredit")
    Call<BooleanResultDto> hasParkbanCredit(@Query("amount") long amount);

    @GET("Parkban/GetParkbanCashDetailsOnDays")
    Call<CashDetailsResultDto> getChargeReport(@Query("parkbanId") long parkbanUserId ,@Query("startDate") String startDate
            ,@Query("endDate") String endDate);

    @GET("carpark/GetUnregisteredCarDebt")
    Call<LongResultDto> getUnregisteredCarDebt(@Query("plate") String plate);

    @GET("carpark/GetDriverFullDebtAndWallet")
    Call<DriverDebtResultDto> getDriverFullDebtAndWallet(@Query("driverPhone") long phoneNumber);


}
