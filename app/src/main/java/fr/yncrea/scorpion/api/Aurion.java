package fr.yncrea.scorpion.api;

import android.util.Log;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

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
                .baseUrl("https://aurion.yncrea.fr")
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
        final String[] name = {"", ""};
        Call<ResponseBody> requestName = aurionService.getHomePageHtml(connCookie);

        try {
            res = requestName.execute();
            String body = null;
            if(res.isSuccessful()){
                try {
                    body = res.body().string();
                    name[0] = "success";
                    name[1] = body.substring(body.indexOf("<h3>") + 4, body.indexOf("</h3>"));
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
    public String[] getPlanningViewState(String connCookie){
        Response<ResponseBody> res = null;
        final String[] viewState = {"", ""};
        Call<ResponseBody> requestName = aurionService.getPlanningPageHtml(connCookie);

        try {
            res = requestName.execute();
            String body = null;
            if(res.isSuccessful()){
                try {
                    body = res.body().string();
                    String from = "id=\"j_id1:javax.faces.ViewState:0\" value=\"";
                    String to = "\" autocomplete=\"off\" />\n</form></div></body>";
                    viewState[0] = "success";
                    int a = body.indexOf(from);
                    int b = body.indexOf(to);
                    viewState[1] = body.substring(body.indexOf(from) + from.length(), body.indexOf(to));
                    Log.d("LOGIN", "ViewState parsing success");
                } catch (IOException e) {
                    viewState[0] = "Error while parsing";
                    Log.d("LOGIN", viewState[0]);
                    e.printStackTrace();
                }
            }
            else{
                viewState[0] = "authentication failed";
            }
        }
        catch(IOException e) {
            e.printStackTrace();
            viewState[0] = "server couldn't be reached : check your connection";
        }
        return viewState;
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
        String viewState = "";
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE);

        long now = Calendar.getInstance().getTimeInMillis();
        long offset = (long)(weekIndex - Calendar.getInstance().get(Calendar.WEEK_OF_YEAR)) * 7 * 24 * 60 * 60 * 1000;
        long start = (now - now % (7*24*60*60*1000) + 4 * 24 * 60 * 60 * 1000) + offset;
        long end = start + (6*24*60*60*1000);
        //long end = (6*24*60*60*1000 + Calendar.getInstance().getTime().getTime() - 1 - 3*24*60*60*1000 - Calendar.getInstance().getTime().getTime() % (604_800_000))
          //      + weekIndex * 7 * 24 * 60 * 60 * 1000;

        String defaultFields = "form=form&form%3AlargeurDivCenter=613&form%3Adate_input="
                + df.format(now).replace("/", "%2F")
                + "&form%3Aweek=" + weekIndex + "-" + Calendar.getInstance().get(Calendar.YEAR)
                + "&form%3Aj_idt132_view=agendaWeek&form%3AoffsetFuseauNavigateur=-3600000&form%3Aonglets_activeIndex=0&form%3Aonglets_scrollState=0&form%3Aj_idt258_focus=&form%3Aj_idt258_input=44323&form%3Asidebar=form%3Asidebar&form%3Asidebar_menuid=0";
        HashMap<String, String> fields = new HashMap<String, String>();
        String[] fieldsArray = defaultFields.split("&");
        for (int i = 0; i < fieldsArray.length; i++) {
            String[] keyVal = fieldsArray[i].split("=");
            if(keyVal.length == 2)
                fields.put(keyVal[0], keyVal[1]);
        }
        viewState = getPlanningViewState(connCookie)[1];
        Call<ResponseBody> request = aurionService.calendarRequest(connCookie, viewState, fields);

        try {
            res = request.execute();
            if(res.code() == 302){
                viewState = getPlanningViewState(connCookie)[1];
                defaultFields = "javax.faces.partial.ajax=true&javax.faces.source=form%3Aj_idt132&javax.faces.partial.execute=form%3Aj_idt132&javax.faces.partial.render=form%3Aj_idt132&form%3Aj_idt132=form%3Aj_idt132"
                        + "&form%3Aj_idt132_start=" + start
                        + "&form%3Aj_idt132_end=" + end
                        + "&form=form&form%3AlargeurDivCenter=&form%3Adate_input=02%2F11%2F2020&form%3Aweek=45-2020&form%3Aj_idt132_view=agendaWeek&form%3AoffsetFuseauNavigateur=-3600000&form%3Aonglets_activeIndex=0&form%3Aonglets_scrollState=0&form%3Aj_idt258_focus=&form%3Aj_idt258_input=44323";
                fields.clear();
                fieldsArray = defaultFields.split("&");
                for (int i = 0; i < fieldsArray.length; i++) {
                    String[] keyVal = fieldsArray[i].split("=");
                    if(keyVal.length == 2)
                        fields.put(keyVal[0], keyVal[1]);
                }
                request = aurionService.calendarRequest(connCookie, viewState, fields);

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
            calendar[0] = "server couldn't be reached : check your connection";
        }
        return calendar;
    }
}
