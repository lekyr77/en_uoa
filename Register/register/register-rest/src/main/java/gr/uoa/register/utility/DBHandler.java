package gr.uoa.register.utility;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.InternalServerErrorException;

import gr.uoa.register.configuration.PropertyReader;
import gr.uoa.register.domain.Citizen;

public class DBHandler {
	static {initDB();}
	
	static final Integer  ID_DIGITS = 8;
	static final Integer  VAT_DIGITS = 9;
	static final String   SEX_MALE = "1";
	static final String   SEX_FEMALE= "2";
	
	private static List<String> error = new ArrayList<String>();
	
	
	
	/**************************************************************************************
	 * 
	 **************************************************************************************/
	private DBHandler() {
		
	}
	
	/**************************************************************************************
	 * 
	 **************************************************************************************/
	private static void createTables(Connection con) throws SQLException {
		Statement stmt = con.createStatement(); 
		stmt.executeUpdate(SqlStm.getCreateTable());		
	}
	
	/**************************************************************************************
	 * 
	 **************************************************************************************/
	private static void initDB() {
		
		Connection conn = null;
		
		try{
			String connectionStr = String.format("jdbc:mysql://%s:%s?user=%s&password=%s",
					                             PropertyReader.getDbHost(),
					                             PropertyReader.getDbPort(),
					                             PropertyReader.getLogin(),
					                             PropertyReader.getPwd());
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection(connectionStr);
		} 
		catch (Exception e)	{			
			error.add("Η σύνδεση με την βάση απέτυχε :"+e.getMessage());
		}
		
		if(conn!=null) {			
			Statement stmt;
			
			try {
				stmt = conn.createStatement();				
				stmt.executeUpdate(SqlStm.getCreateDatabaseStm());
				createTables(conn);				
			} catch (Exception e) {
				error.add("Η αρχικοποίηση της βάσης απέτυχε :"+e.getMessage());				
			}
			finally {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}		
	}
	
	/**************************************************************************************
	 * 
	 **************************************************************************************/
	private static Connection getConnection() throws InternalServerErrorException {
		try {
			Connection con = null;
			if (PropertyReader.isSqlite()){
				Class.forName("org.sqlite.JDBC");
				con = DriverManager.getConnection("jdbc:sqlite:./my.db");
			}
			else {
				Class.forName("com.mysql.cj.jdbc.Driver");  
				con = DriverManager.getConnection(  
			"jdbc:mysql://" + PropertyReader.getDbHost() + ":" + PropertyReader.getDbPort() + "/register",PropertyReader.getLogin(),PropertyReader.getPwd());
			}
			return con;
		}
		catch(Exception e) {
			throw new InternalServerErrorException("Cannot connect to underlying database");
		}
	}
	
	/**************************************************************************************
	 * 
	 **************************************************************************************/	
	private static boolean recordExists(Citizen c1,List<Citizen> list) {
				
		for (Citizen c2 : list) {
			
			if(c2.getId().equals(c1.getId())) {				
				return true;
			}
		}
		
		return false;		
	}
	
	/**************************************************************************************
	 * 
	 **************************************************************************************/
	public static List<Citizen> getAllRecords() throws ClassNotFoundException, SQLException {
		
		List<Citizen> citizens = new ArrayList<Citizen>();
		
		Connection conn = getConnection();
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(SqlStm.getselectAllStm());
		
		 while (rs.next()){
			 Citizen c = new Citizen();
			 
			 c.setId(rs.getString("id"));
			 c.setName(rs.getString("name"));
			 c.setSurname(rs.getString("surname"));
			 c.setSex(rs.getString("sex"));
			 c.setDob(rs.getDate("dob").toString());
			 c.setAfm(rs.getString("vat"));
			 c.setAddress(rs.getString("address"));
			 
			 citizens.add(c);
		 } 
		
		return citizens;		
	}
		
	/**************************************************************************************
	 * 
	 **************************************************************************************/
	private static boolean recordExists(Citizen c1) throws IOException, ClassNotFoundException, SQLException {		
		
		List<Citizen> allRecs = getAllRecords();
				
		for (Citizen c2 : allRecs) {
			
			if(c2.getId().equals(c1.getId())) {				
				return true;
			}
		}
		
		return false;		
	}
	
	/**************************************************************************************
	 * 
	 **************************************************************************************/
	public static Citizen getCitizenById(String id) throws InternalServerErrorException{
		Citizen citizen = null;
		Connection con = getConnection();
		try {
			Statement stmt = con.createStatement();
			String query = "select * from register.citizens where id ='" + id + "'";
			System.out.println("query is: " + query);
			ResultSet rs = stmt.executeQuery(query);
			System.out.println("query was successful!!!");
			if (rs.next()) {
				citizen = getCitizenFromRS(rs);
			}
			System.out.println("processed query results!!!");
			
			con.close();
		}
		catch(Exception e) {
			throw new InternalServerErrorException("An internal error prevented from getting the information of the library's books");
		}
		return citizen;
	}
	
	/**************************************************************************************
	 * 
	 **************************************************************************************/
	private static Citizen getCitizenFromRS(ResultSet rs) throws SQLException{
		
		Citizen c = new Citizen();
		
		c.setId(rs.getString("id"));
		c.setAddress(rs.getString("address"));
		c.setAfm(rs.getString("vat"));
		c.setDob(rs.getString("dob"));
		c.setName(rs.getString("name"));
		c.setSurname(rs.getString("surname"));
		c.setSex(rs.getString("sex"));
		
		return c;
	}
	
	/**************************************************************************************
	 * 
	 **************************************************************************************/
	private static boolean isUserInputValid(Citizen c) throws Exception{
		
		if(c.getId().equals(""))
			error.add("Ο αριθμός ταυτότητας είναι υποχρεωτικό πεδίο.");	
		
		if(!c.getId().equals("") && c.getId().length()!=ID_DIGITS)
			error.add("Ο αριθμός ταυτότητας πρέπει να αποτελείται αποκλειστικά από "+ID_DIGITS+" χαρακτήρες.");
		
		if(c.getName().equals(""))
			error.add("Το όνομα είναι υποχρεωτικό πεδίο.");
		
		if(c.getSurname().equals(""))
			error.add("Το επίθετο είναι υποχρεωτικό πεδίο.");
		
		if(c.getSex().equals(""))
			error.add("Το φύλο είναι υποχρεωτικό πεδίο.");
		
		if(!c.getSex().equals("") && (!c.getSex().equals(SEX_MALE) && !c.getSex().equals(SEX_FEMALE)))
		  error.add("Το φύλο θα πρέπει να έχει την τιμή Άνδρας=1 ή Γυναίκα=2.");
		
		if(c.getDob().equals(""))
			error.add("Η ημερ. γέννησης είναι υποχρεωτικό πεδίο.");
		
		if(!c.getDob().equals("") && !isValidDateOfBirth(c.getDob())) {
			error.add("Η ημερ. γέννησης πρέπει να είναι της μορφής ηη-μμ-εεεε.");
		}
		
		if(!c.getAfm().equals("") && c.getAfm().length()!=VAT_DIGITS)
			error.add("Το ΑΦΜ θα πρέπει να έχει "+VAT_DIGITS+" ψηφία.");
		
		return (error.size()==0);
	}
	
	/**************************************************************************************
	 * 
	 **************************************************************************************/	
	private static boolean isValidDateOfBirth(String dateStr) {
		
        try {
        	LocalDate.parse(dateStr,DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        } catch (DateTimeParseException e) {
            return false;
        }
        return true;
    }
	
	
	/**************************************************************************************
	 * 
	 **************************************************************************************/	
	public static void insertRecord(Citizen c) throws Exception {
		
		if (recordExists(c)) {
			error.add("Υπάρχει ήδη εγράφη με αριθμό ταυτότητας :"+c.getId());
			return;
		}
				
		if (!isUserInputValid(c)) 			
			return;
		
		Connection conn = getConnection();
		
		PreparedStatement preparedStmt = conn.prepareStatement(SqlStm.getInsertStm());		
		preparedStmt.setString (1, c.getId());
		preparedStmt.setString (2, c.getName());
		preparedStmt.setString (3, c.getSurname());
		preparedStmt.setInt(4, Integer.valueOf(c.getSex()));
		
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
	    java.util.Date parsedate = format.parse(c.getDob());
	    java.sql.Date sqlDate = new java.sql.Date(parsedate.getTime());
		preparedStmt.setDate(5, sqlDate);
		
		if(c.getAfm()!=null)
			preparedStmt.setString(6, c.getAfm());
		else 
			preparedStmt.setNull(6, Types.VARCHAR);
		
		if(c.getAddress()!=null)
			preparedStmt.setString(7, c.getAddress());
		else 
			preparedStmt.setNull(7, Types.VARCHAR);
		
		preparedStmt.execute();
		
		conn.close();
	}
		
	/**************************************************************************************
	 * 
	 **************************************************************************************/	
	public static void deleteRecord(String id) throws Exception {
		
		Citizen c1 = new Citizen();
		c1.setId(id);
		
		if(!recordExists(c1)) {
			error.add("Δεν βρέθηκε εγγραφή με αριθμό ταυτότητας :"+id);
			return;
		}
		
		Connection conn = getConnection();		
		PreparedStatement preparedStmt = conn.prepareStatement(SqlStm.getDeleteStm());
		preparedStmt.setString (1,id);
		
		preparedStmt.execute();		
		conn.close();
	}
	
	/**************************************************************************************
	 * 
	 **************************************************************************************/
	public static void updateRecord(String id,String afm,String address) throws Exception {
		
		Citizen c = new Citizen();
		c.setId(id);
		c.setAfm(afm);
		c.setAddress(address);
		
		if(!recordExists(c)) {
			error.add("Δεν βρέθηκε εγγραφή με αριθμό ταυτότητας :"+id);
		}
		
		if(!c.getAfm().equals("") && c.getAfm().length()!=VAT_DIGITS) {
			error.add("Το ΑΦΜ θα πρέπει να έχει "+VAT_DIGITS+" ψηφία.");
			return;
		}			
		
		Connection conn = getConnection();
		PreparedStatement preparedStmt = conn.prepareStatement(SqlStm.getUpdateStm());
		preparedStmt.setString (1,afm);
		preparedStmt.setString (2,address);
		preparedStmt.setString (3,id);
		
		preparedStmt.execute();		
		conn.close();
	}
	
	/**************************************************************************************
	 * 
	 **************************************************************************************/
	public static void setError(List<String> error) {
		DBHandler.error = error;
	}

	/**************************************************************************************
	 * 
	 **************************************************************************************/
	public static List<String> getError() {
		return error;
	}
	
	/**************************************************************************************
	 * 
	 **************************************************************************************/
	public static List<Citizen> searchRecord(Citizen c) throws Exception {
		
		List<Citizen> allRecs = getAllRecords();
		List<Citizen> result = new ArrayList<Citizen>();
			
		for (Citizen rec : allRecs) {
			
			if(rec.getId().equals(c.getId()) && !recordExists(rec, result))				
					result.add(rec);
			
			if(rec.getName().equals(c.getName()) && !recordExists(rec, result))
				result.add(rec);
			
			if(rec.getSurname().equals(c.getSurname()) && !recordExists(rec, result))
				result.add(rec);
			
			if(rec.getSex().equals(c.getSex()) && !recordExists(rec, result))
				result.add(rec);
			
			if(rec.getDob().equals(c.getDob()) && !recordExists(rec, result))
				result.add(rec);
			
			if(rec.getAfm().equals(c.getAfm()) && !recordExists(rec, result))
				result.add(rec);
			
			if(rec.getAddress().equals(c.getAddress()) && !recordExists(rec, result))
				result.add(rec);
		}
		
		return result;
	}
	
	/**************************************************************************************
	 * 
	 **************************************************************************************/
	public static void main(String[] args) throws Exception {
		
		Citizen c1 = new Citizen();
		
		c1.setId("11111111");
		c1.setName("Λευτέρης");
		c1.setSurname("Κυριακίδης");
		c1.setAfm("002457965");
		c1.setSex(SEX_MALE);
		c1.setDob("16-06-1977");
		c1.setAddress("Αμυκλών 31, Λαμπρινή");
		
		Citizen c2 = new Citizen();
		
		c2.setId("22222222");
		c2.setName("Αντιγόνη");
		c2.setSurname("Καλλιπολίτου");
		c2.setAfm("006569852");
		c2.setSex(SEX_FEMALE);
		c2.setDob("10-07-1977");
		c2.setAddress("Αγίας Γλυκερίας 17 , Γαλάτσι");
		
		DBHandler.insertRecord(c1);
		DBHandler.insertRecord(c2);
		
		DBHandler.deleteRecord("11111111");		
		
		DBHandler.updateRecord("22222222", "111111111", "Πουθενά 12 , Παπάγου");
				
		List<Citizen> allRecs = DBHandler.getAllRecords(); 
		if(allRecs!=null && !allRecs.isEmpty()) {
			for (Citizen c : allRecs) {						
				System.out.println(c.toString());			
			}	
		}
		else {
			System.out.println("Δεν βρέθηκαν εγγραφές.");
		}		
		
	}
}
