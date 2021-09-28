package fr.yncrea.scorpion;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import fr.yncrea.scorpion.utils.PreferenceUtils;
import fr.yncrea.scorpion.utils.ThemedActivity;

public class OptionsActivity extends ThemedActivity implements View.OnClickListener{

    private boolean saved = false;
    private String lastComponentName;
    private Spinner themeSpinner;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);
        getSupportActionBar().setSubtitle(PreferenceUtils.getName());

        lastComponentName = getPackageName();
        switch (PreferenceUtils.getTheme()) {
            case R.style.JuniaTheme:
                lastComponentName += ".JuniaIconActivity";
                break;
            case R.style.JuniaISENTheme:
                lastComponentName += ".JuniaISENIconActivity";
                break;
            case R.style.JuniaHEITheme:
                lastComponentName += ".JuniaHEIIconActivity";
                break;
            case R.style.JuniaISATheme:
                lastComponentName += ".JuniaISAIconActivity";
                break;
            default:
                lastComponentName += ".OldISENIconActivity";
                break;
        }


        themeSpinner = (Spinner) findViewById(R.id.themeSpinner);
        String[] themes = getResources().getStringArray(R.array.themes);
        for(int i = 0; i < themes.length; i++) {
            if(PreferenceUtils.getTheme() == R.style.JuniaTheme && themes[i].equals("Junia")
                || PreferenceUtils.getTheme() == R.style.JuniaISENTheme && themes[i].equals("Junia ISEN")
                || PreferenceUtils.getTheme() == R.style.JuniaHEITheme && themes[i].equals("Junia HEI")
                || PreferenceUtils.getTheme() == R.style.JuniaISATheme && themes[i].equals("Junia ISA")
                || PreferenceUtils.getTheme() == R.style.OldISENTheme && themes[i].equals("Ancien ISEN")) {
                themeSpinner.setSelection(i);
                break;
            }
        }
    }


    @Override
    public void onClick(View v) {
        saved = true;
        switch (themeSpinner.getSelectedItemPosition()) {
            case 0:
                PreferenceUtils.setTheme(R.style.JuniaTheme);
                setAppIcon(getPackageName() + ".JuniaIconActivity");
                break;
            case 1:
                PreferenceUtils.setTheme(R.style.JuniaISENTheme);
                setAppIcon(getPackageName() + ".JuniaISENIconActivity");
                break;
            case 2:
                PreferenceUtils.setTheme(R.style.JuniaHEITheme);
                setAppIcon(getPackageName() + ".JuniaHEIIconActivity");
                break;
            case 3:
                PreferenceUtils.setTheme(R.style.JuniaISATheme);
                setAppIcon(getPackageName() + ".JuniaISAIconActivity");
                break;
            case 4:
                PreferenceUtils.setTheme(R.style.OldISENTheme);
                setAppIcon(getPackageName() + ".OldISENIconActivity");
                break;
            default:
                saved = false;
                return;
        }

        recreate();
    }

    public void setAppIcon(String activeName) {
        getPackageManager().setComponentEnabledSetting(
                new ComponentName(this.getPackageName(), lastComponentName),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

        getPackageManager().setComponentEnabledSetting(
                new ComponentName(this.getPackageName(), activeName),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }

    @Override
    public void onBackPressed() {
        saved = false;
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(saved) {
            Intent intent = new Intent(this, SplashScreenActivity.class);
            startActivity(intent);
        }
    }
}
