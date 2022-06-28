package at.kurumi;

import jakarta.ws.rs.core.Response;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/healthCheck")
public class HealthCheckResponder {

    @GET
    public Response healthCheck() {
        return Response.ok().build();
    }
}
