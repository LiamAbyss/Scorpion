package fr.yncrea.scorpion.model;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CourseDetails {

    public Long id = -1L;

    public String date = "Aucun enregistrement";

    public String dateStart = "Aucun enregistrement";

    public String dateEnd = "Aucun enregistrement";

    public String longDate = "Aucun enregistrement";

    public String timeStart = "Aucun enregistrement";

    public String timeEnd = "Aucun enregistrement";

    public String status = "Aucun enregistrement";

    public String roomCode = "Aucun enregistrement";

    public String room = "Aucun enregistrement";

    public String topic = "Aucun enregistrement";

    public String type = "Aucun enregistrement";

    public String description = "Aucun enregistrement";

    public String examStatus = "Aucun enregistrement";

    public List<Person> teachers = new ArrayList<Person>();

    public List<Person> students = new ArrayList<Person>();

    public List<String> groups = new ArrayList<String>();

    public String course = "Aucun enregistrement";

    public String moduleContext = "Aucun enregistrement";

    public CourseDetails() {

    }

    public CourseDetails(@NonNull CourseDetails details) {
        id = details.id;
        date = details.date;
        dateStart = details.dateStart;
        dateEnd = details.dateEnd;
        longDate = details.longDate;
        timeStart = details.timeStart;
        timeEnd = details.timeEnd;
        status = details.status;
        roomCode = details.roomCode;
        room = details.room;
        topic = details.topic;
        type = details.type;
        description = details.description;
        examStatus = details.examStatus;
        teachers = details.teachers;
        students = details.students;
        groups = details.groups;
        course = details.course;
        moduleContext = details.moduleContext;
    }

    public String toString() {
        StringBuilder res = new StringBuilder();
        res.append(id + ";"
                + course + ";"
                + date + ";"
                + dateStart + ";"
                + dateEnd + ";"
                + longDate + ";"
                + timeStart + ";"
                + timeEnd + ";"
                + status + ";"
                + roomCode + ";"
                + room + ";"
                + topic + ";"
                + type + ";"
                + description + ";"
                + examStatus + ";"
                + moduleContext + ";");

        for(int i = 0; i < teachers.size(); i++) {
            if(i != 0) res.append(",");
            res.append(teachers.get(i).firstName + "_" + teachers.get(i).lastName);
        }
        res.append(";");
        for(int i = 0; i < students.size(); i++) {
            if(i != 0) res.append(",");
            res.append(students.get(i).firstName + "_" + students.get(i).lastName);
        }
        res.append(";");
        for(int i = 0; i < groups.size(); i++) {
            if(i != 0) res.append(",");
            res.append(groups.get(i));
        }

        return res.toString();
    }

    public static CourseDetails fromString(String s) {
        CourseDetails res = new CourseDetails();

        String[] data = s.split(";");
        if(data.length != 19) return res;

        res.id = Long.parseLong(data[0]);
        res.course = data[1];
        res.date = data[2];
        res.dateStart = data[3];
        res.dateEnd = data[4];
        res.longDate = data[5];
        res.timeStart = data[6];
        res.timeEnd = data[7];
        res.status = data[8];
        res.roomCode = data[9];
        res.room = data[10];
        res.topic = data[11];
        res.type = data[12];
        res.description = data[13];
        res.examStatus = data[14];
        res.moduleContext = data[15];

        // TEACHERS
        String[] array = data[16].split(",");
        if(array[0].contains("_")) {
            for (int i = 0; i < array.length; i++) {
                Person p = new Person();
                p.firstName = array[i].split("_")[0];
                p.lastName = array[i].split("_")[1];
                res.teachers.add(p);
            }
        }

        // Students
        array = data[17].split(",");
        for(int i = 0; i < array.length; i++) {
            Person p = new Person();
            p.firstName = array[i].split("_")[0];
            p.lastName = array[i].split("_")[1];
            res.students.add(p);
        }

        // GROUPS
        res.groups =  new ArrayList<String>(Arrays.asList(data[18].split(",")));

        return res;
    }
}
