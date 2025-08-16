package irk.staryo.utils;

import irk.staryo.model.Startup;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Repository {
    private static final Repository instance = new Repository();
    public static Repository getInstance(){ return instance; }

    private List<Startup> startupList;
    private Map<String, Color> sectorColor;

    public List<Startup> getStartupList() { return startupList; }
    public void setStartupList(List<Startup> list) { this.startupList = list; }

    public Map<String, Color> getSectorColor() { return sectorColor; }
    public void setSectorColor(Map<String, Color> sectorColor) {this.sectorColor = sectorColor;}

    public Map<String, List<Startup>> getSortedStartups(){
        Map<String, List<Startup>> map = new HashMap<>();
        for (Startup su : startupList){
            if (map.containsKey(su.getSector())){
                List<Startup> li = map.get(su.getSector());
                li.add(su);
            } else {
                List<Startup> li =new ArrayList<>();
                li.add(su);
                map.put(su.getSector(), li);
            }
        }

        return map;
    }
}
