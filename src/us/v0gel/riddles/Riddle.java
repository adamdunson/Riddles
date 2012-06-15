package us.v0gel.riddles;

public class Riddle {
	private Long id;
	private String query;
	private String response;

	Riddle(String query, String response) {
		this.query = query;
		this.response = response;
	}
	
	Riddle(long id, String query, String response) {
		this.id = id;
		this.query = query;
		this.response = response;
	}
	
	public void setId(long id) {
		if(this.id == null && id > 0)
			this.id = id;
	}
	
	public Long getId() {
		return id;
	}
	
	public String getQuery() {
		return query;
	}
	
	public String getResponse() {
		return response;
	}
	
	public static String riddleMeThis(Riddle riddle) {
		return riddle.query;
	}
	
	public static String answerMe(Riddle riddle) {
		return riddle.response;
	}
}
