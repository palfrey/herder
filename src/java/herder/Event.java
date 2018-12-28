package herder.solver.event;

import java.util.UUID;
import java.util.List;
import org.joda.time.Interval;
import org.joda.time.LocalDate;

import org.optaplanner.core.api.domain.variable.CustomShadowVariable;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

@PlanningEntity(difficultyComparatorClass=EventDifficultyComparator.class)
public class Event implements Cloneable {
    private Event chainedEvent;
    @CustomShadowVariable(variableListenerClass = EventUpdatingVariableListener.class,
            sources = {@CustomShadowVariable.Source(variableName = "slot")})
    public Event getChainedEvent() {return chainedEvent;}
    public void setChainedEvent(Event event) {this.chainedEvent = event;}

    public Event() {
        this(UUID.randomUUID());
    }

    private UUID externalId;
    private UUID id;

    public Event(UUID externalId) {
        this.externalId = externalId;
        this.id = UUID.randomUUID();
    }

    private Interval slot;
    @PlanningVariable(valueRangeProviderRefs={"slotRange"}, nullable=true)
    public Interval getSlot(){return slot;}
    public void setSlot(Interval slot) {this.slot = slot;}

    private LocalDate preferredDay;
    public LocalDate getPreferredDay() {return preferredDay;}
    public void setPreferredDay(LocalDate preferredDay) { this.preferredDay = preferredDay;}

    private List<Interval> preferredSlots;
    public List<Interval> getPreferredSlots(){return preferredSlots;}
    public void setPreferredSlots(List<Interval> preferredSlots) { this.preferredSlots = preferredSlots;}

    private int eventDay;
    public int getEventDay() { return eventDay;}
    public void setEventDay(int eventDay) {this.eventDay = eventDay;}

    private int eventCount;
    public int getDependantEventCount() { return eventCount;}
    public void setDependantEventCount(int eventCount) {this.eventCount = eventCount;}

    private EventType eventType;
    public EventType getEventType() { return eventType;}
    public void setEventType(EventType eventType) {this.eventType = eventType;}

    public UUID getId() { return id;}
    public UUID getExternalId() { return externalId;}

    private List<Object> people;
    public List<Object> getPeople(){return people;}
    public void setPeople(List<Object> people) { this.people = people;}

    private String name;
    public String getName() { return name;}
    public void setName(String name) {this.name = name;}

    private List<LocalDate> notAvailableDays;
    public List<LocalDate> getNotAvailableDays(){return notAvailableDays;}
    public void setNotAvailableDays(List<LocalDate> notAvailableDays) { this.notAvailableDays = notAvailableDays;}

    private Event laterEvent;
    public Event getLaterEvent() {return laterEvent;}
    public void setLaterEvent(Event laterEvent) {this.laterEvent = laterEvent;}

    public Event clone() {
        Event ret = new Event(externalId);
        ret.id = this.id;
        ret.slot = this.slot;
        ret.preferredSlots = this.preferredSlots;
        ret.preferredDay = this.preferredDay;
        ret.eventDay = this.eventDay;
        ret.eventCount = this.eventCount;
        ret.eventType = this.eventType;
        ret.people = this.people;
        ret.name = this.name;
        ret.notAvailableDays = this.notAvailableDays;
        ret.chainedEvent = this.chainedEvent;
        ret.laterEvent = this.laterEvent;
        return ret;
    }

    public void fixChainedEventRef(List<Event> events) {
        if (chainedEvent !=null) {
            for (Event e: events) {
                if (e.id == chainedEvent.id) {
                    chainedEvent = e;
                    break;
                }
            }
        }
        if (laterEvent !=null) {
            for (Event e: events) {
                if (e.id == laterEvent.id) {
                    laterEvent = e;
                }
            }
        }
    }
}
