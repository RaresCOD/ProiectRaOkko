package ro.ubbcluj.map.proiectraokko4.domain;

import java.time.LocalDateTime;
import java.util.List;

public class Event extends Entity<Long> {
    String name;
    String description;
    String location;
    LocalDateTime date;
    String participants;

    public Event(String name, String description, String location, LocalDateTime date, String participants) {
        this.name = name;
        this.description = description;
        this.location = location;
        this.date = date;
        this.participants = participants;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public String getParticipants() {
        return participants;
    }
}
