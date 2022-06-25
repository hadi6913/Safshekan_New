package com.khodmohaseb.parkban.utils;

import com.khodmohaseb.parkban.services.dto.khodmohaseb.parkinginfo.Tariff;

public class DailyHourlyFareCalculator {


    public static long calculateHourlyDailyFareVehicle1(long roundedTotalStayLength, Tariff tariff) {
        long price = 0;
        if (roundedTotalStayLength <= tariff.getCircadianCalcBase()) {

            price = HourlyFareCalculator.calculateStepFareVehicle1(roundedTotalStayLength, tariff);
            return price;
        } else {
            if ((roundedTotalStayLength <= 1440) && (roundedTotalStayLength > tariff.getCircadianCalcBase())) {
                price = tariff.getVehicleTariff1().getCostNight1();
                return price;

            } else {


                long nightCount = roundedTotalStayLength / 1440;
                long remainingMinutes = roundedTotalStayLength % 1440;
                if (remainingMinutes >= tariff.getCircadianCalcBase()) {
                    nightCount = nightCount + 1;
                    remainingMinutes = 0;
                }

                long remainingNights = nightCount - tariff.getVehicleTariff1().getDurationNight1();
                if (remainingNights >= 0) {
                    price = price + tariff.getVehicleTariff1().getCostNight1();
                    if (tariff.getVehicleTariff1().getDurationNight2() != null) {
                        price = price +
                                ((remainingNights / tariff.getVehicleTariff1().getDurationNight2()) * tariff.getVehicleTariff1().getCostNight2()) +
                                (((remainingNights % tariff.getVehicleTariff1().getDurationNight2()) * tariff.getVehicleTariff1().getCostNight2()) / tariff.getVehicleTariff1().getDurationNight2());

                        price = price + HourlyFareCalculator.calculateStepFareVehicle1(remainingMinutes, tariff);
                        return price;
                    } else {
                        price = price +
                                ((remainingNights / tariff.getVehicleTariff1().getDurationNight1()) * tariff.getVehicleTariff1().getCostNight1()) +
                                (((remainingNights % tariff.getVehicleTariff1().getDurationNight1()) * tariff.getVehicleTariff1().getCostNight1()) / tariff.getVehicleTariff1().getDurationNight1());
                        price = price + HourlyFareCalculator.calculateStepFareVehicle1(remainingMinutes, tariff);
                        return price;
                    }
                } else {
                    price = price + ((nightCount * tariff.getVehicleTariff1().getCostNight1()) / tariff.getVehicleTariff1().getDurationNight1());
                    price = price + HourlyFareCalculator.calculateStepFareVehicle1(remainingMinutes, tariff);
                    return price;
                }


            }
        }
    }

    public static long calculateHourlyDailyFareVehicle2(long roundedTotalStayLength, Tariff tariff) {
        long price = 0;
        if (roundedTotalStayLength <= tariff.getCircadianCalcBase()) {

            price = HourlyFareCalculator.calculateStepFareVehicle2(roundedTotalStayLength, tariff);
            return price;
        } else {
            if ((roundedTotalStayLength <= 1440) && (roundedTotalStayLength > tariff.getCircadianCalcBase())) {
                price = tariff.getVehicleTariff2().getCostNight1();
                return price;

            } else {


                long nightCount = roundedTotalStayLength / 1440;
                long remainingMinutes = roundedTotalStayLength % 1440;
                if (remainingMinutes >= tariff.getCircadianCalcBase()) {
                    nightCount = nightCount + 1;
                    remainingMinutes = 0;
                }

                long remainingNights = nightCount - tariff.getVehicleTariff2().getDurationNight1();
                if (remainingNights >= 0) {
                    price = price + tariff.getVehicleTariff2().getCostNight1();
                    if (tariff.getVehicleTariff2().getDurationNight2() != null) {
                        price = price +
                                ((remainingNights / tariff.getVehicleTariff2().getDurationNight2()) * tariff.getVehicleTariff2().getCostNight2()) +
                                (((remainingNights % tariff.getVehicleTariff2().getDurationNight2()) * tariff.getVehicleTariff2().getCostNight2()) / tariff.getVehicleTariff2().getDurationNight2());

                        price = price + HourlyFareCalculator.calculateStepFareVehicle2(remainingMinutes, tariff);
                        return price;
                    } else {
                        price = price +
                                ((remainingNights / tariff.getVehicleTariff2().getDurationNight1()) * tariff.getVehicleTariff2().getCostNight1()) +
                                (((remainingNights % tariff.getVehicleTariff2().getDurationNight1()) * tariff.getVehicleTariff2().getCostNight1()) / tariff.getVehicleTariff2().getDurationNight1());
                        price = price + HourlyFareCalculator.calculateStepFareVehicle2(remainingMinutes, tariff);
                        return price;
                    }
                } else {
                    price = price + ((nightCount * tariff.getVehicleTariff2().getCostNight1()) / tariff.getVehicleTariff2().getDurationNight1());
                    price = price + HourlyFareCalculator.calculateStepFareVehicle2(remainingMinutes, tariff);
                    return price;
                }


            }
        }
    }

    public static long calculateHourlyDailyFareVehicle3(long roundedTotalStayLength, Tariff tariff) {
        long price = 0;
        if (roundedTotalStayLength <= tariff.getCircadianCalcBase()) {

            price = HourlyFareCalculator.calculateStepFareVehicle3(roundedTotalStayLength, tariff);
            return price;
        } else {
            if ((roundedTotalStayLength <= 1440) && (roundedTotalStayLength > tariff.getCircadianCalcBase())) {
                price = tariff.getVehicleTariff3().getCostNight1();
                return price;

            } else {


                long nightCount = roundedTotalStayLength / 1440;
                long remainingMinutes = roundedTotalStayLength % 1440;
                if (remainingMinutes >= tariff.getCircadianCalcBase()) {
                    nightCount = nightCount + 1;
                    remainingMinutes = 0;
                }

                long remainingNights = nightCount - tariff.getVehicleTariff3().getDurationNight1();
                if (remainingNights >= 0) {
                    price = price + tariff.getVehicleTariff3().getCostNight1();
                    if (tariff.getVehicleTariff3().getDurationNight2() != null) {
                        price = price +
                                ((remainingNights / tariff.getVehicleTariff3().getDurationNight2()) * tariff.getVehicleTariff3().getCostNight2()) +
                                (((remainingNights % tariff.getVehicleTariff3().getDurationNight2()) * tariff.getVehicleTariff3().getCostNight2()) / tariff.getVehicleTariff3().getDurationNight2());

                        price = price + HourlyFareCalculator.calculateStepFareVehicle3(remainingMinutes, tariff);
                        return price;
                    } else {
                        price = price +
                                ((remainingNights / tariff.getVehicleTariff3().getDurationNight1()) * tariff.getVehicleTariff3().getCostNight1()) +
                                (((remainingNights % tariff.getVehicleTariff3().getDurationNight1()) * tariff.getVehicleTariff3().getCostNight1()) / tariff.getVehicleTariff3().getDurationNight1());
                        price = price + HourlyFareCalculator.calculateStepFareVehicle3(remainingMinutes, tariff);
                        return price;
                    }
                } else {
                    price = price + ((nightCount * tariff.getVehicleTariff3().getCostNight1()) / tariff.getVehicleTariff3().getDurationNight1());
                    price = price + HourlyFareCalculator.calculateStepFareVehicle3(remainingMinutes, tariff);
                    return price;
                }


            }
        }
    }

    public static long calculateHourlyDailyFareVehicle4(long roundedTotalStayLength, Tariff tariff) {
        long price = 0;
        if (roundedTotalStayLength <= tariff.getCircadianCalcBase()) {

            price = HourlyFareCalculator.calculateStepFareVehicle4(roundedTotalStayLength, tariff);
            return price;
        } else {
            if ((roundedTotalStayLength <= 1440) && (roundedTotalStayLength > tariff.getCircadianCalcBase())) {
                price = tariff.getVehicleTariff4().getCostNight1();
                return price;

            } else {


                long nightCount = roundedTotalStayLength / 1440;
                long remainingMinutes = roundedTotalStayLength % 1440;
                if (remainingMinutes >= tariff.getCircadianCalcBase()) {
                    nightCount = nightCount + 1;
                    remainingMinutes = 0;
                }

                long remainingNights = nightCount - tariff.getVehicleTariff4().getDurationNight1();
                if (remainingNights >= 0) {
                    price = price + tariff.getVehicleTariff4().getCostNight1();
                    if (tariff.getVehicleTariff4().getDurationNight2() != null) {
                        price = price +
                                ((remainingNights / tariff.getVehicleTariff4().getDurationNight2()) * tariff.getVehicleTariff4().getCostNight2()) +
                                (((remainingNights % tariff.getVehicleTariff4().getDurationNight2()) * tariff.getVehicleTariff4().getCostNight2()) / tariff.getVehicleTariff4().getDurationNight2());

                        price = price + HourlyFareCalculator.calculateStepFareVehicle4(remainingMinutes, tariff);
                        return price;
                    } else {
                        price = price +
                                ((remainingNights / tariff.getVehicleTariff4().getDurationNight1()) * tariff.getVehicleTariff4().getCostNight1()) +
                                (((remainingNights % tariff.getVehicleTariff4().getDurationNight1()) * tariff.getVehicleTariff4().getCostNight1()) / tariff.getVehicleTariff4().getDurationNight1());
                        price = price + HourlyFareCalculator.calculateStepFareVehicle4(remainingMinutes, tariff);
                        return price;
                    }
                } else {
                    price = price + ((nightCount * tariff.getVehicleTariff4().getCostNight1()) / tariff.getVehicleTariff4().getDurationNight1());
                    price = price + HourlyFareCalculator.calculateStepFareVehicle4(remainingMinutes, tariff);
                    return price;
                }


            }
        }
    }

    public static long calculateHourlyDailyFareVehicle5(long roundedTotalStayLength, Tariff tariff) {
        long price = 0;
        if (roundedTotalStayLength <= tariff.getCircadianCalcBase()) {

            price = HourlyFareCalculator.calculateStepFareVehicle5(roundedTotalStayLength, tariff);
            return price;
        } else {
            if ((roundedTotalStayLength <= 1440) && (roundedTotalStayLength > tariff.getCircadianCalcBase())) {
                price = tariff.getVehicleTariff5().getCostNight1();
                return price;

            } else {


                long nightCount = roundedTotalStayLength / 1440;
                long remainingMinutes = roundedTotalStayLength % 1440;
                if (remainingMinutes >= tariff.getCircadianCalcBase()) {
                    nightCount = nightCount + 1;
                    remainingMinutes = 0;
                }

                long remainingNights = nightCount - tariff.getVehicleTariff5().getDurationNight1();
                if (remainingNights >= 0) {
                    price = price + tariff.getVehicleTariff5().getCostNight1();
                    if (tariff.getVehicleTariff5().getDurationNight2() != null) {
                        price = price +
                                ((remainingNights / tariff.getVehicleTariff5().getDurationNight2()) * tariff.getVehicleTariff5().getCostNight2()) +
                                (((remainingNights % tariff.getVehicleTariff5().getDurationNight2()) * tariff.getVehicleTariff5().getCostNight2()) / tariff.getVehicleTariff5().getDurationNight2());

                        price = price + HourlyFareCalculator.calculateStepFareVehicle5(remainingMinutes, tariff);
                        return price;
                    } else {
                        price = price +
                                ((remainingNights / tariff.getVehicleTariff5().getDurationNight1()) * tariff.getVehicleTariff5().getCostNight1()) +
                                (((remainingNights % tariff.getVehicleTariff5().getDurationNight1()) * tariff.getVehicleTariff5().getCostNight1()) / tariff.getVehicleTariff5().getDurationNight1());
                        price = price + HourlyFareCalculator.calculateStepFareVehicle5(remainingMinutes, tariff);
                        return price;
                    }
                } else {
                    price = price + ((nightCount * tariff.getVehicleTariff5().getCostNight1()) / tariff.getVehicleTariff5().getDurationNight1());
                    price = price + HourlyFareCalculator.calculateStepFareVehicle5(remainingMinutes, tariff);
                    return price;
                }


            }
        }
    }

    public static long calculateHourlyDailyFareVehicle6(long roundedTotalStayLength, Tariff tariff) {
        long price = 0;
        if (roundedTotalStayLength <= tariff.getCircadianCalcBase()) {

            price = HourlyFareCalculator.calculateStepFareVehicle6(roundedTotalStayLength, tariff);
            return price;
        } else {
            if ((roundedTotalStayLength <= 1440) && (roundedTotalStayLength > tariff.getCircadianCalcBase())) {
                price = tariff.getVehicleTariff6().getCostNight1();
                return price;

            } else {


                long nightCount = roundedTotalStayLength / 1440;
                long remainingMinutes = roundedTotalStayLength % 1440;
                if (remainingMinutes >= tariff.getCircadianCalcBase()) {
                    nightCount = nightCount + 1;
                    remainingMinutes = 0;
                }

                long remainingNights = nightCount - tariff.getVehicleTariff6().getDurationNight1();
                if (remainingNights >= 0) {
                    price = price + tariff.getVehicleTariff6().getCostNight1();
                    if (tariff.getVehicleTariff6().getDurationNight2() != null) {
                        price = price +
                                ((remainingNights / tariff.getVehicleTariff6().getDurationNight2()) * tariff.getVehicleTariff6().getCostNight2()) +
                                (((remainingNights % tariff.getVehicleTariff6().getDurationNight2()) * tariff.getVehicleTariff6().getCostNight2()) / tariff.getVehicleTariff6().getDurationNight2());

                        price = price + HourlyFareCalculator.calculateStepFareVehicle6(remainingMinutes, tariff);
                        return price;
                    } else {
                        price = price +
                                ((remainingNights / tariff.getVehicleTariff6().getDurationNight1()) * tariff.getVehicleTariff6().getCostNight1()) +
                                (((remainingNights % tariff.getVehicleTariff6().getDurationNight1()) * tariff.getVehicleTariff6().getCostNight1()) / tariff.getVehicleTariff6().getDurationNight1());
                        price = price + HourlyFareCalculator.calculateStepFareVehicle6(remainingMinutes, tariff);
                        return price;
                    }
                } else {
                    price = price + ((nightCount * tariff.getVehicleTariff6().getCostNight1()) / tariff.getVehicleTariff6().getDurationNight1());
                    price = price + HourlyFareCalculator.calculateStepFareVehicle6(remainingMinutes, tariff);
                    return price;
                }


            }
        }
    }

    public static long calculateHourlyDailyFareVehicle7(long roundedTotalStayLength, Tariff tariff) {
        long price = 0;
        if (roundedTotalStayLength <= tariff.getCircadianCalcBase()) {

            price = HourlyFareCalculator.calculateStepFareVehicle7(roundedTotalStayLength, tariff);
            return price;
        } else {
            if ((roundedTotalStayLength <= 1440) && (roundedTotalStayLength > tariff.getCircadianCalcBase())) {
                price = tariff.getVehicleTariff7().getCostNight1();
                return price;

            } else {


                long nightCount = roundedTotalStayLength / 1440;
                long remainingMinutes = roundedTotalStayLength % 1440;
                if (remainingMinutes >= tariff.getCircadianCalcBase()) {
                    nightCount = nightCount + 1;
                    remainingMinutes = 0;
                }

                long remainingNights = nightCount - tariff.getVehicleTariff7().getDurationNight1();
                if (remainingNights >= 0) {
                    price = price + tariff.getVehicleTariff7().getCostNight1();
                    if (tariff.getVehicleTariff7().getDurationNight2() != null) {
                        price = price +
                                ((remainingNights / tariff.getVehicleTariff7().getDurationNight2()) * tariff.getVehicleTariff7().getCostNight2()) +
                                (((remainingNights % tariff.getVehicleTariff7().getDurationNight2()) * tariff.getVehicleTariff7().getCostNight2()) / tariff.getVehicleTariff7().getDurationNight2());

                        price = price + HourlyFareCalculator.calculateStepFareVehicle7(remainingMinutes, tariff);
                        return price;
                    } else {
                        price = price +
                                ((remainingNights / tariff.getVehicleTariff7().getDurationNight1()) * tariff.getVehicleTariff7().getCostNight1()) +
                                (((remainingNights % tariff.getVehicleTariff7().getDurationNight1()) * tariff.getVehicleTariff7().getCostNight1()) / tariff.getVehicleTariff7().getDurationNight1());
                        price = price + HourlyFareCalculator.calculateStepFareVehicle7(remainingMinutes, tariff);
                        return price;
                    }
                } else {
                    price = price + ((nightCount * tariff.getVehicleTariff7().getCostNight1()) / tariff.getVehicleTariff7().getDurationNight1());
                    price = price + HourlyFareCalculator.calculateStepFareVehicle7(remainingMinutes, tariff);
                    return price;
                }


            }
        }
    }

    public static long calculateHourlyDailyFareVehicle8(long roundedTotalStayLength, Tariff tariff) {
        long price = 0;
        if (roundedTotalStayLength <= tariff.getCircadianCalcBase()) {

            price = HourlyFareCalculator.calculateStepFareVehicle8(roundedTotalStayLength, tariff);
            return price;
        } else {
            if ((roundedTotalStayLength <= 1440) && (roundedTotalStayLength > tariff.getCircadianCalcBase())) {
                price = tariff.getVehicleTariff8().getCostNight1();
                return price;

            } else {


                long nightCount = roundedTotalStayLength / 1440;
                long remainingMinutes = roundedTotalStayLength % 1440;
                if (remainingMinutes >= tariff.getCircadianCalcBase()) {
                    nightCount = nightCount + 1;
                    remainingMinutes = 0;
                }

                long remainingNights = nightCount - tariff.getVehicleTariff8().getDurationNight1();
                if (remainingNights >= 0) {
                    price = price + tariff.getVehicleTariff8().getCostNight1();
                    if (tariff.getVehicleTariff8().getDurationNight2() != null) {
                        price = price +
                                ((remainingNights / tariff.getVehicleTariff8().getDurationNight2()) * tariff.getVehicleTariff8().getCostNight2()) +
                                (((remainingNights % tariff.getVehicleTariff8().getDurationNight2()) * tariff.getVehicleTariff8().getCostNight2()) / tariff.getVehicleTariff8().getDurationNight2());

                        price = price + HourlyFareCalculator.calculateStepFareVehicle8(remainingMinutes, tariff);
                        return price;
                    } else {
                        price = price +
                                ((remainingNights / tariff.getVehicleTariff8().getDurationNight1()) * tariff.getVehicleTariff8().getCostNight1()) +
                                (((remainingNights % tariff.getVehicleTariff8().getDurationNight1()) * tariff.getVehicleTariff8().getCostNight1()) / tariff.getVehicleTariff8().getDurationNight1());
                        price = price + HourlyFareCalculator.calculateStepFareVehicle8(remainingMinutes, tariff);
                        return price;
                    }
                } else {
                    price = price + ((nightCount * tariff.getVehicleTariff8().getCostNight1()) / tariff.getVehicleTariff8().getDurationNight1());
                    price = price + HourlyFareCalculator.calculateStepFareVehicle8(remainingMinutes, tariff);
                    return price;
                }


            }
        }
    }


}
