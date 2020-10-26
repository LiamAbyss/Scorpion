package fr.yncrea.fastaurion;

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


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        mLoginEditText = (EditText) findViewById(R.id.loginEditText);
        mPasswordEditText = (EditText) findViewById(R.id.passwordEditText);

        final String login = PreferenceUtils.getLogin();
        if(!TextUtils.isEmpty(login)) startActivity(getHomeIntent(login));

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
            Toast.makeText(this, R.string.error_no_login, Toast.LENGTH_LONG).show();
            return;
        }

        String login = mLoginEditText.getText().toString();

        if(TextUtils.isEmpty(mPasswordEditText.getText())){
            Toast.makeText(this, R.string.error_no_password, Toast.LENGTH_LONG).show();
            return;
        }

        executor.execute(() -> {
            Response<ResponseBody> res = null;
            final String[] sessionID = {""};
            Call<ResponseBody> request = aurionService.getSessionIdResponse(mLoginEditText.getText().toString(), mPasswordEditText.getText().toString());
            request.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    String cookies = response.headers().get("Set-Cookie");
                    if(response.code() == 302){
                        Log.d("LOGIN", "Login success \n");
                        sessionID[0] = cookies.substring(cookies.indexOf("JSESSIONID"), cookies.indexOf(";", cookies.indexOf("JSESSIONID")));
                        PreferenceUtils.setLogin(sessionID[0]);
                        startActivity(getHomeIntent(sessionID[0]));
                    }
                    else{
                        runOnUiThread(()->{
                            Toast.makeText(FastAurionApplication.getContext(), "Login Failed", Toast.LENGTH_LONG).show();
                        });
                    }

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.d("ok", t.getMessage());
                }
            });
        });
    }


    private Intent getHomeIntent(String userName)
    {
        Intent intent = new Intent(this,MainActivity.class );
        final Bundle extras = new Bundle();
        extras.putString(Constants.Login.EXTRA_LOGIN, userName);
        intent.putExtras(extras);
        return intent;
    }
}
