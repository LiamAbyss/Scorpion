package fr.yncrea.scorpion.api;

import com.google.gson.Gson;
import com.google.gson.JsonArray;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface GithubService {
    @GET("/repos/LiamAbyss/Scorpion/releases")
    Call<JsonArray> getReleases();
}
