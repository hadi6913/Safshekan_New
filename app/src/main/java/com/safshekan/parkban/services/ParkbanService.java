package com.safshekan.parkban.services;

import com.safshekan.parkban.services.dto.BooleanResultDto;
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
import com.safshekan.parkban.services.dto.LongResultDto;
import com.safshekan.parkban.services.dto.ParkAmountResultDto;
import com.safshekan.parkban.services.dto.ParkbanShiftResultDto;
import com.safshekan.parkban.services.dto.ReportResultDto;
import com.safshekan.parkban.services.dto.SendRecordAndPayResultDto;
import com.safshekan.parkban.services.dto.SendRecordResultDto;
import com.safshekan.parkban.services.dto.CarRecordsDto;
import com.safshekan.parkban.services.dto.LoginResultDto;
import com.safshekan.parkban.services.dto.StringResultDto;
import com.safshekan.parkban.services.dto.ThirdElectronicPaymentRequestDto;
import com.safshekan.parkban.services.dto.ThirdElectronicPaymentResponseDto;
import com.safshekan.parkban.services.dto.ThirdPartDto;
import com.safshekan.parkban.services.dto.ThirdPartResponseDto;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ParkbanService {




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
