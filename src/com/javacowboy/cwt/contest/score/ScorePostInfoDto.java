package com.javacowboy.cwt.contest.score;

import com.javacowboy.cwt.core.PostInfoDto;

public class ScorePostInfoDto extends PostInfoDto {
	
	public static final String header = Label.buildHeader(Label.USERNAME, Label.GUESS, Label.PAGE_NUMBER, Label.POST_NUMBER, Label.USER_POST_NUMBER, Label.POST_CONTENT);
	
	private String guess;
	
	//constructors
	public ScorePostInfoDto() {}

	public ScorePostInfoDto(PostInfoDto dto) {
		super(dto);
	}

	//getters and setters
	public String getGuess() {
		return guess;
	}

	public void setGuess(String guess) {
		this.guess = guess;
	}
	
	@Override
	public String toString(){
		return userName + "," + guess + ",page" + pageNumber + "," + postNumber + "," + userPostNumber + "," + quote(postContent.trim());
	}

}
