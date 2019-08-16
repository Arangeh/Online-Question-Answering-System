import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bson.Document;
import org.bson.conversions.Bson;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class DataBase {
	MongoCollection<Document> users , questions;
	public DataBase(){

       MongoClient mongoClient = new MongoClient(); 
        MongoDatabase database = mongoClient.getDatabase("StackoverFlow"); 
        users = database.getCollection("User"); 
        questions = database.getCollection("question");
	}
	
	private Bson in(String string, List<Integer> asList) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static void main(String[] args) {
		new DataBase();
	}
}
