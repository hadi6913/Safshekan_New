package com.khodmohaseb.parkban.utils;

import android.util.Log;

import com.khodmohaseb.parkban.services.dto.khodmohaseb.parkinginfo.Tariff;

public class HourlyFareCalculator {
    private final  static String TAG = "xeagle6913";

    public static long calculateStepFareVehicle1(long roundedTotalStayLength, Tariff tariff) {
        Log.d(TAG, "calculateStepFareVehicle1: > roundedTime:"+roundedTotalStayLength);
        long price = 0;


        if (tariff.getIsNeedCalculateOnlyLastStep().booleanValue()) {

            if ((tariff.getVehicleTariff1().getDurationStep5() != null) && (roundedTotalStayLength >= tariff.getVehicleTariff1().getDurationStep5())) {
                price = price + ((roundedTotalStayLength * tariff.getVehicleTariff1().getCostStep5()) / tariff.getVehicleTariff1().getDurationStep5());
                return price;
            } else if ((tariff.getVehicleTariff1().getDurationStep4() != null) && (roundedTotalStayLength >= tariff.getVehicleTariff1().getDurationStep4())) {
                price = price + ((roundedTotalStayLength * tariff.getVehicleTariff1().getCostStep4()) / tariff.getVehicleTariff1().getDurationStep4());
                return price;
            } else if ((tariff.getVehicleTariff1().getDurationStep3() != null) && (roundedTotalStayLength >= tariff.getVehicleTariff1().getDurationStep3())) {
                price = price + ((roundedTotalStayLength * tariff.getVehicleTariff1().getCostStep3()) / tariff.getVehicleTariff1().getDurationStep3());
                return price;
            } else if ((tariff.getVehicleTariff1().getDurationStep2() != null) && (roundedTotalStayLength >= tariff.getVehicleTariff1().getDurationStep2())) {
                price = price + ((roundedTotalStayLength * tariff.getVehicleTariff1().getCostStep2()) / tariff.getVehicleTariff1().getDurationStep2());
                return price;
            } else {
                price = price + ((roundedTotalStayLength * tariff.getVehicleTariff1().getCostStep1()) / tariff.getVehicleTariff1().getDurationStep1());
                return price;
            }


        } else {
            Log.d(TAG, "calculateStepFareVehicle1: ");
            long remainingStayLength = roundedTotalStayLength - tariff.getVehicleTariff1().getDurationStep1();
            if (remainingStayLength >= 0) {
                price = price + tariff.getVehicleTariff1().getCostStep1();
                if (tariff.getVehicleTariff1().getDurationStep2() != null) {
                    remainingStayLength = remainingStayLength - tariff.getVehicleTariff1().getDurationStep2();
                    if (remainingStayLength >= 0) {
                        price = price + tariff.getVehicleTariff1().getCostStep2();
                        if (tariff.getVehicleTariff1().getDurationStep3() != null) {
                            remainingStayLength = remainingStayLength - tariff.getVehicleTariff1().getDurationStep3();
                            if (remainingStayLength >= 0) {
                                price = price + tariff.getVehicleTariff1().getCostStep3();
                                if (tariff.getVehicleTariff1().getDurationStep4() != null) {
                                    remainingStayLength = remainingStayLength - tariff.getVehicleTariff1().getDurationStep4();
                                    if (remainingStayLength >= 0) {
                                        price = price + tariff.getVehicleTariff1().getCostStep4();
                                        if (tariff.getVehicleTariff1().getDurationStep5() != null) {
                                            price = price +
                                                    ((remainingStayLength / tariff.getVehicleTariff1().getDurationStep5()) * tariff.getVehicleTariff1().getCostStep5()) +
                                                    (((remainingStayLength % tariff.getVehicleTariff1().getDurationStep5()) * tariff.getVehicleTariff1().getCostStep5()) / tariff.getVehicleTariff1().getCostStep5());
                                            return price;
                                        } else {
                                            price = price +
                                                    ((remainingStayLength / tariff.getVehicleTariff1().getDurationStep4()) * tariff.getVehicleTariff1().getCostStep4()) +
                                                    (((remainingStayLength % tariff.getVehicleTariff1().getDurationStep4()) * tariff.getVehicleTariff1().getCostStep4()) / tariff.getVehicleTariff1().getDurationStep4());
                                            return price;
                                        }
                                    } else {
                                        price = price + (((remainingStayLength + tariff.getVehicleTariff1().getDurationStep4()) * tariff.getVehicleTariff1().getCostStep4()) / tariff.getVehicleTariff1().getDurationStep4());
                                        return price;
                                    }
                                } else {
                                    price = price +
                                            ((remainingStayLength / tariff.getVehicleTariff1().getDurationStep3()) * tariff.getVehicleTariff1().getCostStep3()) +
                                            (((remainingStayLength % tariff.getVehicleTariff1().getDurationStep3()) * tariff.getVehicleTariff1().getCostStep3()) / tariff.getVehicleTariff1().getDurationStep3());
                                    return price;
                                }
                            } else {
                                price = price + (((remainingStayLength + tariff.getVehicleTariff1().getDurationStep3()) * tariff.getVehicleTariff1().getCostStep3()) / tariff.getVehicleTariff1().getDurationStep3());
                                return price;
                            }
                        } else {
                            price = price +
                                    ((remainingStayLength / tariff.getVehicleTariff1().getDurationStep2()) * tariff.getVehicleTariff1().getCostStep2()) +
                                    (((remainingStayLength % tariff.getVehicleTariff1().getDurationStep2()) * tariff.getVehicleTariff1().getCostStep2()) / tariff.getVehicleTariff1().getDurationStep2());
                            return price;
                        }
                    } else {
                        price = price + (((remainingStayLength + tariff.getVehicleTariff1().getDurationStep2()) * tariff.getVehicleTariff1().getCostStep2()) / tariff.getVehicleTariff1().getDurationStep2());
                        return price;
                    }
                } else {
                    price = price +
                            ((remainingStayLength / tariff.getVehicleTariff1().getDurationStep1()) * tariff.getVehicleTariff1().getCostStep1()) +
                            (((remainingStayLength % tariff.getVehicleTariff1().getDurationStep1()) * tariff.getVehicleTariff1().getCostStep1()) / tariff.getVehicleTariff1().getDurationStep1());
                    return price;
                }
            } else {
                price = price + ((roundedTotalStayLength * tariff.getVehicleTariff1().getCostStep1()) / tariff.getVehicleTariff1().getDurationStep1());
                return price;
            }

        }


    }


    public static long calculateStepFareVehicle2(long roundedTotalStayLength, Tariff tariff) {
        Log.d(TAG, "calculateStepFareVehicle2: > roundedTime:"+roundedTotalStayLength);
        long price = 0;


        if (tariff.getIsNeedCalculateOnlyLastStep().booleanValue()) {

            if ((tariff.getVehicleTariff2().getDurationStep5() != null) && (roundedTotalStayLength >= tariff.getVehicleTariff2().getDurationStep5())) {
                price = price + ((roundedTotalStayLength * tariff.getVehicleTariff2().getCostStep5()) / tariff.getVehicleTariff2().getDurationStep5());
                return price;
            } else if ((tariff.getVehicleTariff2().getDurationStep4() != null) && (roundedTotalStayLength >= tariff.getVehicleTariff2().getDurationStep4())) {
                price = price + ((roundedTotalStayLength * tariff.getVehicleTariff2().getCostStep4()) / tariff.getVehicleTariff2().getDurationStep4());
                return price;
            } else if ((tariff.getVehicleTariff2().getDurationStep3() != null) && (roundedTotalStayLength >= tariff.getVehicleTariff2().getDurationStep3())) {
                price = price + ((roundedTotalStayLength * tariff.getVehicleTariff2().getCostStep3()) / tariff.getVehicleTariff2().getDurationStep3());
                return price;
            } else if ((tariff.getVehicleTariff2().getDurationStep2() != null) && (roundedTotalStayLength >= tariff.getVehicleTariff2().getDurationStep2())) {
                price = price + ((roundedTotalStayLength * tariff.getVehicleTariff2().getCostStep2()) / tariff.getVehicleTariff2().getDurationStep2());
                return price;
            } else {
                price = price + ((roundedTotalStayLength * tariff.getVehicleTariff2().getCostStep1()) / tariff.getVehicleTariff2().getDurationStep1());
                return price;
            }


        } else {
            long remainingStayLength = roundedTotalStayLength - tariff.getVehicleTariff2().getDurationStep1();
            if (remainingStayLength >= 0) {
                price = price + tariff.getVehicleTariff2().getCostStep1();
                if (tariff.getVehicleTariff2().getDurationStep2() != null) {
                    remainingStayLength = remainingStayLength - tariff.getVehicleTariff2().getDurationStep2();
                    if (remainingStayLength >= 0) {
                        price = price + tariff.getVehicleTariff2().getCostStep2();
                        if (tariff.getVehicleTariff2().getDurationStep3() != null) {
                            remainingStayLength = remainingStayLength - tariff.getVehicleTariff2().getDurationStep3();
                            if (remainingStayLength >= 0) {
                                price = price + tariff.getVehicleTariff2().getCostStep3();
                                if (tariff.getVehicleTariff2().getDurationStep4() != null) {
                                    remainingStayLength = remainingStayLength - tariff.getVehicleTariff2().getDurationStep4();
                                    if (remainingStayLength >= 0) {
                                        price = price + tariff.getVehicleTariff2().getCostStep4();
                                        if (tariff.getVehicleTariff2().getDurationStep5() != null) {
                                            price = price +
                                                    ((remainingStayLength / tariff.getVehicleTariff2().getDurationStep5()) * tariff.getVehicleTariff2().getCostStep5()) +
                                                    (((remainingStayLength % tariff.getVehicleTariff2().getDurationStep5()) * tariff.getVehicleTariff2().getCostStep5()) / tariff.getVehicleTariff2().getCostStep5());
                                            return price;
                                        } else {
                                            price = price +
                                                    ((remainingStayLength / tariff.getVehicleTariff2().getDurationStep4()) * tariff.getVehicleTariff2().getCostStep4()) +
                                                    (((remainingStayLength % tariff.getVehicleTariff2().getDurationStep4()) * tariff.getVehicleTariff2().getCostStep4()) / tariff.getVehicleTariff2().getDurationStep4());
                                            return price;
                                        }
                                    } else {
                                        price = price + (((remainingStayLength + tariff.getVehicleTariff2().getDurationStep4()) * tariff.getVehicleTariff2().getCostStep4()) / tariff.getVehicleTariff2().getDurationStep4());
                                        return price;
                                    }
                                } else {
                                    price = price +
                                            ((remainingStayLength / tariff.getVehicleTariff2().getDurationStep3()) * tariff.getVehicleTariff2().getCostStep3()) +
                                            (((remainingStayLength % tariff.getVehicleTariff2().getDurationStep3()) * tariff.getVehicleTariff2().getCostStep3()) / tariff.getVehicleTariff2().getDurationStep3());
                                    return price;
                                }
                            } else {
                                price = price + (((remainingStayLength + tariff.getVehicleTariff2().getDurationStep3()) * tariff.getVehicleTariff2().getCostStep3()) / tariff.getVehicleTariff2().getDurationStep3());
                                return price;
                            }
                        } else {
                            price = price +
                                    ((remainingStayLength / tariff.getVehicleTariff2().getDurationStep2()) * tariff.getVehicleTariff2().getCostStep2()) +
                                    (((remainingStayLength % tariff.getVehicleTariff2().getDurationStep2()) * tariff.getVehicleTariff2().getCostStep2()) / tariff.getVehicleTariff2().getDurationStep2());
                            return price;
                        }
                    } else {
                        price = price + (((remainingStayLength + tariff.getVehicleTariff2().getDurationStep2()) * tariff.getVehicleTariff2().getCostStep2()) / tariff.getVehicleTariff2().getDurationStep2());
                        return price;
                    }
                } else {
                    price = price +
                            ((remainingStayLength / tariff.getVehicleTariff2().getDurationStep1()) * tariff.getVehicleTariff2().getCostStep1()) +
                            (((remainingStayLength % tariff.getVehicleTariff2().getDurationStep1()) * tariff.getVehicleTariff2().getCostStep1()) / tariff.getVehicleTariff2().getDurationStep1());
                    return price;
                }
            } else {
                price = price + ((roundedTotalStayLength * tariff.getVehicleTariff2().getCostStep1()) / tariff.getVehicleTariff2().getDurationStep1());
                return price;
            }
        }


    }


    public static long calculateStepFareVehicle3(long roundedTotalStayLength, Tariff tariff) {
        Log.d(TAG, "calculateStepFareVehicle3: > roundedTime:"+roundedTotalStayLength);
        long price = 0;


        if (tariff.getIsNeedCalculateOnlyLastStep().booleanValue()) {

            if ((tariff.getVehicleTariff3().getDurationStep5() != null) && (roundedTotalStayLength >= tariff.getVehicleTariff3().getDurationStep5())) {
                price = price + ((roundedTotalStayLength * tariff.getVehicleTariff3().getCostStep5()) / tariff.getVehicleTariff3().getDurationStep5());
                return price;
            } else if ((tariff.getVehicleTariff3().getDurationStep4() != null) && (roundedTotalStayLength >= tariff.getVehicleTariff3().getDurationStep4())) {
                price = price + ((roundedTotalStayLength * tariff.getVehicleTariff3().getCostStep4()) / tariff.getVehicleTariff3().getDurationStep4());
                return price;
            } else if ((tariff.getVehicleTariff3().getDurationStep3() != null) && (roundedTotalStayLength >= tariff.getVehicleTariff3().getDurationStep3())) {
                price = price + ((roundedTotalStayLength * tariff.getVehicleTariff3().getCostStep3()) / tariff.getVehicleTariff3().getDurationStep3());
                return price;
            } else if ((tariff.getVehicleTariff3().getDurationStep2() != null) && (roundedTotalStayLength >= tariff.getVehicleTariff3().getDurationStep2())) {
                price = price + ((roundedTotalStayLength * tariff.getVehicleTariff3().getCostStep2()) / tariff.getVehicleTariff3().getDurationStep2());
                return price;
            } else {
                price = price + ((roundedTotalStayLength * tariff.getVehicleTariff3().getCostStep1()) / tariff.getVehicleTariff3().getDurationStep1());
                return price;
            }


        } else {
            long remainingStayLength = roundedTotalStayLength - tariff.getVehicleTariff3().getDurationStep1();
            if (remainingStayLength >= 0) {
                price = price + tariff.getVehicleTariff3().getCostStep1();
                if (tariff.getVehicleTariff3().getDurationStep2() != null) {
                    remainingStayLength = remainingStayLength - tariff.getVehicleTariff3().getDurationStep2();
                    if (remainingStayLength >= 0) {
                        price = price + tariff.getVehicleTariff3().getCostStep2();
                        if (tariff.getVehicleTariff3().getDurationStep3() != null) {
                            remainingStayLength = remainingStayLength - tariff.getVehicleTariff3().getDurationStep3();
                            if (remainingStayLength >= 0) {
                                price = price + tariff.getVehicleTariff3().getCostStep3();
                                if (tariff.getVehicleTariff3().getDurationStep4() != null) {
                                    remainingStayLength = remainingStayLength - tariff.getVehicleTariff3().getDurationStep4();
                                    if (remainingStayLength >= 0) {
                                        price = price + tariff.getVehicleTariff3().getCostStep4();
                                        if (tariff.getVehicleTariff3().getDurationStep5() != null) {
                                            price = price +
                                                    ((remainingStayLength / tariff.getVehicleTariff3().getDurationStep5()) * tariff.getVehicleTariff3().getCostStep5()) +
                                                    (((remainingStayLength % tariff.getVehicleTariff3().getDurationStep5()) * tariff.getVehicleTariff3().getCostStep5()) / tariff.getVehicleTariff3().getCostStep5());
                                            return price;
                                        } else {
                                            price = price +
                                                    ((remainingStayLength / tariff.getVehicleTariff3().getDurationStep4()) * tariff.getVehicleTariff3().getCostStep4()) +
                                                    (((remainingStayLength % tariff.getVehicleTariff3().getDurationStep4()) * tariff.getVehicleTariff3().getCostStep4()) / tariff.getVehicleTariff3().getDurationStep4());
                                            return price;
                                        }
                                    } else {
                                        price = price + (((remainingStayLength + tariff.getVehicleTariff3().getDurationStep4()) * tariff.getVehicleTariff3().getCostStep4()) / tariff.getVehicleTariff3().getDurationStep4());
                                        return price;
                                    }
                                } else {
                                    price = price +
                                            ((remainingStayLength / tariff.getVehicleTariff3().getDurationStep3()) * tariff.getVehicleTariff3().getCostStep3()) +
                                            (((remainingStayLength % tariff.getVehicleTariff3().getDurationStep3()) * tariff.getVehicleTariff3().getCostStep3()) / tariff.getVehicleTariff3().getDurationStep3());
                                    return price;
                                }
                            } else {
                                price = price + (((remainingStayLength + tariff.getVehicleTariff3().getDurationStep3()) * tariff.getVehicleTariff3().getCostStep3()) / tariff.getVehicleTariff3().getDurationStep3());
                                return price;
                            }
                        } else {
                            price = price +
                                    ((remainingStayLength / tariff.getVehicleTariff3().getDurationStep2()) * tariff.getVehicleTariff3().getCostStep2()) +
                                    (((remainingStayLength % tariff.getVehicleTariff3().getDurationStep2()) * tariff.getVehicleTariff3().getCostStep2()) / tariff.getVehicleTariff3().getDurationStep2());
                            return price;
                        }
                    } else {
                        price = price + (((remainingStayLength + tariff.getVehicleTariff3().getDurationStep2()) * tariff.getVehicleTariff3().getCostStep2()) / tariff.getVehicleTariff3().getDurationStep2());
                        return price;
                    }
                } else {
                    price = price +
                            ((remainingStayLength / tariff.getVehicleTariff3().getDurationStep1()) * tariff.getVehicleTariff3().getCostStep1()) +
                            (((remainingStayLength % tariff.getVehicleTariff3().getDurationStep1()) * tariff.getVehicleTariff3().getCostStep1()) / tariff.getVehicleTariff3().getDurationStep1());
                    return price;
                }
            } else {
                price = price + ((roundedTotalStayLength * tariff.getVehicleTariff3().getCostStep1()) / tariff.getVehicleTariff3().getDurationStep1());
                return price;
            }

        }


    }


    public static long calculateStepFareVehicle4(long roundedTotalStayLength, Tariff tariff) {
        Log.d(TAG, "calculateStepFareVehicle4: > roundedTime:"+roundedTotalStayLength);
        long price = 0;


        if (tariff.getIsNeedCalculateOnlyLastStep().booleanValue()) {

            if ((tariff.getVehicleTariff4().getDurationStep5() != null) && (roundedTotalStayLength >= tariff.getVehicleTariff4().getDurationStep5())) {
                price = price + ((roundedTotalStayLength * tariff.getVehicleTariff4().getCostStep5()) / tariff.getVehicleTariff4().getDurationStep5());
                return price;
            } else if ((tariff.getVehicleTariff4().getDurationStep4() != null) && (roundedTotalStayLength >= tariff.getVehicleTariff4().getDurationStep4())) {
                price = price + ((roundedTotalStayLength * tariff.getVehicleTariff4().getCostStep4()) / tariff.getVehicleTariff4().getDurationStep4());
                return price;
            } else if ((tariff.getVehicleTariff4().getDurationStep3() != null) && (roundedTotalStayLength >= tariff.getVehicleTariff4().getDurationStep3())) {
                price = price + ((roundedTotalStayLength * tariff.getVehicleTariff4().getCostStep3()) / tariff.getVehicleTariff4().getDurationStep3());
                return price;
            } else if ((tariff.getVehicleTariff4().getDurationStep2() != null) && (roundedTotalStayLength >= tariff.getVehicleTariff4().getDurationStep2())) {
                price = price + ((roundedTotalStayLength * tariff.getVehicleTariff4().getCostStep2()) / tariff.getVehicleTariff4().getDurationStep2());
                return price;
            } else {
                price = price + ((roundedTotalStayLength * tariff.getVehicleTariff4().getCostStep1()) / tariff.getVehicleTariff4().getDurationStep1());
                return price;
            }


        } else {
            long remainingStayLength = roundedTotalStayLength - tariff.getVehicleTariff4().getDurationStep1();
            if (remainingStayLength >= 0) {
                price = price + tariff.getVehicleTariff4().getCostStep1();
                if (tariff.getVehicleTariff4().getDurationStep2() != null) {
                    remainingStayLength = remainingStayLength - tariff.getVehicleTariff4().getDurationStep2();
                    if (remainingStayLength >= 0) {
                        price = price + tariff.getVehicleTariff4().getCostStep2();
                        if (tariff.getVehicleTariff4().getDurationStep3() != null) {
                            remainingStayLength = remainingStayLength - tariff.getVehicleTariff4().getDurationStep3();
                            if (remainingStayLength >= 0) {
                                price = price + tariff.getVehicleTariff4().getCostStep3();
                                if (tariff.getVehicleTariff4().getDurationStep4() != null) {
                                    remainingStayLength = remainingStayLength - tariff.getVehicleTariff4().getDurationStep4();
                                    if (remainingStayLength >= 0) {
                                        price = price + tariff.getVehicleTariff4().getCostStep4();
                                        if (tariff.getVehicleTariff4().getDurationStep5() != null) {
                                            price = price +
                                                    ((remainingStayLength / tariff.getVehicleTariff4().getDurationStep5()) * tariff.getVehicleTariff4().getCostStep5()) +
                                                    (((remainingStayLength % tariff.getVehicleTariff4().getDurationStep5()) * tariff.getVehicleTariff4().getCostStep5()) / tariff.getVehicleTariff4().getCostStep5());
                                            return price;
                                        } else {
                                            price = price +
                                                    ((remainingStayLength / tariff.getVehicleTariff4().getDurationStep4()) * tariff.getVehicleTariff4().getCostStep4()) +
                                                    (((remainingStayLength % tariff.getVehicleTariff4().getDurationStep4()) * tariff.getVehicleTariff4().getCostStep4()) / tariff.getVehicleTariff4().getDurationStep4());
                                            return price;
                                        }
                                    } else {
                                        price = price + (((remainingStayLength + tariff.getVehicleTariff4().getDurationStep4()) * tariff.getVehicleTariff4().getCostStep4()) / tariff.getVehicleTariff4().getDurationStep4());
                                        return price;
                                    }
                                } else {
                                    price = price +
                                            ((remainingStayLength / tariff.getVehicleTariff4().getDurationStep3()) * tariff.getVehicleTariff4().getCostStep3()) +
                                            (((remainingStayLength % tariff.getVehicleTariff4().getDurationStep3()) * tariff.getVehicleTariff4().getCostStep3()) / tariff.getVehicleTariff4().getDurationStep3());
                                    return price;
                                }
                            } else {
                                price = price + (((remainingStayLength + tariff.getVehicleTariff4().getDurationStep3()) * tariff.getVehicleTariff4().getCostStep3()) / tariff.getVehicleTariff4().getDurationStep3());
                                return price;
                            }
                        } else {
                            price = price +
                                    ((remainingStayLength / tariff.getVehicleTariff4().getDurationStep2()) * tariff.getVehicleTariff4().getCostStep2()) +
                                    (((remainingStayLength % tariff.getVehicleTariff4().getDurationStep2()) * tariff.getVehicleTariff4().getCostStep2()) / tariff.getVehicleTariff4().getDurationStep2());
                            return price;
                        }
                    } else {
                        price = price + (((remainingStayLength + tariff.getVehicleTariff4().getDurationStep2()) * tariff.getVehicleTariff4().getCostStep2()) / tariff.getVehicleTariff4().getDurationStep2());
                        return price;
                    }
                } else {
                    price = price +
                            ((remainingStayLength / tariff.getVehicleTariff4().getDurationStep1()) * tariff.getVehicleTariff4().getCostStep1()) +
                            (((remainingStayLength % tariff.getVehicleTariff4().getDurationStep1()) * tariff.getVehicleTariff4().getCostStep1()) / tariff.getVehicleTariff4().getDurationStep1());
                    return price;
                }
            } else {
                price = price + ((roundedTotalStayLength * tariff.getVehicleTariff4().getCostStep1()) / tariff.getVehicleTariff4().getDurationStep1());
                return price;
            }
        }


    }


    public static long calculateStepFareVehicle5(long roundedTotalStayLength, Tariff tariff) {
        Log.d(TAG, "calculateStepFareVehicle5: > roundedTime:"+roundedTotalStayLength);
        long price = 0;


        if (tariff.getIsNeedCalculateOnlyLastStep().booleanValue()) {

            if ((tariff.getVehicleTariff5().getDurationStep5() != null) && (roundedTotalStayLength >= tariff.getVehicleTariff5().getDurationStep5())) {
                price = price + ((roundedTotalStayLength * tariff.getVehicleTariff5().getCostStep5()) / tariff.getVehicleTariff5().getDurationStep5());
                return price;
            } else if ((tariff.getVehicleTariff5().getDurationStep4() != null) && (roundedTotalStayLength >= tariff.getVehicleTariff5().getDurationStep4())) {
                price = price + ((roundedTotalStayLength * tariff.getVehicleTariff5().getCostStep4()) / tariff.getVehicleTariff5().getDurationStep4());
                return price;
            } else if ((tariff.getVehicleTariff5().getDurationStep3() != null) && (roundedTotalStayLength >= tariff.getVehicleTariff5().getDurationStep3())) {
                price = price + ((roundedTotalStayLength * tariff.getVehicleTariff5().getCostStep3()) / tariff.getVehicleTariff5().getDurationStep3());
                return price;
            } else if ((tariff.getVehicleTariff5().getDurationStep2() != null) && (roundedTotalStayLength >= tariff.getVehicleTariff5().getDurationStep2())) {
                price = price + ((roundedTotalStayLength * tariff.getVehicleTariff5().getCostStep2()) / tariff.getVehicleTariff5().getDurationStep2());
                return price;
            } else {
                price = price + ((roundedTotalStayLength * tariff.getVehicleTariff5().getCostStep1()) / tariff.getVehicleTariff5().getDurationStep1());
                return price;
            }


        } else {

            long remainingStayLength = roundedTotalStayLength - tariff.getVehicleTariff5().getDurationStep1();
            if (remainingStayLength >= 0) {
                price = price + tariff.getVehicleTariff5().getCostStep1();
                if (tariff.getVehicleTariff5().getDurationStep2() != null) {
                    remainingStayLength = remainingStayLength - tariff.getVehicleTariff5().getDurationStep2();
                    if (remainingStayLength >= 0) {
                        price = price + tariff.getVehicleTariff5().getCostStep2();
                        if (tariff.getVehicleTariff5().getDurationStep3() != null) {
                            remainingStayLength = remainingStayLength - tariff.getVehicleTariff5().getDurationStep3();
                            if (remainingStayLength >= 0) {
                                price = price + tariff.getVehicleTariff5().getCostStep3();
                                if (tariff.getVehicleTariff5().getDurationStep4() != null) {
                                    remainingStayLength = remainingStayLength - tariff.getVehicleTariff5().getDurationStep4();
                                    if (remainingStayLength >= 0) {
                                        price = price + tariff.getVehicleTariff5().getCostStep4();
                                        if (tariff.getVehicleTariff5().getDurationStep5() != null) {
                                            price = price +
                                                    ((remainingStayLength / tariff.getVehicleTariff5().getDurationStep5()) * tariff.getVehicleTariff5().getCostStep5()) +
                                                    (((remainingStayLength % tariff.getVehicleTariff5().getDurationStep5()) * tariff.getVehicleTariff5().getCostStep5()) / tariff.getVehicleTariff5().getCostStep5());
                                            return price;
                                        } else {
                                            price = price +
                                                    ((remainingStayLength / tariff.getVehicleTariff5().getDurationStep4()) * tariff.getVehicleTariff5().getCostStep4()) +
                                                    (((remainingStayLength % tariff.getVehicleTariff5().getDurationStep4()) * tariff.getVehicleTariff5().getCostStep4()) / tariff.getVehicleTariff5().getDurationStep4());
                                            return price;
                                        }
                                    } else {
                                        price = price + (((remainingStayLength + tariff.getVehicleTariff5().getDurationStep4()) * tariff.getVehicleTariff5().getCostStep4()) / tariff.getVehicleTariff5().getDurationStep4());
                                        return price;
                                    }
                                } else {
                                    price = price +
                                            ((remainingStayLength / tariff.getVehicleTariff5().getDurationStep3()) * tariff.getVehicleTariff5().getCostStep3()) +
                                            (((remainingStayLength % tariff.getVehicleTariff5().getDurationStep3()) * tariff.getVehicleTariff5().getCostStep3()) / tariff.getVehicleTariff5().getDurationStep3());
                                    return price;
                                }
                            } else {
                                price = price + (((remainingStayLength + tariff.getVehicleTariff5().getDurationStep3()) * tariff.getVehicleTariff5().getCostStep3()) / tariff.getVehicleTariff5().getDurationStep3());
                                return price;
                            }
                        } else {
                            price = price +
                                    ((remainingStayLength / tariff.getVehicleTariff5().getDurationStep2()) * tariff.getVehicleTariff5().getCostStep2()) +
                                    (((remainingStayLength % tariff.getVehicleTariff5().getDurationStep2()) * tariff.getVehicleTariff5().getCostStep2()) / tariff.getVehicleTariff5().getDurationStep2());
                            return price;
                        }
                    } else {
                        price = price + (((remainingStayLength + tariff.getVehicleTariff5().getDurationStep2()) * tariff.getVehicleTariff5().getCostStep2()) / tariff.getVehicleTariff5().getDurationStep2());
                        return price;
                    }
                } else {
                    price = price +
                            ((remainingStayLength / tariff.getVehicleTariff5().getDurationStep1()) * tariff.getVehicleTariff5().getCostStep1()) +
                            (((remainingStayLength % tariff.getVehicleTariff5().getDurationStep1()) * tariff.getVehicleTariff5().getCostStep1()) / tariff.getVehicleTariff5().getDurationStep1());
                    return price;
                }
            } else {
                price = price + ((roundedTotalStayLength * tariff.getVehicleTariff5().getCostStep1()) / tariff.getVehicleTariff5().getDurationStep1());
                return price;
            }


        }


    }


    public static long calculateStepFareVehicle6(long roundedTotalStayLength, Tariff tariff) {
        Log.d(TAG, "calculateStepFareVehicle6: > roundedTime:"+roundedTotalStayLength);
        long price = 0;


        if (tariff.getIsNeedCalculateOnlyLastStep().booleanValue()) {

            if ((tariff.getVehicleTariff6().getDurationStep5() != null) && (roundedTotalStayLength >= tariff.getVehicleTariff6().getDurationStep5())) {
                price = price + ((roundedTotalStayLength * tariff.getVehicleTariff6().getCostStep5()) / tariff.getVehicleTariff6().getDurationStep5());
                return price;
            } else if ((tariff.getVehicleTariff6().getDurationStep4() != null) && (roundedTotalStayLength >= tariff.getVehicleTariff6().getDurationStep4())) {
                price = price + ((roundedTotalStayLength * tariff.getVehicleTariff6().getCostStep4()) / tariff.getVehicleTariff6().getDurationStep4());
                return price;
            } else if ((tariff.getVehicleTariff6().getDurationStep3() != null) && (roundedTotalStayLength >= tariff.getVehicleTariff6().getDurationStep3())) {
                price = price + ((roundedTotalStayLength * tariff.getVehicleTariff6().getCostStep3()) / tariff.getVehicleTariff6().getDurationStep3());
                return price;
            } else if ((tariff.getVehicleTariff6().getDurationStep2() != null) && (roundedTotalStayLength >= tariff.getVehicleTariff6().getDurationStep2())) {
                price = price + ((roundedTotalStayLength * tariff.getVehicleTariff6().getCostStep2()) / tariff.getVehicleTariff6().getDurationStep2());
                return price;
            } else {
                price = price + ((roundedTotalStayLength * tariff.getVehicleTariff6().getCostStep1()) / tariff.getVehicleTariff6().getDurationStep1());
                return price;
            }


        } else {


            long remainingStayLength = roundedTotalStayLength - tariff.getVehicleTariff6().getDurationStep1();
            if (remainingStayLength >= 0) {
                price = price + tariff.getVehicleTariff6().getCostStep1();
                if (tariff.getVehicleTariff6().getDurationStep2() != null) {
                    remainingStayLength = remainingStayLength - tariff.getVehicleTariff6().getDurationStep2();
                    if (remainingStayLength >= 0) {
                        price = price + tariff.getVehicleTariff6().getCostStep2();
                        if (tariff.getVehicleTariff6().getDurationStep3() != null) {
                            remainingStayLength = remainingStayLength - tariff.getVehicleTariff6().getDurationStep3();
                            if (remainingStayLength >= 0) {
                                price = price + tariff.getVehicleTariff6().getCostStep3();
                                if (tariff.getVehicleTariff6().getDurationStep4() != null) {
                                    remainingStayLength = remainingStayLength - tariff.getVehicleTariff6().getDurationStep4();
                                    if (remainingStayLength >= 0) {
                                        price = price + tariff.getVehicleTariff6().getCostStep4();
                                        if (tariff.getVehicleTariff6().getDurationStep5() != null) {
                                            price = price +
                                                    ((remainingStayLength / tariff.getVehicleTariff6().getDurationStep5()) * tariff.getVehicleTariff6().getCostStep5()) +
                                                    (((remainingStayLength % tariff.getVehicleTariff6().getDurationStep5()) * tariff.getVehicleTariff6().getCostStep5()) / tariff.getVehicleTariff6().getCostStep5());
                                            return price;
                                        } else {
                                            price = price +
                                                    ((remainingStayLength / tariff.getVehicleTariff6().getDurationStep4()) * tariff.getVehicleTariff6().getCostStep4()) +
                                                    (((remainingStayLength % tariff.getVehicleTariff6().getDurationStep4()) * tariff.getVehicleTariff6().getCostStep4()) / tariff.getVehicleTariff6().getDurationStep4());
                                            return price;
                                        }
                                    } else {
                                        price = price + (((remainingStayLength + tariff.getVehicleTariff6().getDurationStep4()) * tariff.getVehicleTariff6().getCostStep4()) / tariff.getVehicleTariff6().getDurationStep4());
                                        return price;
                                    }
                                } else {
                                    price = price +
                                            ((remainingStayLength / tariff.getVehicleTariff6().getDurationStep3()) * tariff.getVehicleTariff6().getCostStep3()) +
                                            (((remainingStayLength % tariff.getVehicleTariff6().getDurationStep3()) * tariff.getVehicleTariff6().getCostStep3()) / tariff.getVehicleTariff6().getDurationStep3());
                                    return price;
                                }
                            } else {
                                price = price + (((remainingStayLength + tariff.getVehicleTariff6().getDurationStep3()) * tariff.getVehicleTariff6().getCostStep3()) / tariff.getVehicleTariff6().getDurationStep3());
                                return price;
                            }
                        } else {
                            price = price +
                                    ((remainingStayLength / tariff.getVehicleTariff6().getDurationStep2()) * tariff.getVehicleTariff6().getCostStep2()) +
                                    (((remainingStayLength % tariff.getVehicleTariff6().getDurationStep2()) * tariff.getVehicleTariff6().getCostStep2()) / tariff.getVehicleTariff6().getDurationStep2());
                            return price;
                        }
                    } else {
                        price = price + (((remainingStayLength + tariff.getVehicleTariff6().getDurationStep2()) * tariff.getVehicleTariff6().getCostStep2()) / tariff.getVehicleTariff6().getDurationStep2());
                        return price;
                    }
                } else {
                    price = price +
                            ((remainingStayLength / tariff.getVehicleTariff6().getDurationStep1()) * tariff.getVehicleTariff6().getCostStep1()) +
                            (((remainingStayLength % tariff.getVehicleTariff6().getDurationStep1()) * tariff.getVehicleTariff6().getCostStep1()) / tariff.getVehicleTariff6().getDurationStep1());
                    return price;
                }
            } else {
                price = price + ((roundedTotalStayLength * tariff.getVehicleTariff6().getCostStep1()) / tariff.getVehicleTariff6().getDurationStep1());
                return price;
            }


        }


    }


    public static long calculateStepFareVehicle7(long roundedTotalStayLength, Tariff tariff) {
        Log.d(TAG, "calculateStepFareVehicle7: > roundedTime:"+roundedTotalStayLength);
        long price = 0;


        if (tariff.getIsNeedCalculateOnlyLastStep().booleanValue()) {

            if ((tariff.getVehicleTariff7().getDurationStep5() != null) && (roundedTotalStayLength >= tariff.getVehicleTariff7().getDurationStep5())) {
                price = price + ((roundedTotalStayLength * tariff.getVehicleTariff7().getCostStep5()) / tariff.getVehicleTariff7().getDurationStep5());
                return price;
            } else if ((tariff.getVehicleTariff7().getDurationStep4() != null) && (roundedTotalStayLength >= tariff.getVehicleTariff7().getDurationStep4())) {
                price = price + ((roundedTotalStayLength * tariff.getVehicleTariff7().getCostStep4()) / tariff.getVehicleTariff7().getDurationStep4());
                return price;
            } else if ((tariff.getVehicleTariff7().getDurationStep3() != null) && (roundedTotalStayLength >= tariff.getVehicleTariff7().getDurationStep3())) {
                price = price + ((roundedTotalStayLength * tariff.getVehicleTariff7().getCostStep3()) / tariff.getVehicleTariff7().getDurationStep3());
                return price;
            } else if ((tariff.getVehicleTariff7().getDurationStep2() != null) && (roundedTotalStayLength >= tariff.getVehicleTariff7().getDurationStep2())) {
                price = price + ((roundedTotalStayLength * tariff.getVehicleTariff7().getCostStep2()) / tariff.getVehicleTariff7().getDurationStep2());
                return price;
            } else {
                price = price + ((roundedTotalStayLength * tariff.getVehicleTariff7().getCostStep1()) / tariff.getVehicleTariff7().getDurationStep1());
                return price;
            }


        } else {


            long remainingStayLength = roundedTotalStayLength - tariff.getVehicleTariff7().getDurationStep1();
            if (remainingStayLength >= 0) {
                price = price + tariff.getVehicleTariff7().getCostStep1();
                if (tariff.getVehicleTariff7().getDurationStep2() != null) {
                    remainingStayLength = remainingStayLength - tariff.getVehicleTariff7().getDurationStep2();
                    if (remainingStayLength >= 0) {
                        price = price + tariff.getVehicleTariff7().getCostStep2();
                        if (tariff.getVehicleTariff7().getDurationStep3() != null) {
                            remainingStayLength = remainingStayLength - tariff.getVehicleTariff7().getDurationStep3();
                            if (remainingStayLength >= 0) {
                                price = price + tariff.getVehicleTariff7().getCostStep3();
                                if (tariff.getVehicleTariff7().getDurationStep4() != null) {
                                    remainingStayLength = remainingStayLength - tariff.getVehicleTariff7().getDurationStep4();
                                    if (remainingStayLength >= 0) {
                                        price = price + tariff.getVehicleTariff7().getCostStep4();
                                        if (tariff.getVehicleTariff7().getDurationStep5() != null) {
                                            price = price +
                                                    ((remainingStayLength / tariff.getVehicleTariff7().getDurationStep5()) * tariff.getVehicleTariff7().getCostStep5()) +
                                                    (((remainingStayLength % tariff.getVehicleTariff7().getDurationStep5()) * tariff.getVehicleTariff7().getCostStep5()) / tariff.getVehicleTariff7().getCostStep5());
                                            return price;
                                        } else {
                                            price = price +
                                                    ((remainingStayLength / tariff.getVehicleTariff7().getDurationStep4()) * tariff.getVehicleTariff7().getCostStep4()) +
                                                    (((remainingStayLength % tariff.getVehicleTariff7().getDurationStep4()) * tariff.getVehicleTariff7().getCostStep4()) / tariff.getVehicleTariff7().getDurationStep4());
                                            return price;
                                        }
                                    } else {
                                        price = price + (((remainingStayLength + tariff.getVehicleTariff7().getDurationStep4()) * tariff.getVehicleTariff7().getCostStep4()) / tariff.getVehicleTariff7().getDurationStep4());
                                        return price;
                                    }
                                } else {
                                    price = price +
                                            ((remainingStayLength / tariff.getVehicleTariff7().getDurationStep3()) * tariff.getVehicleTariff7().getCostStep3()) +
                                            (((remainingStayLength % tariff.getVehicleTariff7().getDurationStep3()) * tariff.getVehicleTariff7().getCostStep3()) / tariff.getVehicleTariff7().getDurationStep3());
                                    return price;
                                }
                            } else {
                                price = price + (((remainingStayLength + tariff.getVehicleTariff7().getDurationStep3()) * tariff.getVehicleTariff7().getCostStep3()) / tariff.getVehicleTariff7().getDurationStep3());
                                return price;
                            }
                        } else {
                            price = price +
                                    ((remainingStayLength / tariff.getVehicleTariff7().getDurationStep2()) * tariff.getVehicleTariff7().getCostStep2()) +
                                    (((remainingStayLength % tariff.getVehicleTariff7().getDurationStep2()) * tariff.getVehicleTariff7().getCostStep2()) / tariff.getVehicleTariff7().getDurationStep2());
                            return price;
                        }
                    } else {
                        price = price + (((remainingStayLength + tariff.getVehicleTariff7().getDurationStep2()) * tariff.getVehicleTariff7().getCostStep2()) / tariff.getVehicleTariff7().getDurationStep2());
                        return price;
                    }
                } else {
                    price = price +
                            ((remainingStayLength / tariff.getVehicleTariff7().getDurationStep1()) * tariff.getVehicleTariff7().getCostStep1()) +
                            (((remainingStayLength % tariff.getVehicleTariff7().getDurationStep1()) * tariff.getVehicleTariff7().getCostStep1()) / tariff.getVehicleTariff7().getDurationStep1());
                    return price;
                }
            } else {
                price = price + ((roundedTotalStayLength * tariff.getVehicleTariff7().getCostStep1()) / tariff.getVehicleTariff7().getDurationStep1());
                return price;
            }


        }


    }


    public static long calculateStepFareVehicle8(long roundedTotalStayLength, Tariff tariff) {
        Log.d(TAG, "calculateStepFareVehicle8: > roundedTime:"+roundedTotalStayLength);
        long price = 0;


        if (tariff.getIsNeedCalculateOnlyLastStep().booleanValue()) {

            if ((tariff.getVehicleTariff8().getDurationStep5() != null) && (roundedTotalStayLength >= tariff.getVehicleTariff8().getDurationStep5())) {
                price = price + ((roundedTotalStayLength * tariff.getVehicleTariff8().getCostStep5()) / tariff.getVehicleTariff8().getDurationStep5());
                return price;
            } else if ((tariff.getVehicleTariff8().getDurationStep4() != null) && (roundedTotalStayLength >= tariff.getVehicleTariff8().getDurationStep4())) {
                price = price + ((roundedTotalStayLength * tariff.getVehicleTariff8().getCostStep4()) / tariff.getVehicleTariff8().getDurationStep4());
                return price;
            } else if ((tariff.getVehicleTariff8().getDurationStep3() != null) && (roundedTotalStayLength >= tariff.getVehicleTariff8().getDurationStep3())) {
                price = price + ((roundedTotalStayLength * tariff.getVehicleTariff8().getCostStep3()) / tariff.getVehicleTariff8().getDurationStep3());
                return price;
            } else if ((tariff.getVehicleTariff8().getDurationStep2() != null) && (roundedTotalStayLength >= tariff.getVehicleTariff8().getDurationStep2())) {
                price = price + ((roundedTotalStayLength * tariff.getVehicleTariff8().getCostStep2()) / tariff.getVehicleTariff8().getDurationStep2());
                return price;
            } else {
                price = price + ((roundedTotalStayLength * tariff.getVehicleTariff8().getCostStep1()) / tariff.getVehicleTariff8().getDurationStep1());
                return price;
            }


        } else {


            long remainingStayLength = roundedTotalStayLength - tariff.getVehicleTariff8().getDurationStep1();
            if (remainingStayLength >= 0) {
                price = price + tariff.getVehicleTariff8().getCostStep1();
                if (tariff.getVehicleTariff8().getDurationStep2() != null) {
                    remainingStayLength = remainingStayLength - tariff.getVehicleTariff8().getDurationStep2();
                    if (remainingStayLength >= 0) {
                        price = price + tariff.getVehicleTariff8().getCostStep2();
                        if (tariff.getVehicleTariff8().getDurationStep3() != null) {
                            remainingStayLength = remainingStayLength - tariff.getVehicleTariff8().getDurationStep3();
                            if (remainingStayLength >= 0) {
                                price = price + tariff.getVehicleTariff8().getCostStep3();
                                if (tariff.getVehicleTariff8().getDurationStep4() != null) {
                                    remainingStayLength = remainingStayLength - tariff.getVehicleTariff8().getDurationStep4();
                                    if (remainingStayLength >= 0) {
                                        price = price + tariff.getVehicleTariff8().getCostStep4();
                                        if (tariff.getVehicleTariff8().getDurationStep5() != null) {
                                            price = price +
                                                    ((remainingStayLength / tariff.getVehicleTariff8().getDurationStep5()) * tariff.getVehicleTariff8().getCostStep5()) +
                                                    (((remainingStayLength % tariff.getVehicleTariff8().getDurationStep5()) * tariff.getVehicleTariff8().getCostStep5()) / tariff.getVehicleTariff8().getCostStep5());
                                            return price;
                                        } else {
                                            price = price +
                                                    ((remainingStayLength / tariff.getVehicleTariff8().getDurationStep4()) * tariff.getVehicleTariff8().getCostStep4()) +
                                                    (((remainingStayLength % tariff.getVehicleTariff8().getDurationStep4()) * tariff.getVehicleTariff8().getCostStep4()) / tariff.getVehicleTariff8().getDurationStep4());
                                            return price;
                                        }
                                    } else {
                                        price = price + (((remainingStayLength + tariff.getVehicleTariff8().getDurationStep4()) * tariff.getVehicleTariff8().getCostStep4()) / tariff.getVehicleTariff8().getDurationStep4());
                                        return price;
                                    }
                                } else {
                                    price = price +
                                            ((remainingStayLength / tariff.getVehicleTariff8().getDurationStep3()) * tariff.getVehicleTariff8().getCostStep3()) +
                                            (((remainingStayLength % tariff.getVehicleTariff8().getDurationStep3()) * tariff.getVehicleTariff8().getCostStep3()) / tariff.getVehicleTariff8().getDurationStep3());
                                    return price;
                                }
                            } else {
                                price = price + (((remainingStayLength + tariff.getVehicleTariff8().getDurationStep3()) * tariff.getVehicleTariff8().getCostStep3()) / tariff.getVehicleTariff8().getDurationStep3());
                                return price;
                            }
                        } else {
                            price = price +
                                    ((remainingStayLength / tariff.getVehicleTariff8().getDurationStep2()) * tariff.getVehicleTariff8().getCostStep2()) +
                                    (((remainingStayLength % tariff.getVehicleTariff8().getDurationStep2()) * tariff.getVehicleTariff8().getCostStep2()) / tariff.getVehicleTariff8().getDurationStep2());
                            return price;
                        }
                    } else {
                        price = price + (((remainingStayLength + tariff.getVehicleTariff8().getDurationStep2()) * tariff.getVehicleTariff8().getCostStep2()) / tariff.getVehicleTariff8().getDurationStep2());
                        return price;
                    }
                } else {
                    price = price +
                            ((remainingStayLength / tariff.getVehicleTariff8().getDurationStep1()) * tariff.getVehicleTariff8().getCostStep1()) +
                            (((remainingStayLength % tariff.getVehicleTariff8().getDurationStep1()) * tariff.getVehicleTariff8().getCostStep1()) / tariff.getVehicleTariff8().getDurationStep1());
                    return price;
                }
            } else {
                price = price + ((roundedTotalStayLength * tariff.getVehicleTariff8().getCostStep1()) / tariff.getVehicleTariff8().getDurationStep1());
                return price;
            }


        }


    }


}
