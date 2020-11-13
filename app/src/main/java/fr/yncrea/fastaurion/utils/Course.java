package fr.yncrea.fastaurion.utils;

import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Course {
    @SerializedName("title")
    public String title;

    @SerializedName("start")
    public String start;

    @SerializedName("end")
    public String end;

    @SerializedName("course_type")
    public String course_type;

    public Date stringToDate(String dateString){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        try {
            return format.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

}
