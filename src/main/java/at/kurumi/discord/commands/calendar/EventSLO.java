package at.kurumi.discord.commands.calendar;

import at.kurumi.Database;
import at.kurumi.discord.commands.user.User;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Stateless
public class EventSLO {

    private final Database database;

    @Inject
    public EventSLO(Database database) {
        this.database = database;
    }

    public Optional<Event> createEvent(User creator, String title, Instant start, Instant end) {
        final var event = new Event();
        event.setTitle(title);
        event.setStart(Timestamp.from(start));
        event.setEnd(Timestamp.from(end));
        event.addUser(creator);
        return database.createEntity(event);
    }

    public boolean deleteEventById(User participant, int eventId) {
        final var event  = database.getSingleResultWhere("id", eventId, Event.class);

        return event.map(e -> e.getUsers().contains(participant)
                        && database.deleteEntityWhere("id", eventId, Event.class))
                .orElse(false);
    }

    /**
     * Share an event from one user to another. A user has to have access to an event (either by creating it
     * or being invited by someone that already has access to it) to share it.
     *
     * @param source the initiating user
     * @param eventId the event id
     * @param target the user to share the event with
     * @return Optional wrapped Event instance, or empty if sharing was not possible
     */
    public Optional<Event> shareEvent(User source, int eventId, User target) {
        final var event  = database.getSingleResultWhere("id", eventId, Event.class);

        final List<Consumer<Event>> transformations = List.of(
                e -> e.addUser(target)
        );

        return event.stream()
                .filter(e -> e.getUsers().contains(source))
                .map(e -> database.updateEntityWhere(transformations, "id", eventId, Event.class))
                .findAny()
                .orElse(Optional.empty());
    }

    public List<Event> getEventsInTimeSpanForUser(User user, Instant begin, Instant end) {
        return user.getEvents().stream().filter(event -> {
            final var startInstant = event.getStart().toInstant();
            final var endInstant = event.getEnd().toInstant();
            // Check if in time span
            return startInstant.isAfter(begin) && endInstant.isBefore(end);
        }).collect(Collectors.toList());
    }

    public List<Event> getEventsDueBy(Instant dueBy) {
        return database.getAllResultsFrom(Event.class).stream()
                .filter(event -> dueBy.isAfter(event.getStart().toInstant()))
                .collect(Collectors.toList());
    }

}
