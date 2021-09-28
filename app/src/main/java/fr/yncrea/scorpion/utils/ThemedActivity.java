package fr.yncrea.scorpion.utils;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AppCompatActivity;

import fr.yncrea.scorpion.R;

public class ThemedActivity extends AppCompatActivity {

    protected Resources.Theme theme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(PreferenceUtils.getTheme());
        theme = new ContextThemeWrapper(this, PreferenceUtils.getTheme()).getTheme();
        super.onCreate(savedInstanceState);
    }
}
