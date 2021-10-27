package fr.yncrea.scorpion;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.gson.JsonArray;

import java.io.IOException;
import java.util.Calendar;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import fr.yncrea.scorpion.api.Aurion;
import fr.yncrea.scorpion.api.GithubService;
import fr.yncrea.scorpion.model.AurionResponse;
import fr.yncrea.scorpion.utils.PreferenceUtils;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final Executor mExecutorGit = Executors.newSingleThreadExecutor();
    private GithubService mGithubService;
    private EditText mLoginEditText;
    private EditText mPasswordEditText;
    private final Aurion aurion = new Aurion();
    private Toast mToast;
    private boolean canClick = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        mLoginEditText = (EditText) findViewById(R.id.loginEditText);
        mPasswordEditText = (EditText) findViewById(R.id.passwordEditText);

        if(!PreferenceUtils.getAcceptedEula().equals(getString(R.string.eula_id))) {
            PreferenceUtils.setPassword("");
            showEula();
        }

        final String username = PreferenceUtils.getLogin();
        final String password = PreferenceUtils.getPassword();

        mLoginEditText.setText(username);
        mPasswordEditText.setText(password);
        if(!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
            if(PreferenceUtils.getSessionId() != null && !PreferenceUtils.getSessionId().equals("")){
                startActivity(getHomeIntent());
                finish();
            }
            else{
                connect(username, password);
            }
        }
        canClick = true;

        mGithubService = new Retrofit.Builder()
                .baseUrl("https://api.github.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(GithubService.class);
        tryRequestUpdate();
    }

    public void tryRequestUpdate() {
        mExecutorGit.execute(() -> {
            Long lastTime = PreferenceUtils.getUpdateTime();
            long now = Calendar.getInstance().getTimeInMillis();

            if(now < lastTime + Long.parseLong(getString(R.string.update_timeout))) {
                return;
            }
            else {
                PreferenceUtils.setUpdateTime(now);
            }

            try {
                Response<JsonArray> releases = mGithubService.getReleases().execute();
                if(releases.body() == null) return;
                String latestVersion = releases.body().get(0).getAsJsonObject().get("tag_name").getAsString();
                if(!latestVersion.equals(getString(R.string.app_version))) {
                    String latestVersionDesc = releases.body().get(0).getAsJsonObject().get("body").getAsString();
                    //String latestVersionLink = releases.body().get(0).getAsJsonObject().get("assets").getAsJsonArray().get(0).getAsJsonObject().get("browser_download_url").getAsString();
                    runOnUiThread(() -> new AlertDialog.Builder(this)
                            .setTitle("Update available !")
                            .setMessage("A new version of Scorpion is available !\n\n"
                                    + "Version : " + latestVersion + "\n\n"
                                    + "Description : \n" + latestVersionDesc)
                            .setPositiveButton("Update now", (dialog, which) -> {
                                try {
                                    Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://liamabyss.github.io/Scorpion/"));
                                    startActivity(myIntent);
                                } catch (ActivityNotFoundException e) {
                                    //runOnUiThread(() -> showToast(this, "No application can handle this request." + " Please install a web browser",  Toast.LENGTH_LONG));
                                    e.printStackTrace();
                                }
                            })
                            .setNegativeButton("Maybe later", null).show());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void showEula() {
        runOnUiThread(() -> {
            AlertDialog dialog = new AlertDialog.Builder(this).create();

            View view = getLayoutInflater().inflate(R.layout.eula, null);
            CheckBox checkBox = (CheckBox) view.findViewById(R.id.eulaCheckBox);

            checkBox.setOnCheckedChangeListener((compoundButton, isChecked) -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(isChecked));

            ScrollView scrollView = (ScrollView) view.findViewById(R.id.eulaScrollView);
            DisplayMetrics displayMetrics = getApplicationContext().getResources().getDisplayMetrics();
            scrollView.setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, (int)(displayMetrics.heightPixels * 0.5)));

            dialog.setCancelable(false);
            dialog.setView(view);
            dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Continue",
                    (dialogInterface, which) -> PreferenceUtils.setAcceptedEula(getString(R.string.eula_id)));
            dialog.show();
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
        });
    }

    @Override
    public void onClick(View v) {
        if(!canClick) return;
        canClick = false;
        if(TextUtils.isEmpty(mLoginEditText.getText())){
            showToast(this, R.string.error_no_login, Toast.LENGTH_LONG);
            canClick = true;
            return;
        }

        if(TextUtils.isEmpty(mPasswordEditText.getText())){
            showToast(this, R.string.error_no_password, Toast.LENGTH_LONG);
            canClick = true;
            return;
        }

        connect(mLoginEditText.getText().toString(), mPasswordEditText.getText().toString());
    }

    void connect(String username, String password){
        mLoginEditText.setEnabled(false);
        mPasswordEditText.setEnabled(false);

        executor.execute(() -> {
            runOnUiThread(()-> showToast(ScorpionApplication.getContext(), "Logging in...", Toast.LENGTH_SHORT));
            AurionResponse sessionID = aurion.connect(username, password);
            if(sessionID.status == AurionResponse.SUCCESS){
                PreferenceUtils.setSessionId(sessionID.cookie);
                runOnUiThread(()-> showToast(ScorpionApplication.getContext(), "Retrieving data...", Toast.LENGTH_LONG));
                AurionResponse name = aurion.getHomePage(sessionID.cookie);
                if(name.status == AurionResponse.SUCCESS){
                    PreferenceUtils.setName(name.name);
                    PreferenceUtils.setLogin(username);
                    PreferenceUtils.setPassword(password);
                    runOnUiThread(() -> {
                        mLoginEditText.setEnabled(true);
                        mPasswordEditText.setEnabled(true);
                    });
                    canClick = true;
                    startActivity(getHomeIntent());
                    finish();
                }
                else {
                    runOnUiThread(() -> {
                        showToast(this, name.message, Toast.LENGTH_LONG);
                        mLoginEditText.setEnabled(true);
                        mPasswordEditText.setEnabled(true);
                    });
                    canClick = true;
                }
                return;
            }
            runOnUiThread(() -> {
                mLoginEditText.setEnabled(true);
                mPasswordEditText.setEnabled(true);
            });
            runOnUiThread(()-> showToast(this, sessionID.message, Toast.LENGTH_LONG));
            canClick = true;
        });
    }

    private Intent getHomeIntent()
    {
        return new Intent(this, MainActivity.class);
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
