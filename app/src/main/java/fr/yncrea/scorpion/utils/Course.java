package fr.yncrea.scorpion.utils;

import com.google.gson.annotations.SerializedName;

import java.text.ParseException;

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

    @SerializedName("day")
    public String date;

    public String getDate() {
        return date;
    }

    @SerializedName("start")
    public String start;

    @SerializedName("end")
    public String end;

    @SerializedName("course_type")
    public String course_type;

    public String toString(){
        return title + ";" + start + ";" + end + ";" + course_type + ";" + date;
    }

    public static Course fromString(String course) throws ParseException {
        Course newCourse = new Course();
        String[] splitString = course.split(";");
        if(splitString.length < 5) return null;
        newCourse.title = splitString[0];
        newCourse.start = splitString[1];
        newCourse.end = splitString[2];
        newCourse.course_type = splitString[3];
        newCourse.date = splitString[4];
        return newCourse;
    }

}
