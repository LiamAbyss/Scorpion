package fr.yncrea.fastaurion.utils;

import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Course {
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getCourseType() {
        return course_type;
    }

    public void setCourse_type(String course_type) {
        this.course_type = course_type;
    }

    @SerializedName("title")
    public String title;

    @SerializedName("start")
    public String start;

    @SerializedName("end")
    public String end;

    @SerializedName("course_type")
    public String course_type;

    public String toString(){
        return this.title + ";" + this.start + ";" + this.end + ";" + this.course_type + ";";
    }

    public static Course fromString(String course){
        Course newCourse = new Course();
        String[] splitString = course.split(";");
        if(splitString.length == 0) return null;
        newCourse.title = splitString[0];
        newCourse.start = splitString[1];
        newCourse.end = splitString[2];
        newCourse.course_type = splitString[3];
        return newCourse;
    }

}
