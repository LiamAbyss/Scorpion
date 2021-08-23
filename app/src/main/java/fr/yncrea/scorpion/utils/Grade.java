package fr.yncrea.scorpion.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Grade {

    public Grade(String _date, String _code, String _libelle, Float _note, String _reason, String _appreciation, String _teachers){
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE);
        try {
            date = df.parse(_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        code = _code;
        libelle = _libelle;
        note = _note;
        reasonForAbsence = _reason;
        appreciation = _appreciation;
        teachers = _teachers;
    }

    public Date date;

    public String code;

    public String libelle;

    public Float note;

    public String reasonForAbsence;

    public String appreciation;

    public String teachers;
}
