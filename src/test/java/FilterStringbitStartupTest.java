import irk.staryo.model.FilteredStringbitStartups;
import irk.staryo.model.Startup;
import irk.staryo.utils.StartupCombination;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FilterStringbitStartupTest {
    @Test
    public void stringBitFilter(){
        Startup s1 = new Startup();
        s1.setName("1");
        s1.setTicketSize(2);
        s1.setSector("A");
        Startup s2 = new Startup();
        s2.setName("2");
        s2.setTicketSize(6);
        s2.setSector("A");
        Startup s3 = new Startup();
        s3.setName("3");
        s3.setTicketSize(7);
        s3.setSector("B");
        Startup s4 = new Startup();
        s4.setName("4");
        s4.setTicketSize(2);
        s4.setSector("B");
        Startup s5 = new Startup();
        s5.setName("5");
        s5.setTicketSize(5);
        s5.setSector("C");
        Startup s6 = new Startup();
        s6.setName("6");
        s6.setTicketSize(3);
        s6.setSector("C");

        List<Startup> check = new ArrayList<>(List.of(s1, s2, s3, s4, s5, s6));
        try{
            Map<Startup, Integer> mapping = StartupCombination.generateStartupIndex(check);
            FilteredStringbitStartups mask = StartupCombination.generateCombinationBitmasks(check, 10);

            for (Map.Entry<Startup, Integer> item : mapping.entrySet()) {
                System.out.println("Startup: " + item.getKey().getName() + ", Index: " + item.getValue());
            }
            System.out.println(mask.getStringBits());
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
