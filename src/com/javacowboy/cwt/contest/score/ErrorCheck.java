package com.javacowboy.cwt.contest.score;

import java.util.List;

import com.javacowboy.cwt.core.Constants;

public class ErrorCheck {
	
	public void check(List<ScorePostInfoDto> results) {
		println("");
		println("Checking for errors.");
		int lineNum = 1;
		for(ScorePostInfoDto dto : results) {
			checkUserPostCount(dto, lineNum);
			checkScore(dto, lineNum);
			lineNum++;
		}
	}
	
	private void checkScore(ScorePostInfoDto dto, int lineNum) {
		String score = dto.getGuess();
		if(score.length() > 0){
			String[] parts = score.split(" ");
			String whole = parts[0];
			//check whole
			try{
				int num = Integer.parseInt(whole);
				if(num < Constants.VALIDATION_MIN_SCORE || num > Constants.VALIDATION_MAX_SCORE){ //between 85 and 160
					throw new NumberFormatException();
				}
			}catch(NumberFormatException e){
				error("Bad Score - " + dto.toString(), lineNum);
				return;
			}
			
			//check fraction
			if(parts.length > 1){
				String frac = parts[1].trim();
				if(frac.length() != 0 && frac.length() != 3){ //no fraction or X/X pattern
					error("Bad Score - " + dto.toString(), lineNum);
				}
			}
		}else{
			//no score
			error("No Score - " + dto.toString(), lineNum);
		}
	}

	private void checkUserPostCount(ScorePostInfoDto dto, int lineNum) {
		//user posted more than once
		if(dto.getUserPostNumber() > 1) {
			String suffix = "";
			switch (dto.getUserPostNumber()) {
			case 1:
				suffix = "st";
				break;
			case 2:
				suffix = "nd";
				break;
			case 3:
				suffix = "rd";
				break;
			default:
				suffix = "th";
				break;
			}
			error(dto.getUserPostNumber() + suffix + " Post - " + dto.toString(), lineNum);
		}
	}

	private void error(String message, int lineNum) {
		println("Line " + lineNum + ": " + message);
	}
	
	private void println(String line) {
		System.out.println(line);
	}

}
