package com.pauphilet_romero.destagram.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Fonctions spécifiques aux dates
 * Created by Jimmy on 25/11/2014.
 */
public class DateFunctions {

    /**
     * Affichage de la date
     * @param date
     * @return
     */
    public static String diffDate(Date date) {
        Date dateToday = new Date();
        long today = dateToday.getTime();
        long diff = (today -  date.getTime()) / (60*60*1000);

        if (diff < 24) {
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
            return formatter.format(date);
        } else {
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/y - HH:mm");
            return formatter.format(date);
        }
    }

    /***
     * Conversion de la date de String à Date
     * @param date
     * @return
     */
    public static Date dateConvert(String date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return formatter.parse(date);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

}
