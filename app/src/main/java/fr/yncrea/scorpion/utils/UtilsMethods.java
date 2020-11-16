package fr.yncrea.scorpion.utils;

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
                dateFormat.applyPattern("EEEE");
                course.day = dateFormat.format(startDate);
            }
            catch (ParseException e){
                e.printStackTrace();
            }
            planning.add(course);
        }
        return planning;
    }
}
