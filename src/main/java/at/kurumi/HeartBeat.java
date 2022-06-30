package at.kurumi;

import at.kurumi.routines.StartupRoutine;
import jakarta.inject.Inject;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
 * HTTP Endpoint to check if payara is still running.
 */
@Path("/heartbeat")
public class HeartBeat {

    @Inject
    private StartupRoutine fuck;

    @GET
    public Response healthCheck() {
        fuck.toString();
        return Response.ok().entity("I'm alive\n").build();
    }
}
