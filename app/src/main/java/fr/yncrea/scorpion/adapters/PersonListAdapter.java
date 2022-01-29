package fr.yncrea.scorpion.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.MessageFormat;
import java.util.List;

import fr.yncrea.scorpion.R;
import fr.yncrea.scorpion.ScorpionApplication;
import fr.yncrea.scorpion.model.Person;

public class PersonListAdapter extends RecyclerView.Adapter<PersonListAdapter.PersonListViewHolder> {
    private List<Person> mPersonList;

    public PersonListAdapter(List<Person> personList) {
        mPersonList = personList;
    }

    @NonNull
    @Override
    public PersonListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(ScorpionApplication.getContext()).inflate(R.layout.person_listitem, parent, false);
        return new PersonListAdapter.PersonListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PersonListViewHolder holder, int position) {
        if(mPersonList.get(position) != null) {
            Person person = mPersonList.get(position);

            holder.firstName.setText(MessageFormat.format("{0} {1}", person.lastName, person.firstName));
        }
    }

    @Override
    public int getItemCount() {
        return mPersonList.size();
    }


    public class PersonListViewHolder extends RecyclerView.ViewHolder {
        public final TextView firstName;


        public PersonListViewHolder(@NonNull View view) {
            super(view);
            firstName = (TextView) view.findViewById(R.id.person_listitem_firstname_textView);
        }
    }
}
