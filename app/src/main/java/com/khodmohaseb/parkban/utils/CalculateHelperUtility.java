package com.khodmohaseb.parkban.utils;

import com.khodmohaseb.parkban.services.dto.khodmohaseb.parkinginfo.Tariff;

public class CalculateHelperUtility {

    public static long roundStayLengthTime(long rawTime, Tariff tariff){

        long n = (rawTime/tariff.getRoundingAmountTime());
        long remaining = (rawTime%tariff.getRoundingAmountTime());

        if(remaining>=tariff.getRoundingBoundaryTime()){
            n = n+1;
        }
        return (n*tariff.getRoundingAmountTime());
    }



    public static long roundFarePrice(long rawPrice, Tariff tariff){


        long n = (rawPrice/tariff.getRoundingAmountCost());
        long remaining = (rawPrice%tariff.getRoundingAmountCost());

        if(remaining>=tariff.getRoundingBoundaryCost()){
            n = n+1;
        }
        return (n*tariff.getRoundingAmountCost());
    }












}
