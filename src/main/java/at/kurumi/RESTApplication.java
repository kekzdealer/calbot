package at.kurumi;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/rest")
public class RESTApplication extends Application {

    private final Set<Object> singletons = new HashSet<>();
    private final Set<Class<?>> empty = new HashSet<>();

    public RESTApplication() {
        singletons.add(new Groceries());
    }

    @Override
    public Set<Object> getSingletons() {
        return super.getSingletons();
    }

    @Override
    public Set<Class<?>> getClasses() {
        return super.getClasses();
    }
}
