package gr.uoa.register.service;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import gr.uoa.register.domain.Citizen;
import gr.uoa.register.utility.DBHandler;
import gr.uoa.register.utility.HTMLHandler;

@Path("/citizens")
public class CitizensService {
	
	private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger("CitizensService");
	
	@GET
	@Produces({MediaType.TEXT_HTML, MediaType.TEXT_PLAIN})
	/*********************************************************************************************
	 * 
	 *********************************************************************************************/
	public Response getCitizensInHtml(@QueryParam("name") @DefaultValue("") String name,
			                       @QueryParam("surname") @DefaultValue("") String surname)
					throws Exception{
		
		logger.info("name=" + name + " surname=" + surname);
		List<Citizen> citizens = null;
		if ((name != null && !name.trim().equals("")) || (surname != null && !surname.trim().equals(""))){
			
			Citizen c = new Citizen();
			c.setName(name);
			c.setSurname(surname);			
			citizens = DBHandler.searchRecord(c);
			
		}
		else citizens = DBHandler.getAllRecords();
		
		if (citizens == null) citizens = new ArrayList<Citizen>();
		String answer = HTMLHandler.createHtmlCitizens(citizens);
		
		return Response.ok(answer, MediaType.TEXT_HTML).build();
	}
	
	@POST
	@Path ("/update")
	/*********************************************************************************************
	 * 
	 *********************************************************************************************/
	public Response updateCitizen(@FormParam("id") String id,
			                      @FormParam("vat") String vat,
			                      @FormParam("address") String address) throws Exception {
		
		logger.info("Trying to update citizen with id: " + id);
		
		DBHandler.setError(new ArrayList<String>());		
		DBHandler.updateRecord(id, vat, address);
		
		if(DBHandler.getError().size()==0){
			String msg = HTMLHandler.createResponseMsg("Επιτυχής ολοκλήρωση.");
			return Response.ok(msg).build();
		}			
		else {
			String msg = HTMLHandler.createResponseMsg("Η ενημέρωση της εγγραφής απέτυχε : "+
		                                               String.join(",", DBHandler.getError()));					
			return Response.ok(msg).build();
		}	
	}
	
	
	@POST
	@Path ("/search")
	/*********************************************************************************************
	 * 
	 *********************************************************************************************/
	public Response searchCitizen(@FormParam("id") String id,
			                      @FormParam("name") String name,
			                      @FormParam("surname") String surname,
			                      @FormParam("dob") String dob,
			                      @FormParam("sex") String sex,
			                      @FormParam("vat") String vat,
			                      @FormParam("address") String address) throws Exception {
		
		logger.info("Trying to search citizens");
		
		Citizen c = new Citizen();
		c.setId(id);
		c.setName(name);
		c.setSurname(surname);
		c.setDob(dob);
		c.setSex(sex);
		c.setAfm(vat);
		c.setAddress(address);
		
		DBHandler.setError(new ArrayList<String>());		
		List<Citizen> foundCitizens = searchRecord(c);
				
		if(DBHandler.getError().size()==0){
			String msg = HTMLHandler.createHtmlCitizens(foundCitizens);
			return Response.ok(msg).build();
		}
		else {
			String msg = HTMLHandler.createResponseMsg("Η αναζήτηση απέτυχε : "+
		                                               String.join(",", DBHandler.getError()));					
			return Response.ok(msg).build();
		}
	}	
	
	@POST
	@Path ("/add")
	/*********************************************************************************************
	 * 
	 *********************************************************************************************/
	public Response addCitizen(@FormParam("id") String id,
			                   @FormParam("name") String name,
			                   @FormParam("surname") String surname,
			                   @FormParam("dob") String dob,
			                   @FormParam("sex") String sex,
			                   @FormParam("vat") String vat,
			                   @FormParam("address") String address) throws Exception {
		
		logger.info("Trying to insert citizen with id: " + id);
		
		Citizen c = new Citizen();
		c.setId(id);
		c.setName(name);
		c.setSurname(surname);
		c.setDob(dob);
		c.setSex(sex);
		c.setAfm(vat);
		c.setAddress(address);
		
		DBHandler.setError(new ArrayList<String>());		
		DBHandler.insertRecord(c);
		
		if(DBHandler.getError().size()==0){
			String msg = HTMLHandler.createResponseMsg("Επιτυχής ολοκλήρωση.");
			return Response.ok(msg).build();
		}			
		else {
			String msg = HTMLHandler.createResponseMsg("Η εισαγωγή της εγγραφής απέτυχε : "+
		                                               String.join(",", DBHandler.getError()));					
			return Response.ok(msg).build();
		}
	}
	
	@POST
	@Path ("/delete")
	/*********************************************************************************************
	 * 
	 *********************************************************************************************/
	public Response deleteCitizen(@FormParam("id") String id) throws Exception {
		logger.info("Trying to delete citizen with id: " + id);		
		
		DBHandler.setError(new ArrayList<String>());		
		DBHandler.deleteRecord(id);
		
		if(DBHandler.getError().size()==0){
			String msg = HTMLHandler.createResponseMsg("Επιτυχής ολοκλήρωση.");
			return Response.ok(msg).build();
		}			
		else {
			String msg = HTMLHandler.createResponseMsg("Η διαγραφή της εγγραφής απέτυχε : "+
		                                               String.join(",", DBHandler.getError()));					
			return Response.ok(msg).build();
		}		
	}
	
	/*********************************************************************************************
	 * 
	 *********************************************************************************************/
	public List<Citizen> searchRecord(Citizen c) throws Exception {
		
		List<Citizen> allRecs = DBHandler.getAllRecords();
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
	
	/*********************************************************************************************
	 * 
	 *********************************************************************************************/
	private boolean recordExists(Citizen c1,List<Citizen> list) {
				
		for (Citizen c2 : list) {
			
			if(c2.getId().equals(c1.getId())) {				
				return true;
			}
		}
		
		return false;		
	}
	
	
	@DELETE
	@Path ("{id}")
	/*********************************************************************************************
	 * 
	 *********************************************************************************************/
	public Response deleteCitizenUsingClient(@PathParam("id") String id) throws Exception {
		logger.info("Trying to delete citizen with id: " + id);		
		
		DBHandler.setError(new ArrayList<String>());		
		DBHandler.deleteRecord(id);
		
		if(DBHandler.getError().size()==0)
			return Response.ok("Επιτυχής ολοκλήρωση.").build();
		else {
			String msg = "Η διαγραφή της εγγραφής απέτυχε : "+String.join(",", DBHandler.getError());
			return Response.ok(msg).build();
		}		
	}
	
	@POST
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML})
	/*********************************************************************************************
	 * 
	 *********************************************************************************************/
	public Response addCitizenUsingClient(@Context UriInfo uriInfo, Citizen citizen) throws Exception {
		
		DBHandler.setError(new ArrayList<String>());		
		DBHandler.insertRecord(citizen);
		
		if(DBHandler.getError().size()==0){
			String msg = HTMLHandler.createResponseMsg("Επιτυχής ολοκλήρωση.");
			return Response.ok(msg).build();
		}			
		else {
			String msg = HTMLHandler.createResponseMsg("Η εισαγωγή της εγγραφής απέτυχε : "+
		                                               String.join(",", DBHandler.getError()));					
			return Response.ok(msg).build();
		}		
	}
	
	@GET
	@Path ("/{id}")
	@Produces(MediaType.APPLICATION_XML)
	/*********************************************************************************************
	 * 
	 *********************************************************************************************/
	public Citizen getCitizenInHtml(@PathParam("id") String id) throws NotFoundException, InternalServerErrorException {
		logger.info("get citizen by id: " + id);
		
		Citizen citizen = DBHandler.getCitizenById(id);
		if (citizen != null) {
			return citizen;
		}
		return new Citizen();
	}
		
	@PUT	
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_HTML})
	/*********************************************************************************************
	 * 
	 *********************************************************************************************/
	public Response updateCitizenUsingClient(Citizen c) throws Exception {
				
		logger.info("Trying to update citizen with id: " + c.getId());
		
		DBHandler.setError(new ArrayList<String>());		
		DBHandler.updateRecord(c.getId(), c.getAfm(), c.getAddress());
		
		if(DBHandler.getError().size()==0){
			String msg = HTMLHandler.createResponseMsg("Επιτυχής ολοκλήρωση.");
			return Response.ok(msg).build();
		}			
		else {
			String msg = HTMLHandler.createResponseMsg("Η ενημέρωση της εγγραφής απέτυχε : "+
		                                               String.join(",", DBHandler.getError()));					
			return Response.ok(msg).build();
		}			
	}
	
}

