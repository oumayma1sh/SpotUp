package tn.esprit.spotup.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import tn.esprit.spotup.R;
import tn.esprit.spotup.database.DataBaseHelper;
import tn.esprit.spotup.entities.Event;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> implements Filterable {
    private List<Event> eventList;
    private List<Event> eventListFull; // Liste complète pour le filtrage
    private Context context;
    private static final String TAG = "EventAdapter"; // Définissez une balise pour les logs

    public EventAdapter(Context context, List<Event> eventList) {
        this.context = context;
        this.eventList = eventList;
        this.eventListFull = new ArrayList<>(eventList); // Copie de la liste pour le filtrage
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event_card, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = eventList.get(position);

        Log.d(TAG, "Binding event at position: " + position);
        Log.d(TAG, "Event ID: " + event.get_id());
        Log.d(TAG, "Event Title: " + event.getTitle());

        holder.eventTitle_txt.setText(event.getTitle());
        holder.eventDescription_txt.setText(event.getDescription());
        holder.eventDateDebut_txt.setText(event.getDateDebut());
        holder.eventDateFin_txt.setText(event.getDateFin());
        holder.eventLocation_txt.setText(event.getLocation());
        holder.Price_txt.setText(event.getPrix());
        byte[] imageBytes = event.getImage();
        if (imageBytes != null && imageBytes.length > 0) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            holder.eventImage.setImageBitmap(bitmap);
            Log.d(TAG, "Image set for event ID: " + event.get_id());
        } else {
            holder.eventImage.setImageResource(R.drawable.ic_launcher_background
            );
            Log.d(TAG, "No image available for event ID: " + event.get_id());
        }

        holder.delete_button.setOnClickListener(view -> confirmDialog(event));

        // OnClickListener for opening UpdateEventActivity
        holder.update_button.setOnClickListener(view -> {
            Intent intent = new Intent(context, UpdateEventActivity.class);

            intent.putExtra("title", event.getTitle());
            intent.putExtra("description", event.getDescription());
            intent.putExtra("dateDebut", event.getDateDebut());
            intent.putExtra("dateFin", event.getDateFin());
            intent.putExtra("image", event.getImage() != null ? event.getImage().toString() : null);
            intent.putExtra("prix", event.getPrix());
            intent.putExtra("location", event.getLocation());

            context.startActivity(intent);
        });
        DataBaseHelper myDB = new DataBaseHelper(context);
        int participantCount = myDB.getParticipantCountByEventTitle(event.getTitle());
        holder.participantCountBadge.setText("Participants: " + participantCount);


        // OnClickListener for opening OverviewEventActivity
        holder.mainLayout.setOnClickListener(view -> {

            Intent intent = new Intent(context, OverViewEventActivity.class);
            intent.putExtra("title", event.getTitle());
            intent.putExtra("description", event.getDescription());
            intent.putExtra("dateDebut", event.getDateDebut());
            intent.putExtra("dateFin", event.getDateFin());
            intent.putExtra("location", event.getLocation());
           // intent.putExtra("image", event.getImage() != null ? event.getImage().toString() : null);
            intent.putExtra("prix", event.getPrix());
            context.startActivity(intent);
        });
    }
    public void updateEvents(List<Event> newEvents) {
        this.eventList = newEvents;
        notifyDataSetChanged();  // Refresh the RecyclerView
    }

    void confirmDialog(Event event) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete " + event.getTitle() + " ?");
        builder.setMessage("Are you sure you want to delete " + event.getTitle() + " ?");

        builder.setPositiveButton("Yes", (dialogInterface, i) -> {
            DataBaseHelper myDB = new DataBaseHelper(context);

            // Delete event from database and update UI
            myDB.deleteEventByTitle(event.getTitle());
            eventList.remove(event);
            notifyDataSetChanged();

            // Log deletion
            Log.d(TAG, "Event deleted: " + event.getTitle());

            // Trigger the AsyncTask to send the SMS after deletion is confirmed
            new SendSmsTask(event.getTitle()).execute();
        });

        builder.setNegativeButton("No", (dialogInterface, i) -> {
            // User canceled deletion, do nothing
        });

        builder.create().show();
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<Event> filteredList = new ArrayList<>();
                if (constraint == null || constraint.length() == 0) {
                    filteredList.addAll(eventListFull);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    for (Event event : eventListFull) {
                        if (event.getTitle().toLowerCase().contains(filterPattern)) {
                            filteredList.add(event);
                        }
                    }
                }

                FilterResults results = new FilterResults();
                results.values = filteredList;
                return results;
            }

            @Override
            @SuppressWarnings("unchecked")
            protected void publishResults(CharSequence constraint, FilterResults results) {
                eventList.clear();
                eventList.addAll((List<Event>) results.values);
                notifyDataSetChanged();
            }
        };
    }
    public static class EventViewHolder extends RecyclerView.ViewHolder {
        CardView mainLayout;
        TextView eventTitle_txt, eventDescription_txt, eventDateDebut_txt, eventLocation_txt, eventDateFin_txt, Price_txt;
        ImageView eventImage;
        ImageButton delete_button,update_button;
        TextView participantCountBadge;
        EventViewHolder(View itemView) {
            super(itemView);
            mainLayout = itemView.findViewById(R.id.mainLayout);
            eventTitle_txt = itemView.findViewById(R.id.eventTitle_txt);
            eventDescription_txt = itemView.findViewById(R.id.eventDescription_txt);
            eventDateDebut_txt = itemView.findViewById(R.id.eventDateDebut_txt);
            eventLocation_txt = itemView.findViewById(R.id.eventLocation_txt);
            eventDateFin_txt = itemView.findViewById(R.id.eventDateFin_txt);
            eventImage = itemView.findViewById(R.id.eventImage);
            Price_txt = itemView.findViewById(R.id.Price_txt);
            delete_button = itemView.findViewById(R.id.delete_button);
            update_button = itemView.findViewById(R.id.update_button);
            participantCountBadge= itemView.findViewById(R.id.participantCountBadge);
        }
    }
}
