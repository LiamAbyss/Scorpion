package fr.yncrea.scorpion.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import fr.arnaudguyon.xmltojsonlib.XmlToJson;
import fr.yncrea.scorpion.model.Course;
import fr.yncrea.scorpion.model.CourseDetails;
import fr.yncrea.scorpion.model.Grade;
import fr.yncrea.scorpion.model.Person;

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
            course.id = -1l;
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
            course.id = currentCourse.getLong("id");
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

    public static List<CourseDetails> planningFromString(String planningString) {
        if(null == planningString || planningString.length() == 0){
            return new ArrayList<>();
        }
        String[] coursesString = planningString.split(";END_OF_SCORPION_LINE;");
        List<CourseDetails> courses = new ArrayList<>();
        for(String course : coursesString){
            courses.add(CourseDetails.fromString(course));
        }
        return courses;
    }

    public static String planningToString(List<CourseDetails> planning) {
        StringBuilder planningString = new StringBuilder();
        for(CourseDetails c : planning){
            planningString.append(c.toString()
                    .replace("&nbsp;", " ")
                    .replace("&quot;", "\"")
                    .replace("&apos;", "'")
                    .replace("&#39;", "'")
                    .replace("&amp;", "&")
                    .replace("&gt;", ">")
                    .replace("&lt;", "<")
            ).append(";END_OF_SCORPION_LINE;");
        }
        return planningString.toString();
    }

    public static List<Grade> parseGrades(String s){
        String from = "<tr data-ri=\"0\" class=\"ui-widget-content ui-datatable-even CursorInitial\" role=\"row\">";
        String to = "</tr>]]>";

        int endFromIndex = s.indexOf(from);
        s = s.substring(endFromIndex, s.indexOf(to, endFromIndex));

        String[] data = s.split("</tr>");

        ArrayList<Grade> grades = new ArrayList<Grade>();
        for(int i = 0; i < data.length; i++){
            grades.add(parseGrade(data[i]));
        }
        return grades;
    }

    public static Grade parseGrade(String s) {
        String to = "<td role=\"gridcell\"";

        s = s.substring(s.indexOf(to));
        String[] data = s.split("</td>");
        String from = "<span class=\"preformatted \">";
        to = "</span>";
        for(int i = 0; i < 6; i++) {
            if(data[i].contains(from)) {
                data[i] = data[i].substring(data[i].indexOf(from) + from.length(), data[i].indexOf(to));
            }
            else data[i] = "";
        }
        return new Grade(data[0], data[1], data[2], data[3], data[4], data[5]);
    }
    public static List<Grade> sortGradesByDate(List<Grade> grades) {
        return sortGradesByDate(grades, false);
    }
    public static List<Grade> sortGradesByDate(List<Grade> grades, boolean reversed) {
        Collections.sort(grades, (o1, o2) -> o1.date.compareTo(o2.date));
        if(reversed)
            Collections.reverse(grades);
        return grades;
    }

    public static CourseDetails parseCourseDetails(String s) {
        CourseDetails details = new CourseDetails();

        // DATE
        String from = "Du</span></label></td><td role=\"gridcell\" class=\"ui-panelgrid-cell\">";
        String to = "</td>";
        int endFromIndex = s.indexOf(from) + from.length();
        if(endFromIndex < 0 || s.contains("<changes><update id=\"form:confirmerSuppression\">") || s.indexOf(to, endFromIndex) < 0) return details;
        details.longDate = s.substring(endFromIndex, s.indexOf(to, endFromIndex));

        // START
        from = "Du</span></label></td><td role=\"gridcell\" class=\"ui-panelgrid-cell\">";
        to = "Au</span></label></td><td role=\"gridcell\" class=\"ui-panelgrid-cell\">";
        endFromIndex = s.indexOf(from) + from.length();
        String zoomStr = s.substring(endFromIndex, s.indexOf(to, endFromIndex));

        from = "à</span></label></td><td role=\"gridcell\" class=\"ui-panelgrid-cell\">";
        to = "</td>";
        endFromIndex = zoomStr.indexOf(from) + from.length();
        details.timeStart = zoomStr.substring(endFromIndex, zoomStr.indexOf(to, endFromIndex));

        // END
        from = "Au</span></label></td><td role=\"gridcell\" class=\"ui-panelgrid-cell\">";
        to = "</tr></tbody></table>";
        endFromIndex = s.indexOf(from) + from.length();
        zoomStr = s.substring(endFromIndex, s.indexOf(to, endFromIndex));

        from = "à</span></label></td><td role=\"gridcell\" class=\"ui-panelgrid-cell\">";
        to = "</td>";
        endFromIndex = zoomStr.indexOf(from) + from.length();
        details.timeEnd = zoomStr.substring(endFromIndex, zoomStr.indexOf(to, endFromIndex));

        // STATUS
        from = "Statut</span></label></div><div class=\"ui-panelgrid-cell ui-grid-col-6\">";
        to = "</div>";
        endFromIndex = s.indexOf(from) + from.length();
        details.status = s.substring(endFromIndex, s.indexOf(to, endFromIndex));

        // TOPIC
        from = "Matière</span></label></div><div class=\"ui-panelgrid-cell ui-grid-col-6\">";
        to = "</div>";
        endFromIndex = s.indexOf(from) + from.length();
        details.topic = s.substring(endFromIndex, s.indexOf(to, endFromIndex));

        // TYPE
        from = "Type d'enseignement</span></label></div><div class=\"ui-panelgrid-cell ui-grid-col-6\">";
        to = "</div>";
        endFromIndex = s.indexOf(from) + from.length();
        details.type = s.substring(endFromIndex, s.indexOf(to, endFromIndex));

        // DESCRIPTION
        from = "Description</span></label></div><div class=\"ui-panelgrid-cell ui-grid-col-6\">";
        to = "</div>";
        endFromIndex = s.indexOf(from) + from.length();
        details.description = s.substring(endFromIndex, s.indexOf(to, endFromIndex));
        details.description = details.description.replaceAll("\\r", "");
        details.description = details.description.replaceAll("(\\n)+\\Z", "");
        details.description = details.description.replaceAll("\\A(\\n)+", "");
        details.description = details.description.replaceAll("(\\w)(\\n)+(\\w)", "$1 $3");

        // EXAMSTATUS
        from = "Est une épreuve</span></label></div><div class=\"ui-panelgrid-cell ui-grid-col-6\">";
        to = "</div>";
        endFromIndex = s.indexOf(from) + from.length();
        details.examStatus = s.substring(endFromIndex, s.indexOf(to, endFromIndex));

        // ROOMCODE
        from = "Code</span></th><th id=\"form:onglets:j_idt163:j_idt167\" class=\"ui-state-default\" role=\"columnheader\" aria-label=\"Libellé\" scope=\"col\"><span class=\"ui-column-title\">Libellé</span></th></tr></thead><tbody id=\"form:onglets:j_idt163_data\" class=\"ui-datatable-data ui-widget-content\"><tr data-ri=\"0\" class=\"ui-widget-content ui-datatable-even\" role=\"row\"><td role=\"gridcell\">";
        to = "</td>";
        endFromIndex = s.indexOf(from) + from.length();
        if(s.contains(from)) {
            details.roomCode = s.substring(endFromIndex, s.indexOf(to, endFromIndex));
        }
        else details.roomCode = "Aucun enregistrement";

        // ROOM
        if(details.roomCode != "Aucun enregistrement") {
            from = "Code</span></th><th id=\"form:onglets:j_idt163:j_idt167\" class=\"ui-state-default\" role=\"columnheader\" aria-label=\"Libellé\" scope=\"col\"><span class=\"ui-column-title\">Libellé</span></th></tr></thead><tbody id=\"form:onglets:j_idt163_data\" class=\"ui-datatable-data ui-widget-content\"><tr data-ri=\"0\" class=\"ui-widget-content ui-datatable-even\" role=\"row\"><td role=\"gridcell\">"
                    + details.roomCode
                    + "</td><td role=\"gridcell\">";
            to = "</td>";
            endFromIndex = s.indexOf(from) + from.length();
            details.room = s.substring(endFromIndex, s.indexOf(to, endFromIndex));
        }
        else details.room = "";

        // TEACHERS
        details.teachers = new ArrayList<Person>();
        from = "Prénom</span></th></tr></thead><tbody id=\"form:onglets:j_idt171_data\" class=\"ui-datatable-data ui-widget-content\">";
        to = "</tbody></table>";
        endFromIndex = s.indexOf(from) + from.length();
        zoomStr = s.substring(endFromIndex, s.indexOf(to, endFromIndex));

        if(!zoomStr.contains("Aucun enregistrement")) {
            String[] teachers = zoomStr.split("<tr data-ri=\"[0-9]+\" class=\"ui-widget-content ui-datatable-(even|odd)\" role=\"row\">");
            for (String t : teachers) {
                if (t.isEmpty()) continue;
                Person p = new Person();
                String[] data = t.split("<td role=\"gridcell\">");
                if(data.length != 3) continue;
                p.lastName = data[1].substring(0, data[1].indexOf("</td>"));
                p.firstName = data[2].substring(0, data[2].indexOf("</td>"));
                details.teachers.add(p);
            }
        }

        // STUDENTS
        details.students = new ArrayList<Person>();
        from = "Prénom</span></th></tr></thead><tbody id=\"form:onglets:apprenantsTable_data\" class=\"ui-datatable-data ui-widget-content\">";
        to = "</tbody></table>";
        endFromIndex = s.indexOf(from) + from.length();
        zoomStr = s.substring(endFromIndex, s.indexOf(to, endFromIndex));

        String[] students = zoomStr.split("<tr data-ri=\"[0-9]+\" class=\"ui-widget-content ui-datatable-(even|odd)\" role=\"row\">");
        for (String st : students) {
            if(st.isEmpty()) continue;
            Person p = new Person();
            String[] data = st.split("<td role=\"gridcell\">");
            p.lastName = data[1].substring(0, data[1].indexOf("</td>"));
            p.firstName = data[2].substring(0, data[2].indexOf("</td>"));
            details.students.add(p);
        }

        // GROUPS
        details.groups = new ArrayList<String>();
        from = "<tbody id=\"form:onglets:j_idt210_data\" class=\"ui-datatable-data ui-widget-content\">";
        to = "</tbody></table>";
        endFromIndex = s.indexOf(from) + from.length();
        zoomStr = s.substring(endFromIndex, s.indexOf(to, endFromIndex));

        String[] groups = zoomStr.split("<tr data-ri=\"[0-9]+\" class=\"ui-widget-content ui-datatable-(even|odd)\" role=\"row\">");
        for (String g : groups) {
            if(g.isEmpty()) continue;
            String[] data = g.split("<td role=\"gridcell\">");
            details.groups.add(data[1].substring(0, data[1].indexOf("</td>")));
        }

        // COURSE NAME
        from = "<tbody id=\"form:onglets:j_idt215_data\" class=\"ui-datatable-data ui-widget-content\"><tr data-ri=\"0\" class=\"ui-widget-content ui-datatable-even\" role=\"row\"><td role=\"gridcell\">";
        to = "</td>";
        endFromIndex = s.indexOf(from) + from.length();
        if(s.contains(from)) {
            details.course = s.substring(endFromIndex, s.indexOf(to, endFromIndex));
        }
        else details.course = "Aucun enregistrement";

        // MODULE
        if(details.course != "Aucun enregistrement") {
            from = "<tbody id=\"form:onglets:j_idt215_data\" class=\"ui-datatable-data ui-widget-content\"><tr data-ri=\"0\" class=\"ui-widget-content ui-datatable-even\" role=\"row\"><td role=\"gridcell\">"
                    + details.course
                    + "</td><td role=\"gridcell\">";
            to = "</td>";
            endFromIndex = s.indexOf(from) + from.length();
            details.moduleContext = s.substring(endFromIndex, s.indexOf(to, endFromIndex));
        }
        else details.moduleContext = "";

        return details;
    }
}
