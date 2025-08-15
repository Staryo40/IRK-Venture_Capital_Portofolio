package irk.staryo.utils;

import irk.staryo.model.Startup;

import java.util.List;

public class Repository {
    private static final Repository instance = new Repository();
    public static Repository getInstance(){ return instance; }

    private List<Startup> startupList;

    public List<Startup> getStartupList() { return startupList; }
    public void setStartupList(List<Startup> list) { this.startupList = list; }
}
