package irk.staryo.model;

import java.util.ArrayList;
import java.util.List;

public class PortofolioDpResult {
    private List<Startup> startups;
    private DiscretePMF distribution;

    public PortofolioDpResult() {
        this.startups = new ArrayList<>();
        this.distribution = null;
    }

    public PortofolioDpResult(List<Startup> startups, DiscretePMF distribution) {
        this.startups = startups;
        this.distribution = distribution;
    }

    public DiscretePMF getDistribution() {
        return distribution;
    }

    public List<Startup> getStartups() {
        return startups;
    }
}
