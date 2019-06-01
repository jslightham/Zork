package com.bayviewglen.zork.Entities;

public class Riddle {
	/*
	 * A simple riddle object that stores a question, and the answer to that question that is used in the riddler class. 
	 */
	private String question; 
	private String answer; 
	
	public Riddle() { 
		this.question = "Why did the chicken cross the road?"; 
		this.answer = "I'm not going through this again."; 
	}
	
	public Riddle(String question, String answer) { 
		this.question = question; 
		this.answer = answer; 
	}

	public String getQuestion() {
		return question;
	}

	public String getAnswer() {
		return answer;
	}
	
	
	
}
