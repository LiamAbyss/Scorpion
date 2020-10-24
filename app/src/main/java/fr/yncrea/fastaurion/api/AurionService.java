package fr.yncrea.fastaurion.api;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface AurionService {
    @FormUrlEncoded
    @POST("/login")
    Call<ResponseBody> getSessionIdResponse(@Field("username") String username, @Field("password") String password);

    @GET("/")
    Call<ResponseBody> getHomePageHtml(@Header("Cookie") String cookie);
}
