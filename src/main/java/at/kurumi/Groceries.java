package at.kurumi;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

@Path("/groceries")
public class Groceries {

    @Inject LoggerFacade log;
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
        var response = "added: " + item + "\n";
        log.info(response);
        return response;
    }

    @GET
    @Path("/list")
    @Produces(MediaType.TEXT_PLAIN)
    public String list() {
        final var sb = new StringBuilder();
        db.getItems().forEach(s -> sb.append(s).append("\n"));
        return sb.toString();
    }
}
