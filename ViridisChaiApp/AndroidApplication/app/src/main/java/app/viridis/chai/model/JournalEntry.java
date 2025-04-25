package app.viridis.chai.model;

// Room annotations for defining a database entity and primary key
import androidx.room.Entity;
import androidx.room.PrimaryKey;

//Represents a single journal entry in the database
// Marked as a Room entity, maps to a table in the SQLite database
@Entity(tableName = "journalentry")  // Table name in the database
public class JournalEntry {
    @PrimaryKey(autoGenerate = true) // Auto-incremented primary key/ unique ID for each entry
    public int id;
    public String date; // Date of the entry in yyyy-MM-dd format
    public String title; // Title of the journal entry
    public String content; // Main content of the entry
    public String imagePath; //Path to an image associated with the entry
    public String audioPath; //Path to an audio file associated with the entry
}

