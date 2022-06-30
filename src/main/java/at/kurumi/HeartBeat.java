package at.kurumi;

import at.kurumi.routines.StartupRoutine;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServlet;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

/**
 * HTTP Endpoint to check if payara is still running.
 */
@RequestScoped
@Path("heartbeat")
public class HeartBeat extends HttpServlet {

    @Inject
    private StartupRoutine fuck;

    @GET
    public Response healthCheck() {
        fuck.toString();
        return Response.ok().entity("I'm alive\n").build();
    }
}
