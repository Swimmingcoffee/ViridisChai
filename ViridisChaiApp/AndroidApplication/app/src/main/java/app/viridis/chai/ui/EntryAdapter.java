package app.viridis.chai.ui;

// Android UI imports
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

// AndroidX RecyclerView imports for list display
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

// App-specific imports
import app.viridis.chai.R;
import app.viridis.chai.model.JournalEntry;

import java.util.List;

// Adapter class to connect a list of JournalEntry items to a RecyclerView,
// shows a scrollable list of journal titles under the calendar.
public class EntryAdapter extends RecyclerView.Adapter<EntryAdapter.ViewHolder> {
    // The list of journal entries to display
    private List<JournalEntry> entries;
    // Listener interface to handle clicks
    private OnItemClickListener listener;

    // Define the interface for handling clicks
    public interface OnItemClickListener {
        void onItemClick(JournalEntry entry);
    }

    public EntryAdapter(List<JournalEntry> entries, OnItemClickListener listener) {
        this.entries = entries;  //List of journal entries to show
        this.listener = listener; //Listener for handling click events
    }

    // Called when RecyclerView needs a new ViewHolder,
    // inflates the layout item_entry.xml for each row
    @NonNull
    @Override
    public EntryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_entry, parent, false);
        return new ViewHolder(view);
    }

    // Update each row in the list
    @Override
    public void onBindViewHolder(@NonNull EntryAdapter.ViewHolder holder, int position) {
        // Checks if the entries list is not null and the position is valid
        if (entries != null && position >= 0 && position < entries.size()) {
            JournalEntry entry = entries.get(position);
            holder.titleTextView.setText(entry.title); // Show the entry title
            holder.itemView.setOnClickListener(v -> listener.onItemClick(entry)); // Handle clicks
        }
    }

    //Returns the total number of items to be shown in the list.
    @Override
    public int getItemCount() {
        return entries.size();
    }

    //Holds reference to the views in each item for better performance
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.entryTitle); // Find TextView in item_entry.xml
        }
    }
}

