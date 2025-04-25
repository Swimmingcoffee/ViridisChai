package app.viridis.chai.decorators;

// Android class used to apply text styling like text color
import android.text.style.ForegroundColorSpan;
// Material CalendarView library classes for customizing calendar details
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.util.Collection;
import java.util.HashSet;

//Custom decorator for highlighting dates with entries in the calendar
public class EventDecorator implements DayViewDecorator {
    private final int color; // Color to use for the decoration
    private final HashSet<CalendarDay> dates; // Set of dates to be decorated

    public EventDecorator(int color, Collection<CalendarDay> dates) {
        this.color = color;
        this.dates = new HashSet<>(dates); // Store dates in a set for fast lookup
    }

    //Determines whether a given day should be decorated
    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return dates.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        // Changes the text color of the date
        view.addSpan(new ForegroundColorSpan(color));
    }
}

