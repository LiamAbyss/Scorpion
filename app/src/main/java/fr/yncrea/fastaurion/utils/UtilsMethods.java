package fr.yncrea.fastaurion.utils;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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
        data = data.replace("\\","");
        data = data.replace(" ","");
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

    public static List<Course> JSONArrayToCourseList(JSONArray JSONCourses) throws JSONException {
        JSONObject currentCourse;
        Course course = new Course();
        List<Course> planning = new ArrayList<>();

        for(int i = 0; i < JSONCourses.length(); i++){
            currentCourse = JSONCourses.getJSONObject(i);
            course.title = currentCourse.getString("title");
            course.start = currentCourse.getString("start");
            course.end = currentCourse.getString("end");
            course.course_type = currentCourse.getString("className");
            planning.add(course);
        }
        return planning;
    }
}
