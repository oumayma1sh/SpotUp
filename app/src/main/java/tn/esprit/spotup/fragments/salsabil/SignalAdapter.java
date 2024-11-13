package tn.esprit.spotup.fragments.salsabil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;


import java.util.List;

import tn.esprit.spotup.database.AppDatabase;
import tn.esprit.spotup.entities.Signal;

import tn.esprit.spotup.R;

public class SignalAdapter extends RecyclerView.Adapter<SignalAdapter.SignalViewHolder> {

    private List<Signal> signals;
    private Context context;
    private AppDatabase db;

    public SignalAdapter(List<Signal> signals, Context context) {
        this.signals = signals;
        this.context = context;
        db = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "spotup_database")
                .allowMainThreadQueries()  // Pour les tests uniquement
                .build();
    }

    @NonNull
    @Override
    public SignalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_signal, parent, false);
        return new SignalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SignalViewHolder holder, int position) {
        Signal signal = signals.get(position);
        holder.nameTextView.setText(signal.nameprofile);
        holder.causeTextView.setText(signal.cause);

        holder.deleteButton.setOnClickListener(v -> {
            db.SignalDao().delete(signal);
            signals.remove(position);
            notifyItemRemoved(position);
            Toast.makeText(context, "Signal supprimé", Toast.LENGTH_SHORT).show();
            ((AffichageSignal) context).refreshList(); // Rafraîchit la liste
        });
    }

    @Override
    public int getItemCount() {
        return signals.size();
    }

    static class SignalViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView causeTextView;
        Button deleteButton;

        SignalViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.signal_name);
            causeTextView = itemView.findViewById(R.id.signal_cause);
            deleteButton = itemView.findViewById(R.id.floatingActionButton3);
        }
    }




}
