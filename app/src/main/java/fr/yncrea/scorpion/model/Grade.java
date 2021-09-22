package fr.yncrea.scorpion.model;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Grade {

    public Grade(String _date, String _code, String _libelle, String _note, String _coeff, String _appreciation){
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE);
        try {
            date = df.parse(_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        code = _code;
        libelle = _libelle;
        note = _note;
        coeff = _coeff;
        appreciation = _appreciation;
    }

    public Date date;

    public String code;

    public String libelle;

    public String note;

    public String coeff;

    public String appreciation;

}
