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
            planningString.append(c.toString()).append(";END_OF_SCORPION_LINE;");
        }
        return planningString.toString();
    }

    public static List<Grade> parseGrades(String s){
        //s = "</tbody><tbody id=\"form:dataTableFavori_data\" class=\"ui-datatable-data ui-widget-content\"><tr data-ri=\"0\" class=\"ui-widget-content ui-datatable-even CursorInitial\" role=\"row\"><td role=\"gridcell\">11/10/2019</td><td role=\"gridcell\" style=\"\">1920_ISEN_CIR3_S1_ANGLAIS_EVAL</td><td role=\"gridcell\">Evaluation CIR3 Anglais S1</td><td role=\"gridcell\">18.15</td><td role=\"gridcell\"></td><td role=\"gridcell\"></td><td role=\"gridcell\">LITTON Evelyne</td></tr><tr data-ri=\"1\" class=\"ui-widget-content ui-datatable-odd CursorInitial\" role=\"row\"><td role=\"gridcell\">15/10/2019</td><td role=\"gridcell\" style=\"\">1920_ISEN_CIR3_S1_APM_EVAL</td><td role=\"gridcell\">Evaluation Ateliers Préparatoires Mathématiques</td><td role=\"gridcell\">16.30</td><td role=\"gridcell\"></td><td role=\"gridcell\"></td><td role=\"gridcell\">CHENEVERT Gabriel</td></tr><tr data-ri=\"2\" class=\"ui-widget-content ui-datatable-even CursorInitial\" role=\"row\"><td role=\"gridcell\">01/10/2019</td><td role=\"gridcell\" style=\"\">1920_ISEN_CIR3_S1_COMMUNICATION_PROJET</td><td role=\"gridcell\">Evaluation CIR3 Projet Communication</td><td role=\"gridcell\">16.80</td><td role=\"gridcell\"></td><td role=\"gridcell\"></td><td role=\"gridcell\"></td></tr><tr data-ri=\"3\" class=\"ui-widget-content ui-datatable-odd CursorInitial\" role=\"row\"><td role=\"gridcell\">12/11/2019</td><td role=\"gridcell\" style=\"\">1920_ISEN_CIR3_S1_ELEC_DS</td><td role=\"gridcell\">Devoir Surveillé Electronique</td><td role=\"gridcell\">16.00</td><td role=\"gridcell\"></td><td role=\"gridcell\"></td><td role=\"gridcell\">STEFANELLI Bruno</td></tr></tbody>";
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
        /*s = "<?xml version='1.0' encoding='UTF-8'?>\n" +
                "<partial-response id=\"j_id1\"><changes><update id=\"form:messages\"><![CDATA[<div id=\"form:messages\" class=\"ui-messages ui-widget\" aria-live=\"polite\"></div>]]></update><update id=\"form:modaleDetail\"><![CDATA[<div id=\"form:modaleDetail\" class=\"ui-dialog ui-widget ui-widget-content ui-corner-all ui-shadow ui-hidden-container modale-detail\"><div class=\"ui-dialog-titlebar ui-widget-header ui-helper-clearfix ui-corner-top\"><span id=\"form:modaleDetail_title\" class=\"ui-dialog-title\"></span><a href=\"#\" class=\"ui-dialog-titlebar-icon ui-dialog-titlebar-close ui-corner-all\" aria-label=\"Close\"><span class=\"ui-icon ui-icon-closethick\"></span></a></div><div class=\"ui-dialog-content ui-widget-content\"><div id=\"form:j_idt133\" class=\"ui-messages ui-widget\" aria-live=\"polite\"></div><div id=\"form:boutonsAbsence\">\n" +
                "                        <p>\n" +
                "                        </p><div style=\"float: right\"></div></div><table id=\"form:j_idt142\" class=\"ui-panelgrid ui-widget panelgrid-debut-fin\" role=\"grid\"><tbody><tr class=\"ui-widget-content\" role=\"row\"><td role=\"gridcell\" class=\"ui-panelgrid-cell\"><label class=\"label\"><span class=\"label\">Du</span></label></td><td role=\"gridcell\" class=\"ui-panelgrid-cell\">mardi 7 septembre 2021</td><td role=\"gridcell\" class=\"ui-panelgrid-cell\"><label class=\"label\"><span class=\"label\">à</span></label></td><td role=\"gridcell\" class=\"ui-panelgrid-cell\">08:30</td></tr><tr class=\"ui-widget-content\" role=\"row\"><td role=\"gridcell\" class=\"ui-panelgrid-cell\"><label class=\"label\"><span class=\"label\">Au</span></label></td><td role=\"gridcell\" class=\"ui-panelgrid-cell\">mardi 7 septembre 2021</td><td role=\"gridcell\" class=\"ui-panelgrid-cell\"><label class=\"label\"><span class=\"label\">à</span></label></td><td role=\"gridcell\" class=\"ui-panelgrid-cell\">12:15</td></tr></tbody></table><div id=\"form:j_idt151\" class=\"ui-panelgrid ui-widget panelgrid-info-epreuve\"><div id=\"form:j_idt151_content\" class=\"ui-panelgrid-content ui-widget-content ui-grid ui-grid-responsive\"><div class=\"ui-grid-row\"><div class=\"ui-panelgrid-cell ui-grid-col-6\"><label class=\"label\"><span class=\"label\">Statut</span></label></div><div class=\"ui-panelgrid-cell ui-grid-col-6\">Planifié</div></div><div class=\"ui-grid-row\"><div class=\"ui-panelgrid-cell ui-grid-col-6\"><label class=\"label\"><span class=\"label\">Matière</span></label></div><div class=\"ui-panelgrid-cell ui-grid-col-6\">Pédagogique</div></div><div class=\"ui-grid-row\"><div class=\"ui-panelgrid-cell ui-grid-col-6\"><label class=\"label\"><span class=\"label\">Type d'enseignement</span></label></div><div class=\"ui-panelgrid-cell ui-grid-col-6\">Cours magistral</div></div><div class=\"ui-grid-row\"><div class=\"ui-panelgrid-cell ui-grid-col-6\"><label class=\"ev_libelle\"><span class=\"ev_libelle\">Description</span></label></div><div class=\"ui-panelgrid-cell ui-grid-col-6\"></div></div><div class=\"ui-grid-row\"><div class=\"ui-panelgrid-cell ui-grid-col-6\"><label class=\"label\"><span class=\"label\">Est une épreuve</span></label></div><div class=\"ui-panelgrid-cell ui-grid-col-6\">Non</div></div></div></div><div id=\"form:onglets\" class=\"ui-tabs ui-widget ui-widget-content ui-corner-all ui-hidden-container ui-tabs-top ui-tabs-scrollable onglets\" data-widget=\"widget_form_onglets\"><div class=\"ui-tabs-navscroller\"><a class=\"ui-tabs-navscroller-btn ui-tabs-navscroller-btn-left ui-state-default ui-corner-right\"><span class=\"ui-icon ui-icon-carat-1-w\"></span></a><a class=\"ui-tabs-navscroller-btn ui-tabs-navscroller-btn-right ui-state-default ui-corner-left\"><span class=\"ui-icon ui-icon-carat-1-e\"></span></a><ul class=\"ui-tabs-nav ui-helper-reset ui-helper-clearfix ui-widget-header ui-corner-all\" role=\"tablist\"><li class=\"ui-state-default ui-tabs-selected ui-state-active ui-corner-top\" role=\"tab\" aria-expanded=\"true\" aria-selected=\"true\"><a href=\"#form:onglets:j_idt162\" tabindex=\"-1\">Ressources</a></li><li class=\"ui-state-default ui-corner-top\" role=\"tab\" aria-expanded=\"false\" aria-selected=\"false\"><a href=\"#form:onglets:j_idt170\" tabindex=\"-1\">Intervenants</a></li><li class=\"ui-state-default ui-corner-top\" role=\"tab\" aria-expanded=\"false\" aria-selected=\"false\"><a href=\"#form:onglets:j_idt178\" tabindex=\"-1\">Apprenants (34)</a></li><li class=\"ui-state-default ui-corner-top\" role=\"tab\" aria-expanded=\"false\" aria-selected=\"false\"><a href=\"#form:onglets:j_idt209\" tabindex=\"-1\">Groupes</a></li><li class=\"ui-state-default ui-corner-top\" role=\"tab\" aria-expanded=\"false\" aria-selected=\"false\"><a href=\"#form:onglets:j_idt214\" tabindex=\"-1\">Cours</a></li></ul></div><div class=\"ui-tabs-panels\"><div id=\"form:onglets:j_idt162\" class=\"ui-tabs-panel ui-widget-content ui-corner-bottom\" role=\"tabpanel\" aria-hidden=\"false\"><div id=\"form:onglets:j_idt163\" class=\"ui-datatable ui-widget\"><div class=\"ui-datatable-tablewrapper\"><table role=\"grid\"><thead id=\"form:onglets:j_idt163_head\"><tr role=\"row\"><th id=\"form:onglets:j_idt163:j_idt164\" class=\"ui-state-default\" role=\"columnheader\" aria-label=\"Code\" scope=\"col\"><span class=\"ui-column-title\">Code</span></th><th id=\"form:onglets:j_idt163:j_idt167\" class=\"ui-state-default\" role=\"columnheader\" aria-label=\"Libellé\" scope=\"col\"><span class=\"ui-column-title\">Libellé</span></th></tr></thead><tbody id=\"form:onglets:j_idt163_data\" class=\"ui-datatable-data ui-widget-content\"><tr data-ri=\"0\" class=\"ui-widget-content ui-datatable-even\" role=\"row\"><td role=\"gridcell\">ISEN_C601</td><td role=\"gridcell\">ISEN C601  - VidéoProj</td></tr></tbody></table></div></div><script id=\"form:onglets:j_idt163_s\" type=\"text/javascript\">$(function(){PrimeFaces.cw(\"DataTable\",\"widget_form_onglets_j_idt163\",{id:\"form:onglets:j_idt163\"});});</script></div><div id=\"form:onglets:j_idt170\" class=\"ui-tabs-panel ui-widget-content ui-corner-bottom ui-helper-hidden\" role=\"tabpanel\" aria-hidden=\"true\"><div id=\"form:onglets:j_idt171\" class=\"ui-datatable ui-widget\"><div class=\"ui-datatable-tablewrapper\"><table role=\"grid\"><thead id=\"form:onglets:j_idt171_head\"><tr role=\"row\"><th id=\"form:onglets:j_idt171:j_idt172\" class=\"ui-state-default\" role=\"columnheader\" aria-label=\"Nom\" scope=\"col\"><span class=\"ui-column-title\">Nom</span></th><th id=\"form:onglets:j_idt171:j_idt175\" class=\"ui-state-default\" role=\"columnheader\" aria-label=\"Prénom\" scope=\"col\"><span class=\"ui-column-title\">Prénom</span></th></tr></thead><tbody id=\"form:onglets:j_idt171_data\" class=\"ui-datatable-data ui-widget-content\"><tr data-ri=\"0\" class=\"ui-widget-content ui-datatable-even\" role=\"row\"><td role=\"gridcell\">IHADDADENE</td><td role=\"gridcell\">Nacim</td></tr></tbody></table></div></div><script id=\"form:onglets:j_idt171_s\" type=\"text/javascript\">$(function(){PrimeFaces.cw(\"DataTable\",\"widget_form_onglets_j_idt171\",{id:\"form:onglets:j_idt171\"});});</script></div><div id=\"form:onglets:j_idt178\" class=\"ui-tabs-panel ui-widget-content ui-corner-bottom ui-helper-hidden\" role=\"tabpanel\" aria-hidden=\"true\"><div id=\"form:onglets:divOngletApprenant\"><div id=\"form:onglets:j_idt179\"><div class=\"cacherImpression\" style=\"position: relative\"></div>\n" +
                "\n" +
                "                                    <p></p><div id=\"form:onglets:apprenantsTable\" class=\"ui-datatable ui-widget datatable-apprenant ui-datatable-reflow\"><div class=\"ui-datatable-header ui-widget-header ui-corner-top\">\n" +
                "\n" +
                "                                            <div style=\"clear: both\"></div></div><div class=\"ui-datatable-tablewrapper\"><table role=\"grid\"><thead id=\"form:onglets:apprenantsTable_head\"><tr role=\"row\"><th id=\"form:onglets:apprenantsTable:j_idt191\" class=\"ui-state-default ui-static-column\" role=\"columnheader\" aria-label=\"Nom\" scope=\"col\"><span class=\"ui-column-title\">Nom</span></th><th id=\"form:onglets:apprenantsTable:j_idt194\" class=\"ui-state-default ui-static-column\" role=\"columnheader\" aria-label=\"Prénom\" scope=\"col\"><span class=\"ui-column-title\">Prénom</span></th></tr></thead><tbody id=\"form:onglets:apprenantsTable_data\" class=\"ui-datatable-data ui-widget-content\"><tr data-ri=\"0\" class=\"ui-widget-content ui-datatable-even\" role=\"row\"><td role=\"gridcell\">ARBACHE</td><td role=\"gridcell\">Rémi</td></tr><tr data-ri=\"1\" class=\"ui-widget-content ui-datatable-odd\" role=\"row\"><td role=\"gridcell\">ASSET</td><td role=\"gridcell\">Antoine</td></tr><tr data-ri=\"2\" class=\"ui-widget-content ui-datatable-even\" role=\"row\"><td role=\"gridcell\">BERNARD</td><td role=\"gridcell\">Hadrien</td></tr><tr data-ri=\"3\" class=\"ui-widget-content ui-datatable-odd\" role=\"row\"><td role=\"gridcell\">BLASSELLE</td><td role=\"gridcell\">Thibaut</td></tr><tr data-ri=\"4\" class=\"ui-widget-content ui-datatable-even\" role=\"row\"><td role=\"gridcell\">BUCAMP</td><td role=\"gridcell\">Paul</td></tr><tr data-ri=\"5\" class=\"ui-widget-content ui-datatable-odd\" role=\"row\"><td role=\"gridcell\">CHANTREL</td><td role=\"gridcell\">Adrien</td></tr><tr data-ri=\"6\" class=\"ui-widget-content ui-datatable-even\" role=\"row\"><td role=\"gridcell\">CHOUKHI</td><td role=\"gridcell\">Imane</td></tr><tr data-ri=\"7\" class=\"ui-widget-content ui-datatable-odd\" role=\"row\"><td role=\"gridcell\">COUDON</td><td role=\"gridcell\">Jean-Charles</td></tr><tr data-ri=\"8\" class=\"ui-widget-content ui-datatable-even\" role=\"row\"><td role=\"gridcell\">COURMONT</td><td role=\"gridcell\">Gaël</td></tr><tr data-ri=\"9\" class=\"ui-widget-content ui-datatable-odd\" role=\"row\"><td role=\"gridcell\">DALMAS</td><td role=\"gridcell\">Eugénie</td></tr><tr data-ri=\"10\" class=\"ui-widget-content ui-datatable-even\" role=\"row\"><td role=\"gridcell\">DEFOORT</td><td role=\"gridcell\">Nicolas</td></tr><tr data-ri=\"11\" class=\"ui-widget-content ui-datatable-odd\" role=\"row\"><td role=\"gridcell\">DELANNOY</td><td role=\"gridcell\">Cyril</td></tr><tr data-ri=\"12\" class=\"ui-widget-content ui-datatable-even\" role=\"row\"><td role=\"gridcell\">DELATTRE</td><td role=\"gridcell\">Thibaut</td></tr><tr data-ri=\"13\" class=\"ui-widget-content ui-datatable-odd\" role=\"row\"><td role=\"gridcell\">DESITTER</td><td role=\"gridcell\">Jimmy</td></tr><tr data-ri=\"14\" class=\"ui-widget-content ui-datatable-even\" role=\"row\"><td role=\"gridcell\">DIROU</td><td role=\"gridcell\">Thibaud</td></tr><tr data-ri=\"15\" class=\"ui-widget-content ui-datatable-odd\" role=\"row\"><td role=\"gridcell\">FRANCOISE</td><td role=\"gridcell\">Arnaud</td></tr><tr data-ri=\"16\" class=\"ui-widget-content ui-datatable-even\" role=\"row\"><td role=\"gridcell\">GAYA</td><td role=\"gridcell\">Victor</td></tr><tr data-ri=\"17\" class=\"ui-widget-content ui-datatable-odd\" role=\"row\"><td role=\"gridcell\">HESEQUE</td><td role=\"gridcell\">Antoine</td></tr><tr data-ri=\"18\" class=\"ui-widget-content ui-datatable-even\" role=\"row\"><td role=\"gridcell\">HUSSON</td><td role=\"gridcell\">Laetitia</td></tr><tr data-ri=\"19\" class=\"ui-widget-content ui-datatable-odd\" role=\"row\"><td role=\"gridcell\">LECLERCQ</td><td role=\"gridcell\">Florian</td></tr><tr data-ri=\"20\" class=\"ui-widget-content ui-datatable-even\" role=\"row\"><td role=\"gridcell\">LHOTELLIER</td><td role=\"gridcell\">Théo</td></tr><tr data-ri=\"21\" class=\"ui-widget-content ui-datatable-odd\" role=\"row\"><td role=\"gridcell\">LOSFELD</td><td role=\"gridcell\">Francois</td></tr><tr data-ri=\"22\" class=\"ui-widget-content ui-datatable-even\" role=\"row\"><td role=\"gridcell\">MEGE</td><td role=\"gridcell\">Mathias</td></tr><tr data-ri=\"23\" class=\"ui-widget-content ui-datatable-odd\" role=\"row\"><td role=\"gridcell\">RAULIN</td><td role=\"gridcell\">Peter</td></tr><tr data-ri=\"24\" class=\"ui-widget-content ui-datatable-even\" role=\"row\"><td role=\"gridcell\">REINA</td><td role=\"gridcell\">Romain</td></tr><tr data-ri=\"25\" class=\"ui-widget-content ui-datatable-odd\" role=\"row\"><td role=\"gridcell\">ROUSSEAU</td><td role=\"gridcell\">Mathis</td></tr><tr data-ri=\"26\" class=\"ui-widget-content ui-datatable-even\" role=\"row\"><td role=\"gridcell\">SARTORIUS</td><td role=\"gridcell\">Ghislain</td></tr><tr data-ri=\"27\" class=\"ui-widget-content ui-datatable-odd\" role=\"row\"><td role=\"gridcell\">SOLTYSIAK</td><td role=\"gridcell\">Alexis</td></tr><tr data-ri=\"28\" class=\"ui-widget-content ui-datatable-even\" role=\"row\"><td role=\"gridcell\">VALLET</td><td role=\"gridcell\">Pierre-Louis</td></tr><tr data-ri=\"29\" class=\"ui-widget-content ui-datatable-odd\" role=\"row\"><td role=\"gridcell\">VANDENBILCKE</td><td role=\"gridcell\">Maxence</td></tr><tr data-ri=\"30\" class=\"ui-widget-content ui-datatable-even\" role=\"row\"><td role=\"gridcell\">VAN MIEGEM</td><td role=\"gridcell\">Julien</td></tr><tr data-ri=\"31\" class=\"ui-widget-content ui-datatable-odd\" role=\"row\"><td role=\"gridcell\">VASSEUR</td><td role=\"gridcell\">Adélie</td></tr><tr data-ri=\"32\" class=\"ui-widget-content ui-datatable-even\" role=\"row\"><td role=\"gridcell\">WATTELIER</td><td role=\"gridcell\">Erwan</td></tr><tr data-ri=\"33\" class=\"ui-widget-content ui-datatable-odd\" role=\"row\"><td role=\"gridcell\">XIANG</td><td role=\"gridcell\">Zhenyu</td></tr></tbody></table></div></div><script id=\"form:onglets:apprenantsTable_s\" type=\"text/javascript\">$(function(){PrimeFaces.cw(\"DataTable\",\"widget_form_onglets_apprenantsTable\",{id:\"form:onglets:apprenantsTable\",reflow:true});});</script></div></div></div><div id=\"form:onglets:j_idt209\" class=\"ui-tabs-panel ui-widget-content ui-corner-bottom ui-helper-hidden\" role=\"tabpanel\" aria-hidden=\"true\"><div id=\"form:onglets:j_idt210\" class=\"ui-datatable ui-widget\"><div class=\"ui-datatable-tablewrapper\"><table role=\"grid\"><thead id=\"form:onglets:j_idt210_head\"><tr role=\"row\"><th id=\"form:onglets:j_idt210:j_idt211\" class=\"ui-state-default\" role=\"columnheader\" aria-label=\"Libellé\" scope=\"col\"><span class=\"ui-column-title\">Libellé</span></th></tr></thead><tbody id=\"form:onglets:j_idt210_data\" class=\"ui-datatable-data ui-widget-content\"><tr data-ri=\"0\" class=\"ui-widget-content ui-datatable-even\" role=\"row\"><td role=\"gridcell\">3ème année de Cycle Ingénieur (M2)</td></tr></tbody></table></div></div><script id=\"form:onglets:j_idt210_s\" type=\"text/javascript\">$(function(){PrimeFaces.cw(\"DataTable\",\"widget_form_onglets_j_idt210\",{id:\"form:onglets:j_idt210\"});});</script></div><div id=\"form:onglets:j_idt214\" class=\"ui-tabs-panel ui-widget-content ui-corner-bottom ui-helper-hidden\" role=\"tabpanel\" aria-hidden=\"true\"><div id=\"form:onglets:j_idt215\" class=\"ui-datatable ui-widget\"><div class=\"ui-datatable-tablewrapper\"><table role=\"grid\"><thead id=\"form:onglets:j_idt215_head\"><tr role=\"row\"><th id=\"form:onglets:j_idt215:j_idt216\" class=\"ui-state-default\" role=\"columnheader\" aria-label=\"Cours\" scope=\"col\"><span class=\"ui-column-title\">Cours</span></th><th id=\"form:onglets:j_idt215:j_idt219\" class=\"ui-state-default\" role=\"columnheader\" aria-label=\"Module\" scope=\"col\"><span class=\"ui-column-title\">Module</span></th></tr></thead><tbody id=\"form:onglets:j_idt215_data\" class=\"ui-datatable-data ui-widget-content\"><tr data-ri=\"0\" class=\"ui-widget-content ui-datatable-even\" role=\"row\"><td role=\"gridcell\">Module de Base Intelligence Articielle Avancé</td><td role=\"gridcell\">Modules de M2</td></tr></tbody></table></div></div><script id=\"form:onglets:j_idt215_s\" type=\"text/javascript\">$(function(){PrimeFaces.cw(\"DataTable\",\"widget_form_onglets_j_idt215\",{id:\"form:onglets:j_idt215\"});});</script></div></div><input type=\"hidden\" id=\"form:onglets_activeIndex\" name=\"form:onglets_activeIndex\" value=\"0\" autocomplete=\"off\" /><input type=\"hidden\" id=\"form:onglets_scrollState\" name=\"form:onglets_scrollState\" value=\"0\" autocomplete=\"off\" /></div><script id=\"form:onglets_s\" type=\"text/javascript\">PrimeFaces.cw(\"TabView\",\"widget_form_onglets\",{id:\"form:onglets\",effectDuration:\"normal\",scrollable:true});</script></div></div><script id=\"form:modaleDetail_s\" type=\"text/javascript\">$(function(){PrimeFaces.cw(\"Dialog\",\"modaleDetail\",{id:\"form:modaleDetail\",modal:true,width:\"95vw\",height:\"80vh\",closeOnEscape:true});});</script>]]></update><update id=\"form:confirmerSuppression\"><![CDATA[<script id=\"form:confirmerSuppression_s\" type=\"text/javascript\">$(function(){PrimeFaces.cw(\"ConfirmDialog\",\"confirmerSuppression\",{id:\"form:confirmerSuppression\",responsive:true});});</script><div id=\"form:confirmerSuppression\" class=\"ui-confirm-dialog ui-dialog ui-widget ui-widget-content ui-corner-all ui-shadow ui-hidden-container planning\"><div class=\"ui-dialog-titlebar ui-widget-header ui-helper-clearfix ui-corner-top\"><span class=\"ui-dialog-title\">Confirmation</span><a href=\"#\" class=\"ui-dialog-titlebar-icon ui-dialog-titlebar-close ui-corner-all\"><span class=\"ui-icon ui-icon-closethick\"></span></a></div><div class=\"ui-dialog-content ui-widget-content\"><span class=\"ui-icon ui-icon-alert ui-confirm-dialog-severity\"></span><span class=\"ui-confirm-dialog-message\">On supprime cette information ?</span></div><div class=\"ui-dialog-buttonpane ui-dialog-footer ui-widget-content ui-helper-clearfix\"><button id=\"form:j_idt225\" name=\"form:j_idt225\" class=\"ui-button ui-widget ui-state-default ui-corner-all ui-button-text-icon-left RedButton\" onclick=\"PrimeFaces.ab({s:&quot;form:j_idt225&quot;,p:&quot;form:j_idt225&quot;,onco:function(xhr,status,args){PF('confirmerSuppression').hide(); PF('modaleDetail').hide();                                            PF('schedule').jqc.fullCalendar('removeEvents', '16941991');}});return false;\" type=\"submit\"><span class=\"ui-button-icon-left ui-icon ui-c fa fa-check White\"></span><span class=\"ui-button-text ui-c\">Oui</span></button><script id=\"form:j_idt225_s\" type=\"text/javascript\">PrimeFaces.cw(\"CommandButton\",\"widget_form_j_idt225\",{id:\"form:j_idt225\"});</script><button id=\"form:j_idt226\" name=\"form:j_idt226\" class=\"ui-button ui-widget ui-state-default ui-corner-all ui-button-text-icon-left GrayButton\" onclick=\"PF('confirmerSuppression').hide(); return false;;PrimeFaces.ab({s:&quot;form:j_idt226&quot;});return false;\" type=\"submit\"><span class=\"ui-button-icon-left ui-icon ui-c fas fa-times\"></span><span class=\"ui-button-text ui-c\">Non</span></button><script id=\"form:j_idt226_s\" type=\"text/javascript\">PrimeFaces.cw(\"CommandButton\",\"widget_form_j_idt226\",{id:\"form:j_idt226\"});</script></div></div>]]></update><update id=\"form:j_idt227\"><![CDATA[<span id=\"form:j_idt227\"></span><script id=\"form:j_idt227_s\" type=\"text/javascript\">$(function(){PrimeFaces.cw('Growl','widget_form_j_idt227',{id:'form:j_idt227',sticky:true,life:6000,escape:true,msgs:[]});});</script>]]></update><update id=\"j_id1:javax.faces.ViewState:0\"><![CDATA[-5066905941473846576:-3464469988206764138]]></update></changes></partial-response>";
        */
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

        String[] teachers = zoomStr.split("<tr data-ri=\"[0-9]+\" class=\"ui-widget-content ui-datatable-(even|odd)\" role=\"row\">");
        for (String t : teachers) {
            if(t.isEmpty()) continue;
            Person p = new Person();
            String[] data = t.split("<td role=\"gridcell\">");
            p.lastName = data[1].substring(0, data[1].indexOf("</td>"));
            p.firstName = data[2].substring(0, data[2].indexOf("</td>"));
            details.teachers.add(p);
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
