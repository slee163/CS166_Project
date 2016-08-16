/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 * Donovan O'Connor
 * docon001 
 * 861016751
 * 
 * Spencer Lee 
 * slee163
 * 861008681
 *
 * Group #52
 */


import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;

/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */
public class ProfNetwork {

   // reference to physical database connection.
   private Connection _connection = null;

   // handling the keyboard inputs through a BufferedReader
   // This variable can be global for convenience.
   static BufferedReader in = new BufferedReader(
                                new InputStreamReader(System.in));

   /**
    * Creates a new instance of ProfNetwork
    *
    * @param hostname the MySQL or PostgreSQL server hostname
    * @param database the name of the database
    * @param username the user name used to login to the database
    * @param password the user login password
    * @throws java.sql.SQLException when failed to make a connection.
    */
   public ProfNetwork (String dbname, String dbport, String user, String passwd) throws SQLException {

      System.out.print("Connecting to database...");
      try{
         // constructs the connection URL
         String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
         System.out.println ("Connection URL: " + url + "\n");

         // obtain a physical connection
         this._connection = DriverManager.getConnection(url, user, passwd);
         System.out.println("Done");
      }catch (Exception e){
         System.err.println("Error - Unable to Connect to Database: " + e.getMessage() );
         System.out.println("Make sure you started postgres on this machine");
         System.exit(-1);
      }//end catch
   }//end ProfNetwork

   /**
    * Method to execute an update SQL statement.  Update SQL instructions
    * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
    *
    * @param sql the input SQL string
    * @throws java.sql.SQLException when update failed
    */
   public void executeUpdate (String sql) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the update instruction
      stmt.executeUpdate (sql);

      // close the instruction
      stmt.close ();
   }//end executeUpdate

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and outputs the results to
    * standard out.
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQueryAndPrintResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and output them to standard out.
      boolean outputHeader = true;
      while (rs.next()){
	 if(outputHeader){
	    for(int i = 1; i <= numCol; i++){
		System.out.print(rsmd.getColumnName(i) + "\t");
	    }
	    System.out.println();
	    outputHeader = false;
	 }
         for (int i=1; i<=numCol; ++i)
            System.out.print (rs.getString (i) + "\t");
         System.out.println ();
         ++rowCount;
      }//end while
      stmt.close ();
      return rowCount;
   }//end executeQuery

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the results as
    * a list of records. Each record in turn is a list of attribute values
    *
    * @param query the input query string
    * @return the query result as a list of records
    * @throws java.sql.SQLException when failed to execute the query
    */
   public List<List<String>> executeQueryAndReturnResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and saves the data returned by the query.
      boolean outputHeader = false;
      List<List<String>> result  = new ArrayList<List<String>>();
      while (rs.next()){
          List<String> record = new ArrayList<String>();
         for (int i=1; i<=numCol; ++i)
            record.add(rs.getString (i));
         result.add(record);
      }//end while
      stmt.close ();
      return result;
   }//end executeQueryAndReturnResult

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the number of results
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQuery (String query) throws SQLException {
       // creates a statement object
       Statement stmt = this._connection.createStatement ();

       // issues the query instruction
       ResultSet rs = stmt.executeQuery (query);

       int rowCount = 0;

       // iterates through the result set and count nuber of results.
       if(rs.next()){
          rowCount++;
       }//end while
       stmt.close ();
       return rowCount;
   }

   /**
    * Method to fetch the last value from sequence. This
    * method issues the query to the DBMS and returns the current
    * value of sequence used for autogenerated keys
    *
    * @param sequence name of the DB sequence
    * @return current value of a sequence
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int getCurrSeqVal(String sequence) throws SQLException {
	Statement stmt = this._connection.createStatement ();

	ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
	if (rs.next())
		return rs.getInt(1);
	return -1;
   }

   /**
    * Method to close the physical connection if it is open.
    */
   public void cleanup(){
      try{
         if (this._connection != null){
            this._connection.close ();
         }//end if
      }catch (SQLException e){
         // ignored.
      }//end try
   }//end cleanup

   /**
    * The main execution method
    *
    * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
    */
   public static void main (String[] args) {
      if (args.length != 3) {
         System.err.println (
            "Usage: " +
            "java [-classpath <classpath>] " +
            ProfNetwork.class.getName () +
            " <dbname> <port> <user>");
         return;
      }//end if

      Greeting();
      ProfNetwork esql = null;
      try{
         // use postgres JDBC driver.
         Class.forName ("org.postgresql.Driver").newInstance ();
         // instantiate the ProfNetwork object and creates a physical
         // connection.
         String dbname = args[0];
         String dbport = args[1];
         String user = args[2];
         esql = new ProfNetwork (dbname, dbport, user, "");

         boolean keepon = true;
         while(keepon) {
            // These are sample SQL statements
            System.out.println("MAIN MENU");
            System.out.println("---------");
            System.out.println("1. Create user");
            System.out.println("2. Log in");
            System.out.println("9. < EXIT");
            String authorisedUser = null;
            switch (readChoice()){
               case 1: CreateUser(esql); break;
               case 2: authorisedUser = LogIn(esql); break;
               case 9: keepon = false; break;
               default : System.out.println("Unrecognized choice!"); break;
            }//end switch
            if (authorisedUser != null) {
              boolean usermenu = true;
              while(usermenu) {
                System.out.println("MAIN MENU");
                System.out.println("---------");
                System.out.println("1. Goto Friend List");
                System.out.println("2. View and Update Profile");
                System.out.println("3. Write a new message");
                System.out.println("4. Send Friend Request");
                System.out.println("5. View Messages");
                System.out.println("6. Search People");
                System.out.println(".........................");
                System.out.println("9. Log out");
                switch (readChoice()){
                   case 1: FriendList(esql, authorisedUser); break;
                   case 2: UpdateProfile(esql, authorisedUser); break;
                   case 3: NewMessage(esql, authorisedUser, null); break;
                   case 4: SendRequest(esql, authorisedUser, null); break;
                   case 5: ViewMessages(esql, authorisedUser); break;
                   case 6: SearchPeople(esql, authorisedUser, null); break;
                   case 9: usermenu = false; break;
                   default : System.out.println("Unrecognized choice!"); break;
                }
              }
            }
         }//end while
      }catch(Exception e) {
         System.err.println (e.getMessage ());
      }finally{
         // make sure to cleanup the created table and close the connection.
         try{
            if(esql != null) {
               System.out.print("Disconnecting from database...");
               esql.cleanup ();
               System.out.println("Done\n\nBye !");
            }//end if
         }catch (Exception e) {
            // ignored.
         }//end try
      }//end try
   }//end main

   public static void Greeting(){
      System.out.println(
         "\n\n*******************************************************\n" +
         "              User Interface      	               \n" +
         "*******************************************************\n");
   }//end Greeting

   /*
    * Reads the users choice given from the keyboard
    * @int
    **/
   public static int readChoice() {
      int input;
      // returns only if a correct value is given.
      do {
         System.out.print("Please make your choice: ");
         try { // read the integer, parse it and break.
            input = Integer.parseInt(in.readLine());
            break;
         }catch (Exception e) {
            System.out.println("Your input is invalid!");
            continue;
         }//end try
      }while (true);
      return input;
   }//end readChoice

   /*
    * Creates a new user with privided login, passowrd and phoneNum
    * An empty block and contact list would be generated and associated with a user
    **/
   public static void CreateUser(ProfNetwork esql){
      try{
         System.out.print("\tEnter user login: ");
         String login = in.readLine();
         System.out.print("\tEnter user password: ");
         String password = in.readLine();
         System.out.print("\tEnter user email: ");
         String email = in.readLine();

	 //Creating empty contact\block lists for a user
	 String query = String.format("INSERT INTO USR (userId, password, email) VALUES ('%s','%s','%s')", login, password, email);

         esql.executeUpdate(query);
         System.out.println ("User successfully created!");
      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }//end

   /*
    * Check log in credentials for an existing user
    * @return User login or null is the user does not exist
    **/
   public static String LogIn(ProfNetwork esql){
      try{
         System.out.print("\tEnter user login: ");
         String login = in.readLine();
         System.out.print("\tEnter user password: ");
         String password = in.readLine();

         String query = String.format("SELECT * FROM USR WHERE userId = '%s' AND password = '%s'", login, password);
         int userNum = esql.executeQuery(query);
         if (userNum > 0)
        	 return login;
         System.out.println("\tInvalid Username or Password: ");
         return null;
      }catch(Exception e){
         System.err.println (e.getMessage ());
         return null;
      }
   }//end

// Rest of the functions definition go in here
   public static void FriendList(ProfNetwork esql, String authorisedUser){
	   try{
		   boolean usermenu = true;
	       while(usermenu) {
	         System.out.println("Friends List");
	         System.out.println("---------");
	         System.out.println("1. View Friends");
	         System.out.println("2. View Friend Requests");
	         System.out.println("3. Send a Friend Request");
	         System.out.println(".........................");
	         System.out.println("9. Go Back");
	         switch (readChoice()){
	            case 1: ViewFriends(esql, authorisedUser); return;
	            case 2: ViewFriendRequests(esql,authorisedUser); return;
	            case 3: SendRequest(esql,authorisedUser, null); return;
	            case 9: usermenu = false; break;
	            default : System.out.println("Unrecognized choice!"); break;
	         }
	       }
		   return;
	   }catch(Exception e){
	         System.err.println (e.getMessage ());
	         return;
	   }
   }
   
   public static String ViewFriends(ProfNetwork esql, String user){
	   try{
		   String query = String.format(
				   "SELECT U.userId, U.email, U.name, U.dateOfBirth "
				   + "FROM USR U, CONNECTION_USR C "
				   + "WHERE C.userId = '%s' AND C.status = 'Accept' AND C.connectionId = U.userId "
				   + "UNION "
				   + "SELECT U.userId, U.email, U.name, U.dateOfBirth "
				   + "FROM USR U, CONNECTION_USR C "
				   + "WHERE C.connectionId = '%s' AND C.status = 'Accept' AND C.userId = U.userId", user, user);
		   List<List<String>> userList = esql.executeQueryAndReturnResult(query);
		   if(userList.isEmpty() == true){
			   System.out.println("You have no friends.");
			   return null;
		   }
		   PrintUser(userList, true, false);
		   boolean usermenu = true;
	       while(usermenu) {
	         System.out.println("View Friends");
	         System.out.println("---------");
	         System.out.println("1. Goto Friend's Profile");
	         System.out.println(".........................");
	         System.out.println("9. Main Menu");
	         switch (readChoice()){
	            case 1: 
	            	System.out.print("Enter Name of Friend: ");
	            	String Username = in.readLine();
	            	String searchRet = SearchPeople(esql, user, Username);
	            	if(searchRet == null){
	            		System.out.println("Invalid User");
	            		break;
	            	}
	            	return user;

	            case 9: usermenu = false; break;
	            default : System.out.println("Unrecognized choice!"); break;
	         }
	       }
		   return user;
	   }catch(Exception e){
	         System.err.println (e.getMessage ());
	         return null;
	   }
   }
   
   public static void ViewFriendRequests(ProfNetwork esql, String authorisedUser){
	   try{
		   String query = String.format(
				   "SELECT U.userId, U.email, U.name, U.dateOfBirth "
				   + "FROM USR U, CONNECTION_USR C "
				   + "WHERE C.connectionId = '%s' AND C.status = 'Request' AND C.userId = U.userId", authorisedUser);
		   List<List<String>> userList = esql.executeQueryAndReturnResult(query);
		   if(userList.isEmpty() == true){
			   System.out.println("No Friend Requests");
			   return;
		   }
		   PrintUser(userList, false, false);
		   boolean usermenu = true;
	       while(usermenu) {
		         System.out.println("View Friend requests");
		         System.out.println("---------");
		         System.out.println("1. Accept A Friend Request");
		         System.out.println("1. Reject A Friend Request");
		         System.out.println(".........................");
		         System.out.println("9. Main Menu");
		         String request = "";
		         switch (readChoice()){
		            case 1: 
		            	System.out.print("Enter Username of request to accept: ");
		            	request = in.readLine();
		            	query = String.format(
		            			"UPDATE CONNECTION_USR "
		            			+ "SET status = 'Accept' "
		            			+ "WHERE userId = '%s' AND connectionId = '%s'", request, authorisedUser);
		            	esql.executeUpdate(query);
		            	System.out.println("Friend Request Accepted");
		            	return;
		            case 2: 
		            	System.out.print("Enter Username of request to reject: ");
		            	request = in.readLine();
		            	query = String.format(
		            			"UPDATE CONNECTION_USR "
		            			+ "SET status = 'Reject' "
		            			+ "WHERE userId = '%s' AND connectionId = '%s'", request, authorisedUser);
		            	esql.executeUpdate(query);
		            	System.out.println("Friend Request Rejected");
		            	return;	
		            case 9: usermenu = false; break;
		            default : System.out.println("Unrecognized choice!"); break;
		         }
		       }
		   return;
	   }catch(Exception e){
	         //System.err.println (e.getMessage ());
	         System.out.println("Invalid Request, Returning to Main Menu");
	         return;
	   }
   }
   
   public static void ViewSelf(ProfNetwork esql, String authorisedUser){
	   try{
	       String query = String.format("SELECT userId, email, name, dateOfBirth FROM USR WHERE userId = '%s'", authorisedUser);
	       List<List<String>> Users = esql.executeQueryAndReturnResult(query);
	       if (!Users.isEmpty()){
	    	   PrintUser(Users, true, true);
	    	   query = String.format("SELECT company, role, location, startDate, endDate "
	    	   		+ "FROM WORK_EXPR WHERE userId = '%s'", authorisedUser);
	    	   List<List<String>> WorkList = esql.executeQueryAndReturnResult(query);
	    	   PrintWorkExp(WorkList);
	    	   query = String.format("SELECT instituitionName, major, degree, startDate, endDate "
	    	   		+ "FROM EDUCATIONAL_DETAILS WHERE userId = '%s'", authorisedUser);
	    	   List<List<String>> EduList= esql.executeQueryAndReturnResult(query);
	    	   PrintEduDet(EduList);
	       }
	   }catch(Exception e){
		         System.err.println (e.getMessage());
		         return;
	   }
   }
   
   public static void UpdateProfile(ProfNetwork esql, String authorisedUser){
	   try{
		   ViewSelf(esql, authorisedUser);
		   boolean usermenu = true;
		   
	       while(usermenu) {
	         System.out.println("Update Your Profile");
	         System.out.println("---------");
	         System.out.println("1. Change Password");
	         System.out.println("2. Add Work Experience");
	         System.out.println("3. Add Educational Detail");
	         System.out.println(".........................");
	         System.out.println("9. Go Back");
	         switch (readChoice()){
	            case 1: ChangePassword(esql, authorisedUser); break;
	            case 2: AddWork(esql,authorisedUser); ViewSelf(esql, authorisedUser); break;
	            case 3: AddEdu(esql,authorisedUser); ViewSelf(esql, authorisedUser); break;
	            case 9: usermenu = false; break;
	            default : System.out.println("Unrecognized choice!"); break;
	         }
	       }
		   return;
	   }catch(Exception e){
	         System.err.println (e.getMessage ());
	         return;
	   }
   }
   
   public static void NewMessage(ProfNetwork esql, String authorisedUser, String target){
	   try{
		   System.out.println("Send New Message");
		   System.out.println("---------");
		   String username = target;
		   if(username == null){
			   System.out.print("Enter a recepiant's username: ");
			   username = in.readLine();
		   }
		   System.out.print("Enter a message: ");
		   String Contents = in.readLine();
		   String query = String.format(
				   "INSERT INTO MESSAGE (senderID, receiverID, contents, status) "
				   + "VALUES ('%s','%s','%s', %s)", authorisedUser, username, Contents, "'Sent'");
		   esql.executeUpdate(query);
		   System.out.println("Messege Sent");
		   return;
	   }catch(Exception e){
	         System.err.println (e.getMessage ());
	         return;
	   }
   }
   
   public static boolean CheckDepth(ProfNetwork esql, String authorisedUser, String username, String via1, String via2){
	   try{
		   String query = "";
		   if(via1 == null && via2 == null){
			   query = String.format(
					   "SELECT C.userId, C.connectionId "
					   + "FROM CONNECTION_USR C "
					   + "WHERE C.userId = '%s' AND C.status = 'Accept'"
					   + "UNION "
					   + "SELECT C.userId, C.connectionId "
					   + "FROM CONNECTION_USR C "
					   + "WHERE C.connectionId = '%s' AND C.status = 'Accept'", authorisedUser, authorisedUser);
			   List<List<String>> userList = esql.executeQueryAndReturnResult(query);
			   if(userList.size() < 5)
				   return true;
			   for(int i = 0; i < userList.size(); i++) {
				   if(userList.get(i).get(0) == authorisedUser){
					   if(CheckDepth(esql,authorisedUser,username, userList.get(i).get(1), null))
						   return true;
				   }
				   else{
					   if(CheckDepth(esql,authorisedUser,username, userList.get(i).get(0), null))
						   return true;
				   }
			   }
			   return false;
		   }
		   String q1 = "";
		   if(via2 == null)
			   q1 = via1;
		   else
			   q1 = via2;
		   
		   query = String.format(
				   "SELECT C.userId, C.connectionId "
				   + "FROM CONNECTION_USR C "
				   + "WHERE C.userId = '%s' AND C.status = 'Accept' AND C.connectionId = '%s'"
				   + "UNION "
				   + "SELECT C.userId, C.connectionId "
				   + "FROM CONNECTION_USR C "
				   + "WHERE C.connectionId = '%s' AND C.status = 'Accept' AND C.userId = '%s'", q1, username, q1, username);
		   int rows = esql.executeQuery(query);
		   if(rows > 0)
			   return true;
		   if(via2 == null){
		   	   query = String.format(
				   "SELECT C.userId, C.connectionId "
				   + "FROM CONNECTION_USR C "
				   + "WHERE C.userId = '%s' AND C.status = 'Accept' AND C.connectionId != '%s'"
				   + "UNION "
				   + "SELECT C.userId, C.connectionId "
				   + "FROM CONNECTION_USR C "
				   + "WHERE C.connectionId = '%s' AND C.status = 'Accept' AND C.userId != '%s'", q1, authorisedUser, q1, authorisedUser);
			   List<List<String>>userList = esql.executeQueryAndReturnResult(query);
		   	   for(int i = 0; i < userList.size(); i++) {
				   if(userList.get(i).get(0) == authorisedUser){
					   if(CheckDepth(esql,authorisedUser,username, via1, userList.get(i).get(1)))
						   return true;
				   }
				   else{
					   if(CheckDepth(esql,authorisedUser,username, via1, userList.get(i).get(0)))
						   return true;
				   }
			   }   
		   }
		   else{
			   /*
		   	   query = String.format(
				   "SELECT C.userId, C.connectionId "
				   + "FROM CONNECTION_USR C "
				   + "WHERE C.userId = '%s' AND C.status = 'Accept' AND C.connectionId != '%s' AND C.connectionId != '%s'"
				   + "UNION "
				   + "SELECT C.userId, C.connectionId "
				   + "FROM CONNECTION_USR C "
				   + "WHERE C.connectionId = '%s' AND C.status = 'Accept' AND C.userId != '%s' AND C.connectionId != '%s'", q1, authorisedUser, via1, q1, authorisedUser, via1);
		   	   userList = esql.executeQueryAndReturnResult(query);*/
			   return false;   
		   }
		   return false;
	   }catch(Exception e){
	         System.err.println (e.getMessage ());
	         return false;
	   }
   }
   
   public static void SendRequest(ProfNetwork esql, String authorisedUser, String target){
	   try{
		   System.out.println("Search People");
		   System.out.println("---------");
		   String username = target;
		   if(username == null){
			   System.out.print("Enter a Username: ");
			   username = in.readLine();
		   }
		   String query = String.format(
				   "SELECT * "
				   + "FROM CONNECTION_USR C "
				   + "WHERE (C.userId = '%s' AND C.connectionId = '%s')", authorisedUser, username);
		   int numRows = esql.executeQuery(query);
		   if(numRows > 0){
			   System.out.println("You already have a friend request for this user");
			   return;
		   }
		   //If the target already has a pending friend request with the user
		   //Insert the request into the table (a trigger will automatically approve the existing one isntead)
		   query = String.format(
				   "SELECT * "
				   + "FROM CONNECTION_USR C "
				   + "WHERE (C.userId = '%s' AND C.connectionId = '%s')", username, authorisedUser);
		   numRows = esql.executeQuery(query);
		   if(numRows > 0){
			   query = String.format(
					   "INSERT INTO CONNECTION_USR (userID, connectionID, status) "
					   + "VALUES ('%s', '%s', 'Request')", authorisedUser, username );
			   esql.executeUpdate(query);
			   System.out.println("Friend Request Sent");
			   return;
			   
		   }
		   if(CheckDepth(esql, authorisedUser, username, null, null) == true){
			   query = String.format(
					   "INSERT INTO CONNECTION_USR (userID, connectionID, status) "
					   + "VALUES ('%s', '%s', 'Request')", authorisedUser, username);
			   esql.executeUpdate(query);
			   System.out.println("Friend Request Sent");
			   return;
		   }
		   else{
			   System.out.println("That user is out of your connection range, unable to send request");
			   return; 
		   }
	   }catch(Exception e){
	         System.err.println (e.getMessage ());
	         return;
	   }
   }
   
   public static void PrintMessages(List<List<String>> msgList){
	   try{
		   String Headder1 = String.format("%126s", "").replace(' ', '-');
		   
		   for(int i = 0; i < msgList.size(); i++){
			   System.out.println(Headder1);
			   String formattedLine = String.format("| %-15s | %-105s", "Message ID", msgList.get(i).get(0)) + "|";
			   System.out.println(formattedLine);
			   formattedLine = String.format("| %-15s | %-105s", "Sender", msgList.get(i).get(1)) + "|";
			   System.out.println(formattedLine);
			   formattedLine = String.format("| %-15s | %-105s", "Receiver", msgList.get(i).get(2)) + "|";
			   System.out.println(formattedLine);
			   formattedLine = String.format("| %-15s | %-105s", "Time Stamp", msgList.get(i).get(4)) + "|";
			   System.out.println(formattedLine);
			   formattedLine = String.format("| %-15s | %-105s", "Status", msgList.get(i).get(5)) + "|";
			   System.out.println(formattedLine);
			   
			   System.out.println(Headder1);
			   String msg = msgList.get(i).get(3).trim();

			   int jOld = 0;
			   int j = 0;
			   while (j + 90 < msg.length() && (j = msg.lastIndexOf(" ", j + 90)) != -1) {
			       formattedLine = String.format("| %-123s", msg.substring(jOld,j)) + "|";
				   System.out.println(formattedLine);
				   jOld = j;
			       
			   }
			   if(j < msg.length()){
			       formattedLine = String.format("| %-123s", msg.substring(j)) + "|";
				   System.out.println(formattedLine);
			   }
			   System.out.println(Headder1);
			   System.out.println("");
		   } 
	   }catch(Exception e){
	         System.err.println (e.getMessage ());
	         return;
	   }	   
   }
   
   
   public static void ViewMessages(ProfNetwork esql, String authorisedUser){
	   try{
		   String query = String.format(
				   "SELECT M.msgId, M.senderId, M.receiverId, M.contents, M. sendTime, M.status "
				   + "FROM MESSAGE M "
				   + "WHERE M.senderId = '%s' AND deleteStatus != 1 AND deleteStatus < 3 "
				   + "UNION "
				   + "SELECT M.msgId, M.senderId, M.receiverId, M.contents, M. sendTime, M.status "
				   + "FROM MESSAGE M "
				   + "WHERE M.receiverId = '%s' AND deleteStatus < 2 ", authorisedUser, authorisedUser);
		   List<List<String>> msgList = esql.executeQueryAndReturnResult(query);
		   PrintMessages(msgList);
		   //Update received message status to show receiving
		   query = String.format(
					"UPDATE MESSAGE "
					+ "SET status = 'Delivered' "
					+ "WHERE receiverId = '%s' AND status = 'sent'", authorisedUser);
		   esql.executeUpdate(query);
		   String input = "";
		   boolean usermenu = true;
		   while(usermenu) {
		         System.out.println("View Messages");
		         System.out.println("---------");
		         System.out.println("1. Delete A Message");
		         System.out.println(".........................");
		         System.out.println("9. Main Menu");
		         String request = "";
		         switch (readChoice()){
		            case 1: 
		            	System.out.print("Enter the Message ID of the message you want to delete: ");
		            	input = in.readLine();
		            	query = String.format(
		            			"UPDATE MESSAGE "
		            			+ "SET deleteStatus = deleteStatus + 1 "
		            			+ "WHERE senderId = '%s' AND msgId = '%s'", authorisedUser, input);
		            	esql.executeUpdate(query);
		            	query = String.format(
		            			"UPDATE MESSAGE "
		            			+ "SET deleteStatus = deleteStatus + 2 "
		            			+ "WHERE receiverId = '%s' AND msgId = '%s'", authorisedUser, input);
		            	esql.executeUpdate(query);
		            	System.out.println("Message Deleted");
		            	return;
		            case 9: usermenu = false; break;
		            default : System.out.println("Unrecognized choice!"); break;
		         }
		       }
		   return;
	   }catch(Exception e){
	         System.err.println (e.getMessage ());
	         return;
	   }
   }
   
   public static void PrintWorkExp(List<List<String>> WorkList){
	   try{
		   String Headder1 = String.format("%126s", "").replace(' ', '-');
		   String Headder2 = String.format("| %-123s", "Work Experience") + "|";
		   System.out.println(Headder1);
		   System.out.println(Headder2);
		   System.out.println(Headder1);
		   
		   String titleLine = String.format("| %-20s | %-40s | %-30s | %-10s | %-10s |",
				   "Company Name",
				   "Role",
				   "Location",
				   "Start Date",
				   "End Date");

		   System.out.println(titleLine);
		   System.out.println(Headder1);
		   
		   for(int i = 0; i < WorkList.size(); i++){
			   String formattedLine = String.format("| %-20s | %-40s | %-30s | %s | %s |",
					   WorkList.get(i).get(0).trim(),
					   WorkList.get(i).get(1).trim(),
					   WorkList.get(i).get(2).trim(),
					   WorkList.get(i).get(3).trim(),
					   WorkList.get(i).get(4).trim());

			   System.out.println(formattedLine);
		   }
		   System.out.println(Headder1);
		   return;
	   }catch(Exception e){
	         System.err.println (e.getMessage ());
	         return;
	   }
		   
   }
   
   public static void PrintEduDet(List<List<String>> EduList){
	   try{
		   String Headder1 = String.format("%126s", "").replace(' ', '-');
		   String Headder2 = String.format("| %-123s", "Educational Details") + "|";
		   System.out.println(Headder1);
		   System.out.println(Headder2);
		   System.out.println(Headder1);
		   
		   String titleLine = String.format("| %-20s | %-40s | %-30s | %-10s | %-10s |",
				   "Institution Name",
				   "Major",
				   "Degree",
				   "Start Date",
				   "End Date");

		   System.out.println(titleLine);
		   System.out.println(Headder1);
		   
		   for(int i = 0; i < EduList.size(); i++){
			   String formattedLine = String.format("| %-20s | %-40s | %-30s | %s | %s |",
					   EduList.get(i).get(0).trim(),
					   EduList.get(i).get(1).trim(),
					   EduList.get(i).get(2).trim(),
					   EduList.get(i).get(3).trim(),
					   EduList.get(i).get(4).trim());

			   System.out.println(formattedLine);
		   }
		   System.out.println(Headder1);
		   return;
	   }catch(Exception e){
	         System.err.println (e.getMessage ());
	         return;
	   }	   
   }
   
   public static void PrintUser(List<List<String>> UserList, boolean isFriend, boolean everything){
	   try{
		   String Headder1 = String.format("%126s", "").replace(' ', '-');
		   System.out.println(Headder1);
		   
		   for(int i = 0; i < UserList.size(); i++){
			   String formattedLine = String.format("| %-15s | %-105s", "Username", UserList.get(i).get(0)) + "|";
			   System.out.println(formattedLine);
			   if(everything){
				   formattedLine = String.format("| %-15s | %-105s", "email", UserList.get(i).get(1)) + "|";
				   System.out.println(formattedLine);
			   }
			   formattedLine = String.format("| %-15s | %-105s", "Name", UserList.get(i).get(2)) + "|";
			   System.out.println(formattedLine);
			   if(isFriend || everything){
				   formattedLine = String.format("| %-15s | %-105s", "Date of Birth", UserList.get(i).get(3)) + "|";
				   System.out.println(formattedLine);
			   }
			   System.out.println(Headder1);
		   }

		   return;
	   }catch(Exception e){
	         System.err.println (e.getMessage ());
	         return;
	   }	
   }
   
   public static String SearchPeople(ProfNetwork esql, String authorisedUser, String target){
	   try{
		   System.out.println("Search People");
		   System.out.println("---------");
		   String username = target;
		   if(username == null){
			   System.out.print("Enter a Username: ");
			   username = in.readLine();
		   }
		   
	       String query = String.format("SELECT userId, email, name, dateOfBirth FROM USR WHERE userId = '%s'", username);
	       List<List<String>> Users = esql.executeQueryAndReturnResult(query);
	       if (!Users.isEmpty()){
	    	   PrintUser(Users, false, false);
	    	   query = String.format("SELECT company, role, location, startDate, endDate "
	    	   		+ "FROM WORK_EXPR WHERE userId = '%s'", username);
	    	   List<List<String>> WorkList = esql.executeQueryAndReturnResult(query);
	    	   PrintWorkExp(WorkList);
	    	   query = String.format("SELECT instituitionName, major, degree, startDate, endDate "
	    	   		+ "FROM EDUCATIONAL_DETAILS WHERE userId = '%s'", username);
	    	   List<List<String>> EduList= esql.executeQueryAndReturnResult(query);
	    	   PrintEduDet(EduList);
	    	   
			   boolean usermenu = true;
		       while(usermenu) {
		         System.out.println("Viewing Profile");
		         System.out.println("---------");
		         System.out.println("1. Send Friend Request");
		         System.out.println("2. Send Message");
		         if(target != null)
		        	 System.out.println("3. View Friends");
		         System.out.println(".........................");
		         System.out.println("9. Main Menu");
		         switch (readChoice()){
		            case 1: SendRequest(esql, authorisedUser, username); return username;
		            case 2: NewMessage(esql,authorisedUser, username); return username;
		            case 3:
		            	if(target != null){
		            		String viewRet = ViewFriends(esql, target);
			            	if(viewRet == null){
			            		System.out.println("Invalid User");
			            		break;
			            	}
		            		return username;
		            	}
		            	else
		            		System.out.println("Unrecognized choice!");
		            	break;
		            case 9: usermenu = false; break;
		            default : System.out.println("Unrecognized choice!"); break;
		         }
		       }
	    	   return username;
	       }
	       else{
	    	   System.out.println("User Not Found");
	    	   return null;
	       }
	   }catch(Exception e){
	         System.err.println (e.getMessage ());
	         return null;
	   }
   }
   
   public static void ChangePassword(ProfNetwork esql, String authorisedUser){
	   try{
		   System.out.println("---------");
		   System.out.print("\tEnter New Password: ");
		   String newpass = in.readLine();
		   System.out.print("\tEnter your current password: ");
		   String oldpass = in.readLine();
	       String query = String.format("\tSELECT * FROM USR WHERE userId = '%s' AND password = '%s'", authorisedUser, oldpass);
	       int userNum = esql.executeQuery(query);
	       if (userNum > 0){
	    	   query = String.format("UPDATE USR SET password = '%s' WHERE userId = '%s' AND password = '%s'", newpass, authorisedUser, oldpass);
	    	   esql.executeUpdate(query);
	    	   System.out.println("\tPassword Successfully Changed");
	    	   return;
	       }
	       else{
	    	   System.out.println("Incorrect Password. Returning to main menu.");
	    	   return;
	       }
	   }catch(Exception e){
	         System.err.println (e.getMessage ());
	         return;
	   }
   }
   
   public static void AddEdu(ProfNetwork esql, String authorisedUser){
	   try{
		   System.out.println("---------");
		   System.out.print("\tEnter institution name: ");
		   String company = in.readLine();
		   System.out.print("\tEnter major: ");
		   String role = in.readLine();
		   System.out.print("\tEnter degree: ");
		   String location = in.readLine();
		   System.out.print("\tEnter start date (YYYY/MM/DD): ");
		   String startDate = in.readLine();
		   System.out.print("\tEnter end date (YYYY/MM/DD): ");
		   String endDate = in.readLine();
		   
		   String query = String.format("INSERT INTO EDUCATIONAL_DETAILS (userId, instituitionName, major, degree, startDate, endDate)"
		   		+ " VALUES ('%s','%s','%s','%s','%s','%s')",authorisedUser, company, role, location, startDate, endDate);
		   esql.executeUpdate(query);
		   System.out.println("\tWork Experience Added");
		   return;
	   }catch(Exception e){
	         System.err.println (e.getMessage ());
	         return;
	   }
   }
   
   public static void AddWork(ProfNetwork esql, String authorisedUser){
	   try{
		   System.out.println("---------");
		   System.out.print("\tEnter company name: ");
		   String instName = in.readLine();
		   System.out.print("\tEnter role: ");
		   String major = in.readLine();
		   System.out.print("\tEnter location: ");
		   String degree = in.readLine();
		   System.out.print("\tEnter start date (YYYY-MM-DD): ");
		   String startDate = in.readLine();
		   System.out.print("\tEnter end date (YYYY-MM-DD): ");
		   String endDate = in.readLine();
		   
		   String query = String.format("INSERT INTO WORK_EXPR (userId, company, role, location, startDate, endDate)"
		   		+ " VALUES ('%s','%s','%s','%s','%s','%s')", authorisedUser, instName, major, degree, startDate, endDate);
		   esql.executeUpdate(query);
		   System.out.println("\tEducational Detail Added");
		   return;
	   }catch(Exception e){
	         System.err.println (e.getMessage ());
	         return;
	   }
   }
}//end ProfNetwork
