package at.kurumi;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * Apparently needed to get JAX RS running.
 */
@ApplicationPath("/")
public class JAXRSActivator extends Application {
}
