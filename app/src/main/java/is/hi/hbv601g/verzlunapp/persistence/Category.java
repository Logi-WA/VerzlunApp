package is.hi.hbv601g.verzlunapp.persistence;

import java.io.Serializable;
import java.util.UUID;

public class Category implements Serializable {
    private UUID id;
    private String name;

    public Category() {
    }

    // --- Getters and Setters ---
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}