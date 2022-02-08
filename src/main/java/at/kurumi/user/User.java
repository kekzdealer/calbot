package at.kurumi.user;

import at.kurumi.calendar.Event;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "User")
public class User {

    @Column(name = "id") @Id @GeneratedValue    private int id;                 // Table PK
    @Column(name = "discordId")                 private long discordId;         // Discord user snowflake
    @Column(name = "name")                      private String name;            // Discord username or nickname if set

    @ManyToMany(mappedBy="users")
    private Set<Event> events = new HashSet<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getDiscordId() {
        return discordId;
    }

    public void setDiscordId(long discordId) {
        this.discordId = discordId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Event> getEvents() {
        return events;
    }

    public void setEvents(Set<Event> events) {
        this.events = events;
    }

}
