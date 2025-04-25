package app.viridis.chai;

// AndroidX libraries for activity, fragments, and database handling
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.room.Room;

import android.os.Bundle;

// App-specific imports of project files
import app.viridis.chai.data.JournalDatabase;
import app.viridis.chai.databinding.ActivityMainBinding;
import app.viridis.chai.ui.CalendarFragment;
import app.viridis.chai.ui.WelcomeFragment;
import app.viridis.chai.ui.WriteFragment;

//Main entry point of the application, the navigation and initializes the database and UI
public class MainActivity extends AppCompatActivity {
    // Access to Room database
    public static JournalDatabase db;
    // ViewBinding instance to easily access views from activity_main.xml
    private ActivityMainBinding binding;

    //Method called when the activity is first created
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize ViewBinding and set the content view
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Room Database for storing journal entries
        db = Room.databaseBuilder(getApplicationContext(),
                        JournalDatabase.class, "journal_db")
                .allowMainThreadQueries()
                .build();

        // Load the initial fragment (WelcomeFragment)
        loadFragment(new WelcomeFragment());

        // Setup Bottom Navigation to switch between fragments
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            if (item == binding.bottomNavigation.getMenu().findItem(R.id.nav_home)) {
                selectedFragment = new WelcomeFragment(); // Home screen
            } else if (item == binding.bottomNavigation.getMenu().findItem(R.id.nav_calendar)) {
                selectedFragment = new CalendarFragment(); // Calendar and entries
            } else if (item == binding.bottomNavigation.getMenu().findItem(R.id.nav_write)) {
                selectedFragment = new WriteFragment(); // Create a new journal entry
            }

            // Replace the current fragment if one is selected
            if (selectedFragment != null) {
                loadFragment(selectedFragment);
            }
            return true;
        });

    }

    // Method for fragment transitions
    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)  // Use container defined in activity_main.xml
                .commit();
    }
}

