package app.viridis.chai;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;

import app.viridis.chai.data.JournalDao;
import app.viridis.chai.data.JournalDatabase;
import app.viridis.chai.model.JournalEntry;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class JournalDaoTest {
    private JournalDao journalDao;
    private JournalDatabase db;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, JournalDatabase.class)
                .allowMainThreadQueries()
                .build();
        journalDao = db.journalDao();
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

    //Test inserting a journal entry and retrieving it
    @Test
    public void insertAndGetEntry() {
        JournalEntry entry = new JournalEntry();
        entry.date = "2025-04-15";
        entry.title = "Test Title";
        entry.content = "Test Content";
        journalDao.insert(entry);

        List<JournalEntry> entries = journalDao.getAllEntries();
        assertEquals(1, entries.size());
        assertEquals("Test Title", entries.get(0).title);
    }

    @Test
    //Test updating an entry's content
    public void updateEntry() {
        JournalEntry entry = new JournalEntry();
        entry.date = "2025-04-15";
        entry.title = "Original Title";
        entry.content = "Original Content";
        journalDao.insert(entry);

        JournalEntry insertedEntry = journalDao.getAllEntries().get(0);
        insertedEntry.title = "Updated Title";
        journalDao.update(insertedEntry);

        JournalEntry updated = journalDao.getEntryById(insertedEntry.id);
        assertEquals("Updated Title", updated.title);
    }

    //Test deleting a journal entry
    @Test
    public void deleteEntry() {
        JournalEntry entry = new JournalEntry();
        entry.date = "2025-04-15";
        entry.title = "Title";
        entry.content = "Content";
        journalDao.insert(entry);

        JournalEntry insertedEntry = journalDao.getAllEntries().get(0);
        journalDao.delete(insertedEntry);

        List<JournalEntry> entries = journalDao.getAllEntries();
        assertTrue(entries.isEmpty());
    }

    // Test filtering entries by specific date
    @Test
    public void getEntriesByDate() {
        JournalEntry entry1 = new JournalEntry();
        entry1.date = "2025-04-15";
        entry1.title = "Entry 1";
        entry1.content = "Content 1";

        JournalEntry entry2 = new JournalEntry();
        entry2.date = "2025-04-15";
        entry2.title = "Entry 2";
        entry2.content = "Content 2";

        journalDao.insert(entry1);
        journalDao.insert(entry2);

        List<JournalEntry> entries = journalDao.getEntriesByDate("2025-04-21");
        assertEquals(2, entries.size());
    }

    //Test retrieving a journal entry by ID
    @Test
    public void getEntryById() {
        JournalEntry entry = new JournalEntry();
        entry.date = "2025-04-15";
        entry.title = "Specific Title";
        entry.content = "Specific Content";
        journalDao.insert(entry);

        JournalEntry inserted = journalDao.getAllEntries().get(0);
        JournalEntry result = journalDao.getEntryById(inserted.id);
        assertNotNull(result);
        assertEquals("Specific Title", result.title);
    }
}
