package at.kurumi;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

@Path("/groceries")
public class Groceries {

    @Inject private Database db;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String index() {
        return "Use /add to add\n";
    }

    @GET
    @Path("/add")
    @Produces(MediaType.TEXT_PLAIN)
    public String add(@QueryParam("name") String item) {
        db.add(item);
        return "added: " + item;
    }
}
