package fr.yncrea.fastaurion;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import fr.yncrea.fastaurion.api.AurionService;
import fr.yncrea.fastaurion.utils.*;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    private Executor executor = Executors.newSingleThreadExecutor();
    private EditText mLoginEditText;
    private EditText mPasswordEditText;
    private AurionService aurionService;
    private Toast mToast;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        mLoginEditText = (EditText) findViewById(R.id.loginEditText);
        mPasswordEditText = (EditText) findViewById(R.id.passwordEditText);

        final String login = PreferenceUtils.getLogin();
        final String name = PreferenceUtils.getName();
        if(!TextUtils.isEmpty(login) && !TextUtils.isEmpty(name)) startActivity(getHomeIntent(login, name));

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

    @Override
    public void onClick(View v) {

        if(TextUtils.isEmpty(mLoginEditText.getText())){
            showToast(this, R.string.error_no_login, Toast.LENGTH_LONG);
            return;
        }

        String login = mLoginEditText.getText().toString();

        if(TextUtils.isEmpty(mPasswordEditText.getText())){
            showToast(this, R.string.error_no_password, Toast.LENGTH_LONG);
            return;
        }

        mLoginEditText.setEnabled(false);
        mPasswordEditText.setEnabled(false);

        executor.execute(() -> {
            Response<ResponseBody> res = null;
            final String[] sessionID = {""};
            Call<ResponseBody> request = aurionService.getSessionIdResponse(mLoginEditText.getText().toString(), mPasswordEditText.getText().toString());
            runOnUiThread(()-> {
                showToast(FastAurionApplication.getContext(), "Logging in...", Toast.LENGTH_SHORT);
            });
            request.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    String cookies = response.headers().get("Set-Cookie");
                    if(response.code() == 302){
                        Log.d("LOGIN", "Login success \n");
                        sessionID[0] = cookies.substring(cookies.indexOf("JSESSIONID"), cookies.indexOf(";", cookies.indexOf("JSESSIONID")));
                        PreferenceUtils.setLogin(sessionID[0]);

                        // Request home page to get user real name
                        // Stores it into PreferenceUtils
                        getNameThenLogIn();
                    }
                    else{
                        runOnUiThread(()-> {
                            showToast(FastAurionApplication.getContext(), "Login Failed", Toast.LENGTH_LONG);
                        });
                        mLoginEditText.setEnabled(true);
                        mPasswordEditText.setEnabled(true);
                    }

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.d("LOGIN", t.getMessage());
                    runOnUiThread(()-> {
                        showToast(FastAurionApplication.getContext(), "Connection error", Toast.LENGTH_LONG);
                    });
                    mLoginEditText.setEnabled(true);
                    mPasswordEditText.setEnabled(true);
                }
            });
        });
    }

    private void getNameThenLogIn(){
        String login = PreferenceUtils.getLogin();
        final String[] name = {""};
        Call<ResponseBody> requestName = aurionService.getHomePageHtml(login);
        requestName.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String body = null;
                try {
                    body = response.body().string();
                    name[0] = body.substring(body.indexOf("<h3>") + 4, body.indexOf("</h3>"));
                    PreferenceUtils.setName(name[0]);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(response.isSuccessful()){
                    Log.d("LOGIN", "Name parsing success \n");
                    startActivity(getHomeIntent(login, name[0]));
                    mLoginEditText.setEnabled(true);
                    mPasswordEditText.setEnabled(true);
                }
                else{
                    runOnUiThread(()-> {
                        showToast(FastAurionApplication.getContext(), "Name parsing failed", Toast.LENGTH_LONG);
                    });
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("LOGIN", t.getMessage());
                runOnUiThread(()-> {
                    showToast(FastAurionApplication.getContext(), "Connection error", Toast.LENGTH_LONG);
                });
                mLoginEditText.setEnabled(true);
                mPasswordEditText.setEnabled(true);
            }
        });
    }

    private Intent getHomeIntent(String userName, String name)
    {
        Intent intent = new Intent(this, MainActivity.class);
        final Bundle extras = new Bundle();
        extras.putString(Constants.Login.EXTRA_LOGIN, userName);
        extras.putString(Constants.Preferences.PREF_NAME, name);
        intent.putExtras(extras);
        return intent;
    }

    private void showToast(Context context, int resId, int duration){
        if(mToast != null) mToast.cancel();
        mToast = Toast.makeText(context, resId, duration);
        mToast.show();
    }

    private void showToast(Context context, CharSequence text, int duration){
        if(mToast != null) mToast.cancel();
        mToast = Toast.makeText(context, text, duration);
        mToast.show();
    }
}
