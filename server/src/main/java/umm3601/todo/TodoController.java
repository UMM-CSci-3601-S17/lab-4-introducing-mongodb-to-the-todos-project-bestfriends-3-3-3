package umm3601.todo;
import java.lang.Object;

import com.mongodb.DBCursor;
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

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Filters;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.Iterator;

import static com.mongodb.client.model.Filters.*;


public class TodoController implements ActionListener{

    private final MongoCollection<Document> todoCollection;


    public TodoController() throws IOException{

        MongoClient mongoClient = new MongoClient();

        MongoDatabase db = mongoClient.getDatabase("test");

        todoCollection = db.getCollection("todos");



    }

    public String listTodos(Map<String, String[]> queryParams){
        Document filterDoc = new Document();
        Block<Document> printBlock = new Block<Document>() {
            @Override
            public void apply(final Document document) {
                System.out.println(document.toJson());
            }
        };

        if (queryParams.containsKey("todoSummary")){

            todoCollection.aggregate(
                    Arrays.asList(
                            Aggregates.match(Filters.eq("owner", "Blanche")),
                            Aggregates.group("owner = Blanche", Accumulators.sum("count", 1))
                    )
            ).forEach(printBlock);
            return JSON.serialize(printBlock);
        }

        if(queryParams.containsKey("owner")){
            String owner = queryParams.get("owner")[0];
            filterDoc = filterDoc.append("owner", owner);
        }

        if(queryParams.containsKey("category")){
            String category = queryParams.get("category")[0];
            filterDoc = filterDoc.append("category", category);
        }

        if(queryParams.containsKey("status")){
            boolean status;
            String temp = queryParams.get("status")[0];

            if(temp.equals("complete")){
                status = true;
            }else {
                status = false;
            }
            filterDoc = filterDoc.append("status", status);
        }

        FindIterable<Document> matchingTodos = todoCollection.find(filterDoc);

        if(queryParams.containsKey("orderBy")){
            String order = queryParams.get("orderBy")[0];
            matchingTodos.sort(Sorts.ascending(order));
        }

        if(queryParams.containsKey("contains")) {
            String parameter = queryParams.get("contains")[0];
            matchingTodos.filter(regex("body", parameter));
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



    public void actionPerformed(ActionEvent actionEvent) {

    }
}
