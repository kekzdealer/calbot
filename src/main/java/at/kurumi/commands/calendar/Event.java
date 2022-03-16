package at.kurumi.commands.calendar;

import at.kurumi.commands.user.User;
import jakarta.persistence.*;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Event")
public class Event implements Comparable<Event> {

    @Column(name = "id") @Id @GeneratedValue    private int id;                 // Table PK
    @Column(name = "title")                     private String title;           // Event title
    @Column(name = "start")                     private Timestamp start;        // Event start timestamp
    @Column(name = "end")                       private Timestamp end;          // Event end timestamp

    @ManyToMany
    @JoinTable(name = "event_user",
            joinColumns = {@JoinColumn(name = "fkEvent")},
            inverseJoinColumns = {@JoinColumn(name = "fkUser")})
    private Set<User> users = new HashSet<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Timestamp getStart() {
        return start;
    }

    public void setStart(Timestamp start) {
        this.start = start;
    }

    public Timestamp getEnd() {
        return end;
    }

    public void setEnd(Timestamp end) {
        this.end = end;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    public void addUser(User user) {
        users.add(user);
        user.getEvents().add(this);
    }

    public void removeUser(User user) {
        users.remove(user);
        user.getEvents().remove(this);
    }

    @Override
    public int compareTo(Event o) {
        return start.compareTo(o.getStart());
    }
}
