package com.javacowboy.cwt.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Constants {
	
	public static String propertiesFile = "resources/config/application.xml";
	public static String userPropertiesFile = "resources/config/user.xml";
	public static SimpleDateFormat INERNAL_DATE_FORMATTER = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
	
	//for getting pages from cwt.com
	public static String FORUM_URL;
	public static String FORUM_TOPIC_URL;
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
    
    //property file keys
    public static final String FORUM_URL_PROP_KEY = "forum.url";
    public static final String FORUM_TOPIC_URL_PROP_KEY = "forum.topic.url";
    public static final String FORUM_PAGE_KEY_PROP_KEY = "forum.page.key";
    public static final String FORUM_POSTS_PER_PAGE_PROP_KEY = "forum.posts.per.page";
    public static final String HTML_DIR_PROP_KEY = "html.dir";
    public static final String RESULTS_DIR_PROP_KEY = "results.dir";
    public static final String RESULTS_FILENAME_PROP_KEY = "results.filename";
    public static final String RESULTS_DATE_FORMAT_PROP_KEY = "results.date.format";
    public static final String VALIDATION_MIN_SCORE_PROP_KEY = "validation.min.score";
    public static final String VALIDATION_MAX_SCORE_PROP_KEY = "validation.max.score";
    public static final String IMAGES_DIR_PROP_KEY = "images.dir";
	
	static final Logger logger = Logger.getLogger(Constants.class.getSimpleName());
	
	static {
		try {
			Properties properties = new Properties();
			properties.loadFromXML(new FileInputStream(propertiesFile));
			File userPropFile = new File(userPropertiesFile);
			if(userPropFile.exists()) {
				Properties userProperties = new Properties();
				userProperties.loadFromXML(new FileInputStream(userPropertiesFile));
				properties.putAll(userProperties);
			}
			FORUM_TOPIC_URL = properties.getProperty(FORUM_TOPIC_URL_PROP_KEY, null);
			FORUM_PAGE_KEY = properties.getProperty(FORUM_PAGE_KEY_PROP_KEY, null);
			FORUM_POSTS_PER_PAGE = toInteger(properties, FORUM_POSTS_PER_PAGE_PROP_KEY, 15);
			HTML_DIR = properties.getProperty(HTML_DIR_PROP_KEY, "./html");
			RESULTS_DIR = properties.getProperty(RESULTS_DIR_PROP_KEY, "./results");
			RESULTS_FILENAME = properties.getProperty(RESULTS_FILENAME_PROP_KEY, "results.csv");
            RESULTS_DATE_FORMAT = properties.getProperty(RESULTS_DATE_FORMAT_PROP_KEY, "M/dd/yyyy H:mm");
            VALIDATION_MIN_SCORE = toInteger(properties, VALIDATION_MIN_SCORE_PROP_KEY, 85);
            VALIDATION_MAX_SCORE = toInteger(properties, VALIDATION_MAX_SCORE_PROP_KEY, 160);
            IMAGES_DIR = properties.getProperty(IMAGES_DIR_PROP_KEY, "./images");
		} catch (FileNotFoundException e) {
			logger.log(Level.SEVERE, "Properties file not found at: " + propertiesFile, e);
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Problem reading properties file: " + propertiesFile, e);
		}
	}
	
	public static String getExampleTopicUrl() {
		return FORUM_TOPIC_URL;
	}
	
	protected static Integer toInteger(Properties properties, String name, Integer defaultValue) {
		try {
			return Integer.valueOf(properties.getProperty(name));
		}catch(NumberFormatException e) {
			return defaultValue;
		}
	}
	
	public static void saveUserProperty(String key, String value) throws InvalidPropertiesFormatException, FileNotFoundException, IOException {
		Properties properties = new Properties();
		File propFile = new File(userPropertiesFile);
		if(propFile.exists()) {
			properties.loadFromXML(new FileInputStream(propFile));
		}
		properties.put(key, value);
		Date today = new Date();
		String comment = "User properties that override the defaults.  Last modified: " + INERNAL_DATE_FORMATTER.format(today);
		properties.storeToXML(new FileOutputStream(propFile), comment);
	}

}
