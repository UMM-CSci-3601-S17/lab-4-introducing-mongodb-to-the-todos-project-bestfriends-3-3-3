package umm3601.todo;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.*;
import org.bson.codecs.*;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.json.JsonReader;
import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class TodoControllerSpec
{
    private TodoController todoController;
    private String blancheIdString;

    @Before
    public void clearAndPopulateDB() throws IOException {
        MongoClient mongoClient = new MongoClient();
        MongoDatabase db = mongoClient.getDatabase("test");
        MongoCollection<Document> todoDocument = db.getCollection("todos");
        todoDocument.drop();
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
        ObjectId blancheID = new ObjectId();
        BasicDBObject blanche = new BasicDBObject("_id", blancheID);
        blanche = blanche.append("owner", "John")
                .append("_id", "58895985186754887e0381f5")
                .append("category", "software design");
        blancheIdString = blancheID.toHexString();
        todoDocument.insertMany(testTodos);
        todoDocument.insertOne(Document.parse(blanche.toJson()));

        // It might be important to construct this _after_ the DB is set up
        // in case there are bits in the constructor that care about the state
        // of the database.
        todoController = new TodoController();
    }

    // http://stackoverflow.com/questions/34436952/json-parse-equivalent-in-mongo-driver-3-x-for-java
    private BsonArray parseJsonArray(String json) {
        final CodecRegistry codecRegistry
                = CodecRegistries.fromProviders(Arrays.asList(
                new ValueCodecProvider(),
                new BsonValueCodecProvider(),
                new DocumentCodecProvider()));

        JsonReader reader = new JsonReader(json);
        BsonArrayCodec arrayReader = new BsonArrayCodec(codecRegistry);

        return arrayReader.decode(reader, DecoderContext.builder().build());
    }

    private static String getName(BsonValue val) {
        BsonDocument doc = val.asDocument();
        return ((BsonString) doc.get("owner")).getValue();
    }

    @Test
    public void getAllTodos() {
        Map<String, String[]> emptyMap = new HashMap<>();
        String jsonResult = todoController.listTodos(emptyMap);
        BsonArray docs = parseJsonArray(jsonResult);

        assertEquals("Should be 4 todos", 4, docs.size());
        List<String> names = docs
                .stream()
                .map(TodoControllerSpec::getName)
                .sorted()
                .collect(Collectors.toList());
        List<String> expectedNames = Arrays.asList("Blanche", "Fry", "Fry", "John");
        assertEquals("Names should match", expectedNames, names);
    }

    @Test
    public void getTodosWithSoftwareDesign() {
        Map<String, String[]> argMap = new HashMap<>();
        argMap.put("category", new String[] { "software design" });
        String jsonResult = todoController.listTodos(argMap);
        BsonArray docs = parseJsonArray(jsonResult);

        assertEquals("Should be 2 todos", 2, docs.size());
        List<String> names = docs
                .stream()
                .map(TodoControllerSpec::getName)
                .sorted()
                .collect(Collectors.toList());
        List<String> expectedNames = Arrays.asList("Blanche", "John");
        assertEquals("Names should match", expectedNames, names);
    }

}
