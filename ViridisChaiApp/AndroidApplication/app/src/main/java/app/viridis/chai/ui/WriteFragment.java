package app.viridis.chai.ui;

// Android and media-related imports
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

// AndroidX libraries for fragment and permission handling
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

// Java standard utility and file handling imports
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

//Project-specific imports
import app.viridis.chai.MainActivity;
import app.viridis.chai.R;
import app.viridis.chai.model.JournalEntry;

/** This fragment handles journal writing functionalities:
 * - Input fields for title, date, and content
 * - Attach image from gallery
 * - Record audio note using the microphone
 * - Submit and store entry to the Room DB */
public class WriteFragment extends Fragment {
    // UI components
    private EditText dateField, titleField, contentField;
    private ImageButton recordAudioButton, chooseImageButton;
    private Button submitButton;
    private final Calendar myCalendar = Calendar.getInstance();

    // Media paths
    private String imagePath = "";
    private String audioPath = "";

    // Activity launchers
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private ActivityResultLauncher<String[]> requestPermissionLauncher;

    // Audio recording tools
    private MediaRecorder mediaRecorder;
    private boolean isRecording = false;

    // Initialize and return the fragment's UI view
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_write, container, false);

        // Setup permission and request required permissions
        setupPermissionLauncher();
        requestNecessaryPermissions();

        // Link layout views
        dateField = view.findViewById(R.id.dateField);
        titleField = view.findViewById(R.id.titleField);
        contentField = view.findViewById(R.id.contentField);
        recordAudioButton = view.findViewById(R.id.recordAudioButton);
        chooseImageButton = view.findViewById(R.id.chooseImageButton);
        submitButton = view.findViewById(R.id.submitButton);

        updateDateField(); // Set today’s date
        dateField.setOnClickListener(v -> showDatePickerDialog()); // Show calendar on click

        setupActivityResultLaunchers(); // Set up gallery image picker

        // Handle audio record button click
        recordAudioButton.setOnClickListener(v -> {
            if (!isRecording) {
                startRecording();
            } else {
                stopRecording();
            }
        });

        // Handle gallery image selection
        chooseImageButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent);
        });

        // Submit journal entry
        submitButton.setOnClickListener(v -> {
            String dateText = dateField.getText().toString();
            String titleText = titleField.getText().toString();
            String contentText = contentField.getText().toString();

            if (dateText.isEmpty() || titleText.isEmpty() || contentText.isEmpty()) {
                Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            JournalEntry entry = new JournalEntry();
            entry.date = dateText;
            entry.title = titleText;
            entry.content = contentText;
            entry.imagePath = imagePath;
            entry.audioPath = audioPath;

            MainActivity.db.journalDao().insert(entry);

            Toast.makeText(getContext(), "Entry saved!", Toast.LENGTH_SHORT).show();

            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new CalendarFragment())
                    .commit();
        });

        return view;
    }

    // Initializes the permission launcher
    private void setupPermissionLauncher() {
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                result -> {
                    boolean allGranted = true;

                    for (Boolean granted : result.values()) {
                        if (granted == null || !granted) {
                            allGranted = false;
                            break;
                        }
                    }

                    if (allGranted) {
                        Toast.makeText(getContext(), "Permissions granted. You can use all features now.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Some permissions were not granted. Limited functionality.", Toast.LENGTH_SHORT).show();
                    }

                }
        );
    }

    //Requests runtime permissions required for recording and accessing storage
    private void requestNecessaryPermissions() {
        List<String> permissions = new ArrayList<>();
        permissions.add(android.Manifest.permission.RECORD_AUDIO);
        permissions.add(android.Manifest.permission.READ_EXTERNAL_STORAGE);

        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.Q) {
            permissions.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        requestPermissionLauncher.launch(permissions.toArray(new String[0]));
    }

    //Handles result from selecting image from gallery
    private void setupActivityResultLaunchers() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri selectedImage = result.getData().getData();
                        if (selectedImage != null) {
                            imagePath = saveToInternalStorage(selectedImage, "gallery_image_" + System.currentTimeMillis() + ".jpg");
                            Toast.makeText(getContext(), "Image selected", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // Starts recording audio using MediaRecorder
    private void startRecording() {
        File audioFile = new File(requireContext().getFilesDir(), "audio_" + System.currentTimeMillis() + ".3gp");
        audioPath = audioFile.getAbsolutePath();

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setOutputFile(audioPath);

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            isRecording = true;
            Toast.makeText(getContext(), "Recording started...", Toast.LENGTH_SHORT).show();
            recordAudioButton.setImageResource(R.drawable.ic_stop);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Recording failed", Toast.LENGTH_SHORT).show();
        }
    }

    // Stops and saves the audio recording
    private void stopRecording() {
        try {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            isRecording = false;
            Toast.makeText(getContext(), "Recording saved", Toast.LENGTH_SHORT).show();
            recordAudioButton.setImageResource(R.drawable.ic_mic);
        } catch (RuntimeException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error stopping recording", Toast.LENGTH_SHORT).show();
        }
    }

    //Saves the selected gallery image to internal storage
    private String saveToInternalStorage(Uri uri, String filename) {
        try {
            InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
            File file = new File(requireContext().getFilesDir(), filename);
            FileOutputStream outputStream = new FileOutputStream(file);

            byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, len);
            }

            outputStream.close();
            inputStream.close();

            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    //Sets today's date by default in the journal entry
    private void updateDateField() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        dateField.setText(sdf.format(new Date()));
    }

    //Shows a date picker dialog and updates the selected date
    private void showDatePickerDialog() {
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, month);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        };

        new DatePickerDialog(getContext(), dateSetListener,
                myCalendar.get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    //Updates the date field with the chosen date
    private void updateLabel() {
        String myFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
        dateField.setText(sdf.format(myCalendar.getTime()));
    }
}
