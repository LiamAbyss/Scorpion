package fr.yncrea.scorpion.api;

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HEAD;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface AurionService {
    @FormUrlEncoded
    @POST("/login")
    Call<ResponseBody> getSessionIdResponse(@Field("username") String username, @Field("password") String password, @Field("j_idt28") String form);

    @GET("/")
    Call<ResponseBody> getHomePageHtml(@Header("Cookie") String cookie);

    @GET("/faces/Planning.xhtml")
    Call<ResponseBody> getPlanningPageHtml(@Header("Cookie") String cookie);

    @FormUrlEncoded
    @POST("/faces/MainMenuPage.xhtml")
    Call<ResponseBody> postMainMenuPage(@Header("Cookie") String cookie, @Field("javax.faces.ViewState") String viewState, @FieldMap(encoded = true) HashMap<String, String> otherFields);

    @FormUrlEncoded
    @POST("/faces/Planning.xhtml")
    Call<ResponseBody> postPlanningPage(@Header("Cookie") String cookie, @Field("javax.faces.ViewState") String viewState, @FieldMap(encoded = true) HashMap<String, String> otherFields);

    @GET("/faces/ChoixIndividu.xhtml")
    Call<ResponseBody> getGradesHtml(@Header("Cookie") String cookie);

    @FormUrlEncoded
    @POST("/faces/ChoixIndividu.xhtml")
    Call<ResponseBody> postGrades(@Header("Cookie") String cookie, @Field("javax.faces.ViewState") String viewState, @FieldMap(encoded = true) HashMap<String, String> otherFields);

    @GET("/faces/MesAbsences.xhtml")
    Call<ResponseBody> getAbsencesHtml(@Header("Cookie") String cookie);
}
