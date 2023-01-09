package gr.uoa.register.client;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;

import gr.uoa.citizen.domain.Citizen;

public class CitizenClient {
	
	private String ip;
    private int port;
    private String basePath;    
    
    /*********************************************************************
     * 
     *********************************************************************/
    public CitizenClient(String ip, int port) {
    	this.ip = ip;
    	this.port = port;
    	basePath = "http://" + ip + ":" + port + "/register-rest/api/citizens"; 
    }
    
    /*********************************************************************
     * 
     *********************************************************************/
    private WebTarget getTarget(String methodName) {
    	ClientConfig cc = new ClientConfig();
    	Client c = ClientBuilder.newClient(cc);
    	HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic("admin", "admin");
    	c.register(feature);    	
    	WebTarget target = c.target(basePath + methodName);
    	
		return target;
    }
    
    /*********************************************************************
     * 
     *********************************************************************/
    private Citizen createCitizen(String id) {
    	Citizen citizen = new Citizen();    	
    	citizen.setId(id);
    	citizen.setName("name_"+id);
    	citizen.setSurname("surname_"+id);
    	citizen.setDob("16-06-1977");
    	citizen.setSex("1");
    	citizen.setAddress("some where in the world_"+id);
    	citizen.setAfm("124578958");
    	return citizen;
    }
    
    
    /*********************************************************************
     * 
     *********************************************************************/
    public void addCitizen(String id, MediaType type) {
    	System.out.println("Invoking POST /citizens");
    	WebTarget r = getTarget(""); 
    	Citizen citizen = createCitizen(id);
    	Invocation.Builder builder = r.request();
 	    Response response = builder.post(Entity.entity(citizen,type),Response.class);
 	    int status = response.getStatus();
 	    if (status >= 300){
 	    	System.out.println("Something wrong happened when calling addCitizen");
 	    	System.out.println(response.readEntity(String.class));
 	    }
 	    else{
 	    	System.out.println("Got successful result from invocation");
 	    	System.out.println(response.readEntity(String.class));
 	    }
    }
    
    /*********************************************************************
     * 
     *********************************************************************/
    public void getCitizensWithParams(String name, String surname, MediaType type) {
    	System.out.println("Invoking GET /citizens with query parameters");
    	WebTarget r = getTarget("");
    	if (name != null) r = r.queryParam("name",name);
    	if (surname != null) r = r.queryParam("surname",surname);
    	
    	Invocation.Builder builder = r.request(type);
 	    Response response = builder.get();
 	    int status = response.getStatus();
 	    if (status >= 300){
 	    	System.out.println("Something wrong happened when calling getCitizens with parameters");
 	    	System.out.println(response.readEntity(String.class));
 	    }
 	    else{
 	    	System.out.println("Got successful result from invocation");
 	    	System.out.println(response.readEntity(String.class));
 	    }
    }
    
    /*********************************************************************
     * 
     *********************************************************************/
    public void deleteCitizen(String id) {
    	System.out.println("Invoking DELETE /citizens/" + id);
    	WebTarget r = getTarget("/" + id);
    	Invocation.Builder builder = r.request();
 	    Response response = builder.delete();
 	    int status = response.getStatus();
 	    if (status >= 300){
 	    	System.out.println("Something wrong happened when calling deleteCitizen");
 	    	System.out.println(response.readEntity(String.class));
 	    }
 	    else{
 	    	System.out.println("Got successful result from invocation");
 	    	System.out.println(response.readEntity(String.class));
 	    }
    }
    
    /*********************************************************************
     * 
     *********************************************************************/
    public Citizen getCitizen(String id, MediaType type) {
    	System.out.println("Invoking GET /citizen/" + id);
    	
    	Citizen citizen = new Citizen();
    	
    	WebTarget r = getTarget("/" + id);
    	Invocation.Builder builder = r.request(type);
 	    Response response = builder.get();
 	    int status = response.getStatus();
 	    if (status >= 300){
 	    	System.out.println("Something wrong happened when calling getCitizen");
 	    	System.out.println(response.readEntity(String.class));
 	    }
 	    else{
 	    	System.out.println("Got successful result from invocation");
 	    	if (!type.equals(MediaType.TEXT_HTML_TYPE)) {
 	    		citizen = response.readEntity(Citizen.class);
 	    		System.out.println("Got citizen: " + citizen);
 	    	}
 	    	else System.out.println(response.readEntity(String.class));
 	    }
 	    
		return citizen;
    }
    
    /*********************************************************************
     * 
     *********************************************************************/
    public void updateCitizen(Citizen citizen ,String newVat,String newAddress, MediaType type) {
    	System.out.println("Invoking PUT /citizens/");
    	
    	WebTarget r = getTarget("");
    	
    	citizen.setAfm(newVat);
    	citizen.setAddress(newAddress);
    	
    	Invocation.Builder builder = r.request(type);
    	
 	    Response response = builder.put(Entity.entity(citizen,type));
 	    int status = response.getStatus();
 	    if (status >= 300){
 	    	System.out.println("Something wrong happened when calling updateCitizen");
 	    	System.out.println(response.readEntity(String.class));
 	    }
 	    else{
 	    	System.out.println("Got successful result from invocation");
 	    	System.out.println(response.readEntity(String.class));
 	    }
    }
    
    
    /*********************************************************************
     * 
     *********************************************************************/
    public static void main(String[] args) {
    	
    	MediaType html = MediaType.TEXT_HTML_TYPE;
    	MediaType json = MediaType.APPLICATION_JSON_TYPE;
    	MediaType xml = MediaType.APPLICATION_XML_TYPE;
    	
    	CitizenClient cc = new CitizenClient("localhost", 8080);
    	//Εισαγωγή εγγραφών
    	cc.addCitizen("11111111", json);
    	cc.addCitizen("22222222", json);   
    	cc.addCitizen("33333333", json);
    	cc.addCitizen("44444444", json);
    	
    	//Αναζήτηση με κριτήρια
    	cc.getCitizensWithParams("name_11111111", null, html);
    	cc.getCitizensWithParams(null, "surname_22222222", html); 
    	
    	//Διαγραφή Εγράφης
    	cc.deleteCitizen("33333333");
    	
    	Citizen citizen_44444444 = cc.getCitizen("44444444", xml);
    	System.out.print(citizen_44444444);
    	
    	//Ενημέρωση εγράφης
    	cc.updateCitizen(citizen_44444444,"newvat123","updated address",xml);
	}

}
