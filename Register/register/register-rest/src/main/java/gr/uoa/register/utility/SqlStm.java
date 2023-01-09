package gr.uoa.register.utility;

public class SqlStm {
	
	public static final String   SCHEMA = "register";
	public static final String   TABLE = "citizens";
	
	public static String getCreateDatabaseStm() {
		StringBuilder sql = new StringBuilder();
		sql.append("create database if not exists ").append(SCHEMA);
		return sql.toString();
	}
	
	public static String getselectAllStm() {
		StringBuilder sql = new StringBuilder();
		sql.append(" select id,name,surname,sex,dob,vat,address from  ").append(SCHEMA).append(".").append(TABLE);
		return sql.toString();
	}
	
	public static String getUpdateStm() {		
		
		StringBuilder sql = new StringBuilder();
		
		sql.append(" update ").append(SCHEMA).append(".").append(TABLE).append(" set vat = ? , address = ? where id = ? "); 
		
		return sql.toString();		
	}
	
	public static String getDeleteStm() {
		
		StringBuilder sql = new StringBuilder();		
		sql.append(" delete from  ").append(SCHEMA).append(".citizens where id = ? ");
		return sql.toString();		
	}
	
	public static String getInsertStm() {
		
		StringBuilder sql = new StringBuilder();
		sql.append(" insert into ").append(SCHEMA).append(".citizens (id,name,surname,sex,dob,vat,address) ");
		sql.append(" values(?,?,?,?,?,?,?) ");
		
		return sql.toString();		
	}
	
	public static String getCreateTable() {
		
		StringBuilder sql = new StringBuilder();
		
		sql.append("create table if not exists register.citizens( ");
		sql.append("	id varchar(8) not null, ");
		sql.append(" name varchar(60) not null, ");
		sql.append(" surname varchar(60) not null, ");
		sql.append(" sex int not null, ");
		sql.append(" dob date not null, ");
		sql.append(" vat varchar(9), ");
		sql.append(" address varchar(60) ,     ");
		sql.append(" primary key(id) )");
		
		return sql.toString();	
	}
}
