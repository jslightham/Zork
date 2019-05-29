package com.bayviewglen.zork.Entities;

public class Riddle {
	private String question; 
	private String answer; 
	
	public Riddle() { 
		this.question = "The quick brown fox jumped over the lazy dog"; 
		this.answer = "Okay"; 
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
