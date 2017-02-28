package umm3601.todo;
import com.mongodb.MongoClient;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Sorts;
import com.mongodb.util.JSON;
import org.bson.Document;
import org.bson.types.ObjectId;
import java.io.IOException;
import java.util.*;
import java.util.Iterator;

import static com.mongodb.client.model.Filters.*;


public class TodoController{

    private final MongoCollection<Document> todoCollection;


    public TodoController() throws IOException{

        MongoClient mongoClient = new MongoClient();

        MongoDatabase db = mongoClient.getDatabase("test");

        todoCollection = db.getCollection("todos");

    }

    public String listTodos(Map<String, String[]> queryParams) {
        Document filterDoc = new Document();

        if (queryParams.containsKey("owner")) {
            String owner = queryParams.get("owner")[0];
            filterDoc = filterDoc.append("owner", owner);
        }

        if (queryParams.containsKey("category")) {
            String category = queryParams.get("category")[0];
            filterDoc = filterDoc.append("category", category);
        }

        if (queryParams.containsKey("status")) {
            boolean status;
            String temp = queryParams.get("status")[0];

            if (temp.equals("complete")) {
                status = true;
            } else {
                status = false;
            }
            filterDoc = filterDoc.append("status", status);
        }

        FindIterable<Document> matchingTodos = todoCollection.find(filterDoc);

        if (queryParams.containsKey("orderBy")) {
            String order = queryParams.get("orderBy")[0];
            matchingTodos.sort(Sorts.ascending(order));
        }

        if (queryParams.containsKey("contains")) {
            String parameter = queryParams.get("contains")[0];
            matchingTodos.filter(regex("body", parameter));

        }

        if (queryParams.containsKey("limit")) {
            int limit = Integer.parseInt(queryParams.get("limit")[0]);
            matchingTodos.limit(limit);
        }

            return JSON.serialize(matchingTodos);
    }

    private List<String> getAllField(String field){
        AggregateIterable<Document> uniqueFields = todoCollection.aggregate(Arrays.asList(Aggregates.group(field)));
        List<String> allTodos = new ArrayList<>();

        for(Document doc: uniqueFields){
            allTodos.add(doc.getString("_id"));
        }

        return allTodos;
    }

    private Document categoriesCompelete(List<String> cats){
        Document result = new Document();

        for(int i = 0; i < cats.size(); i++){
            String category = cats.get(i);
            result.append(category,
                    (float)completeField("category", category)/(float) fieldTotalMatching("category", category));
        }


        return result;
    }

    private Document ownersCompelete(List<String> cats){
        Document result = new Document();

        for(int i = 0; i < cats.size(); i++){
            String owner = cats.get(i);
            result.append(owner,
                    (float)completeField("owner", owner)/(float)fieldTotalMatching("owner", owner));
        }


        return result;
    }


    private long fieldTotalMatching(String field, String val) {
        Document countDoc = new Document();
        countDoc.append(field, val);
        return todoCollection.count(countDoc);
    }

    private long completeField(String fields, String val){
        Document countDoc = new Document();
        countDoc.append(fields, val);
        countDoc.append("status", true);

        return todoCollection.count(countDoc);
    }

    private float getPercentageComplete(){
        Document temp = new Document();
        float result = 0;

        temp.append("status", true);

        result = todoCollection.count(temp);

        return result / todoCollection.count();
    }

    public String todoSummary(){
        Document result = new Document();
        float percentageComplete = getPercentageComplete();

        List<String> allCats = getAllField("$category");
        List<String> allOwners = getAllField("$owner");
        result.append("percentage todos complete", percentageComplete);
        result.append("categories percent complete", categoriesCompelete(allCats));
        result.append("owners complete", ownersCompelete(allOwners));

        return JSON.serialize(result);
    }

    public String getTodo(String id) {
        FindIterable<Document> jsonTodos = todoCollection.find(eq("_id" , new ObjectId(id)));

        Iterator<Document> iterator = jsonTodos.iterator();

        Document todo = iterator.next();

        return todo.toJson();
    }
}
