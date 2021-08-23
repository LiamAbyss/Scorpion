package fr.yncrea.scorpion.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.StringRes;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import fr.yncrea.scorpion.R;
import fr.yncrea.scorpion.ScorpionApplication;
import fr.yncrea.scorpion.utils.Course;
import fr.yncrea.scorpion.utils.Grade;

public class GradesAdapter extends RecyclerView.Adapter<GradesAdapter.GradesViewHolder> {
    private List<Grade> mGrades;
    private int mPosition;

    public GradesAdapter(List<Grade> grades) {
        mGrades = grades;
    }

    @Override
    public GradesAdapter.GradesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(ScorpionApplication.getContext()).inflate(R.layout.grade_listitem, parent, false);
        return new GradesAdapter.GradesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GradesAdapter.GradesViewHolder holder, int position) {
        if(mGrades.get(position) != null){
            DateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE);
            Grade currentGrade = mGrades.get(position);
            holder.date.setText(df.format(currentGrade.date));
            holder.code.setText(currentGrade.code.isEmpty() ? "/" : currentGrade.code);
            holder.libelle.setText(currentGrade.libelle.isEmpty() ? "/" : currentGrade.libelle);
            holder.note.setText(currentGrade.note);
            holder.appreciation.setText(currentGrade.appreciation.isEmpty() ? "/" : currentGrade.appreciation);
            holder.reason.setText(currentGrade.reasonForAbsence.isEmpty() ? "/" : currentGrade.reasonForAbsence);
            holder.teachers.setText(currentGrade.teachers.isEmpty() ? "/" : currentGrade.teachers);
        }
    }

    @Override
    public int getItemCount() {
        return mGrades.size();
    }

    public static class GradesViewHolder extends RecyclerView.ViewHolder{

        public final TextView date;
        public final TextView code;
        public final TextView libelle;
        public final TextView note;
        public final TextView reason;
        public final TextView appreciation;
        public final TextView teachers;

        public GradesViewHolder(final View view) {
            super(view);
            date = (TextView) view.findViewById(R.id.gradeDateTextView);
            code = (TextView) view.findViewById(R.id.gradeCodeTextView);
            libelle = (TextView) view.findViewById(R.id.gradeLibelleTextView);
            note = (TextView) view.findViewById(R.id.gradeNoteTextView);
            reason = (TextView) view.findViewById(R.id.gradeReasonTextView);
            appreciation = (TextView) view.findViewById(R.id.gradeAppreciationTextView);
            teachers = (TextView) view.findViewById(R.id.gradeTeachersTextView);
        }

    }
}
