package gr.uoa.register.utility;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import gr.uoa.register.domain.Citizen;


public class HTMLHandler {
	
	/*************************************************************
	 * 
	 *************************************************************/
	private static String createCitizenRow(Citizen c) {
		String str = "<tr>";
		str += "<td>" + c.getId() + "</td>";
		str += "<td>" + c.getName() + "</td>";		
		str += "<td>" + c.getSurname() + "</td>";
		str += "<td>" + (c.getSex()!=null && c.getSex().equals("1")?"Άνδρας":"Γυναίκα")+ "</td>";
		str += "<td>" + c.getAfm() + "</td>";
		str += "<td>" + c.getDob() + "</td>";		
		str += "<td>" + c.getAddress() + "</td>";
		str += "</tr>\n";
		
		return str;
	}
	
	/*************************************************************
	 * 
	 *************************************************************/
	public static String createResponseMsg(String msg) {
		String answer = "<html>\n";
		
		answer += "<head>\n";
		answer += "<meta charset=\"UTF-8\">\n";		 
		answer += "<title>Εγγραφές μητρώου</title>\n";
		answer += "</head>\n";		
		answer += "<body>\n";
		answer +="<a href=\"/register-rest/MainPage.html\">Αρχική</a><br><br>";
		
		List<String> msgList = Arrays.stream(msg.split("\\,"))
				                     .map(str -> str.trim())
				                     .collect(Collectors.toList()); 
		
		for (String item : msgList) {
			answer += "<p>"+item+"</p>\n";
		}
		
		answer += "</body>\n";		
		answer += "</html>";
		
		return answer;		
	}
	
	/*************************************************************
	 * 
	 *************************************************************/
	public static String createHtmlCitizens(List<Citizen> citizens) {
		String answer = "<html>\n";
		
		answer += "<head>\n";
		answer += "<meta charset=\"UTF-8\">\n";		 
		answer += "<title>Εγγραφές μητρώου</title>\n";
		answer += "</head>\n";
		
		answer += "<body>\n";
		answer +="<a href=\"/register-rest/MainPage.html\">Αρχική</a><br><br>";
		answer += "<h1>Μητρώο</h1>\n";
		answer += "<table border=\"1\" width=\"60%\" align=\"center\">\n";
		answer += "<caption>Λίστα πολίτων</caption>\n";
		answer += "<tr><th>Αριθμός ταυτότητας</th><th>Όνομα</th><th>Επίθετο</th><th>Φύλο</th>";
		answer += "<th>ΑΦΜ</th><th>Ημερ. γέννησης</th><th>Διεύθυνση κατοικίας</th>";		
		answer += "</tr>\n";
		if (citizens != null) for (Citizen citizen: citizens) answer += createCitizenRow(citizen);
		answer += "</table>\n";
		answer += "</body>\n";
		
		answer += "</html>";
		
		return answer;
	}
	
	/*************************************************************
	 * 
	 *************************************************************/
	public static String createHtmlCitizen(Citizen citizen) {
		String answer = "<html>\n";
		
		answer += "<head>\n";
		answer += "<title>A citizen from Register Library</title>\n";
		answer += "</head>\n";
		
		answer += "<body>\n";
		if (citizen != null && citizen.getId() != null && !citizen.getId().trim().equals(""))
			answer += "<h1>CITIZEN " + citizen.getId() + "</h1>\n";
		answer += "<table border=\"1\" width=\"60%\" align=\"center\">\n";
		answer += "<caption>The requested book</caption>\n";
		answer += "<tr><th>Id</th><th>Name</th><th>Surname</th><th>Sex</th>";
		answer += "<th>Vat</th><th>DoB</th><th>Address</th>";		
		answer += "</tr>\n";
		
		if (citizen != null && citizen.getId() != null && citizen.getName() != null 
				&& citizen.getSurname() != null) 
			answer += createCitizenRow(citizen);
		answer += "</table>\n";
		answer += "</body>\n";
		
		answer += "</html>";
		
		return answer;
	}
	
}
