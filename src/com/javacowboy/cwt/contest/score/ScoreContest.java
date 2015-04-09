package com.javacowboy.cwt.contest.score;

import com.javacowboy.cwt.contest.IContest;
import com.javacowboy.cwt.core.Constants;
import com.javacowboy.cwt.core.FileManager;
import com.javacowboy.cwt.core.HtmlParser;
import com.javacowboy.cwt.core.PostInfoDto;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: MatthewRYoung
 */
public class ScoreContest implements IContest {
	
	final Logger logger = Logger.getLogger(this.getClass().getSimpleName());
	Calculations calculations = new Calculations();
	ErrorCheck errorCheck = new ErrorCheck();

    public static void main(String[] arg) {
        ScoreContest instance = new ScoreContest();
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
      	List<ScorePostInfoDto> list = getGuessAndWriteResults(posts);
      	calculations.run(list);
      	errorCheck.check(list);
      	logger.info("Complete");
    }

	private List<ScorePostInfoDto> getGuessAndWriteResults(List<PostInfoDto> posts) {
		try {
			List<ScorePostInfoDto> list = new ArrayList<ScorePostInfoDto>();
			FileManager.createDirectory(new File(Constants.RESULTS_DIR));
			File resultsFile = new File(Constants.RESULTS_DIR, Constants.RESULTS_FILENAME);
			logger.info("Writing results to: " + resultsFile.getPath());
			FileWriter fstream = new FileWriter(resultsFile);
			BufferedWriter writer = new BufferedWriter(fstream);
			writer.write(ScorePostInfoDto.header);
			writer.newLine();
			for(PostInfoDto post : posts) {
				ScorePostInfoDto dto = new ScorePostInfoDto(post);
				dto.setGuess(getGuess(dto.getPostContent()));
				list.add(dto);
				writer.write(dto.toString());
				writer.newLine();
			}
			writer.close();
			return list;
		}catch (Exception e) {
			logger.log(Level.SEVERE, "Getting guess or writing to results file.", e);
		}
		return null;
	}
	
	private String getGuess(String text){
		  if(text == null || text.length() == 0){
				return text;
		  }
		  //replace backslash with forwardslash
		  text = text.replace("\\", "/");
		  //fix decimal guesses
		  text = text.replace(".5", " 4/8");

		  String score = "";
		  for(int i=0; i<text.length(); i++) {
			  Character car = text.charAt(i);
		      if(isNumber(car) || ("/".equals(car.toString()))){
		          score += car.toString();
		      }
		  }
		  //if score has fraction, add space before fraction
		  String whole = "";
		  String frac = "";
		  if(score.endsWith("/8")){
		      whole = score.substring(0, score.indexOf("/8")-1);
		      frac = score.substring(score.indexOf("/8")-1);
		      score = whole + " " + frac;
		  }
		  //fix quarter and half guesses
		  if(score.endsWith("/4") || score.endsWith("/2")){
		      whole = score.substring(0, score.lastIndexOf("/")-1);
		      frac = score.substring(score.lastIndexOf("/")-1);
		      frac = fixFraction(frac);
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
	
	private String fixFraction(String frac){
		if(frac.length() == 3){
			try{
				int numerator = Integer.parseInt(frac.split("/")[0]);
				int denominator = Integer.parseInt(frac.split("/")[1]);
				int mult = 8 / denominator;
				frac = numerator * mult + "/" + denominator * mult;
			}catch (NumberFormatException e){
				//return original value
			}
		}
		return frac;
	}
}
