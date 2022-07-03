package at.kurumi;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

@Path("/groceries")
public class Groceries {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String index() {
        return "Use /add to add";
    }

    @POST
    @Path("/add")
    @Produces(MediaType.TEXT_PLAIN)
    public String add(@QueryParam("name") String item) {
        return "added: " + item;
    }
}
