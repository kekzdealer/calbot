package at.kurumi;

import jakarta.servlet.http.HttpServlet;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/healthCheck")
public class HeartBeat extends HttpServlet {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String healthCheck() {
        return "healthy";
    }
}
