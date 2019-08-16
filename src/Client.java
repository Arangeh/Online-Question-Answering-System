import java.net.*;
import java.io.*;
import java.util.*;

import org.omg.CosNaming.IstringHelper;


public class Client {

    // for I/O
    private ObjectInputStream sInput;		// to read from the socket
    private ObjectOutputStream sOutput;		// to write on the socket
    private Socket socket;

    // the Server, the port and the username
    private String server, username;
    private int port;
    static boolean isLogin = false;
    static boolean isrepeatUser = false;
	static boolean isrepeatEmail = false;
    private Client(String server, int port, String username) {
        this.server = server;
        this.port = port;
        this.username = username;
    }

    
    private boolean start() {
        // try to connect to the Server
        try {
            socket = new Socket(server, port);
            
        }
        // if it failed not much I can so
        catch(Exception ec) {
             System.out.println("Error connecting to Server:" + ec);
            return false;
        }

        String msg = "Connection accepted " + socket.getInetAddress() + ":" + socket.getPort();
         System.out.println(msg);

		/* Creating both Data Stream */
        try
        {
            sInput  = new ObjectInputStream(socket.getInputStream());
            sOutput = new ObjectOutputStream(socket.getOutputStream());
        }
        catch (IOException eIO) {
             System.out.println("Exception creating new Input/output Streams: " + eIO);
            return false;
        }

        // creates the Thread to listen from the Server
        new ListenFromServer().start();
        // Send our username to the Server this is the only message that we
        // will send as a String. All other messages will be Message objects
        try
        {
            sOutput.writeObject(username);
        }
        catch (IOException eIO) {
             System.out.println("Exception doing login : " + eIO);
            disconnect();
            return false;
        }
        // success we inform the caller that it worked
        return true;
    }


    /*
     * To send a message to the Server
     */
    private void sendMessage(Message msg) {
        try {
            sOutput.writeObject(msg);
        }
        catch(IOException e) {
            System.out.println("Exception writing to Server: " + e);
        }
    }

    /*
     * When something goes wrong
     * Close the Input/Output streams and disconnect not much to do in the catch clause
     */
    private void disconnect() {
        try {
            if(sInput != null) sInput.close();
        }
        catch(Exception ignored) {} // not much else I can do
        try {
            if(sOutput != null) sOutput.close();
        }
        catch(Exception ignored) {} // not much else I can do
        try{
            if(socket != null) socket.close();
        }
        catch(Exception ignored) {} // not much else I can do

    }

    public static void main(String[] args) {
        // default values
        int portNumber = 8000;
        String serverAddress = "DESKTOP-HBLS5R3";
        String userName = "Anonymous1";
        String uName = "";
        // create the Client object
        Client client = new Client(serverAddress, portNumber, userName);
        // test if we can start the connection to the Server
        // if it failed nothing we can do
//        System.out.println("mamad");
        if(!client.start())
            return;
//        System.out.println("here");
        // wait for messages from user
        Scanner scan = new Scanner(System.in);
        // loop forever for message from the user
        while(true) {
            // read message from user
        	System.out.println("PLs Enter UR Requset");
            String msg = scan.nextLine();
            // message SEARCH
            // message LOGIN
            if(msg.equalsIgnoreCase("LOGIN")) {
            	String PAS , det = "";
            	System.out.println("Pls Enter ur UserName");
            	uName = scan.nextLine();
            	det += uName + " ";
            	System.out.println("Pls Enter ur PassWord");
            	PAS = scan.nextLine();
            	det += PAS;
                client.sendMessage(new Message(Message.LOGIN,det));
            }
            // message LOGOUT
            else if(msg.equalsIgnoreCase("LOGOUT")) {
                client.sendMessage(new Message(Message.LOGOUT, "Bye" + userName + "You Are Logout"));
                break;
            }
            // message REGISTER
            else if(msg.equalsIgnoreCase("REGISTER")) {
            	System.out.println("Pls Enter Ur Name");
            	String name = scan.nextLine();
            	String usern , password , email , Details = "" , favorits;
            	if(!name.equals("")){
            		Details += name;
            		System.out.println("pls Enter Ur UserName");
            		usern = scan.nextLine();
            		client.sendMessage(new Message(Message.CheckUserId, usern));
            		if(!usern.equals("")){
            			Details += "," + usern;
            			System.out.println("Pls Enter Email");
            			email = scan.nextLine();
            			client.sendMessage(new Message(Message.CheckEmail, email));
            			if(!email.equals("")){
            				Details += "," + email;
            				System.out.println("Pls Enter password");
            				password = scan.nextLine();
            				if(!password.equals("")){
            					Details += "," + password;
            					System.out.println("Pls Enter Ur Favorites In Each Line And Ended Enter 0");
            					favorits = scan.nextLine();
            					while(!favorits.equals("0")){
            						Details += "," + favorits;
            						favorits = scan.nextLine();
            					}
            					if(!isrepeatEmail){
            						if(!isrepeatUser){
            							System.out.println("Ur SignUp now , Close Program and Open Again now u Can Login");
            							client.sendMessage(new Message(Message.REGISTER,Details));
            						}else{
            							System.out.println("Ur User Name is Repeated");
            						}
            					}else{
        							System.out.println("Ur Email is Repeted");
        						}
            				}else{
            					System.out.println("U should be Enter Email");
            				}
            			}else{
            				System.out.println("U must have Password");
            			}
            		}else{
            			System.out.println("This User Already Exist");
            			
            		}
            	}else{
            		System.out.println("Name Can't Empty");
            	}
            }else if(msg.equalsIgnoreCase("QUESTION")){
            	if(isLogin){
                	System.out.println("Pls Enter Ur Question");
                	String Ques = scan.nextLine();
                	Ques += ",::," + uName;
                	System.out.println("Pls Enter Ur KeyWord Question");
                	Ques += ",::," + scan.nextLine();
            		client.sendMessage(new Message(Message.QUESTION, Ques));
            	}else{
            		System.out.println("U Are Most Login First Then U Can Add Question");
            	}
            }else if(msg.equalsIgnoreCase("SEARCH")){
            	if(isLogin){
            	System.out.println("Enter Ur Search Type : 1.ByTime 2.ByQuestion");
            	String search = scan.nextLine();
            	if(search.equals("1")){
            		System.out.println("Enter Ur KeyWord For Search");
            		search = scan.nextLine();
            		client.sendMessage(new Message(Message.SearchByTime, search));	
            	}else if(search.equals("2")){
            		System.out.println("Pls Enter Ur Question For Search");
            		search = scan.nextLine();
            		client.sendMessage(new Message(Message.SEARCHByQuestion, search));
            	}
            	}else{
                	System.out.println("Enter Ur Search Type : 1.ByTime 2.ByQuestion");
                	String search = scan.nextLine();
                	if(search.equals("1")){
                		System.out.println("Enter Ur KeyWord For Search");
                		search = scan.nextLine();
                		client.sendMessage(new Message(Message.SearchByTimeN, search));	
                	}else if(search.equals("2")){
                		System.out.println("Pls Enter Ur Question For Search");
                		search = scan.nextLine();
                		client.sendMessage(new Message(Message.SearchByQuestionN, search));
                	}
            	}
            }else if(msg.equalsIgnoreCase("GETQUESTION")){
            	client.sendMessage(new Message(Message.GetQuestion, ""));
            	System.out.println("Now U Can Answer Question With \"Answer\" Requset ");
            }else if(msg.equalsIgnoreCase("ANSWER")){
            	System.out.println("Enter 1.withoutAnswerQuestion 2.NormalAnswer");
            	String answer = scan.nextLine();
            	if(answer.equals("1")){
            	System.out.println("Enter Question ID Pls");
            	answer = uName + "#";
            	answer += scan.nextLine();
            	System.out.println("Enter Your Answer Pls");
            	answer += "#" + scan.nextLine();
            	System.out.println(answer);
            	client.sendMessage(new Message(Message.ANSWER, answer));
            	}else if(answer.equalsIgnoreCase("2")){
            		client.sendMessage(new Message(Message.withoutAnswerQ, uName));
            		System.out.println("Enter Question ID Pls");
                	answer = uName + "#";
                	answer += scan.nextLine();
                	System.out.println("Enter Your Answer Pls");
                	answer += "#" + scan.nextLine();
                	System.out.println(answer);
                	client.sendMessage(new Message(Message.ANSWER, answer));
                }

            }else if(msg.equalsIgnoreCase("AddComment")){
            	if(isLogin){
            		System.out.println("Commnet For : 1.Question 2.Answer");
            		String cm = scan.nextLine();
            		if(cm.equals("1")){
            			System.out.println("Enter Your QuestionID");
            			cm = uName + "#";
            			cm += scan.nextLine() + "#";
            			System.out.println("Enter Your Comment");
            			cm += scan.nextLine();
            			client.sendMessage(new Message(Message.AddCommentQ ,cm));
            		}else if(cm.equals("2")){
            			System.out.println("Enter Your AnswerID");
            			cm = uName + "#";
            			cm += scan.nextLine() + "#";
            			System.out.println("Enter Your Comment");
            			cm += scan.nextLine();
            			client.sendMessage(new Message(Message.AddCommentA ,cm));	
            		}
            	}else{
            		System.out.println("U Can't Add Comment U should Be Logged in First");
            	}
            }else if(msg.equalsIgnoreCase("UPDATE")){
            	if(isLogin){
            		System.out.println("Enter Ur Update Type : 1.Comment 2.Answer 3.Question");
            		String update = scan.nextLine();
            		if(update.equals("1")){
            			update = uName + "#";
            			System.out.println("Enter UR Comment ID pls");
            			update += scan.nextLine() + "#" ;
            			System.out.println("Enter UR Update Text");
            			update += scan.nextLine();
            			client.sendMessage(new Message(Message.UpdateC, update));
            		}else if(update.equals("2")){
            			update = uName + "#";
            			System.out.println("Enter UR Answer ID pls");
            			update += scan.nextLine() + "#" ;
            			System.out.println("Enter UR Update Text");
            			update += scan.nextLine();
            			client.sendMessage(new Message(Message.UpdateA, update));	
            		}else if(update.equals("3")){
            			update = uName + "#";
            			System.out.println("Enter UR Question ID pls");
            			update += scan.nextLine() + "#" ;
            			System.out.println("Enter UR Update Text");
            			update += scan.nextLine();
            			client.sendMessage(new Message(Message.UpdateQ, update));
            		}
            	}else{
            		System.out.println("U Can't Update U should Be Logged in First");
            	}
            }else if(msg.equalsIgnoreCase("SHOW")){
            	System.out.println("Enter Ur Show Type : 1.Answer 2.Question 3.Comment");
            	String show = scan.nextLine();
            	if(show.equals("1")){
            		client.sendMessage(new Message(Message.ShowA, uName));
            	}else if(show.equals("2")){
            		client.sendMessage(new Message(Message.ShowQ, uName));
            	}else if(show.equals("3")){
            		client.sendMessage(new Message(Message.ShowC, uName));
            	}
            }else if(msg.equalsIgnoreCase("DELETE")){
            	System.out.println("Enter Ur Delete Type : 1.Answer 2.Question 3.Comment");
            	if(isLogin){
            		String delete = scan.nextLine();
            		if(delete.equals("1")){
            			delete = uName;
            			System.out.println("Enter Ur Answer ID");
            			delete += "#" + scan.nextLine();
            			client.sendMessage(new Message(Message.DeleteA, delete));
            		}else if(delete.equals("2")){
            			delete = uName;
            			System.out.println("Enter Ur Question ID");
            			delete += "#" + scan.nextLine();
            			client.sendMessage(new Message(Message.DeleteQ, delete));
            		}else if(delete.equals("3")){
            			delete = uName;
            			System.out.println("Enter Ur Comment ID");
            			delete += "#" + scan.nextLine();
            			client.sendMessage(new Message(Message.DeleteC, delete));
            		}
            	}else{
            		System.out.println("U most First Logged in and u can delete");
            	}
            }else if(msg.equalsIgnoreCase("ADDRATE")){
            	if(isLogin){
            		System.out.println("Enter UR Rate Type : 1.Answer 2.Question 3.user ");
            		String rate = scan.nextLine();
            		if(rate.equals("1")){
            			System.out.println("Enter Answer ID");
            			rate = uName + "#" + scan.nextLine() + "#";
            			System.out.println("Enter 1 or -1 for score");
            			rate += scan.nextLine();
            			client.sendMessage(new Message(Message.RateA, rate));
            		}else if(rate.equals("2")){
            			System.out.println("Enter Question ID");
            			rate = uName + "#" + scan.nextLine() + "#";
               			System.out.println("Enter 1 or -1 for score");
               			rate += scan.nextLine();
               			client.sendMessage(new Message(Message.RateQ, rate));
            		}else if(rate.equals("3")){
            			System.out.println("Enter userName");
            			rate = uName + "#" + scan.nextLine() + "#";
               			System.out.println("Enter 1 or -1 for score");
               			rate += scan.nextLine();
               			client.sendMessage(new Message(Message.RateU, rate));
            		}
            	}else{
            		System.out.println("U most First Logged in and u can Rating");	
            	}
            }else if(msg.equalsIgnoreCase("DELETEACCOUNT")){
            	if(isLogin){
            		client.sendMessage(new Message(Message.DeleteAccount, uName));
            	}else{
            		System.out.println("U most First Logged in and u can Deleted Account");			
            	}
            }else if(msg.equalsIgnoreCase("EDIT")){
            	System.out.println("Pls Enter Edit Type : 1.Answer 2.Question");
            	if(isLogin){
            		String edit = scan.nextLine();
            		if(edit.equals("1")){
            			System.out.println("Enter Answer ID pls");
            			edit = scan.nextLine() + "#";	
            			System.out.println("Enter Ur Text Edit");
            			edit += uName + "#";
            			edit += scan.nextLine();
            			client.sendMessage(new Message(Message.EditedA, edit));
            		}else if(edit.equals("2")){
            			System.out.println("Enter Question ID pls");
            			edit = scan.nextLine() + "#";
            			System.out.println("Enter Ur Text Edit");
            			edit += uName + "#";
            			edit += scan.nextLine();
            			client.sendMessage(new Message(Message.EditedQ, edit));
            		}
            	}else{
            		System.out.println("U most First Logged in and u can Editing");				
            	}
            }else if(msg.equalsIgnoreCase("PRIVATE")){
            	if(isLogin){
            		System.out.println("Enter 1.Locked 2.unlocked");
            		String lock = scan.nextLine();
            		lock += "#" + uName;
            		client.sendMessage(new Message(Message.PrivateFromNormal, lock));
            	}else{
            		System.out.println("U most First Logged in and u can Lock Ur information for Normal User");				
            	}
            }else if(msg.equalsIgnoreCase("SEEANSWERREQ")){
            	if(isLogin){
            		client.sendMessage(new Message(Message.SeeEditA,uName));
            	}else{
            		System.out.println("U most First Logged in and u can See Edited Requset");				
                }
            	}else if(msg.equalsIgnoreCase("SEEQUESTIONREQ")){
            		if(isLogin){
                		client.sendMessage(new Message(Message.SeeEditQ,uName));
                	}else{
                		System.out.println("U most First Logged in and u can See Edited Requset");				
                	}
                }else if(msg.equalsIgnoreCase("ANSWEREDIT")){
            		if(isLogin){
            			System.out.println("Pls Ennter Edit Type : 1.Answer 2.Question");
            			String AnswerEdit = scan.nextLine();
            			if(AnswerEdit.equals("1")){
            				System.out.println("Pls Enter Answer ID");
            				AnswerEdit = scan.nextLine() + "#";
            				System.out.println("Pls Enter username that request");
            				AnswerEdit += scan.nextLine() + "#";
            				System.out.println("Enter 1.Accept 2.Reject");
            				AnswerEdit += scan.nextLine();
            				client.sendMessage(new Message(Message.AnswerEditA, AnswerEdit));
            			}else if(AnswerEdit.equals("2")){
            				System.out.println("Pls Enter Question ID");
            				AnswerEdit = scan.nextLine() + "#";
            				System.out.println("Pls Enter username that request");
            				AnswerEdit += scan.nextLine() + "#";
            				System.out.println("Enter 1.Accept 2.Reject");
            				AnswerEdit += scan.nextLine();
            				client.sendMessage(new Message(Message.AnswerEditQ, AnswerEdit));
            			}
            		}else{
            			System.out.println("U most First Logged in and u can Answer Edited Requset");				
            		 }
            	}
        }
        // done disconnect
        client.disconnect();
    }

    /*
     * a class that waits for the message from the Server and append them to the JTextArea
     * if we have a GUI or simply System.out.println() it in console mode
     */
    class ListenFromServer extends Thread {
    	
        public void run() {
            while(true) {
                try {
                	String msg = (String) sInput.readObject();
                    if(msg.equals("Ur Login Now")){
                    	isLogin = true;
                        System.out.println(msg);
                    }
                    else if(msg.equals("Ur UserName Already Exist")){
                    	isrepeatUser = true;
                    }
                    else if(msg.equals("User Corrected")){
                    	isrepeatUser = false;
                    }
                    else if(msg.equals("Email Corrected")){
                    	isrepeatEmail = false;
                    }
                    else if(msg.equals("Ur Email Already Exist")){
                    	isrepeatEmail = true;
                    }else{
                        System.out.println(msg);
                    }
                    
                    // if console mode print the message and add back the prompt
                }
                catch(IOException e) {
                    System.out.println("Server has close the connection: " + e);
                    break;
                }
                // can't happen with a String object but need the catch anyhow
                catch(ClassNotFoundException ignored) {
                }
            }
        }
    }
}

