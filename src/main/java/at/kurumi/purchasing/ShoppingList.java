package at.kurumi.purchasing;

import at.kurumi.Database;
import at.kurumi.logging.LoggingRouter;
import jakarta.persistence.PersistenceException;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ShoppingList {

    @Inject Database database;

    @Inject LoggingRouter log;

    public void add(String name, String note) {
        final var search = "SELECT * " +
                "FROM Groceries" +
                "WHERE name = ? AND note = ?;";

        final var update = "UPDATE isActive VALUES(?)" +
                "FROM Groceries" +
                "WHERE name = ? AND note = ?;";

        final var insert = "INSERT INTO Groceries(name, note)" +
                "VALUES(?, ?);";

        try (final var session = database.openSession()) {

        }


        final var item = new Groceries();
        item.setName(name);
        item.setNote(note);

    }

    public void remove(String name) {
        final var deactivate = "UPDATE Groceries" +
                "SET isActive = false" +
                "WHERE name = ? AND note = ?;";
    }

    public List<GroceriesI> list() {
        try (final var session = database.openSession()) {
            final var criteriaBuilder = session.getCriteriaBuilder();
            var criteria = criteriaBuilder.createQuery(Groceries.class);

            final var root = criteria.from(Groceries.class);
            criteria = criteria.select(root)
                    .where(criteriaBuilder.equal(root.get("active"), true));

            final var query = session.createQuery(criteria);

            final var results = query.getResultList();

            return new ArrayList<>(results);
        } catch (PersistenceException persistenceException) {
            log.internalError("List active Grocery items", "Database interaction failed");
            return Collections.emptyList();
        }
    }

    /**
     * Purge old entries that haven't been used in x time.
     *
     * @param timeSinceLastActive milliseconds since the last time this item was active
     */
    public void purge(long timeSinceLastActive) {
        final var first = System.currentTimeMillis();
        final var purge = "DELETE FROM Groceries" +
                "WHERE ? + ? > lastActive";
    }

    public List<GroceriesI> getSuggestionsForString(String input) {

        return Collections.emptyList();
    }
}
