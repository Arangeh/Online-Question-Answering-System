import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Vector;
import com.mongodb.*;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Indexes;

import static com.mongodb.client.model.Filters.*;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.result.DeleteResult;
import static com.mongodb.client.model.Updates.*;
import com.mongodb.client.result.UpdateResult;
import org.bson.conversions.Bson;
import javax.jws.soap.SOAPBinding.Use;
import javax.print.Doc;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter.DEFAULT;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Collation;

public class Server {
    // a unique ID for each connection
    private static int uniqueId;
    private static String resultQues ;
    private static String Show;
    // an ArrayList to keep the list of the Client
    private ArrayList<ClientThread> al;
    // to display time
    private SimpleDateFormat sdf;
    // the port number to listen for connection
    private int port;
    // the boolean that will be turned of to stop the Server
    private boolean keepGoing;
    // to make a connection to a running MongoDB instance
    private MongoClient mongoClient;
    // to access a database
    private MongoDatabase database;
    private MongoCollection<Document> users , questions , answer , comment , edit , rate;
    private Server(int port) {
        // the port
        this.port = port;
        // to display hh:mm:ss
        sdf = new SimpleDateFormat("HH:mm:ss");
        // ArrayList for the Client list
        al = new ArrayList<ClientThread>();
        MongoClient mongoClient = new MongoClient(); 
        database = mongoClient.getDatabase("StackoverFlow"); 
        users = database.getCollection("User"); 
        questions = database.getCollection("question");    
        answer = database.getCollection("Answer");
        comment = database.getCollection("comment");
        rate = database.getCollection("Rate");
        edit = database.getCollection("Edit");
    }
    
	public void CreatUser(String name , String username , String email , String Password ,ArrayList<String> Favorits){
	    Document doc = new Document("name", name) 
                .append("username", username) 
                .append("Email", email) 
                .append("password", Password)
                .append("Favorites" , Favorits)
                .append("score", "0");
        users.insertOne(doc);
	}

	public int Login(String UserName , String Password){
		Document u = users.find(eq("username" , UserName)).first();
		String pas = "";
		if(u!=null)
			pas = (String) u.get("password");
		if(u==null){
			return 0;
		}
		else if(pas.equals(Password))
			return 2;
		else
			return 1;
	}
	
	public boolean Check_ID(String UserName){
		Document u = users.find(eq("username" , UserName)).first();
		if(u!=null)
			return false;
		else
			return true;
	}
	
	public boolean Check_Email(String Email){
		Document u = users.find(eq("email" , Email)).first();
		if(u!=null)
			return false;
		else
			return true;
	}
	
	private void AddQuestion(String Question , String Username , String Keys){
		String []Det = Keys.split(" ");
		Arrays.sort(Det);
		String key = "" ;
		for(int i=0 ; i<Det.length ; i++){
			System.out.println(Det[i]);
			key += Det[i] + " ";
		}
		System.out.println(key);
		String id = Integer.toString((int) (questions.count() + 1));
		Document doc = new Document("username" , Username)
				.append("score" , "0")
				.append("data", System.currentTimeMillis())
				.append("id", id)
				.append("question", Question)
				.append("KEYS", key)
				.append("lastversion", "")
				.append("beforedit", "")
				.append("editedby", "")
				.append("isprivate", "2");
		questions.insertOne(doc);
	}
	
    private void start() {
        keepGoing = true;
		/* create socket Server and wait for connection requests */
        try
        {
            // the socket used by the Server
            ServerSocket serverSocket = new ServerSocket(port);

            // infinite loop to wait for connections
            while(keepGoing)
            {
                // format message saying we are waiting
                display("Server waiting for Clients on port " + port + ".");

                Socket socket = serverSocket.accept();  	// accept connection
                // if I was asked to stop
                if(!keepGoing)
                    break;
                ClientThread t = new ClientThread(socket);  // make a thread of it
                al.add(t);									// save it in the ArrayList
                t.start();
            }
            // I was asked to stop
            try {
                serverSocket.close();
                for (ClientThread tc : al) {
                    try {
                        tc.sInput.close();
                        tc.sOutput.close();
                        tc.socket.close();
                    } catch (IOException ioE) {
                        // not much I can do
                    }
                }
            }
            catch(Exception e) {
                display("Exception closing the Server and clients: " + e);
            }
        }
        // something went bad
        catch (IOException e) {
            String msg = sdf.format(new Date()) + " Exception on new ServerSocket: " + e + "\n";
            display(msg);
        }
    }


    //Display an event
    private void display(String msg) {
        String time = sdf.format(new Date()) + " " + msg;
        System.out.println(time);
    }


    private synchronized String SearchByQuestion(String message){
        	String[] words = message.split("\\s");//splits the string based on whitespace
        	resultQues = "" ;
        	Document allKeywords = questions.find(new Document("allKeywords",
                new Document("$exists", true))).first();
        String keywords = "" ;
                ArrayList<String> q = (ArrayList<String>) allKeywords.get("allKeywords");
                for(String w:words){
                    if (q.contains(w)){
                        keywords += w + " ";    
//                        keywords.add(w);
//                        System.out.println(w);
                    }
                }                 
                questions.createIndex(Indexes.text("KEYS"));
                Block<Document> printBlock = new Block<Document>() {
                @Override
                public void apply(final Document document) {
//                    System.out.println(document.toJson());
                   // to find all the answers and comments for the current document
                	resultQues += getQandA(document);
                }
                 };
                 questions.find(Filters.text(keywords))
                .projection(Projections.metaTextScore("score")).sort(Sorts.metaTextScore("score")).forEach(printBlock);
        //   Vector<String> keywords = new Vector<>();
    //    Document allKeywords = collection.find(new Document("allKeywords", new Document("$exists", true))).first();
    //    ArrayList<String> q = (ArrayList<String>) allKeywords.get("allKeywords");
    //    for(String w:words){
    //        if (q.contains(w)){
   //             keywords.add(w);
  //              System.out.println(w);
 //           }
//        }
         return resultQues;
    }

    private synchronized String SearchByQuestionNormal(String message){
    	String[] words = message.split("\\s");//splits the string based on whitespace
    	resultQues = "" ;
    	Document allKeywords = questions.find(new Document("allKeywords",
            new Document("$exists", true))).first();
    String keywords = "" ;
            ArrayList<String> q = (ArrayList<String>) allKeywords.get("allKeywords");
            for(String w:words){
                if (q.contains(w)){
                    keywords += w + " ";    
//                    keywords.add(w);
//                    System.out.println(w);
                }
            }                 
            questions.createIndex(Indexes.text("KEYS"));
            Block<Document> printBlock = new Block<Document>() {
            @Override
            public void apply(final Document document) {
//                System.out.println(document.toJson());
               // to find all the answers and comments for the current document
            	resultQues += getQandANormal(document);
            }
             };
             questions.find(Filters.text(keywords))
            .projection(Projections.metaTextScore("score")).sort(Sorts.metaTextScore("score")).forEach(printBlock);
    //   Vector<String> keywords = new Vector<>();
//    Document allKeywords = collection.find(new Document("allKeywords", new Document("$exists", true))).first();
//    ArrayList<String> q = (ArrayList<String>) allKeywords.get("allKeywords");
//    for(String w:words){
//        if (q.contains(w)){
//             keywords.add(w);
//              System.out.println(w);
//           }
//    }
     return resultQues;
}

    
    
    // for a Client who logoff using the LOGOUT message
    private synchronized void remove(int id) {
        // scan the array list until we found the Id
        for(int i = 0; i < al.size(); ++i) {
            ClientThread ct = al.get(i);
            // found it
            if(ct.id == id) {
                al.remove(i);
                return;
            }
        }
    }
    
    private String get_Question_Comment(String quesID){
    	System.out.println("ques_id" +":" + quesID);
    	Show = "";
    	MongoCursor<Document> cursor = (MongoCursor<Document>) comment.find(eq("ques_id",quesID)).iterator();
    	try {
            while (cursor.hasNext()) {
          	  Document doc = cursor.next();
          	  Show += doc.get("id") + ". ";
          	  Show += doc.get("comment") + "\n";
            }
      	} finally {
            cursor.close();
        }
      	return Show;
    }
   

    private String get_Question_CommentNormal(String quesID){
    	Show = "";
    	MongoCursor<Document> cursor = (MongoCursor<Document>) comment.find(eq("ques_id",quesID)).iterator();
    	try {
            while (cursor.hasNext()) {
          	  Document doc = cursor.next();
          	  if(doc.get("isprivate").equals("2")){
          		Show += doc.get("id") + ". ";
          	  	Show += doc.get("comment") + "\n";
          	  }
            }
      	} finally {
            cursor.close();
        }
      	return Show;
    }
    
    
    private String get_Answer_Comment(String ansID){
    	Show = "\n";
    	MongoCursor<Document> cursor = (MongoCursor<Document>) comment.find(eq("answer_id",ansID)).iterator();
    	try {
            while (cursor.hasNext()) {
          	  Document doc = cursor.next();
          	  Show += doc.get("id") + ". ";
          	  Show += doc.get("comment") + "\n";
            }
      	} finally {
            cursor.close();
        }
      	return Show;
    }
    
    private String get_Answer_CommentNormal(String ansID){
    	Show = "";
    	MongoCursor<Document> cursor = (MongoCursor<Document>) comment.find(eq("answer_id",ansID)).iterator();
    	try {
            while (cursor.hasNext()) {
          	  Document doc = cursor.next();
          	  if(doc.get("isprivare").equals("2")){
          		  Show += doc.get("id") + ". ";
          		  Show += doc.get("comment") + "\n";
          	  }
            }
      	} finally {
            cursor.close();
        }
      	return Show;
    }


    
    private String getQandA(Document document){
        String resultQues = "";
        String id = (String)document.get("id");
        MongoCursor<Document> cursor = (MongoCursor<Document>) answer.find(eq("ques_id",id)).iterator();
        resultQues += "Question: ";
        resultQues += document.get("id") + ". ";
        resultQues += document.get("question") ;
        if(!document.get("editedby").equals(""))
        resultQues += "\n" + "Queston Befor edited By :"  + document.get("editedby") + " :" + document.get("beforedit") ;
        if(!document.get("lastversion").equals(""))
        resultQues += "\n" + "lastVersion : " + document.get("lastversion");
        resultQues += "\n" + "Qusetion Comments :" + "\n";
        resultQues += get_Question_Comment((String)document.get("id"));
        resultQues += "Answers:" + "\n";
        try {
                while (cursor.hasNext()) {
                Document ans = cursor.next();
                resultQues += ans.get("id") + ". ";
                resultQues += ans.get("answer");
                resultQues += "\n" + "Answer Comments :" + get_Answer_Comment((String)ans.get("id")) + "\n";
                if(ans.get("editedby")!=null)
                    resultQues += "Answer Befor edited By "  + ans.get("editedby") + " :" + ans.get("beforedit") +"\n" ;
                if(ans.get("lastversion")!=null)
                    resultQues += "lastVersion : " + ans.get("lastversion");
                }
            } finally {
                cursor.close();
            }    
        return resultQues;
    }
 
    private String getQandANormal(Document document){
        String resultQues = "";
        String id = (String)document.get("id");
        MongoCursor<Document> cursor = (MongoCursor<Document>) answer.find(eq("ques_id",id)).iterator();
        if(document.get("isprivate").equals("2")){
        resultQues += "Question: ";
        resultQues += document.get("id") + ". ";
        resultQues += document.get("question");
        if(!document.get("editedby").equals(""))
        resultQues += "Queston Befor edited By "  + document.get("editedby") + document.get("beforedit") +"\n" ;
        if(!document.get("lastversion").equals(""))
        resultQues += "lastVersion : " + document.get("lastversion");
        resultQues += "\n" + "Qusetion Comments :" + "\n";
        resultQues += get_Question_CommentNormal((String)document.get("id"));
        resultQues += "Answers:" + "\n";
        try {
                while (cursor.hasNext()) {
                Document ans = cursor.next();
                if(ans.get("isprivate").equals("2")){
                resultQues += ans.get("id") + ". ";
                resultQues += ans.get("answer");
                resultQues += "\n" + "Answer Comments :" + "\n" + get_Answer_CommentNormal((String)ans.get("id")) + "\n";
                if(ans.get("editedby")!=null)
                    resultQues += "Queston Befor edited By "  + ans.get("editedby") + ans.get("beforedit") +"\n" ;
                if(ans.get("lastversion")!=null)
                    resultQues += "lastVersion : " + ans.get("lastversion");
                }
                }
            } finally {
                cursor.close();
            }    
        }
        return resultQues;
    }

    private String GetAllQuestion(){
      String Result = "" ;
    	MongoCursor<Document> cursor = questions.find().iterator();
      try {
          while (cursor.hasNext()) {
        	  Document doc = cursor.next();
        	  if(doc.get("id")!=null){
        		Result  += doc.get("id") + ".";
        	  	Result += doc.get("question");
        	  	Result += "\n";
        	  }
          }
      } finally {
          cursor.close();
      }
      return Result;
    }
    public static void main(String[] args) {
        // start Server on port 8000 unless a PortNumber is specified
        int portNumber = 8000;
        // create a Server object and start it
        Server server = new Server(portNumber);
        server.start();
    }

    /** One instance of this thread will run for each Client */
    class ClientThread extends Thread {
        // the socket where to listen/talk
        Socket socket;
        ObjectInputStream sInput;
        ObjectOutputStream sOutput;
        // my unique id (easier for disconnection)
        int id;
        // the Username of the Client
        String username;
        // the only type of message a will receive
        Message cm;
        // the date I connect
        String date;

        ClientThread(Socket socket) {
            // a unique id
            id = ++uniqueId;
            this.socket = socket;
			/* Creating both Data Stream */
            System.out.println("Thread trying to create Object Input/Output Streams");
            try
            {
                // create output first
                sOutput = new ObjectOutputStream(socket.getOutputStream());
                sInput  = new ObjectInputStream(socket.getInputStream());
                // read the username
                username = (String) sInput.readObject();
                display(username + " just connected.");
            }
            catch (IOException e) {
                display("Exception creating new Input/output Streams: " + e);
                return;
            }
            // have to catch ClassNotFoundException
            // but I read a String, I am sure it will work
            catch (ClassNotFoundException ignored) {
            }
            date = new Date().toString() + "\n";
        }

        // what will run forever
        public void run() {
            // to loop until LOGOUT
            boolean keepGoing = true;
            while(keepGoing) {
                // read a String (which is an object)
                try {
                    cm = (Message) sInput.readObject();
                    System.out.println(cm.getMessage());
                }
                catch (IOException e) {
                    display(username + " Exception reading Streams: " + e);
                    break;
                }
                catch(ClassNotFoundException e2) {
                    break;
                }
                // the message part of the Message
                String message = cm.getMessage();
                System.out.println(cm.getType());
                String []Details , Keys;
                // Switch on the type of message receive
                switch(cm.getType()) {
                    case Message.SEARCHByQuestion:
                        writeMsg(SearchByQuestion(message));
                        break;
                    case Message.SearchByTime:
                    	writeMsg(SearchByTime(message));
                        break;
                    case Message.SearchByQuestionN:
                        writeMsg(SearchByQuestionNormal(message));
                        break;
                    case Message.SearchByTimeN:
                    	writeMsg(SearchByTimeNormal(message));
                        break;
                    case Message.LOGOUT:
                        display(username + " disconnected with a LOGOUT message.");
                        keepGoing = false;
                        break;
                    case Message.LOGIN:
                     	Details = message.split(" ");
                       // writeMsg("List of the users connected at " + sdf.format(new Date()) + "\n");
                        // scan al the users connected
                       // for(int i = 0; i < al.size(); ++i) {
                        //    ClientThread ct = al.get(i);
                        //    writeMsg((i+1) + ") " + ct.username + " since " + ct.date);
                       // }
                        int State = Login(Details[0],Details[1]);
                        switch(State){
                    		case 0:
                    			writeMsg("User Not Founded");
                    			break;
                    		case 1:
                    			writeMsg("Incorrect Password");
                    			break;
                    		case 2:
                    			writeMsg("Ur Login Now");
                    			break;
                    		default:
                    		break;
                        }
                        break;
                    case Message.REGISTER:
                    	Details = message.split(",");
                    	ArrayList<String> Favorits = new ArrayList<String>();
                    	for(int i=4 ; i<Details.length ; i++){
                    		Favorits.add(Details[i]);
                    	}
                    	CreatUser(Details[0], Details[1], Details[2], Details[3], Favorits);
                    	break;
                    case Message.CheckUserId:
                    	boolean cu = Check_ID(message);
                    	if(cu){
                    		writeMsg("User Corrected");
                    	}else{
                    		writeMsg("Ur UserName Already Exist");
                    	}
                    	break;
                    case Message.CheckEmail:
                    	boolean ce = Check_Email(message);
                    	if(ce){
                    		writeMsg("Email Corrected");
                    	}else{
                    		writeMsg("Ur Email Already Exist");
                    	}
                    	break;
                    case Message.QUESTION:
                    	Details = message.split(",::,");
                    	AddQuestion(Details[0],Details[1],Details[2]);
                    	writeMsg("Ur Question Add Succsessfully");
                    	break;
                    case Message.GetQuestion:
                    	writeMsg(GetAllQuestion());
                    	break;
                    case Message.ANSWER:
                    	Details = message.split("#");
                    	AddAnswerToQuestion(Details[0] , Details[1] , Details[2]);
                    	break;
                    case Message.AddCommentA:
                    	Details = message.split("#");
                    	AddCommentToAnswer(Details[0], Details[1], Details[2]);
                    	writeMsg("Comment Add Successfully");
                    	break;
                    case Message.AddCommentQ:
                    	Details = message.split("#");
                    	AddCommentToQuestion(Details[0], Details[1], Details[2]);
                    	writeMsg("Comment Add Successfully");
                    	break;
                    case Message.UpdateA :
                    		Details = message.split("#");
                    		int s = updateAnswer(Details[0], Details[2], Details[1]);
                    		switch(s){
                    		case 0:
                    			writeMsg("Answer Not Founded");
                    			break;
                    		case 1:
                    			writeMsg("Update Succesfully");
                    			break;
                    		case 2:
                    			writeMsg("U Can,t Update Other Answer");
                    			break;
                    		}
                    	break;
                    case Message.UpdateQ:
                    		Details = message.split("#");
                    		s = updateQuestion(Details[0], Details[2], Details[1]);
                    		switch(s){
                    		case 0:
                    			writeMsg("Question Not Founded");
                    			break;
                    		case 1:
                    			writeMsg("Update Succesfully");
                    			break;
                    		case 2:
                    			writeMsg("U Can,t Update Other Question");
                    			break;
                    		}
                    		

                    	break;
                    case Message.UpdateC:
                    		Details = message.split("#");
                    		s =	updateComment(Details[0], Details[2], Details[1]);
                    		switch(s){
                    		case 0:
                    			writeMsg("Comment Not Founded");
                    			break;
                    		case 1:
                    			writeMsg("Update Succesfully");
                    			break;
                    		case 2:
                    			writeMsg("U Can,t Update Other's Comments");
                    			break;
                    		}
                   		break;
                    case Message.ShowA:
                    		writeMsg(ShowAnswer(message));
                    	break;
                    case Message.ShowQ:
                    		writeMsg(ShowQuestion(message));
                    	break;
                    case Message.ShowC:
                    		writeMsg(ShowComment(message));
                    	break;
                    case Message.DeleteA:
                    	Details = message.split("#");
                    	boolean del1 = DeleteAnswer(Details[1], Details[0], false);
                    	if(del1){
                    		writeMsg("Delete Succesfully");
                    	}else{
                    		writeMsg("Delete Unsuccesfully");
                    	}
                    	break;
                    case Message.DeleteC:
                    	Details = message.split("#");
                    	del1 = DeleteComment(Details[1], Details[0],false);
                    	if(del1){
                    		writeMsg("Delete Succesfully");
                    	}else{
                    		writeMsg("Delete Unsuccesfully");
                    	}
                    	
                    	break;
                    case Message.DeleteQ:
                    	Details = message.split("#");
                    	del1 = DeleteQuestion(Details[1], Details[0]);
                    	if(del1){
                    		writeMsg("Delete Succesfully");
                    	}else{
                    		writeMsg("Delete Unsuccesfully");
                    	}
                    	break;
                    case Message.DeleteAccount:
                    	DeleteAccount(message);
                    	writeMsg("Account Deleted Succsefully");
                    	keepGoing = false;
                    	break;
                    case Message.RateA :
                    	Details = message.split("#");
                    	int r = addRateA(Details[2], Details[0], Details[1]);
                    	if(r==0){
                    		writeMsg("U Rated This Question ago");
                    	}else if(r==1){
                    		writeMsg("Rated Question Successfully");
                    	}else{
                    		writeMsg("Answer Not Founded");
                    	}
                    	break;
                    case Message.RateQ :
                    	Details = message.split("#");
                    	r = addRateQ(Details[2], Details[0], Details[1]);
                    	if(r==0){
                    		writeMsg("U Rated This Question ago");
                    	}else if(r==1){
                    		writeMsg("Rated Question Successfully");
                    	}else{
                    		writeMsg("Question Not Founded");
                    	}
                    	break;
                    case Message.RateU:
                    	Details = message.split("#");
                    	r = addRateU(Details[2], Details[0], Details[1]);
                    	if(r==0){
                    		writeMsg("U Rated This Question ago");
                    	}else if(r==1){
                    		writeMsg("Rated Question Successfully");
                    	}else{
                    		writeMsg("Question Not Founded");
                    	}
                    case Message.EditedA:
                    	Details = message.split("#");
                    	r = editA(Details[0],Details[1], Details[2]);
                    	if(r==0){
                    		writeMsg("Ur Edit Requset Text Save");
                    	}else if(r==1){
                    		writeMsg("U have unsolve Text Edit");
                    	}else{
                    		writeMsg("Answer Not Founded");
                    	}
                    	break;
                    case Message.EditedQ:
                    	Details = message.split("#");
                    	r = editQ(Details[0],Details[1], Details[2]);
                    	if(r==0){
                    		writeMsg("Ur Edit Requset Text Save");
                    	}else if(r==1){
                    		writeMsg("U have unsolve Text Edit");
                    	}else{
                    		writeMsg("Question Not Founded");
                    	}
                    	break;
                    case Message.SeeEditA:
                    	writeMsg(seeAnswerRequest(message));
                    	break;
                    case Message.SeeEditQ:
                    	writeMsg(seeQuestionRequest(message));
                    	break;
                    case Message.AnswerEditA:
                    	Details = message.split("#");
                    	boolean a = answerEditA(Details[0], Details[1], Details[2]);
                    	if(a){
                    		writeMsg("Succsefull");
                    	}else{
                    		writeMsg("UnSuccsefull");                    		
                    	}
                    	break;
                    case Message.AnswerEditQ:
                    	Details = message.split("#");
                    	a = answerEditQ(Details[0], Details[1], Details[2]);
                    	if(a){
                    		writeMsg("Succsefull");
                    	}else{
                    		writeMsg("UnSuccsefull");                    		
                    	}
                    	break;
                    case Message.PrivateFromNormal:
                    	Details = message.split("#");
                    		doPrivate(Details[1], Details[0]);
                    		if(Details[0].equals("1")){
                    			writeMsg("Locked Account Successfully");
                    		}else if(Details[0].equals("2")){
                    			writeMsg("Locked Account Successfully");                    			
                    		}
                    	break;
                    case Message.withoutAnswerQ:
                    		withoutAnswerQ(message);
                    	break;
                }
            }
            // remove myself from the arrayList containing the list of the
            // connected Clients
            remove(id);
            close();
        }
        
        private void withoutAnswerQ(String Uname){
        	Document u = users.find(eq("username", Uname)).first();
        	
        	
        	
        }
       
        private void doPrivate(String uName , String l){
        	if(l.equals("1")){
        		questions.updateMany(eq("username",uName),set("isprivate","1"));
            	answer.updateMany(eq("username",uName),set("isprivate","1"));
            	comment.updateMany(eq("username",uName),set("isprivate","1"));
            }else if(l.equals("2")){
            	questions.updateMany(eq("username",uName),set("isprivate","2"));
            	answer.updateMany(eq("username",uName),set("isprivate","2"));
            	comment.updateMany(eq("username",uName),set("isprivate","2"));
            }
        }
        
        
        private boolean answerEditA(String AnswerID , String userName , String s){
        	if(s.equals("1")){
        		Document doc = answer.find(eq("id", AnswerID)).first();
            	Document e = edit.find(combine(eq("username" , userName) , eq("answer_id",AnswerID))).first();
        		String BeforEdit = (String) doc.get("answer");
        		answer.updateOne(eq("id", AnswerID) , combine(set("answer",(String)e.get("edit")), 
        				set("beforedit",BeforEdit ),set("editedby", userName)));
        		edit.deleteOne(combine(eq("username" , userName) , eq("answer_id",AnswerID)));
        		return true;
        	}else if(s.equals("2")){
        		edit.deleteOne(combine(eq("username" , userName) , eq("answer_id",AnswerID)));
        		return true;
        	}else{
        		return false;
        	}
        }
        
        private boolean answerEditQ(String QuesID , String userName , String s){
        	if(s.equals("1")){
        		Document doc = questions.find(eq("id", QuesID)).first();
            	Document e = edit.find(combine(eq("username" , userName) , eq("ques_id",QuesID))).first();
        		String BeforEdit = (String) doc.get("answer");
        		questions.updateOne(eq("id",QuesID ) , combine(set("question",(String)e.get("edit")), 
        				set("beforedit",BeforEdit ),set("editedby", userName)));
           		edit.deleteOne(combine(eq("username" , userName) , eq("ques_id",QuesID)));
        		return true;
        	}else if(s.equals("2")){
           		edit.deleteOne(combine(eq("username" , userName) , eq("ques_id",QuesID)));
        		return true;
        	}else{
        		return false;
        	}
        }
        
        private int editA(String AnswerID , String userN , String editText){
        	Document f = answer.find(eq("id",AnswerID)).first();
        	Document e = edit.find(combine(eq("username" , userN) , eq("answer_id",AnswerID))).first();
        	if(f!=null){
        		if(e==null){
        		Document doc = new Document("answer_id" , AnswerID)
        								.append("username",userN)
        								.append("edit", editText);
        		edit.insertOne(doc);
        		return 0;
        		}else{
        			return 1;
        		}
        	}else{
        		return 2;
        	}
        }
        
        private String seeAnswerRequest(String userName){
        	resultQues = "";
        	MongoCursor<Document> cursor1 = (MongoCursor<Document>) answer.find(eq("username",userName)).iterator();
        	try {
                while (cursor1.hasNext()) {
                	Document doc = cursor1.next();
                	MongoCursor<Document> cursor2 = (MongoCursor<Document>) edit.find(eq("answer_id",doc.get("id"))).iterator();
                	try {
                		while(cursor2.hasNext()){
                			Document d = cursor2.next();
                				resultQues +=  d.get("edit") + "   Request By:" + d.get("username");
                				resultQues += "\n";
                		}
                	} finally {
						cursor2.close();
					}
                }
          	} finally {
                cursor1.close();
            }
        
        	return resultQues;
        }

        private String seeQuestionRequest(String userName){
        	resultQues = "";
        	System.out.println(":)))");
        	MongoCursor<Document> cursor1 = (MongoCursor<Document>) questions.find(eq("username",userName)).iterator();
        	try {
                while (cursor1.hasNext()) {
                	Document doc = cursor1.next();
                	MongoCursor<Document> cursor2 = (MongoCursor<Document>) edit.find(eq("ques_id",doc.get("id"))).iterator();
                	try {
                		while(cursor2.hasNext()){
                			System.out.println(":(((((");
                			Document d = cursor2.next();
                				resultQues +=  d.get("edit") + "   Request By:" + d.get("username");
                				resultQues += "\n";
                		}
                	} finally {
						cursor2.close();
					}
                }
          	} finally {
                cursor1.close();
            }
        	return resultQues;
        }
        
        private int editQ(String QuesID , String userN , String editText){
        	Document f = questions.find(eq("id",QuesID)).first();
        	Document e = edit.find(combine(eq("username" , userN) , eq("ques_id",QuesID))).first();
        	if(f!=null){
        		if(e==null){
        		Document doc = new Document("ques_id" , QuesID)
        								.append("username",userN)
        								.append("edit", editText);
        		edit.insertOne(doc);
        		return 0;
        		}else{
        			return 1;
        		}
        	}else{
        		return 2;
        	}
        }
        
        private int addRateA(String Score , String UserName , String AnswerID){
        	Document u = rate.find(combine(eq("username" , UserName) , eq("answer_id",AnswerID))).first();
        	if(u!=null){
        		return 0; 
        	}else{
        		Document doc = new Document("username" , UserName )
        				.append("answer_id", AnswerID);
        		Document f = answer.find(eq("id", AnswerID)).first();
        		if(f!=null){
        		int i = Integer.parseInt((String)f.get("score"));
        		i += Integer.parseInt(Score);
        		answer.updateOne(eq("id" , AnswerID),set("score", Integer.toString(i)));
        		return 1;
        		}else{
        			return 2;
        		}
        	}
        }
        
        private int addRateQ(String Score , String UserName , String QuestionID){
        	Document u = rate.find(combine(eq("username" , UserName) , eq("ques_id",QuestionID))).first();
        	if(u!=null){
        		return 0; 
        	}else{
        		Document doc = new Document("username" , UserName )
        				.append("ques_id", QuestionID);
        		Document f = questions.find(eq("id", QuestionID)).first();
        		if(f!=null){
        		int i = Integer.parseInt((String)f.get("score"));
        		i += Integer.parseInt(Score);
        		System.out.println(i + " score" );
        		questions.updateOne(eq("id" , QuestionID),set("score", Integer.toString(i)));
        		rate.insertOne(doc);
        		return 1;
        		}else{
        			return 2;
        		}
        	}
        }
        
        private int addRateU(String Score , String UserName , String User){
        	Document u = rate.find(combine(eq("username" , UserName) , eq("user",User))).first();
        	if(u!=null){
        		return 0; 
        	}else{
        		Document doc = new Document("username" , UserName )
        				.append("user", User);
        		Document f = users.find(eq("username", User)).first();
        		if(f!=null){
        		int i = Integer.parseInt((String)f.get("score"));
        		i += Integer.parseInt(Score);
        		System.out.println(i + " score" );
        		users.updateOne(eq("username" , User),set("score", Integer.toString(i)));
        		rate.insertOne(doc);
        		return 1;
        		}else{
        			return 2;
        		}
        	}
        }
        
        private void DeleteAccount(String uName){
        	questions.updateMany(eq("username",uName),set("username","DeletedAcoount"));
        	answer.updateMany(eq("username",uName),set("username","DeletedAccount"));
        	comment.updateMany(eq("username",uName),set("username","DeletedAccount"));
        	users.deleteOne(eq("username",uName));
        }
       
        private boolean DeleteAnswer(String identifier , String uname , boolean can){
        		Document doc = answer.find(eq("id",identifier)).first();
        		if(doc!=null){
        		if(uname.equals((String)doc.get("username")) || can){
                answer.deleteOne(eq("id", identifier));//just deletes
            	MongoCursor<Document> cursor1 = (MongoCursor<Document>) comment.find(eq("answer_id",identifier)).iterator();
            	try {
                    while (cursor1.hasNext()) {
                  	  Document doc2 = cursor1.next();
                  	  DeleteComment((String) doc2.get("id"), uname , true);
                    }
              	} finally {
                    cursor1.close();
                }
                //Now we should update the remaining documents so that add works correctly
                MongoCursor<Document> cursor = (MongoCursor<Document>) answer.find().iterator();
                String curs = "";
                try {
                    while (cursor.hasNext()) {
                        Document dc = cursor.next();
                        curs = (String)dc.get("id");
                        if(curs == null)//does't contain this field
                        {
                            continue;
                        }
                        //update in case of being greater for our document's id
                        if(Integer.parseInt(curs) > Integer.parseInt(identifier))               
                        {
                        	comment.updateMany(eq("answer_id",curs),set("answer_id",Integer.toString(Integer.parseInt(curs) - 1)));
                            UpdateResult up = answer.updateOne(                        
                            eq("id",curs),set("id",Integer.toString(Integer.parseInt(curs) - 1)));
                        }
                    }
                } finally {
                    cursor.close();
                }
                	return true;
        		}else{
        			return false;
        		}
        		}else{
        			return false;
        		}
            } 	
        
        private boolean DeleteQuestion(String identifier , String uname){
        	Document doc = questions.find(eq("id",identifier)).first();
        	if(doc!=null){
        	if(uname.equals((String)doc.get("username"))){
        	questions.deleteOne(eq("id", identifier));//just deletes
            //Now we should update the remaining documents so that add works correctly
            MongoCursor<Document> cursor1 = (MongoCursor<Document>) answer.find(eq("ques_id",identifier)).iterator();
        	
            try {
                while (cursor1.hasNext()) {
              	  Document doc2 = cursor1.next();
              	  DeleteAnswer((String) doc2.get("id"), uname , true);
                }
          	} finally {
                cursor1.close();
            }
        	
        	MongoCursor<Document> cursor2 = (MongoCursor<Document>) comment.find(eq("ques_id",identifier)).iterator();
        	try {
                while (cursor2.hasNext()) {
              	  Document doc2 = cursor2.next();
              	  DeleteComment((String) doc2.get("id"), uname , true);
                }
          	} finally {
                cursor2.close();
            }
        	
        	MongoCursor<Document> cursor = (MongoCursor<Document>) questions.find().iterator();
            String curs = "";
            try {
                while (cursor.hasNext()) {
                    Document dc = cursor.next();
                    curs = (String)dc.get("id");
                    if(curs == null)//does't contain this field
                    {
                        continue;
                    }
                    //update in case of being greater for our document's id
                    if(Integer.parseInt(curs) > Integer.parseInt(identifier))               
                    {
                    	
                    	comment.updateOne(eq("ques_id",curs),set("ques_id",Integer.toString(Integer.parseInt(curs) - 1)));
                        answer.updateOne(eq("ques_id",curs),set("ques_id",Integer.toString(Integer.parseInt(curs) - 1)));
                    	UpdateResult up = questions.updateOne(                        
                        eq("id",curs),set("id",Integer.toString(Integer.parseInt(curs) - 1)));
                    }
                }
            } finally {
                cursor.close();
            }        
            return true;
        	}else{
        		return false;
        	}
        	}else{
        		return false;
        	}
        }
        
        private boolean DeleteComment(String identifier , String uname ,boolean Can){
        	Document doc = comment.find(eq("id",identifier)).first();
    		if(doc!=null){
        	String un = (String)doc.get("username");
    		if(uname.equals(un) || Can){
            comment.deleteOne(eq("id", identifier));//just deletes
            //Now we should update the remaining documents so that add works correctly
            MongoCursor<Document> cursor = (MongoCursor<Document>) comment.find().iterator();
            String curs = "";
            try {
                while (cursor.hasNext()) {
                    Document dc = cursor.next();
                    curs = (String)dc.get("id");
                    if(curs == null)//does't contain this field
                    {
                        continue;
                    }
                    //update in case of being greater for our document's id
                    System.out.println(Integer.parseInt(curs) +":"+ Integer.parseInt(identifier));
                    if(Integer.parseInt(curs) > Integer.parseInt(identifier))               
                    {
                    	UpdateResult up = comment.updateOne(                        
                        eq("id",curs),set("id",Integer.toString(Integer.parseInt(curs) - 1)));
                    }
                }
            } finally {
                cursor.close();
            }	
            	return true;
    		}else{
    			return false;
    		}
    		}else{
    			return false;
    		}
        }
        
        
        
        private String ShowQuestion(String UserName){
        	Show = "";
        	MongoCursor<Document> cursor = (MongoCursor<Document>) questions.find(eq("username",UserName)).iterator();
        	try {
              while (cursor.hasNext()) {
            	  Document doc = cursor.next();
            	  Show += doc.get("id") + ". ";
            	  Show += doc.get("question") + "\n";
              }
        	} finally {
              cursor.close();
          }
        	return Show;
        }
        
        private String ShowComment(String UserName){
        	Show = "";
        	MongoCursor<Document> cursor = (MongoCursor<Document>) comment.find(eq("username",UserName)).iterator();
        	try {
                while (cursor.hasNext()) {
              	  Document doc = cursor.next();
              	  Show += doc.get("id") + ". ";
              	  Show += doc.get("comment") + "\n";
                }
          	} finally {
                cursor.close();
            }
          	return Show;
        }
        
        private String ShowAnswer(String UserName){
        	Show = "";
        	MongoCursor<Document> cursor = (MongoCursor<Document>) answer.find(eq("username",UserName)).iterator();
        	try {
                while (cursor.hasNext()) {
              	  Document doc = cursor.next();
              	  Show += doc.get("id") + ". ";
              	  Show += doc.get("answer") + "\n";
                }
          	} finally {
                cursor.close();
            }
          	return Show;
        }
        
        
        private int updateQuestion(String UserName , String update , String Quesid){
        	Document u = questions.find(eq("id" , Quesid)).first();
         	if(u==null){
        		return 0;
        	}else{
        		if(u.get("username").equals(UserName)){
           		String befor = (String) u.get("question");
       			UpdateResult up = questions.updateMany(
        		eq("id",Quesid),combine(set("question",update),set("lastversion", befor)));
        		System.out.println(up.getModifiedCount());
        		return 1;
        		}else{
        			return 2; //not ur question
        		}
        	}
        }
        
        private int updateAnswer(String UserName , String update , String Answerid){
        	Document u = answer.find(eq("id" , Answerid)).first();
        	if(u==null){
        		return 0;
        	}else{
            	if(u.get("username").equals(UserName)){
            		String befor = (String) u.get("answer");
            		UpdateResult up = answer.updateOne(
        			eq("id",Answerid),combine(set("answer",update),set("lastversion", befor)));
        			System.out.println(up.getModifiedCount()); 
        			return 1;
        		}else{
        			return 2;
        		}
        	}
        }
        
        private int updateComment(String UserName , String update , String Commentid){
        	Document u = comment.find(eq("id" , Commentid)).first();
        	if(u==null){
        		return 0;
        	}
        		if(u.get("username").equals(UserName)){
                	String befor = (String) u.get("comment");
        			UpdateResult up = answer.updateMany(
        			eq("id",Commentid),combine(set("comment",update),set("lastversion", befor)));
        			System.out.println(up.getModifiedCount()); 	
        			return 1;
        		}else{
        			return 2;
        		}
        }
        
        private String SearchByTime(String KeyWords){
//          when user wants to do a search based on a certain 
            resultQues = "" ;
            //change the userk to proper oe
        	String msg = KeyWords;
            String res = "";
            String[] userk = msg.split(" ");
            Arrays.sort(userk);
            msg = "";
            String[] qk;
            boolean contains = true;
            //now we have user keywords in a sorted way that can be compared to KEYS
           
            MongoCursor<Document> cursor = questions.find().sort(Sorts.descending("data")).iterator();
            try {
               while (cursor.hasNext()) {
                   contains = true;
                   Document dc = cursor.next();
                   msg = (String)dc.get("KEYS");//now msg is the  KEYS field
                   if(msg==null){
                	   continue;
                   }
                   qk = msg.split(" ");
                   for(int j = 0;j < userk.length;j++)
                   {
                       if(!Arrays.asList(qk).contains(userk[j]))
                       {
                           contains = false;
                       }
                   }
                   if(contains)//all the userk elements found somewhere in qk
                   {
                       //call some method to return String containing the question with all answers
                       //posted so far
                       res += getQandA(dc);
                   }
               }
           } finally {
               cursor.close();
           }
            resultQues = res;
            return resultQues;
        }

        private String SearchByTimeNormal(String KeyWords){
//          when user wants to do a search based on a certain 
            resultQues = "" ;
            //change the userk to proper oe
        	String msg = KeyWords;
            String res = "";
            String[] userk = msg.split(" ");
            Arrays.sort(userk);
            msg = "";
            String[] qk;
            boolean contains = true;
            //now we have user keywords in a sorted way that can be compared to KEYS
           
            MongoCursor<Document> cursor = questions.find().sort(Sorts.descending("data")).iterator();
            try {
               while (cursor.hasNext()) {
                   contains = true;
                   Document dc = cursor.next();
                   msg = (String)dc.get("KEYS");//now msg is the  KEYS field
                   if(msg==null){
                	   continue;
                   }
                   qk = msg.split(" ");
                   for(int j = 0;j < userk.length;j++)
                   {
                       if(!Arrays.asList(qk).contains(userk[j]))
                       {
                           contains = false;
                       }
                   }
                   if(contains)//all the userk elements found somewhere in qk
                   {
                       //call some method to return String containing the question with all answers
                       //posted so far
                       res += getQandANormal(dc);
                   }
               }
           } finally {
               cursor.close();
           }
            resultQues = res;
            return resultQues;
        }
        
        private void AddCommentToAnswer(String userName , String AnswerID , String Comment){
        	String id = Integer.toString((int) (comment.count() + 1));
        	Document doc = new Document("answer_id" , AnswerID)
        			.append("username", userName)
    				.append("data", System.currentTimeMillis())
    				.append("id", id)
    				.append("comment", Comment)
    				.append("lastversion", "")
    				.append("isprivate", "2");
        	comment.insertOne(doc);        	        	
        }
        
        private void AddCommentToQuestion(String userName , String QuesID , String Comment){
        	String id = Integer.toString((int) (comment.count() + 1));
        	Document doc = new Document("ques_id" , QuesID)
        			.append("username", userName)
    				.append("data", System.currentTimeMillis())
    				.append("id", id)
    				.append("comment", Comment)
    				.append("lastversion", "")
    				.append("isprivate", "2");
        	comment.insertOne(doc);
        }
        
        private void AddAnswerToQuestion(String userName, String QuesID , String Answer){
        	String id = Integer.toString((int) (answer.count() + 1));
        	Document doc = new Document("ques_id" , QuesID)
        			.append("username", userName)
    				.append("score" , "0")
    				.append("data", System.currentTimeMillis())
    				.append("id", id)
    				.append("answer", Answer)
    				.append("lastversion", "")
    				.append("beforedit", "")
    				.append("editedby", "")
    				.append("isprivate", "2");
        	answer.insertOne(doc);
        }
        
        // try to close everything
        private void close() {
            // try to close the connection
            try {
                if(sOutput != null) sOutput.close();
            }
            catch(Exception ignored) {}
            try {
                if(sInput != null) sInput.close();
            }
            catch(Exception ignored) {}
            try {
                if(socket != null) socket.close();
            }
            catch (Exception ignored) {}
        }

        /*
         * Write a String to the Client output stream
         */
 
        
        private boolean writeMsg(String msg) {
            // if Client is still connected send the message to it
            if(!socket.isConnected()) {
                close();
                return false;
            }
            // write the message to the stream
            try {
                sOutput.writeObject(msg);
            }
            // if an error occurs, do not abort just inform the user
            catch(IOException e) {
                display("Error sending message to " + username);
                display(e.toString());
            }
            return true;
        }
    }
}