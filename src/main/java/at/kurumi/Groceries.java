package at.kurumi;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;

@Path("groceries")
public class Groceries {

    @POST
    @Path("add")
    public String add(@QueryParam("name") String item) {
        return "added: " + item;
    }
}
