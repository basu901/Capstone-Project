package com.example.shaunakbasu.capstone;

import android.util.FloatMath;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by shaunak basu on 15-11-2016.
 */
public class Utility {

    final static String DATE_FORMAT = "MM/dd/yyyy";

    public static String getBMIDetails(float val){
        String bmi;
        if(val<18.5){
            bmi="(Under Weight)";
        }

        else if(val>=18.5&&val<25) {
            bmi = "(Normal)";
        }
        else if(val>=25&&val<=30){
            bmi="(Over Weight)";
        }
        else {
            bmi = "(Obese)";
        }

        return bmi;
    }

    public static double distance(double lat1, double lon1, double lat2, double lon2){
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515*1000*1.34;
        return (dist);
    }

    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }
    private static double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    public static double distFrom(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double dist = (double) (earthRadius * c);

        return dist;
    }



    public static String twoPlaceConverter(String number){
        String formatted_number;
        int index;
        if(number.indexOf('.')>-1){
            index=number.indexOf('.');
            String checker=number.substring(index+1,number.length());

            if(checker.length()>2){
                String front=number.substring(0,index+1);
                String sub=number.substring(index+3,index+4);
                if(Integer.parseInt(sub)>=5){
                    int n_num=Integer.parseInt(number.substring(index+1,index+3))+1;
                    formatted_number=front+Integer.toString(n_num);
                }
                else{
                    formatted_number=number.substring(0,index+3);
                }
                return formatted_number;
            }
            else
                return number;
        }
        else
            return number;
    }

    public static boolean dateChecker(String date){
        Calendar calendar=Calendar.getInstance();
        int year=calendar.get(Calendar.YEAR);
        try{
            String[] dob_checker=date.split("/");
            int yr=Integer.parseInt(dob_checker[2]);

            DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
            dateFormat.setLenient(false);
            dateFormat.parse(date);
            if(yr>year){
                return false;
            }

        }catch(Exception e){
            return false;
        }
        return true;
    }

    public static boolean hasOnlyAlphabets(String s){
        return s.matches("[a-zA-Z]+") && s.length() > 2;
    }

    public static ArrayList<Integer> foodItem(String s){
        Log.v("CHECKING FOOD",s);
        ArrayList<Integer> index=new ArrayList<>();
        for(int i=0;i<s.length();i++){
            char ch = s.charAt(i);
            if (Character.isLetter(ch)) {
                continue;
            }
            if(ch==' ') {
                Log.v("THIS IS A SPACE",Integer.toString(i));
                index.add(i);
            }
            else{Log.v("THIS IS NOT ENTERED!","!");
                index.clear();
                return index;
            }

        }
        index.add(-1);
        return index;
    }

    public static String noSpacesFood(String s,ArrayList<Integer> spaces){
        int last_index=0;
        String fin="";
        int j=0;

        for(int i=0;i<spaces.size();i++){
            last_index=spaces.get(i);
            while(j<last_index){
                 fin+=s.charAt(j);
                 j++;
            }
            j=last_index+1;
        }

        fin=fin+s.substring(last_index+1,s.length());
        return fin;
    }

}
