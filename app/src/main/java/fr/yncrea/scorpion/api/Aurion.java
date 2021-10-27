package fr.yncrea.scorpion.api;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import fr.yncrea.scorpion.model.AurionResponse;
import fr.yncrea.scorpion.model.Course;
import fr.yncrea.scorpion.model.CourseDetails;
import fr.yncrea.scorpion.utils.PreferenceUtils;
import fr.yncrea.scorpion.utils.UtilsMethods;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class Aurion {
    private AurionService aurionService;

    public Aurion(){
        OkHttpClient client = new OkHttpClient.Builder()
                .followRedirects(false)
                .followSslRedirects(false)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(/*"https://formation.ensta-bretagne.fr")*/"https://aurion.junia.com")
                .build();

        this.aurionService = retrofit.create(AurionService.class);
    }

    /**
     * @param username
     * @param password
     * @return AurionResponse
     */
    public AurionResponse connect(String username, String password){
        Response<ResponseBody> res = null;
        Call<ResponseBody> request = aurionService.getSessionIdResponse(username, password, "");
        AurionResponse aurionResponse = new AurionResponse();

        try {
            res = request.execute();
            String cookies = res.headers().get("Set-Cookie");
            if(res.code() == 302){
                Log.d("LOGIN", "Login success");
                aurionResponse.status = AurionResponse.SUCCESS;
                aurionResponse.cookie = cookies.substring(cookies.indexOf("JSESSIONID"), cookies.indexOf(";", cookies.indexOf("JSESSIONID")));
                PreferenceUtils.setSessionId(aurionResponse.cookie);
            }
            else{
                aurionResponse.status = AurionResponse.FAILURE;
                aurionResponse.message = "Authentication failed";
                Log.d("LOGIN", aurionResponse.message);
            }
        }
        catch(IOException e) {
            e.printStackTrace();
            aurionResponse.status = AurionResponse.FAILURE;
            aurionResponse.message = "Server couldn't be reached : check your connection";
        }
        return aurionResponse;
    }

    /**
     * @param connCookie
     * @return AurionResponse
     */
    public AurionResponse getHomePage(String connCookie){
        Response<ResponseBody> res = null;
        Call<ResponseBody> requestName = aurionService.getHomePageHtml(connCookie);
        AurionResponse aurionResponse = new AurionResponse();

        try {
            res = requestName.execute();
            String body = null;
            if(res.isSuccessful()){
                try {
                    body = res.body().string();
                    aurionResponse.status = AurionResponse.SUCCESS;
                    aurionResponse.name = body.substring(body.indexOf("<h3>") + 4, body.indexOf("</h3>"));

                    String from = "id=\"j_id1:javax.faces.ViewState:0\" value=\"";
                    String to = "\" autocomplete=\"off\" />\n</form></div></body>";
                    aurionResponse.viewState = body.substring(body.indexOf(from) + from.length(), body.indexOf(to));

                    to = "Mon Planning";
                    String tmpSubstring = body.substring(body.indexOf(to) - 300, body.indexOf(to));
                    from = "form:sidebar_menuid':'";
                    to = "'})";
                    aurionResponse.schoolingMenuId = tmpSubstring.substring(tmpSubstring.indexOf(from) + from.length(), tmpSubstring.indexOf(to));

                    Log.d("LOGIN", "Name parsing success");
                } catch (IOException e) {
                    aurionResponse.status = AurionResponse.FAILURE;
                    aurionResponse.message = "Parsing failed";
                    Log.d("LOGIN", aurionResponse.message);
                    e.printStackTrace();
                }
            }
            else{
                aurionResponse.status = AurionResponse.FAILURE;
                aurionResponse.message = "Authentication failed";
            }
        }
        catch(IOException e) {
            e.printStackTrace();
            aurionResponse.status = AurionResponse.FAILURE;
            aurionResponse.message = "Server couldn't be reached : check your connection";
        }
        return aurionResponse;
    }

    /**
     * @param connCookie
     * @return AurionResponse
     */
    public AurionResponse getPlanningData(String connCookie){
        Response<ResponseBody> res = null;
        Call<ResponseBody> requestName = aurionService.getPlanningPageHtml(connCookie);
        AurionResponse aurionResponse = new AurionResponse();

        try {
            res = requestName.execute();
            String body = null;
            if(res.isSuccessful()){
                try {
                    body = res.body().string();
                    aurionResponse.status = AurionResponse.SUCCESS;

                    String from = "id=\"j_id1:javax.faces.ViewState:0\" value=\"";
                    String to = "\" autocomplete=\"off\" />\n</form></div></body>";
                    aurionResponse.viewState = body.substring(body.indexOf(from) + from.length(), body.indexOf(to));

                    from = "<div id=\"form:j_idt";
                    to = "\" class=\"schedule\">";
                    if(body.indexOf(to) != -1 && body.indexOf(from) != -1)
                    {
                        String tmpSubstring = body.substring(body.indexOf(to) - 50, body.indexOf(to) + to.length());
                        aurionResponse.formId = tmpSubstring.substring(tmpSubstring.indexOf(from) + from.length(), tmpSubstring.indexOf(to));
                    }
                    else {
                        aurionResponse.formId = "117";
                    }

                    Log.d("LOGIN", "ViewState parsing success");
                } catch (IOException e) {
                    aurionResponse.status = AurionResponse.FAILURE;
                    aurionResponse.message = "Parsing failed";
                    Log.d("LOGIN", aurionResponse.message);
                    e.printStackTrace();
                }
            }
            else{
                aurionResponse.status = AurionResponse.FAILURE;
                aurionResponse.message = "Authentication failed";
            }
        }
        catch(IOException e) {
            e.printStackTrace();
            aurionResponse.status = AurionResponse.FAILURE;
            aurionResponse.message = "Server couldn't be reached : check your connection";
        }
        return aurionResponse;
    }

    /**
     * @param connCookie
     * @return AurionResponse
     */
    public AurionResponse getCalendarAsXML(String connCookie, int weekIndex){
        Response<ResponseBody> res = null;
        AurionResponse aurionResponse = new AurionResponse();
        AurionResponse data;
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE);

        long now = Calendar.getInstance().getTimeInMillis();
        long offset = (long)(weekIndex - Calendar.getInstance().get(Calendar.WEEK_OF_YEAR)) * 7 * 24 * 60 * 60 * 1000;
        long start = (now - (now + 3 * 24 * 60 * 60 * 1000) % (7*24*60*60*1000)) + offset;
        long end = start + (6*24*60*60*1000);
        //long end = (6*24*60*60*1000 + Calendar.getInstance().getTime().getTime() - 1 - 3*24*60*60*1000 - Calendar.getInstance().getTime().getTime() % (604_800_000))
        //      + weekIndex * 7 * 24 * 60 * 60 * 1000;

        String defaultFields;
        HashMap<String, String> fields = new HashMap<String, String>();
        String[] fieldsArray;

        //accueil
        data = getHomePage(connCookie);

        //postState
        defaultFields = "form=form&form%3AlargeurDivCenter=835&form%3Asauvegarde=&form%3Aj_idt772_focus=&form%3Aj_idt772_input=44323&form%3Asidebar=form%3Asidebar&" +
                "form%3Asidebar_menuid=" + data.schoolingMenuId;
        fields.clear();
        fieldsArray = defaultFields.split("&");
        for (int i = 0; i < fieldsArray.length; i++) {
            String[] keyVal = fieldsArray[i].split("=");
            if(keyVal.length == 2)
                fields.put(keyVal[0], keyVal[1]);
            else fields.put(keyVal[0], "");
        }

        Call<ResponseBody> request = aurionService.postMainMenuPage(connCookie, data.viewState, fields);
        try {
            res = request.execute();
            if(res.code() == 302){
                data = getPlanningData(connCookie);
                defaultFields = "javax.faces.partial.ajax=true"
                        + "&javax.faces.source=form%3Aj_idt" + data.formId
                        + "&javax.faces.partial.execute=form%3Aj_idt" + data.formId
                        + "&javax.faces.partial.render=form%3Aj_idt" + data.formId
                        + "&form%3Aj_idt" + data.formId + "=form%3Aj_idt" + data.formId
                        + "&form%3Aj_idt" + data.formId + "_start=" + start
                        + "&form%3Aj_idt" + data.formId + "_end=" + end
                        + "&form=form"
                        + "&form%3Adate_input=" + df.format(start).replace("/", "%2F")
                        + "&form%3Aweek=" + weekIndex + "-" + Calendar.getInstance().get(Calendar.YEAR);
                fields.clear();
                fieldsArray = defaultFields.split("&");
                for (int i = 0; i < fieldsArray.length; i++) {
                    String[] keyVal = fieldsArray[i].split("=");
                    if(keyVal.length == 2)
                        fields.put(keyVal[0], keyVal[1]);
                }
                request = aurionService.postPlanningPage(connCookie, data.viewState, fields);

                String body = null;
                res = request.execute();
                if(res.isSuccessful()){
                    body = res.body().string();
                    aurionResponse.status = AurionResponse.SUCCESS;
                    aurionResponse.body = body;
                }
            }
            else{
                aurionResponse.status = AurionResponse.FAILURE;
                aurionResponse.message = "Authentication failed";
            }
        }
        catch(IOException e) {
            e.printStackTrace();
            aurionResponse.status = AurionResponse.FAILURE;
            aurionResponse.message = "Server couldn't be reached : check your connection";
        }

        return aurionResponse;
    }

    /**
     * @return AurionResponse
     */
    public AurionResponse getGrades(){
        String cookie = PreferenceUtils.getSessionId();
        AurionResponse aurionResponse = new AurionResponse();
        AurionResponse data;
        Response<ResponseBody> res = null;

        //accueil
        data = getHomePage(cookie);

        //postState
        String defaultFields = "javax.faces.partial.ajax=true&javax.faces.source=form%3Aj_idt52&" +
                "javax.faces.partial.execute=form%3Aj_idt52&javax.faces.partial.render=form%3Asidebar&" +
                "form%3Aj_idt52=form%3Aj_idt52&webscolaapp.Sidebar.ID_SUBMENU=submenu_44413&form=form&" +
                "form%3AlargeurDivCenter=1219&form%3Asauvegarde=&form%3Aj_idt772_focus=&form%3Aj_idt772_input=44323";
        HashMap<String, String> fields = new HashMap<String, String>();
        String[] fieldsArray = defaultFields.split("&");
        for (int i = 0; i < fieldsArray.length; i++) {
            String[] keyVal = fieldsArray[i].split("=");
            if(keyVal.length == 2)
                fields.put(keyVal[0], keyVal[1]);
            else fields.put(keyVal[0], "");
        }

        Call<ResponseBody> request = aurionService.postMainMenuPage(cookie, data.viewState, fields);
        try {
            res = request.execute();
            if(res.isSuccessful()){
                String body = res.body().string();

                String from = "";
                String to = "Mes notes</span>";
                String menuid = body.substring(body.indexOf(to) - 300, body.indexOf(to));
                from = "form:sidebar_menuid':'";
                to = "'})";
                menuid = menuid.substring(menuid.indexOf(from) + from.length(), menuid.indexOf(to));

                defaultFields = "form=form&form%3AlargeurDivCenter=1219&form%3Asauvegarde=&" +
                        "form%3Aj_idt772_focus=&form%3Aj_idt772_input=44323&" +
                        "form%3Asidebar=form%3Asidebar&form%3Asidebar_menuid=" + menuid;
                fields.clear();
                fieldsArray = defaultFields.split("&");
                for (int i = 0; i < fieldsArray.length; i++) {
                    String[] keyVal = fieldsArray[i].split("=");
                    if(keyVal.length == 2)
                        fields.put(keyVal[0], keyVal[1]);
                    else fields.put(keyVal[0], "");
                }
                request = aurionService.postMainMenuPage(cookie, data.viewState, fields);

                res = request.execute();
                if(res.code() == 302){
                    //body = res.headers().toString();
                    request = aurionService.getGradesHtml(cookie);
                    res = request.execute();
                    if(res.isSuccessful()){
                        body = res.body().string();

                        from = "javax.faces.ViewState:0\" value=\"";
                        to = "\" autocomplete=\"off\" />";

                        data.viewState = body.substring(body.indexOf(from) + from.length(), body.indexOf(to));
                        int start = 0, rows = 10000;
                        defaultFields = "javax.faces.partial.ajax=true&javax.faces.source=form%3Aj_idt181&" +
                                "javax.faces.partial.execute=form%3Aj_idt181&javax.faces.partial.render=form%3Aj_idt181&" +
                                "form%3Aj_idt181=form%3Aj_idt181&form%3Aj_idt181_pagination=true&" +
                                "form%3Aj_idt181_first=" + start +
                                "&form%3Aj_idt181_rows=" + rows +
                                "&form%3Aj_idt181_skipChildren=true&form%3Aj_idt181_encodeFeature=true&" +
                                "form=form&form%3AlargeurDivCenter=835&form%3AmessagesRubriqueInaccessible=&form%3Asearch-texte=&" +
                                "form%3Asearch-texte-avancer=&form%3Ainput-expression-exacte=&form%3Ainput-un-des-mots=&" +
                                "form%3Ainput-aucun-des-mots=&form%3Ainput-nombre-debut=&form%3Ainput-nombre-fin=&" +
                                "form%3AcalendarDebut_input=&form%3AcalendarFin_input=&form%3Aj_idt181_reflowDD=0_0&" +
                                "form%3Aj_idt181%3Aj_idt186%3Afilter=&form%3Aj_idt181%3Aj_idt188%3Afilter=&" +
                                "form%3Aj_idt181%3Aj_idt190%3Afilter=&form%3Aj_idt181%3Aj_idt192%3Afilter=&" +
                                "form%3Aj_idt181%3Aj_idt194%3Afilter=&form%3Aj_idt181%3Aj_idt196%3Afilter=&form%3Aj_idt254_focus=&" +
                                "form%3Aj_idt254_input=44323";
                        fields.clear();
                        fieldsArray = defaultFields.split("&");
                        for (int i = 0; i < fieldsArray.length; i++) {
                            String[] keyVal = fieldsArray[i].split("=");
                            if(keyVal.length == 2)
                                fields.put(keyVal[0], keyVal[1]);
                        }

                        request = aurionService.postGrades(cookie, data.viewState, fields);
                        res = request.execute();

                        if(res.isSuccessful()) {
                            body = res.body().string();
                            aurionResponse.status = AurionResponse.SUCCESS;
                            aurionResponse.body = body;
                        }
                    }
                }

            }
            else{
                aurionResponse.status = AurionResponse.FAILURE;
                aurionResponse.message = "Authentication failed";
            }
        }
        catch(IOException e) {
            e.printStackTrace();
            aurionResponse.status = AurionResponse.FAILURE;
            aurionResponse.message = "Server couldn't be reached : check your connection";
        }

        return aurionResponse;
    }

    /**
     * @param id The id of the course
     * @return AurionResponse
     */
    public AurionResponse detailsPlanning(Long id) {
        return detailsPlanning(id, null);
    }

    /**
     * @param id The id of the course
     * @param viewState
     * @return AurionResponse
     */
    public AurionResponse detailsPlanning(Long id, String viewState) {

        String cookie = PreferenceUtils.getSessionId();
        Response<ResponseBody> res = null;
        AurionResponse aurionResponse = new AurionResponse();
        AurionResponse data;

        //accueil
        if(viewState == null) {
            data = getPlanningData(cookie);
            viewState = data.viewState;
        }
        //postState
        String defaultFields = "javax.faces.partial.ajax=true&javax.faces.source=form%3Aj_idt117&" +
                "javax.faces.partial.execute=form%3Aj_idt117&javax.faces.partial.render=form%3AmodaleDetail+form%3AconfirmerSuppression&" +
                "javax.faces.behavior.event=eventSelect&javax.faces.partial.event=eventSelect&" +
                "form%3Aj_idt117_selectedEventId=" + id + "&" +
                "form=form&form%3AlargeurDivCenter=1219&form%3Aj_idt117_view=agendaWeek&" +
                "form%3AoffsetFuseauNavigateur=-7200000&form%3Aonglets_activeIndex=0&form%3Aonglets_scrollState=0&form%3Aj_idt236_focus=&" +
                "form%3Aj_idt236_input=44323";
        HashMap<String, String> fields = new HashMap<String, String>();
        String[] fieldsArray = defaultFields.split("&");
        for (int i = 0; i < fieldsArray.length; i++) {
            String[] keyVal = fieldsArray[i].split("=");
            if(keyVal.length == 2)
                fields.put(keyVal[0], keyVal[1]);
            else fields.put(keyVal[0], "");
        }

        Call<ResponseBody> request = aurionService.postPlanningPage(cookie, viewState, fields);
        try {
            res = request.execute();
            if(res.isSuccessful()){
                String body = res.body().string();
                aurionResponse.status = AurionResponse.SUCCESS;
                aurionResponse.body = body;
            }
            else{
                aurionResponse.status = AurionResponse.FAILURE;
                aurionResponse.message = "Authentication failed";
            }
        }
        catch(IOException e) {
            e.printStackTrace();
            aurionResponse.status = AurionResponse.FAILURE;
            aurionResponse.message = "Server couldn't be reached : check your connection";
        }

        return aurionResponse;
    }

    public List<CourseDetails> getPlanning(int weekIndex) {
        AurionResponse calendar = getCalendarAsXML(PreferenceUtils.getSessionId(), weekIndex);
        List<Course> tmpCourses = new ArrayList<>();
        List<CourseDetails> res = new ArrayList<>();

        if(calendar.status == AurionResponse.SUCCESS) {
            JSONArray planningJSON = UtilsMethods.XMLToJSONArray(calendar.body);
            if(planningJSON == null) {
                return null;
            }

            try {
                tmpCourses = UtilsMethods.JSONArrayToCourseList(planningJSON);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if(tmpCourses.size() == 1 && tmpCourses.get(0).id == -1) {
                res.add(new CourseDetails());
            }
            else {
                String viewState = getPlanningData(PreferenceUtils.getSessionId()).viewState;

                for (int i = 0; i < tmpCourses.size(); i++) {
                    AurionResponse details = detailsPlanning(tmpCourses.get(i).id, viewState);

                    if (details.status == AurionResponse.SUCCESS) {
                        CourseDetails d = UtilsMethods.parseCourseDetails(details.body);
                        if (d.longDate != "") {
                            d.date = tmpCourses.get(i).date;
                            d.id = tmpCourses.get(i).id;
                            d.dateStart = tmpCourses.get(i).start;
                            d.dateEnd = tmpCourses.get(i).end;
                            res.add(d);
                        }
                    }
                }
            }

            if(res.size() > 0) return res;
        }
        else {
            if(connect(PreferenceUtils.getLogin(), PreferenceUtils.getPassword()).status
                    == AurionResponse.SUCCESS) {
                return getPlanning(weekIndex);
            }
        }
        return null;
    }

}
