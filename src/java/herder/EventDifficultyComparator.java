package herder.solver.event;

import java.util.Comparator;

public class EventDifficultyComparator implements Comparator<Event>
{
    public int compare(Event a, Event b)
    {
        if (a == null) return -1;
        if (b == null) return 1;
        return new Integer(a.getDependantEventCount()).compareTo(new Integer(b.getDependantEventCount()));
    }
}
