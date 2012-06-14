package us.v0gel.riddles;

public class Riddle {
	private String query;
	private String response;

	Riddle(String query, String response) {
		this.query = query;
		this.response = response;
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
