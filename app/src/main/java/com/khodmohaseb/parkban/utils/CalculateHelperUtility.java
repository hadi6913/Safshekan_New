package com.khodmohaseb.parkban.utils;

import com.khodmohaseb.parkban.services.dto.khodmohaseb.parkinginfo.Tariff;

public class CalculateHelperUtility {

    public static long roundStayLengthTime(long rawTime, Tariff tariff){

        if(tariff.getRoundingAmountTime() == 0){


            return rawTime;

        }else{
            long n = (rawTime/tariff.getRoundingAmountTime());
            long remaining = (rawTime%tariff.getRoundingAmountTime());

            if(remaining>=tariff.getRoundingBoundaryTime()){
                n = n+1;
            }
            return (n*tariff.getRoundingAmountTime());
        }



    }



    public static long roundFarePrice(long rawPrice, Tariff tariff){


        if(tariff.getRoundingAmountCost() == 0){
            return rawPrice;

        }else{
            long n = (rawPrice/tariff.getRoundingAmountCost());
            long remaining = (rawPrice%tariff.getRoundingAmountCost());

            if(remaining>=tariff.getRoundingBoundaryCost()){
                n = n+1;
            }
            return (n*tariff.getRoundingAmountCost());
        }



    }












}
