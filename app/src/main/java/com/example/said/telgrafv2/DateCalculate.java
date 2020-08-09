package com.example.said.telgrafv2;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateCalculate {

    public static Date currentDate=new Date();
    final static long MILISECONDSTHREEHOUR=10800000;

    public static String calculate(String date){

        try {

            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            Date timeInService=simpleDateFormat.parse(date);

            timeInService.setTime(timeInService.getTime()+MILISECONDSTHREEHOUR);

            Calendar calendar=Calendar.getInstance();
            calendar.setTime(timeInService);
            int inServiceYear=calendar.get(Calendar.YEAR);
            int inServiceMonth=calendar.get(Calendar.MONTH);
            int inServiceDay=calendar.get(Calendar.DAY_OF_MONTH);

            long betweenTimeMilisecond=currentDate.getTime()-timeInService.getTime();
            long betweenTime=betweenTimeMilisecond/1000;

            if (betweenTime / 60 < 1) {
                return "hemen şimdi";
            } else if (betweenTime / 60 <= 59) {
                betweenTime = betweenTime / 60;
                return betweenTime + " dk";
            } else if (betweenTime / 3600 <= 23) {
                betweenTime = betweenTime / 3600;
                return betweenTime + " saat";
            } else if (betweenTime / (3600 * 24) <= 6) {
                betweenTime = betweenTime / (3600 * 24);
                return betweenTime + " gün";
            } else if(betweenTime / (3600 * 24 * 7) < 4){
                betweenTime = betweenTime / (3600 * 24 * 7);
                return  betweenTime+ " hafta";
            }else{
                return inServiceDay+"."+inServiceMonth+"."+inServiceYear;
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "hata";
    }
    public static String messageHour(String date){

        String hour="ss" ,minute="dd";

        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        Date timeInService= null;
        try {
            timeInService = simpleDateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        timeInService.setTime(timeInService.getTime()+MILISECONDSTHREEHOUR);

        Calendar calendar=Calendar.getInstance();
        calendar.setTime(timeInService);

        int inServiceHour=calendar.get(Calendar.HOUR_OF_DAY);
        int inServiceMinute=calendar.get(Calendar.MINUTE);


        if(inServiceHour<10){
            hour="0"+inServiceHour;
        }else{
            hour=String.valueOf(inServiceHour);
        }

        if(inServiceMinute<10){
            minute="0"+inServiceMinute;
        }else{
            minute=String.valueOf(inServiceMinute);
        }

        return hour+":"+minute;
    }
}
