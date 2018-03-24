package es.unizar.tmdad.lab0.repo;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.persistence.Column;

@Entity
@Table(name="tweets", schema = "public")
public class TweetSaved {
	String id;
	String text;
	String fromUser;
	String query = "default";
	
	@Id
	@Column(name = "ID")
	public String getId(){
		return id;
	}
	@Column(name = "CONTENT")
	public String getText(){
		return text;
	}
	@Column(name = "TUSER")
	public String getFromUser(){
		return fromUser;
	}
	
	@Column(name = "QUERY")
	public String getQuery(){
		return query;
	}
	public void setQuery(String pQuery){
		query = pQuery;
	}
	
	public void setId(String pId){
		id = pId;
	}
	
	public void setText(String pText){
		text = pText;
	}
	
	public void setFromUser(String pUser){
		fromUser = pUser;
	}
}
