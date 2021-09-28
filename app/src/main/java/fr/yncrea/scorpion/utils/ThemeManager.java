package fr.yncrea.scorpion.utils;

import android.content.res.Resources;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;

import androidx.annotation.ColorInt;

import fr.yncrea.scorpion.R;
import fr.yncrea.scorpion.ScorpionApplication;

public class ThemeManager {
    public static int getColorId(int attrId) {
        Resources.Theme theme = new ContextThemeWrapper(ScorpionApplication.getContext(), PreferenceUtils.getTheme()).getTheme();
        TypedValue typedValue = new TypedValue();
        theme.resolveAttribute(attrId, typedValue, true);
        return typedValue.resourceId;
    }
}
