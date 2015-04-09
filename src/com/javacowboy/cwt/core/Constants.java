package com.javacowboy.cwt.core;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Constants {
	
	public static String propertiesFile = "resources/config/application.xml";
	
	//for getting pages from cwt.com
	public static String FORUM_URL;
	public static String FORUM_TOPIC_KEY;
	public static int FORUM_TOPIC_ID;
	public static String FORUM_PAGE_KEY;
	public static int FORUM_POSTS_PER_PAGE;
	//where to save the html to
	public static String HTML_DIR;
	//where to save the results to
	public static String RESULTS_DIR;
	public static String RESULTS_FILENAME;
    public static String RESULTS_DATE_FORMAT;
    //validation for score contest
    public static int VALIDATION_MIN_SCORE;
    public static int VALIDATION_MAX_SCORE;
    //images directory for photo contest
    public static String IMAGES_DIR;
	
	static final Logger logger = Logger.getLogger(Constants.class.getSimpleName());
	
	static {
		try {
			Properties properties = new Properties();
			properties.loadFromXML(new FileInputStream(propertiesFile));
			FORUM_URL = properties.getProperty("forum.url", null);
			FORUM_TOPIC_KEY = properties.getProperty("forum.topic.key", null);
			FORUM_TOPIC_ID = toInteger(properties, "forum.topic.id", null);
			FORUM_PAGE_KEY = properties.getProperty("forum.page.key", null);
			FORUM_POSTS_PER_PAGE = toInteger(properties, "forum.posts.per.page", 15);
			HTML_DIR = properties.getProperty("html.dir", "./html");
			RESULTS_DIR = properties.getProperty("results.dir", "./results");
			RESULTS_FILENAME = properties.getProperty("results.filename", "results.csv");
            RESULTS_DATE_FORMAT = properties.getProperty("results.date.format", "M/dd/yyyy H:mm");
            VALIDATION_MIN_SCORE = toInteger(properties, "validation.min.score", 85);
            VALIDATION_MAX_SCORE = toInteger(properties, "validation.max.score", 160);
            IMAGES_DIR = properties.getProperty("images.dir", "./images");
		} catch (FileNotFoundException e) {
			logger.log(Level.SEVERE, "Properties file not found at: " + propertiesFile, e);
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Problem reading properties file: " + propertiesFile, e);
		}
	}
	
	public static String getTopicUrl(int topicId) {
		return FORUM_URL + "?" + FORUM_TOPIC_KEY + "=" + topicId;
	}
	
	protected static Integer toInteger(Properties properties, String name, Integer defaultValue) {
		try {
			return Integer.valueOf(properties.getProperty(name));
		}catch(NumberFormatException e) {
			return defaultValue;
		}
	}

    /**
     * A method to set the forum topic id and save it to the application.xml properties file.
     * If you only want to change the topic id while the programs runs, do: Constants.FORUM_TOPIC_ID = 12345;
     * @param topicId
     */
    public static void setForumTopicId(int topicId) {
        Constants.FORUM_TOPIC_ID = topicId;
        //TODO: save the change to the xml config file
    }

}
