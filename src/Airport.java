

import java.util.ArrayList;
import java.util.List;

public class Airport {
    private String name;
    private List<String> alias;

    public Airport(String name, List<String> alias) {
        this.name = name;
        this.alias = alias;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getAlias() {
        return alias;
    }

    public void setAlias(List<String> alias) {
        this.alias = alias;
    }
}
