package app.viridis.chai.data;

// Room library annotations for defining database operations
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
// Data model representing a journal entry
import app.viridis.chai.model.JournalEntry;
// For returning entries in a list
import java.util.List;

//Data Access Object interface for performing operations on the journalentry table
@Dao
public interface JournalDao {
    @Insert
    void insert(JournalEntry entry);  // Insert a new journal entry into the database
    @Update
    void update(JournalEntry entry);    // Update an existing journal entry
    @Delete
    void delete(JournalEntry entry);     // Delete a specific journal entry
    @Query("SELECT * FROM journalentry WHERE date = :date")     // Retrieve all entries for a specific date
    List<JournalEntry> getEntriesByDate(String date);
    @Query("SELECT * FROM journalentry")  // Retrieve all journal entries in the database
    List<JournalEntry> getAllEntries();
    @Query("SELECT * FROM journalentry WHERE id = :id LIMIT 1")   // Retrieve a specific journal entry by its unique ID
    JournalEntry getEntryById(int id);

}


