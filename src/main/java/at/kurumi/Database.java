package at.kurumi;

import at.kurumi.calendar.Event;
import at.kurumi.user.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Database {

    private static final Logger LOG = LogManager.getLogger();

    private final SessionFactory sessionFactory;

    public Database() {
        LOG.info("Initializing Hibernate");
        sessionFactory = new Configuration().configure("hibernate.cfg.xml")
                .addAnnotatedClass(User.class)
                .addAnnotatedClass(Event.class)
                .buildSessionFactory();
    }

    /**
     * Open a session and return it. It is the caller's responsibility to close the session after using it.
     *
     * @return Session object, closeable
     */
    public Session openSession() {
        return sessionFactory.openSession();
    }

}
