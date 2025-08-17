package irk.staryo.model;

public class StartupPMF {
    private Startup startup;
    private DiscretePMF pmf;

    public Startup getStartup() {
        return startup;
    }

    public void setStartup(Startup startup) {
        this.startup = startup;
    }

    public DiscretePMF getPmf() {
        return pmf;
    }

    public void setPmf(DiscretePMF pmf) {
        this.pmf = pmf;
    }

    public StartupPMF(Startup startup, DiscretePMF pmf) {
        this.startup = startup;
        this.pmf = pmf;
    }
}
