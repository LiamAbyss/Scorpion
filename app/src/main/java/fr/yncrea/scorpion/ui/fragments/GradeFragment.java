package fr.yncrea.scorpion.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import fr.yncrea.scorpion.R;

public class GradeFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    /*private static final String PARAM_DATE = "paramDate";
    private static final String PARAM_CODE = "paramCode";
    private static final String PARAM_LIBELLE = "paramLibelle";
    private static final String PARAM_NOTE = "paramNote";
    private static final String PARAM_REASON = "paramReason";
    private static final String PARAM_APPRECIATION = "paramAppreciation";
    private static final String PARAM_TEACHERS = "paramTeachers";

    private String mDate;
    private String mCode;
    private String mLibelle;
    private String mNote;
    private String mReason;
    private String mAppreciation;
    private String mTeachers;

    public GradeFragment() {
        // Required empty public constructor
    }

    public static GradeFragment newInstance(String date, String code, String libelle, Float note, String reason, String appreciation, String teachers) {
        GradeFragment fragment = new GradeFragment();
        Bundle args = new Bundle();
        args.putString(PARAM_APPRECIATION, appreciation);
        args.putString(PARAM_CODE, code);
        args.putString(PARAM_DATE, date);
        args.putString(PARAM_LIBELLE, libelle);
        args.putString(PARAM_NOTE, note.toString());
        args.putString(PARAM_REASON, reason);
        args.putString(PARAM_TEACHERS, teachers);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mAppreciation = getArguments().getString(PARAM_APPRECIATION);
            mCode = getArguments().getString(PARAM_CODE);
            mDate = getArguments().getString(PARAM_DATE);
            mLibelle = getArguments().getString(PARAM_LIBELLE);
            mNote = getArguments().getString(PARAM_NOTE);
            mReason = getArguments().getString(PARAM_REASON);
            mTeachers = getArguments().getString(PARAM_TEACHERS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.grade_listitem,container,false);
        ((TextView) view.findViewById(R.id.gradeAppreciationTextView)).setText(mAppreciation);
        ((TextView) view.findViewById(R.id.gradeCodeTextView)).setText(mCode);
        ((TextView) view.findViewById(R.id.gradeDateTextView)).setText(mDate);
        ((TextView) view.findViewById(R.id.gradeLibelleTextView)).setText(mLibelle);
        ((TextView) view.findViewById(R.id.gradeNoteTextView)).setText(mNote);
        ((TextView) view.findViewById(R.id.gradeReasonTextView)).setText(mReason);
        ((TextView) view.findViewById(R.id.gradeTeachersTextView)).setText(mTeachers);

        return view;
    }*/
}
