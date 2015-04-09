package com.javacowboy.cwt.contest.weight;

import com.javacowboy.cwt.core.PostInfoDto;

public class WeightPostInfoDto extends PostInfoDto {
	
	public static final String header = Label.buildHeader(Label.USERNAME, Label.GUESS, Label.PAGE_NUMBER, Label.POST_NUMBER, Label.USER_POST_NUMBER, Label.POST_CONTENT);
	
	String guess;
	
	//constructors
	public WeightPostInfoDto() {}
	
	public WeightPostInfoDto(PostInfoDto dto) {
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
	public String toString() {
		return userName + "," + guess + ",page" + pageNumber + "," + postNumber + "," + userPostNumber + "," + quote(postContent.trim());
	}

}
