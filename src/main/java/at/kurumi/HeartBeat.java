package at.kurumi;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/heartbeat")
public class HeartBeat {

    @GET
    @Path("/")
    public Response healthCheck() {
        return Response.ok().entity("I'm alive\n").build();
    }
}
