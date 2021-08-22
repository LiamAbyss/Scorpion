package fr.yncrea.scorpion.utils;

import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import fr.arnaudguyon.xmltojsonlib.XmlToJson;
import fr.yncrea.scorpion.ScorpionApplication;

public class UtilsMethods {

    public static JSONArray XMLToJSONArray(String xmlString){
        XmlToJson Json = new XmlToJson.Builder(xmlString).build();
        JSONObject JSON = Json.toJson();
        String data = "";
        try {
            if (JSON != null) {
                data = JSON.getJSONObject("partial-response").getJSONObject("changes").getJSONArray("update").getJSONObject(1).get("content").toString();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        data = data.replace("\\n","\n");
        data = data.replace("\u200B","");
        JSONObject events = null;
        try {
            events = new JSONObject(data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            if (events != null) {
                return events.getJSONArray("events");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static List<Course> JSONArrayToCourseList(JSONArray coursesJSON) throws JSONException {
        if(coursesJSON == null) return new ArrayList<>();
        JSONObject currentCourse;
        List<Course> planning = new ArrayList<>();
        if(coursesJSON.length() == 0) {
            Course course = new Course();
            course.title = "\u200B";
            course.start = "\u200B";
            course.end = "\u200B";
            course.course_type = "\u200B";
            course.date = "\u200B";
            planning.add(course);
        }
        String lastDay = "";
        for(int i = 0; i < coursesJSON.length(); i++){
            Course course = new Course();
            currentCourse = coursesJSON.getJSONObject(i);
            course.title = currentCourse.getString("title");
            course.start = currentCourse.getString("start");
            course.end = currentCourse.getString("end");
            course.course_type = currentCourse.getString("className");

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssZ", Locale.FRANCE);
            try{
                Date startDate = dateFormat.parse(course.start);
                dateFormat.applyPattern("EEEE dd MMMM");
                course.date = dateFormat.format(startDate);
                if(course.date.equals(lastDay)) {
                    course.date = "\u200B";
                }
                else {
                    lastDay = course.date;
                }
            }
            catch (ParseException e){
                e.printStackTrace();
            }
            planning.add(course);
        }
        return planning;
    }

    public static List<Course> planningFromString(String planningString) {
        if(null == planningString || planningString.length() == 0){
            return new ArrayList<>();
        }
        String[] coursesString = planningString.split(";END_OF_SCORPION_LINE;");
        List<Course> courses = new ArrayList<>();
        for(String course : coursesString){
            try {
                courses.add(Course.fromString(course));
            }
            catch (ParseException e){
                e.printStackTrace();
            }
        }
        return courses;
    }

    public static String planningToString(List<Course> planning) {
        StringBuilder planningString = new StringBuilder();
        for(Course c : planning){
            planningString.append(c.toString()).append(";END_OF_SCORPION_LINE;");
        }
        return planningString.toString();
    }

    public static List<Grade> parseGrades(String s){
        s = "</tbody><tbody id=\"form:dataTableFavori_data\" class=\"ui-datatable-data ui-widget-content\"><tr data-ri=\"0\" class=\"ui-widget-content ui-datatable-even CursorInitial\" role=\"row\"><td role=\"gridcell\">11/10/2019</td><td role=\"gridcell\" style=\"\">1920_ISEN_CIR3_S1_ANGLAIS_EVAL</td><td role=\"gridcell\">Evaluation CIR3 Anglais S1</td><td role=\"gridcell\">18.15</td><td role=\"gridcell\"></td><td role=\"gridcell\"></td><td role=\"gridcell\">LITTON Evelyne</td></tr><tr data-ri=\"1\" class=\"ui-widget-content ui-datatable-odd CursorInitial\" role=\"row\"><td role=\"gridcell\">15/10/2019</td><td role=\"gridcell\" style=\"\">1920_ISEN_CIR3_S1_APM_EVAL</td><td role=\"gridcell\">Evaluation Ateliers Préparatoires Mathématiques</td><td role=\"gridcell\">16.30</td><td role=\"gridcell\"></td><td role=\"gridcell\"></td><td role=\"gridcell\">CHENEVERT Gabriel</td></tr><tr data-ri=\"2\" class=\"ui-widget-content ui-datatable-even CursorInitial\" role=\"row\"><td role=\"gridcell\">01/10/2019</td><td role=\"gridcell\" style=\"\">1920_ISEN_CIR3_S1_COMMUNICATION_PROJET</td><td role=\"gridcell\">Evaluation CIR3 Projet Communication</td><td role=\"gridcell\">16.80</td><td role=\"gridcell\"></td><td role=\"gridcell\"></td><td role=\"gridcell\"></td></tr><tr data-ri=\"3\" class=\"ui-widget-content ui-datatable-odd CursorInitial\" role=\"row\"><td role=\"gridcell\">12/11/2019</td><td role=\"gridcell\" style=\"\">1920_ISEN_CIR3_S1_ELEC_DS</td><td role=\"gridcell\">Devoir Surveillé Electronique</td><td role=\"gridcell\">16.00</td><td role=\"gridcell\"></td><td role=\"gridcell\"></td><td role=\"gridcell\">STEFANELLI Bruno</td></tr></tbody>";
        String from = "<tbody id=\"form:dataTableFavori_data\" class=\"ui-datatable-data ui-widget-content\">";
        String to = "</tbody>";

        int endFromIndex = s.indexOf(from) + from.length();
        s = s.substring(endFromIndex, s.indexOf(to, endFromIndex));

        String[] data = s.split("</tr>");

        ArrayList<Grade> grades = new ArrayList<Grade>();
        for(int i = 0; i < data.length; i++){
            grades.add(parseGrade(data[i]));
        }
        return new ArrayList<Grade>();
    }

    public static Grade parseGrade(String s){
        //s = "<tr data-ri=\"0\" class=\"ui-widget-content ui-datatable-even CursorInitial\" role=\"row\"><td role=\"gridcell\">11/10/2019</td><td role=\"gridcell\" style=\"\">1920_ISEN_CIR3_S1_ANGLAIS_EVAL</td><td role=\"gridcell\">Evaluation CIR3 Anglais S1</td><td role=\"gridcell\">18.15</td><td role=\"gridcell\"></td><td role=\"gridcell\"></td><td role=\"gridcell\">LITTON Evelyne</td>";
        String from = "<tr data-ri=\"0\" class=\"ui-widget-content ui-datatable-even CursorInitial\" role=\"row\">";
        s = s.substring(s.indexOf(from) + from.length()).replaceAll(" style=\\\"\\\"", "");
        String[] data = s.split("</td>");
        from = "<td role=\"gridcell\">";
        for(int i = 0; i < 7; i++){
            data[i] = data[i].substring(data[i].indexOf(from) + from.length());
        }
        return new Grade(data[0], data[1], data[2], Float.parseFloat(data[3]), data[4], data[5], data[6]);
    }
}
