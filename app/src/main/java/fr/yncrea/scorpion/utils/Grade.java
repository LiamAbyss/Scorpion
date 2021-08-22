package fr.yncrea.scorpion.utils;

public class Grade {

    public Grade(String _date, String _code, String _libelle, Float _note, String _reason, String _appreciation, String _teachers){
        date = _date;
        code = _code;
        libelle = _libelle;
        note = _note;
        reasonForAbsence = _reason;
        appreciation = _appreciation;
        teachers = _teachers;
    }

    public String date;

    public String code;

    public String libelle;

    public Float note;

    public String reasonForAbsence;

    public String appreciation;

    public String teachers;
}
