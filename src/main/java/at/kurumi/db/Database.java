package at.kurumi.db;

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

    public static final String DBMS = "postgresql";

    private static final Logger LOG = LogManager.getLogger();

    private final SessionFactory sessionFactory;

    public Database() {
        LOG.info("Initializing Hibernate");
        sessionFactory = new Configuration().configure().buildSessionFactory();
    }

    /**
     * Open a session and return it. It is the caller's responsibility to close the session after using it.
     *
     * @return Session object, closeable
     */
    public Session openSession() {
        return sessionFactory.openSession();
    }

    /**
     * Open a database connection.
     *
     * @param url database url in the form of host:port
     * @param user the username
     * @param password the password
     * @return a connection object
     * @throws SQLException if something goes wrong
     */
    private Connection connect(String url, String user, String password) throws SQLException {
        final var properties = new Properties();
        properties.put("user", user);
        properties.put("password", password);
        // using this format jdbc:postgresql://host:port/
        final var jdbcString = String.format("jdbc:%s://%s/", DBMS, url);
        return DriverManager.getConnection(jdbcString, properties);
    }

}
