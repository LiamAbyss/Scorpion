package fr.yncrea.fastaurion.api;

import android.util.Log;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
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

        /*new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String cookies = response.headers().get("Set-Cookie");
                if(response.code() == 302){
                    Log.d("LOGIN", "Login success");
                    sessionID[0] = "success";
                    sessionID[1] = cookies.substring(cookies.indexOf("JSESSIONID"), cookies.indexOf(";", cookies.indexOf("JSESSIONID")));
                }
                else{
                    sessionID[0] = "authentication failed";
                    Log.d("LOGIN", sessionID[0]);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("LOGIN", t.getMessage());
                sessionID[0] = "server couldn't be reached : check your connection";
            }
        });
        while (sessionID[0] == ""){};*/
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
        /*requestName.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String body = null;
                if(response.isSuccessful()){
                    try {
                        body = response.body().string();
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

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("LOGIN", t.getMessage());
                name[0] = "server couldn't be reached : check your connection";
            }
        });
        while (name[0] == ""){};*/
        return name;
    }
}
