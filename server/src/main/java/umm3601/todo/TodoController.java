package umm3601.todo;

import com.mongodb.MongoClient;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Sorts;
import com.mongodb.util.JSON;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

import static com.mongodb.client.model.Filters.eq;


public class TodoController {

    private final MongoCollection<Document> todoCollection;

    public TodoController() throws IOException{

        MongoClient mongoClient = new MongoClient();

        MongoDatabase db = mongoClient.getDatabase("test");

        todoCollection = db.getCollection("todos");
    }

    public String listTodos(Map<String, String[]> queryParams){
        Document filterDoc = new Document();

        if(queryParams.containsKey("owner")){
            String owner = queryParams.get("owner")[0];
            filterDoc = filterDoc.append("owner", owner);
        }

        if(queryParams.containsKey("category")){
            String category = queryParams.get("category")[0];
            filterDoc = filterDoc.append("category", category);
        }

        if(queryParams.containsKey("contains")){
            String parameter = queryParams.get("contains")[0];
            filterDoc = filterDoc.append("body", parameter);
        }

        if(queryParams.containsKey("status")){
            boolean status;
            String temp = queryParams.get("status")[0];

            if(temp.equals("true")){
                status = true;
            }else {
                status = false;
            }
            filterDoc = filterDoc.append("status", status);
        }

        FindIterable<Document> matchingTodos = todoCollection.find(filterDoc);

        if(queryParams.containsKey("orderBy")){
            String order = queryParams.get("orderBy")[0];
            matchingTodos.sort(Sorts.descending(order));
        }

        if(queryParams.containsKey("limit")){
            int limit = Integer.parseInt(queryParams.get("limit")[0]);
            matchingTodos.limit(limit);
        }

        return JSON.serialize(matchingTodos);
    }

    public String getTodo(String id) {
        FindIterable<Document> jsonTodos = todoCollection.find(eq("_id" , new ObjectId(id)));

        Iterator<Document> iterator = jsonTodos.iterator();

        Document todo = iterator.next();

        return todo.toJson();
    }



}
