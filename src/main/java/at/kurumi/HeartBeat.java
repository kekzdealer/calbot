package at.kurumi;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
 * HTTP Endpoint to check if payara is still running.
 */
@Path("/heartbeat")
public class HeartBeat {

    @GET
    public Response healthCheck() {
        return Response.ok().entity("I'm alive\n").build();
    }
}
