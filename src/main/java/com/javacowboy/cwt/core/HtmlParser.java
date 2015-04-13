package com.javacowboy.cwt.core;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class HtmlParser {
	
	static final String POST_TIME_FORMAT = "h:mm a"; //To handle the case of: Posted Today, 12:03 PM
	static final String POST_DATE_FORMAT = "MMMM dd yyyy - " + POST_TIME_FORMAT; //Posted September 25 2012 - 09:10 AM
	static final SimpleDateFormat postTimeFormat = new SimpleDateFormat(POST_TIME_FORMAT);
    static final SimpleDateFormat postDateFormat = new SimpleDateFormat(POST_DATE_FORMAT);
    
	static final Logger logger = Logger.getLogger(HtmlParser.class.getSimpleName());
	
	//
	static final Map<String, Integer> userPostCountMap = new HashMap<String, Integer>();
	
	/**
	 * A method that determines how many total post pages there are by parsing one of the html pages.
	 * @param htmlFile
	 * @return
	 */
	public static int getTotalNumberOfPages(File htmlFile) {
		try {
			String pageText = getPageInfo(htmlFile);//Page 1 of 6
			String[] values = pageText.split(" ");
			String value = values[values.length -1];
			return Integer.valueOf(value);
		} catch (NumberFormatException e) {
			logger.log(Level.SEVERE, "Error getting total number of pages.", e);
		}
		return -1;
	}
	
	/**
	 * A method that determines what the current html page number is.
	 * @param htmlFile
	 * @return
	 */
	public static int getCurrentPageNumber(File htmlFile) {
		try {
			String pageText = getPageInfo(htmlFile);//Page 1 of 6
			String[] values = pageText.split(" ");
			String value = values[values.length -3];
			return Integer.valueOf(value);
		} catch (NumberFormatException e) {
			logger.log(Level.SEVERE, "Error getting total number of pages.", e);
		}
		return -1;
	}
	
	/**
	 * A method that return the page info String such as: Page 1 of 6
	 * @param htmlFile
	 * @return
	 */
	static String getPageInfo(File htmlFile) {
		try {
			Document doc = Jsoup.parse(htmlFile, Charset.defaultCharset().name());
			Elements results = doc.getElementsByClass(TagClass.TOTAL_PAGES.getName());
			return results.first().text();
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Error getting page info.", e);
			return "";
		}
	}
	
	/**
	 * A method that scrapes the html for post info
	 * @param htmlFile
	 * @return
	 */
	public static List<PostInfoDto> getPostInfo(File htmlFile) {
		List<PostInfoDto> list = new ArrayList<PostInfoDto>();
		try {
			int pageNumber = getCurrentPageNumber(htmlFile);
			Document doc = Jsoup.parse(htmlFile, Charset.defaultCharset().name());
			Elements userPosts = doc.getElementsByClass(TagClass.USER_POST.getName());
			for(Element element : userPosts) {
				PostInfoDto dto = new PostInfoDto();
				dto.setPageNumber(pageNumber);
				dto.setUserName(parseForTagAttribute(TagAttribute.USER_NAME, element));
				dto.setPostDate(parseForPostDate(element));
				dto.setPostNumber(parseForIntegerTagAttribute(TagAttribute.POST_NUMBER, element));
				dto.setUserPostNumber(getUserPostNumber(dto.getUserName()));
				dto.setPostContent(parseForPostContent(element));
				logger.info("Parsed post info for: " + dto.getUserName());
				list.add(dto);
			}
			
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Error getting post info.", e);
		}
		
		return list;
	}
	
	protected static int getUserPostNumber(String userName) {
		if(!userPostCountMap.containsKey(userName)) {
			userPostCountMap.put(userName, 0);
		}
		Integer count = userPostCountMap.get(userName);
		count = count + 1;
		userPostCountMap.put(userName, count);
		return count;
	}

	protected static String parseForPostContent(Element element) {
		// Need to ignore quoted text, or quoted comments from previous users
		Elements results = element.getElementsByAttributeValue(TagAttribute.POST_TEXT.getKey(), TagAttribute.POST_TEXT.getValue());
		Element content = results.first();
		removeChildElementsByClass(TagClass.QUOTE_CITATION, content);
		removeChildElementsByClass(TagClass.QUOTE_TEXT, content);
		return content.text();
	}

	protected static void removeChildElementsByClass(TagClass tagClass, Element element) {
		Elements results = element.getElementsByClass(tagClass.getName());
		for(Element result : results) {
			result.remove();
		}
	}

	@SuppressWarnings("deprecation")
	protected static Date parseForPostDate(Element element) {
		try {
			String value = parseForTagAttribute(TagAttribute.POST_TIME, element);
            //Today, 11:40 PM
			if(value.toLowerCase().contains("today")) {
				value = value.substring(value.indexOf(",") + 1).trim();
				Date time = postTimeFormat.parse(value);
				Date today = new Date();
				today.setHours(time.getHours());
				today.setMinutes(time.getMinutes());
				return today;
            //Yesterday, 11:40 PM
            }else if (value.toLowerCase().contains("yesterday")) {
                value = value.substring(value.indexOf(",") + 1).trim();
                Date time = postTimeFormat.parse(value);
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DATE, -1);
                Date yesterday = cal.getTime();
                yesterday.setHours(time.getHours());
                yesterday.setMinutes(time.getMinutes());
                return yesterday;
			}else {
				return postDateFormat.parse(value);
			}
		} catch (ParseException e) {
			logger.log(Level.SEVERE, "Error getting post date and time.", e);
		}
		return null;
	}
	
	protected static Integer parseForIntegerTagAttribute(TagAttribute attribute, Element element) {
		try {
			String value = parseForTagAttribute(attribute, element);
			value = value.replace("#", "");
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			logger.log(Level.SEVERE, "Error getting number for " + attribute, e);
		}
		return 0;
	}
	
	protected static String parseForTagAttribute(TagAttribute attribute, Element element) {
		Elements results = element.getElementsByAttributeValue(attribute.getKey(), attribute.getValue());
		return results.first().text();
	}

	/*
	 * An enum used to track the html element class name used for finding an element.
	 */
	public enum TagClass {
		//example: <li class="pagejump clickable pj0228983001" id="anonymous_element_1"> ... Page 6 of 6 ... </li>
		TOTAL_PAGES("pagejump"),
		USER_POST("post_block"),
		QUOTE_CITATION("citation"),
		QUOTE_TEXT("blockquote"),
        PHOTO_LINK("resized_img"), //the <a> tag
        PHOTO_IMG("attach"); //the <img> tag inside the <a>
		
		private String name;


        private TagClass(String className) {
			this.name = className;
		}
		
		public String getName() {
			return name;
		}
	}
	
	/*
	 * An enum used to track element attributes used for finding and element.
	 */
	public enum TagAttribute {
		//example: <span itemprop="name">Santana Outdoors</span>
		USER_NAME("itemprop", "name"),
		POST_TIME("itemprop", "commentTime"),
		POST_NUMBER("itemprop", "replyToUrl"),
		POST_TEXT("itemprop", "commentText"),
		MOVIE_LINK("name", "movie");
		
		private String key;
		private String value;
		private TagAttribute(String key, String value) {
			this.key = key;
			this.value = value;
		}
		
		public String getKey() {
			return key;
		}
		public String getValue() {
			return value;
		}
		
	}
}
