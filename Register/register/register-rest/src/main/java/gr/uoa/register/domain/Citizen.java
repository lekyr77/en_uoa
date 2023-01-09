package gr.uoa.register.domain;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Citizen {

	private String id;
	private String name;
	private String surname;
	private String sex;
	private String dob;
	private String afm;	
	private String address;
	
	/*************************************
	 * 
	 *************************************/
	public String getId() {
		return id;
	}
	
	/*************************************
	 * 
	 *************************************/
	public void setId(String id) {
		this.id = id;
	}
	
	/*************************************
	 * 
	 *************************************/
	public String getName() {
		return name;
	}
	
	/*************************************
	 * 
	 *************************************/
	public void setName(String name) {
		this.name = name;
	}
	
	/*************************************
	 * 
	 *************************************/
	public String getSurname() {
		return surname;
	}
	
	/*************************************
	 * 
	 *************************************/
	public void setSurname(String surname) {
		this.surname = surname;
	}
	
	/*************************************
	 * 
	 *************************************/
	public String getSex() {
		return sex;
	}
	
	/*************************************
	 * 
	 *************************************/
	public void setSex(String sex) {
		this.sex = sex;
	}
	
	/*************************************
	 * 
	 *************************************/
	public String getDob() {
		return dob;
	}
	
	/*************************************
	 * 
	 *************************************/
	public void setDob(String dob) {
		this.dob = dob;
	}
	
	/*************************************
	 * 
	 *************************************/
	public String getAfm() {
		return afm;
	}
	
	/*************************************
	 * 
	 *************************************/
	public void setAfm(String afm) {
		this.afm = afm;
	}
	
	/*************************************
	 * 
	 *************************************/
	public String getAddress() {
		return address;
	}
	
	/*************************************
	 * 
	 *************************************/
	public void setAddress(String address) {
		this.address = address;
	}
	
	/*************************************
	 * 
	 *************************************/
	private String formatDob(String dobStr) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		Date d = null;
		try {
			d = sdf.parse(dob.toString());
		} catch (ParseException e) {
			
			e.printStackTrace();
		}
		
		sdf.applyPattern("dd-MM-yyyy");
		
		return sdf.format(d);		
	}
	
	/*************************************
	 * 
	 *************************************/	
	@Override
	public String toString() {
		
		return String.format("Ταυτότητα=%s \tΌνομα=%s \t\tΈπίθετο=%s \t\tΦύλο=%s \tΗμερ. γέννησης=%s \tΑΦΜ=%s \tΔιεύθυνση=%s",
				             id,name,surname,sex.equals("1")?"Άνδρας":"Γυναικά",formatDob(dob.toString()),afm.trim(),address.trim());
	}		
	
}
