package com.javacowboy.cwt.contest.weight;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.javacowboy.cwt.contest.IContest;
import com.javacowboy.cwt.core.Constants;
import com.javacowboy.cwt.core.FileManager;
import com.javacowboy.cwt.core.HtmlParser;
import com.javacowboy.cwt.core.PostInfoDto;

public class WeightContest implements IContest { //23809
	
	final Logger logger = Logger.getLogger(this.getClass().getSimpleName());

	public static void main(String[] args) {
		WeightContest instance = new WeightContest();
		instance.run();
	}
	
	@Override
	public void run() {
        FileManager.clean();
        FileManager.init();
        File htmlDir = new File(Constants.HTML_DIR);
        //get all html pages
        FileManager.allTopicPagesToFiles(Constants.getTopicUrl(Constants.FORUM_TOPIC_ID), htmlDir);
        //convert from html to dtos
      	List<File> pages = FileManager.listFilesAlphaNumeric(htmlDir);
      	List<PostInfoDto> posts = new ArrayList<PostInfoDto>();
      	for(File page : pages) {
      		posts.addAll(HtmlParser.getPostInfo(page));
      	}
      	//parse each users post for a guess
      	getGuessAndWriteResults(posts);
      	logger.info("Complete");
	}
	
	private void getGuessAndWriteResults(List<PostInfoDto> posts) {
		try {
			FileManager.createDirectory(new File(Constants.RESULTS_DIR));
			File resultsFile = new File(Constants.RESULTS_DIR, Constants.RESULTS_FILENAME);
			logger.info("Writing results to: " + resultsFile.getPath());
			FileWriter fstream = new FileWriter(resultsFile);
			BufferedWriter writer = new BufferedWriter(fstream);
			writer.write(WeightPostInfoDto.header);
			writer.newLine();
			for(PostInfoDto post : posts) {
				WeightPostInfoDto dto = new WeightPostInfoDto(post);
				dto.setGuess(getGuess(dto.getPostContent()));
				writer.write(dto.toString());
				writer.newLine();
			}
			writer.close();
		}catch (Exception e) {
			logger.log(Level.SEVERE, "Getting guess or writing to results file.", e);
		}
	
	}

	private String getGuess(String text) {
		if(text == null || text.length() == 0){
			return text;
		}
		//fix decimal guesses
		text = text.replace(".25", " 1/4");
		text = text.replace(".5", " 1/2");
		text = text.replace(".75", " 3/4");

		String score = "";
		  for(int i=0; i<text.length(); i++) {
			  Character car = text.charAt(i);
		      if(isNumber(car) || ("/".equals(car.toString()))){
		          score += car.toString();
		      }
		}
		//if score has fraction, add space before fraction
		if(score.contains("/")){
			String whole = score.substring(0, score.indexOf("/")-1);
			String frac = score.substring(score.indexOf("/")-1);
		    score = whole + " " + frac;
		}
		  
		//println(score)
		return score;
	}
	
	private boolean isNumber(Character text){
		try{
			Integer.parseInt(text.toString());
			return true;
		}catch(NumberFormatException e){
			return false;
		}
	}

}
