package irk.staryo.utils;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import irk.staryo.enums.FundingStage;
import irk.staryo.model.ProceedsScenarioTrend;
import irk.staryo.model.Startup;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class DatabaseLoader {
    public static void load(){
        try{
            MongoClient mongoClient = MongoClients.create(
                    "mongodb://admin:admin123@localhost:27017/?authSource=admin"
            );

            MongoDatabase database = mongoClient.getDatabase("startupsDB");
            MongoCollection<Document> collection = database.getCollection("startups");

            List<Startup> startups = new ArrayList<>();
            for (Document doc : collection.find()) {
                Startup s = new Startup();
                s.setName(doc.getString("name"));
                s.setDescription(doc.getString("description"));

                s.setFundingStage(FundingStage.valueOf(doc.getString("fundingStage")));

                s.setTicketSize(doc.getInteger("ticketSize"));
                s.setLocation(doc.getString("location"));
                s.setFoundYear(doc.getInteger("foundedYear"));

                Document trendDoc = doc.get("proceedsScenarioTrend", Document.class);
                ProceedsScenarioTrend trend = new ProceedsScenarioTrend(
                        trendDoc.getList("Pessimistic", Integer.class),
                        trendDoc.getList("Realistic", Integer.class),
                        trendDoc.getList("Optimistic", Integer.class)
                );
                s.setProceedsScenarioTrend(trend);

                startups.add(s);
            }
            Repository rep = Repository.getInstance();
            rep.setStartupList(startups);

            mongoClient.close();
        } catch (Exception e){
            // ERROR CHECKING
        }
    }

}
