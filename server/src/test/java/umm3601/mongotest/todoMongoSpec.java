package umm3601.mongotest;

import com.mongodb.MongoClient;
import com.mongodb.client.*;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Sorts;
import org.bson.Document;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Projections.*;
import static org.junit.Assert.*;


@Ignore public class todoMongoSpec {

    private MongoCollection<Document> todoDocuments;

    @Before
    public void clearAndPopulateDB() {
        MongoClient mongoClient = new MongoClient();
        MongoDatabase db = mongoClient.getDatabase("testingdb");
        todoDocuments = db.getCollection("todos");
        todoDocuments.drop();
        List<Document> testTodos = new ArrayList<>();
        testTodos.add(Document.parse("{\n" +
                "                    _id: \"58895985a22c04e761776d54\",\n" +
                "                    owner: \"Blanche\", \n" +
                "                    category: \"software design\",\n" +
                "                }"));
        testTodos.add(Document.parse("{\n" +
                "                    _id: \"58895985c1849992336c219b\",\n" +
                "                    owner: \"Fry\", \n" +
                "                    category: \"video games\",\n" +
                "                }"));
        testTodos.add(Document.parse("{\n" +
                "                    _id: \"58895985ae3b752b124e7663\",\n" +
                "                    owner: \"Fry\", \n" +
                "                    category: \"homework\",\n" +
                "                }"));
        todoDocuments.insertMany(testTodos);
    }

    private List<Document> intoList(MongoIterable<Document> documents) {
        List<Document> todos = new ArrayList<>();
        documents.into(todos);
        return todos;
    }

    private int countTodos(FindIterable<Document> documents) {
        List<Document> todos = intoList(documents);
        return todos.size();
    }

    @Test
    public void shouldBeThreeTodos() {
        FindIterable<Document> documents = todoDocuments.find();
        int numberOfTodos = countTodos(documents);
        assertEquals("Should be 3 total users", 3, numberOfTodos);
    }

    @Test
    public void shouldBeOneBlanche() {
        FindIterable<Document> documents = todoDocuments.find(eq("owner", "Blanche"));
        int numberOfTodos = countTodos(documents);
        assertEquals("Should be 1 Chris", 1, numberOfTodos);
    }


    @Test
    public void shouldBeOneFry() {
        FindIterable<Document> documents = todoDocuments.find(eq("category", "homework"));
        int numberOfTodos = countTodos(documents);
        assertEquals("Should be 1 Fry", 1, numberOfTodos);
    }

    @Test
    public void FryAndHomework() {
        FindIterable<Document> documents
                = todoDocuments.find(and(gt("owner", "Fry"),
                eq("category", "homework")));
        List<Document> docs = intoList(documents);
        assertEquals("Should be 1", 1, docs.size());
        assertEquals("First should be Fry", "Fry",
                docs.get(0).get("owner"));
    }


}