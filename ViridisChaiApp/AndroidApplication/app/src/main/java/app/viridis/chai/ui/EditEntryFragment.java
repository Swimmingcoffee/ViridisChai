package app.viridis.chai.ui;

//Android & AndroidX imports for UI and fragments
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

// App files imports
import app.viridis.chai.MainActivity;
import app.viridis.chai.R;
import app.viridis.chai.model.JournalEntry;

public class EditEntryFragment extends Fragment {
    // UI elements for editing the title and content
    private EditText editTitle, editContent;
    private Button saveButton;
    // The journal entry being edited
    private JournalEntry entry;

    //Method to create a new instance of EditEntryFragment with a specific entry ID
    public static EditEntryFragment newInstance(int entryId) {
        EditEntryFragment fragment = new EditEntryFragment();
        Bundle args = new Bundle();
        args.putInt("entry_id", entryId);
        fragment.setArguments(args);
        return fragment;
    }

    // To initialize and create the UI for the fragment, inflate the layout and set up the view logic
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout XML file in this fragment
        View view = inflater.inflate(R.layout.fragment_edit_entry, container, false);

        // Get references to the EditText fields and Save button in the layout
        editTitle = view.findViewById(R.id.editTitle);
        editContent = view.findViewById(R.id.editContent);
        saveButton = view.findViewById(R.id.saveButton);

        // Retrieve the ID of the journal entry
        int entryId = getArguments().getInt("entry_id", -1);
        // If the entry ID is valid, fetch the entry from the database
        if (entryId != -1) {
            entry = MainActivity.db.journalDao().getEntryById(entryId);
            if (entry != null) {
                editTitle.setText(entry.title);
                editContent.setText(entry.content);
            }
        }
        // Handle clicking the Save button
        saveButton.setOnClickListener(v -> {
            entry.title = editTitle.getText().toString();
            entry.content = editContent.getText().toString();
            MainActivity.db.journalDao().update(entry); // Update entry information in the database
            requireActivity().getSupportFragmentManager().popBackStack(); // Go back to the previous screen
        });

        return view;
    }
}

