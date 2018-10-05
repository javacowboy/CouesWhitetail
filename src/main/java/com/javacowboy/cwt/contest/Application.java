package com.javacowboy.cwt.contest;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.InvalidPropertiesFormatException;
import java.util.Scanner;
import java.util.logging.Logger;

import com.javacowboy.cwt.contest.draw.DrawResultsContest;
import com.javacowboy.cwt.contest.generic.GenericContest;
import com.javacowboy.cwt.contest.photo.PhotoContest;
import com.javacowboy.cwt.contest.score.CalcAndErrorCheckContest;
import com.javacowboy.cwt.contest.score.ScoreContest;
import com.javacowboy.cwt.contest.weight.WeightContest;
import com.javacowboy.cwt.core.Constants;

/**
 * Created by IntelliJ IDEA.
 * User: MatthewRYoung
 */
public class Application {
	
	final Logger logger = Logger.getLogger(this.getClass().getSimpleName());

    public static void main(String[] args) {
        Application instance = new Application();
        instance.run();
    }

    public void run() {
    	//ask which contest to run
        Scanner scanner = new Scanner(System.in);
        promptForContestSelection();
        Integer selection = null;
        String topicString = null;
        try {
        	selection = scanner.nextInt();
	        if(validSelection(selection)) {
	        	IContest contest = null;
	        	if(Contest.SCORE.getSelectionValue() == selection.intValue()) { contest = new ScoreContest(); }
	        	else if(Contest.SCORE_CALC_ERROR.getSelectionValue() == selection.intValue()) {contest = new CalcAndErrorCheckContest(); contest.run(); return; }
	        	else if(Contest.WEIGHT.getSelectionValue() == selection.intValue()) { contest = new WeightContest(); }
	        	else if(Contest.PHOTO.getSelectionValue() == selection.intValue()) { contest = new PhotoContest(); }
	        	else if(Contest.DRAW.getSelectionValue() == selection.intValue()) { contest = new DrawResultsContest(); }
	        	else if(Contest.GENERIC.getSelectionValue() == selection.intValue()) {contest = new GenericContest(); }
	        	
	        	if(contest == null) {
	        		throw new UnsupportedOperationException("This contest is not yet implemented.");
	        	}
	        	//ask for the topic id or use the one in the xml config file
	        	promptForTopicUrl();
	        	scanner = new Scanner(System.in);
	        	topicString = scanner.nextLine();
	        	if(topicString != null && !topicString.trim().isEmpty()) {
	        		Constants.FORUM_URL = topicString;
	        	}else {
	        		Constants.FORUM_URL = Constants.FORUM_TOPIC_URL;
	        	}
	        	parseAndSaveTopicId(Constants.FORUM_URL);
	        		        	
	        	//run the contest
	        	contest.run();
	        }else {
	        	throw new IllegalArgumentException("Not a valid selection.");
	        }
        }catch (Exception e) {
        	logger.severe(e.getMessage());
        	System.out.println("Please make a valid selection.");
        	run();
        }
    }
    
    private void parseAndSaveTopicId(String url) throws InvalidPropertiesFormatException, FileNotFoundException, IOException {
	    	if(url != null) {
	    		Constants.saveUserProperty(Constants.FORUM_TOPIC_URL_PROP_KEY, url);
	    	}
    }

	protected String validateUrl(String url) throws MalformedURLException, IOException {
		//the url needs to end with a / or the html doesn't include page numbers (navigation links)
		//open a connection.  if it's successful, the resolved url should have everything we need.
		logger.info("Trying url: " + url);
		HttpURLConnection connection = (HttpURLConnection)new URL(url).openConnection();
		int responseCode = connection.getResponseCode();
		if(responseCode == 200) {
			return connection.getURL().toString();
		}
		throw new IllegalArgumentException(url + " was not found.  Code: " + responseCode);
	}

	private void promptForTopicUrl() {
		System.out.println();
		System.out.println("Please enter a contest url or leave it blank to use the previous contest.");
		System.out.println("Example: " + Constants.getExampleTopicUrl());
		System.out.print("[" + Constants.FORUM_TOPIC_URL + "]: ");
	}

	private boolean validSelection(Integer selection) {
		if(selection != null && selection.intValue() > 0 && selection.intValue() <= Contest.values().length) {
			return true;
		}
		return false;
	}

	private void promptForContestSelection() {
        int i = 1;
        System.out.println("Select the contest type:");
        for (Contest c : Contest.values()) {
            System.out.println(i + ". " + c.getName() + " (" + c.getDescription() + ")");
            i++;
        }
        System.out.println();
        System.out.print("Selection: ");
    }

    public enum Contest {
        SCORE("Guess the Score Contest", "A contest where users guess the score of an antler or set of antlers."),
        SCORE_CALC_ERROR("Score Contest Error Check", "Run error checks after manually fixing problems in the results file."),
        WEIGHT("Guess the Weight Contest", "A contest where users guess the weight of an animal."),
        PHOTO("Trailcam Photo Contest", "A contest where users post pictures from their trail cameras."),
        DRAW("Draw Results Contest", "A contest where users guess what day and time the hunt draw results will come out."),
        GENERIC("All Topic Posts", "A way to pull the contents of any forum thread into a spreadsheet.");

        private String name;
        private String description;

        private Contest(String name, String description) {
            this.name = name;
            this.description = description;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }
        
        public int getSelectionValue() {
        	return this.ordinal() + 1;
        }
    }
}
