package irk.staryo.model;

import java.util.List;
import java.util.Map;

public class FilteredStringbitStartups {
    private List<String> stringBits;
    private Map<Integer, Startup> startupMap;

    public FilteredStringbitStartups(List<String> stringBits, Map<Integer, Startup> startupMap) {
        this.stringBits = stringBits;
        this.startupMap = startupMap;
    }

    public List<String> getStringBits() {
        return stringBits;
    }

    public void setStringBits(List<String> stringBits) {
        this.stringBits = stringBits;
    }

    public Map<Integer, Startup> getStartupMap() {
        return startupMap;
    }

    public void setStartupMap(Map<Integer, Startup> startupMap) {
        this.startupMap = startupMap;
    }
}
