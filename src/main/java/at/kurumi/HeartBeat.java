package at.kurumi;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;

/**
 * HTTP Endpoint to check if payara is still running.
 */
@Path("/heartbeat")
public class HeartBeat {

    private static final Logger LOG = Logger.getLogger("HeartBeat");

    @GET
    public Response healthCheck() {
        LOG.severe("Beat");
        return Response.ok().entity("I'm alive\n").build();
    }
}
