package com.bayviewglen.zork.Items;

public class Riddle extends Item{
	private String question; 
	private String answer; 
	
	public Riddle() {
		super(36, "Riddle", "A faded engraving on the wall. It may help you, or hurt you.", false, 0, 0); 
		this.question = "The quick brown fox jumped over the lazy dog"; 
	}
	
	public Riddle(String question, String answer) {
		super(36, "Riddle", "A faded engraving on the wall. It may help you, or hurt you.", false, 0, 0); 
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
