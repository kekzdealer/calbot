package at.kurumi;

import jakarta.enterprise.context.ApplicationScoped;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class Database {

    private final List<String> items = new ArrayList<>();

    public List<String> getItems() {
        return items;
    }

    public void add(String item) {
        items.add(item);
    }

}
