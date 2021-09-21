package fr.yncrea.scorpion;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import fr.yncrea.scorpion.api.Aurion;
import fr.yncrea.scorpion.api.AurionService;
import fr.yncrea.scorpion.utils.*;
import fr.yncrea.scorpion.utils.security.EncryptionUtils;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    private Executor executor = Executors.newSingleThreadExecutor();
    private EditText mLoginEditText;
    private EditText mPasswordEditText;
    private AurionService aurionService;
    private Aurion aurion = new Aurion();
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
                startActivity(getHomeIntent(PreferenceUtils.getSessionId(), PreferenceUtils.getName()));
                finish();
            }
            else{
                connect(username, password);
            }
        }
        canClick = true;
    }

    public void showEula() {
        runOnUiThread(() -> {
            AlertDialog dialog = new AlertDialog.Builder(this).create();

            View view = getLayoutInflater().inflate(R.layout.eula, null);
            CheckBox checkBox = (CheckBox) view.findViewById(R.id.eulaCheckBox);

            checkBox.setOnCheckedChangeListener((compoundButton, isChecked) -> {
                if(isChecked) {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                }
                else {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                }
            });

            ScrollView scrollView = (ScrollView) view.findViewById(R.id.eulaScrollView);
            DisplayMetrics displayMetrics = getApplicationContext().getResources().getDisplayMetrics();
            int dpHeight = (int) (displayMetrics.heightPixels / displayMetrics.density);
            scrollView.setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, (int)(displayMetrics.heightPixels * 0.5)));

            dialog.setCancelable(false);
            dialog.setView(view);
            dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Continue", (dialogInterface, which) -> {
                PreferenceUtils.setAcceptedEula(getString(R.string.eula_id));
                return;
            });
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

        String login = mLoginEditText.getText().toString();

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
            String[] sessionID = aurion.connect(username, password);
            if(sessionID[0].equals("success")){
                PreferenceUtils.setSessionId(sessionID[1]);
                runOnUiThread(()-> showToast(ScorpionApplication.getContext(), "Retrieving data...", Toast.LENGTH_LONG));
                String[] name = aurion.getName(sessionID[1]);
                if(name[0].equals("success")){
                    PreferenceUtils.setName(name[1]);
                    PreferenceUtils.setLogin(username);
                    PreferenceUtils.setPassword(password);
                    runOnUiThread(() -> {
                        mLoginEditText.setEnabled(true);
                        mPasswordEditText.setEnabled(true);
                    });
                    canClick = true;
                    startActivity(getHomeIntent(sessionID[1], name[1]));
                    finish();
                }
                else {
                    if(name[0].contains("connection")) {
                        runOnUiThread(() -> showToast(ScorpionApplication.getContext(), "Connection error", Toast.LENGTH_LONG));
                    }
                    else {
                        runOnUiThread(() -> showToast(ScorpionApplication.getContext(), "Authentication Failed", Toast.LENGTH_LONG));
                    }
                    runOnUiThread(() -> {
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
            if(sessionID[0].contains("connection")){
                runOnUiThread(()-> showToast(ScorpionApplication.getContext(), "Connection error", Toast.LENGTH_LONG));
            }
            else {
                runOnUiThread(() -> showToast(ScorpionApplication.getContext(), "Authentication Failed", Toast.LENGTH_LONG));
            }
            canClick = true;
        });
    }

    private Intent getHomeIntent(String userName, String name)
    {
        Intent intent = new Intent(this, MainActivity.class);
        final Bundle extras = new Bundle();
        extras.putString(Constants.Preferences.PREF_LOGIN, userName);
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
