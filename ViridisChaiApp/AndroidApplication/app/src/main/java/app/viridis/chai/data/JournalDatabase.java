package app.viridis.chai.data;

// Room library classes for defining a Room database
import androidx.room.Database;
import androidx.room.RoomDatabase;
// Data model representing a table in the database
import app.viridis.chai.model.JournalEntry;

/**Main database class for the app using Room.
 Defines the database configuration*/
@Database(entities = {JournalEntry.class}, version = 1, exportSchema = false)
public abstract class JournalDatabase extends RoomDatabase {
    //Returns the DAO that provides methods for interacting with the journalentry table.
    public abstract JournalDao journalDao();
}
