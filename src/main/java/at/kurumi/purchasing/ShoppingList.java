package at.kurumi.purchasing;

import at.kurumi.Database;
import at.kurumi.LoggerFacade;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.RollbackException;
import me.xdrop.fuzzywuzzy.FuzzySearch;
import me.xdrop.fuzzywuzzy.model.BoundExtractedResult;
import org.hibernate.Session;
import org.hibernate.query.Query;

import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ShoppingList {

    private static final LoggerFacade LOG = LoggerFacade.getLogger(ShoppingList.class);

    @Inject Database database;

    private Query<Groceries> getIdQuery(Session session, String name, String note) {
        final var cb = session.getCriteriaBuilder();
        final var cr = cb.createQuery(Groceries.class);
        final var root = cr.from(Groceries.class);

        final var nameEq = cb.equal(root.get("name"), name);
        final var noteEq = cb.equal(root.get("note"), note);

        cr.select(root).where(cb.and(nameEq, noteEq));

        return session.createQuery(cr);
    }

    /**
     * Add a Grocery item to the list. If the item is in the backlog, reactivate it instead.
     *
     * @param name Name of the item
     * @param note Additional notes
     */
    public void add(String name, String note) {
        try (final var session = database.openSession()) {
            // Check if the item was added before
            Optional<Groceries> item;
            try {
                item = Optional.of(getIdQuery(session, name, note)
                        .getSingleResult());
            } catch (NoResultException noResultException) {
                item = Optional.empty();
            }

            final var transaction = session.beginTransaction();
            item.ifPresentOrElse(i -> i.setActive(true), () -> {
                final var i = new Groceries();
                i.setName(name);
                i.setNote(note);
                session.persist(i);
            });
            transaction.commit();
        } catch (RollbackException rollbackException) {
            LOG.error("Rollback occurred while adding/activating grocery item");
        } catch (PersistenceException persistenceException) {
            LOG.error("Persistence exception while adding/activating grocery item");
        }
    }

    public void remove(String name, String note) {
        try (final var session = database.openSession()) {
            // Check if the item was added before
            Optional<Groceries> item;
            try {
                item = Optional.of(getIdQuery(session, name, note)
                        .getSingleResult());
            } catch (NoResultException noResultException) {
                item = Optional.empty();
            }

            final var transaction = session.beginTransaction();
            item.ifPresentOrElse(i -> i.setActive(false), () -> {
                // TODO add feedback
            });
            transaction.commit();
        } catch (RollbackException rollbackException) {
            LOG.error("Rollback occurred while deactivating grocery item");
        } catch (PersistenceException persistenceException) {
            LOG.error("Persistence exception while deactivating grocery item");
        }
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
            LOG.error("Persistence exception while listing grocery items");
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

    public List<GroceriesI> getSuggestionsForName(String nameFragment) {
        final var set = database.getAllResultsFrom(Groceries.class);
        return FuzzySearch.extractSorted(nameFragment, set, Groceries::getName, 10)
                .stream().map(BoundExtractedResult::getReferent)
                .collect(Collectors.toList());
    }

    public List<GroceriesI> getSuggestionsForNote(String name, String noteFragment) {
        final var set = database.getResultsWhere("name", name, Groceries.class);
        return FuzzySearch.extractSorted(noteFragment, set, Groceries::getNote, 10)
                .stream().map(BoundExtractedResult::getReferent)
                .collect(Collectors.toList());
    }
}
