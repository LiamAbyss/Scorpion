package fr.yncrea.scorpion.api;

import android.util.Log;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import fr.yncrea.scorpion.utils.Grade;
import fr.yncrea.scorpion.utils.PreferenceUtils;
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
     * @return A String array of size 2
     * Element 0 is either "success" or an error message
     * If element 0 is "success", element 1 is the connection cookie requested, otherwise it is an empty String
     */
    public String[] connect(String username, String password){
        Response<ResponseBody> res = null;
        final String[] sessionID = {"", ""};
        Call<ResponseBody> request = aurionService.getSessionIdResponse(username, password);

        try {
            res = request.execute();
            String cookies = res.headers().get("Set-Cookie");
            if(res.code() == 302){
                Log.d("LOGIN", "Login success");
                sessionID[0] = "success";
                sessionID[1] = cookies.substring(cookies.indexOf("JSESSIONID"), cookies.indexOf(";", cookies.indexOf("JSESSIONID")));
                PreferenceUtils.setSessionId(sessionID[1]);
            }
            else{
                sessionID[0] = "authentication failed";
                Log.d("LOGIN", sessionID[0]);
            }
        }
        catch(IOException e) {
            e.printStackTrace();
            sessionID[0] = "server couldn't be reached : check your connection";
        }
        return sessionID;
    }

    /**
     * @param connCookie
     * @return A String array of size 2
     * Element 0 is either "success" or an error message
     * If element 0 is "success", element 1 is the name requested, otherwise it is an empty String
     */
    public String[] getName(String connCookie){
        Response<ResponseBody> res = null;
        final String[] name = {"", "", "", ""};
        Call<ResponseBody> requestName = aurionService.getHomePageHtml(connCookie);

        try {
            res = requestName.execute();
            String body = null;
            if(res.isSuccessful()){
                try {
                    body = res.body().string();
                    name[0] = "success";
                    name[1] = body.substring(body.indexOf("<h3>") + 4, body.indexOf("</h3>"));

                    String from = "id=\"j_id1:javax.faces.ViewState:0\" value=\"";
                    String to = "\" autocomplete=\"off\" />\n</form></div></body>";
                    name[2] = body.substring(body.indexOf(from) + from.length(), body.indexOf(to));

                    to = "Mon Planning";
                    String tmpSubstring = body.substring(body.indexOf(to) - 300, body.indexOf(to));
                    from = "form:sidebar_menuid':'";
                    to = "'})";
                    name[3] = tmpSubstring.substring(tmpSubstring.indexOf(from) + from.length(), tmpSubstring.indexOf(to));

                    Log.d("LOGIN", "Name parsing success");
                } catch (IOException e) {
                    name[0] = "Error while parsing";
                    Log.d("LOGIN", name[0]);
                    e.printStackTrace();
                }
            }
            else{
                name[0] = "authentication failed";
            }
        }
        catch(IOException e) {
            e.printStackTrace();
            name[0] = "server couldn't be reached : check your connection";
        }
        return name;
    }

    /**
     * @param connCookie
     * @return A String array of size 2
     * Element 0 is either "success" or an error message
     * If element 0 is "success", element 1 is the viewState requested, otherwise it is an empty String
     */
    public String[] getPlanningData(String connCookie){
        Response<ResponseBody> res = null;
        final String[] data = {"", "", ""};
        Call<ResponseBody> requestName = aurionService.getPlanningPageHtml(connCookie);

        try {
            res = requestName.execute();
            String body = null;
            if(res.isSuccessful()){
                try {
                    body = res.body().string();
                    String from = "id=\"j_id1:javax.faces.ViewState:0\" value=\"";
                    String to = "\" autocomplete=\"off\" />\n</form></div></body>";
                    data[0] = "success";
                    data[1] = body.substring(body.indexOf(from) + from.length(), body.indexOf(to));

                    from = "<div id=\"form:j_idt";
                    to = "\" class=\"schedule\">";
                    if(body.indexOf(to) != -1 && body.indexOf(from) != -1)
                    {
                        String tmpSubstring = body.substring(body.indexOf(to) - 50, body.indexOf(to) + to.length());
                        data[2] = tmpSubstring.substring(tmpSubstring.indexOf(from) + from.length(), tmpSubstring.indexOf(to));
                    }
                    //data[2]="117";

                    Log.d("LOGIN", "ViewState parsing success");
                } catch (IOException e) {
                    data[0] = "Error while parsing";
                    Log.d("LOGIN", data[0]);
                    e.printStackTrace();
                }
            }
            else{
                data[0] = "authentication failed";
            }
        }
        catch(IOException e) {
            e.printStackTrace();
            data[0] = "server couldn't be reached : check your connection";
        }
        return data;
    }

    /**
     * @param connCookie
     * @return A String array of size 2
     * Element 0 is either "success" or an error message
     * If element 0 is "success", element 1 is the name requested, otherwise it is an empty String
     */
    public String[] getCalendarAsXML(String connCookie, int weekIndex){
        Response<ResponseBody> res = null;
        final String[] calendar = {"", ""};
        String[] data;
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
        data = getName(connCookie);

        //postState
        defaultFields = "form=form&form%3AlargeurDivCenter=835&form%3Asauvegarde=&form%3Aj_idt772_focus=&form%3Aj_idt772_input=44323&form%3Asidebar=form%3Asidebar&" +
                "form%3Asidebar_menuid=" + data[3];
        fields.clear();
        fieldsArray = defaultFields.split("&");
        for (int i = 0; i < fieldsArray.length; i++) {
            String[] keyVal = fieldsArray[i].split("=");
            if(keyVal.length == 2)
                fields.put(keyVal[0], keyVal[1]);
            else fields.put(keyVal[0], "");
        }
        Call<ResponseBody> request = aurionService.postMainMenuPage(connCookie, data[2], fields);
        try {
            res = request.execute();
            if(res.code() == 302){
                data = getPlanningData(connCookie);
                defaultFields = "javax.faces.partial.ajax=true"
                        + "&javax.faces.source=form%3Aj_idt" + data[2]
                        + "&javax.faces.partial.execute=form%3Aj_idt" + data[2]
                        + "&javax.faces.partial.render=form%3Aj_idt" + data[2]
                        + "&form%3Aj_idt" + data[2] + "=form%3Aj_idt" + data[2]
                        + "&form%3Aj_idt" + data[2] + "_start=" + start
                        + "&form%3Aj_idt" + data[2] + "_end=" + end
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
                request = aurionService.postPlanningPage(connCookie, data[1], fields);

                String body = null;
                res = request.execute();
                if(res.isSuccessful()){
                    body = res.body().string();
                    calendar[0] = "success";
                    calendar[1] = body;
                }
            }
            else{
                calendar[0] = "authentication failed";
            }
        }
        catch(IOException e) {
            e.printStackTrace();
            calendar[0] = "Server couldn't be reached : check your connection";
        }

        return calendar;
    }

    public String[] getGrades(){
        String cookie = PreferenceUtils.getSessionId();

        Response<ResponseBody> res = null;

        String[] data;
        final String[] grades = {"", ""};

        //accueil
        data = getName(cookie);

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

        Call<ResponseBody> request = aurionService.postMainMenuPage(cookie, data[2], fields);
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
                request = aurionService.postMainMenuPage(cookie, data[2], fields);

                res = request.execute();
                if(res.code() == 302){
                    body = res.headers().toString();
                    request = aurionService.getGradesHtml(cookie);
                    res = request.execute();
                    if(res.isSuccessful()){
                        body = res.body().string();

                        from = "javax.faces.ViewState:0\" value=\"";
                        to = "\" autocomplete=\"off\" />";

                        data[2] = body.substring(body.indexOf(from) + from.length(), body.indexOf(to));
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

                        request = aurionService.postGrades(cookie, data[2], fields);
                        res = request.execute();

                        if(res.isSuccessful()) {
                            body = res.body().string();
                            grades[0] = "success";
                            grades[1] = body;
                        }
                    }
                }

            }
            else{
                grades[0] = "authentication failed";
            }
        }
        catch(IOException e) {
            e.printStackTrace();
            grades[0] = "Server couldn't be reached : check your connection";
        }

        return grades;
    }

    public String[] detailsPlanning(Long id) {

        String cookie = PreferenceUtils.getSessionId();

        Response<ResponseBody> res = null;

        String[] data;
        final String[] details = {"", ""};

        //accueil
        data = getPlanningData(cookie);

        //postState
        String defaultFields = "javax.faces.partial.ajax=true&javax.faces.source=form%3Aj_idt117&" +
                "javax.faces.partial.execute=form%3Aj_idt117&javax.faces.partial.render=form%3AmodaleDetail+form%3AconfirmerSuppression&" +
                "javax.faces.behavior.event=eventSelect&javax.faces.partial.event=eventSelect&" +
                "form%3Aj_idt117_selectedEventId=" + id + "&" +
                "form=form&form%3AlargeurDivCenter=1219&form%3Adate_input=30%2F08%2F2021&form%3Aweek=35-2021&form%3Aj_idt117_view=agendaWeek&" +
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

        Call<ResponseBody> request = aurionService.postPlanningPage(cookie, data[1], fields);
        try {
            res = request.execute();
            if(res.isSuccessful()){
                String body = res.body().string();
                details[0] = "success";
                details[1] = body;
            }
            else{
                details[0] = "authentication failed";
            }
        }
        catch(IOException e) {
            e.printStackTrace();
            details[0] = "Server couldn't be reached : check your connection";
        }

        return details;
    }
}
