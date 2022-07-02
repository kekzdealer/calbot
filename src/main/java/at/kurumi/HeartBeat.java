package at.kurumi;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * HTTP Endpoint to check if payara is still running.
 */
@WebServlet(
        name = "HeartBeat",
        description = "Check if the Server is still running",
        urlPatterns = {"/heartbeat"}
)
public class HeartBeat extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        var out = resp.getWriter();
        out.println("<p>I'm alive</p>");
    }
}
