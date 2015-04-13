package com.javacowboy.cwt.contest.draw;

import java.util.Date;

import com.javacowboy.cwt.core.PostInfoDto;

public class DrawPostInfoDto extends PostInfoDto {
	
	public static final String header = Label.buildHeader(Label.PAGE_NUMBER.getLabel(), Label.POST_NUMBER.getLabel(), 
			Label.POST_DATE.getLabel(), Label.USER_POST_NUMBER.getLabel(), Label.USERNAME.getLabel(), Label.GUESS.getLabel(), 
			"Evaluated Pattern", "Evaluated Value", Label.POST_CONTENT.getLabel());

	protected Date guess;
	protected String matched;
	protected String guessString;
	
	//constructors
	public DrawPostInfoDto() {}
	
	public DrawPostInfoDto(PostInfoDto dto) {
		super(dto);
	}
	
	//getters and setters
	public Date getGuess() {
		return guess;
	}

	public void setGuess(Date guess) {
		this.guess = guess;
	}

	public String getMatched() {
		return matched;
	}

	public void setMatched(String matched) {
		this.matched = matched;
	}

	public String getGuessString() {
		return guessString;
	}

	public void setGuessString(String guessString) {
		this.guessString = guessString;
	}
	
	@Override
	public String toString(){
		return "page" + pageNumber
				+ "," + postNumber
				+ "," + postDateToString()
				+ "," + userPostNumber
				+ "," + userName
				+ "," + guessToString()
				+ "," + quote(matched)
				+ "," + quote(guessString)
				+ "," + quote(postContent.trim());
	}
	
	public String guessToString(){
		return (guess == null ? "" : formatter.format(guess)); 
	}
	
}
