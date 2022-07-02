package at.kurumi;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

/**
 * Apparently needed to get JAX RS running.
 */
@ApplicationPath("/rest")
public class JAXRSActivator extends Application {

}