package at.kurumi;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/healthCheck")
public class HealthCheckResponder {

    @GET
    public Response healthCheck() {
        return Response.ok().build();
    }
}
