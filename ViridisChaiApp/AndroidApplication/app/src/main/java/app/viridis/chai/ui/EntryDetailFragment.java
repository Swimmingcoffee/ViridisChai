package app.viridis.chai.ui;

// Android system and UI imports
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

// AndroidX support libraries for fragments
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

// Project file imports
import app.viridis.chai.MainActivity;
import app.viridis.chai.R;
import app.viridis.chai.model.JournalEntry;

//Displays the full content of a single journal entry (title, content, image, and audio)
public class EntryDetailFragment extends Fragment {
    // Key used to pass entry ID into this fragment
    private static final String ARG_ENTRY_ID = "entry_id";
    // The journal entry to display
    private JournalEntry entry;
    // For audio playback
    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;

    private Button playAudioButton;

    //Method to create a new instance of this fragment with the entry ID as argument
    public static EntryDetailFragment newInstance(int entryId) {
        EntryDetailFragment fragment = new EntryDetailFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_ENTRY_ID, entryId);
        fragment.setArguments(args);
        return fragment;
    }

    //To create the view for this fragment,
    //loads the entry based on the  ID and populates the UI with its data
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_entry_detail, container, false);

        // Retrieve entry ID passed as argument
        int entryId = getArguments().getInt(ARG_ENTRY_ID);
        entry = MainActivity.db.journalDao().getEntryById(entryId);

        // Initialize UI elements
        TextView titleView = view.findViewById(R.id.detailTitle);
        TextView contentView = view.findViewById(R.id.detailContent);
        Button deleteButton = view.findViewById(R.id.deleteButton);
        Button editButton = view.findViewById(R.id.editButton);
        ImageView photoView = view.findViewById(R.id.detailImage);
        playAudioButton = view.findViewById(R.id.playAudioButton);

        if (entry != null) {
            // Display title and content
            titleView.setText(entry.title);
            contentView.setText(entry.content);

            // Show image if available
            if (entry.imagePath != null && !entry.imagePath.isEmpty()) {
                photoView.setImageURI(Uri.parse(entry.imagePath));
                photoView.setVisibility(View.VISIBLE);
            } else {
                photoView.setVisibility(View.GONE);
            }

            // Handle audio playback if audio exists
            if (entry.audioPath != null && !entry.audioPath.isEmpty()) {
                playAudioButton.setVisibility(View.VISIBLE);
                playAudioButton.setText("Play Audio");

                playAudioButton.setOnClickListener(v -> toggleAudioPlayback());
            } else {
                playAudioButton.setVisibility(View.GONE);
            }

            // Delete entry from database
            deleteButton.setOnClickListener(v -> {
                MainActivity.db.journalDao().delete(entry);
                requireActivity().getSupportFragmentManager().popBackStack();;
            });

            // Navigate to edit screen
            editButton.setOnClickListener(v -> {
                Fragment editFragment = EditEntryFragment.newInstance(entry.id);
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, editFragment)
                        .addToBackStack(null)
                        .commit();
            });
        }

        return view;
    }

    //Handles toggling between play and pause states of the audio
    private void toggleAudioPlayback() {
        try {
            if (isPlaying) {
                // Pause and reset audio
                mediaPlayer.pause();
                mediaPlayer.seekTo(0);
                playAudioButton.setText("Play Audio");
                isPlaying = false;
            } else {
                // Start audio playback
                if (mediaPlayer != null) {
                    mediaPlayer.release();
                }

                mediaPlayer = MediaPlayer.create(getContext(), Uri.parse(entry.audioPath));
                mediaPlayer.setOnCompletionListener(mp -> {
                    playAudioButton.setText("Play Audio");
                    isPlaying = false;
                });

                mediaPlayer.start();
                playAudioButton.setText("Pause Audio");
                isPlaying = true;
            }
        } catch (Exception e) {
            Toast.makeText(getContext(), "Unable to play audio", Toast.LENGTH_SHORT).show();
        }
    }

    //Release audio player resources when fragment is destroyed
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
