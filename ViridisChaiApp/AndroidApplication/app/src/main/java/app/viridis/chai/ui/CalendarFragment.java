package app.viridis.chai.ui;

// Android framework imports
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log; // Used for logging debug info
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

// AndroidX and RecyclerView components
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

// CalendarView imports
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

// Java utilities
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// Different files of the project
import app.viridis.chai.MainActivity;
import app.viridis.chai.R;
import app.viridis.chai.model.JournalEntry;
import app.viridis.chai.decorators.EventDecorator;

public class CalendarFragment extends Fragment {

    // UI elements
    private MaterialCalendarView calendarView;
    private RecyclerView entriesRecyclerView;
    private TextView entriesLabel;

    // Adapter and data
    private EntryAdapter entryAdapter;
    private List<JournalEntry> entryList = new ArrayList<>();

    public CalendarFragment() {
        // Required empty constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        // Initialize UI components
        calendarView = view.findViewById(R.id.calendarView);
        entriesRecyclerView = view.findViewById(R.id.entriesRecyclerView);
        entriesLabel = view.findViewById(R.id.entriesLabel);

        // Set up the RecyclerView with vertical scrolling and an adapter
        entriesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        entryAdapter = new EntryAdapter(entryList, this::openEntryDetail);
        entriesRecyclerView.setAdapter(entryAdapter);

        // Respond to user clicking on a date in the calendar
        calendarView.setOnDateChangedListener((widget, date, selected) -> {
            String dateString = String.format("%04d-%02d-%02d", date.getYear(), date.getMonth() + 1, date.getDay());
            loadEntriesForDate(dateString);
        });

        // Change the colour of calendar dates that have entries
        decorateEntryDates();

        return view;
    }

    //Loads journal entries for the selected date from the database
    private void loadEntriesForDate(String date) {
        Log.d("CalendarFragment", "Trying to load entries for date: " + date);

        entryList.clear();
        List<JournalEntry> entries = MainActivity.db.journalDao().getEntriesByDate(date);

        if (entries != null && !entries.isEmpty()) {
            Log.d("CalendarFragment", "Found " + entries.size() + " entries");
            for (JournalEntry entry : entries) {
                Log.d("CalendarFragment", "Entry title: " + entry.title + " | date: " + entry.date);
            }

            entryList.addAll(entries);
            entriesLabel.setText("Entries");
            entriesRecyclerView.setVisibility(View.VISIBLE);
        } else {
            Log.d("CalendarFragment", "No entries found for this date");
            entriesLabel.setText("No entries");
            entriesRecyclerView.setVisibility(View.GONE);
        }

        entriesLabel.setVisibility(View.VISIBLE);
        entryAdapter.notifyDataSetChanged();
    }

    //Highlights the dates in the calendar that have entries saved
    private void decorateEntryDates() {
        List<JournalEntry> allEntries = MainActivity.db.journalDao().getAllEntries(); // Add this DAO method
        Set<CalendarDay> entryDates = new HashSet<>();

        for (JournalEntry entry : allEntries) {
            try {
                String[] parts = entry.date.split("-");
                int year = Integer.parseInt(parts[0]);
                int month = Integer.parseInt(parts[1]) - 1;
                int day = Integer.parseInt(parts[2]);
                entryDates.add(CalendarDay.from(year, month, day));
            } catch (Exception e) {
                e.printStackTrace(); // Handles invalid dates
            }
        }
        // Change the date color using custom decorator
        calendarView.addDecorator(new EventDecorator(Color.parseColor("#319496"), entryDates));
    }

    //Navigates to the detail view of a selected entry
    private void openEntryDetail(JournalEntry entry) {
        Fragment detailFragment = EntryDetailFragment.newInstance(entry.id);
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, detailFragment)
                .addToBackStack(null)
                .commit();
    }

}
