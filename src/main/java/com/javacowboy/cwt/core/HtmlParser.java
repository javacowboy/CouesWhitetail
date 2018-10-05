package com.javacowboy.cwt.core;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
	
	static final String POST_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'"; //2016-10-19T20:47:19Z
    static final SimpleDateFormat postDateFormat = new SimpleDateFormat(POST_DATE_FORMAT);
    
	static final Logger logger = Logger.getLogger(HtmlParser.class.getSimpleName());
	
	//
	static final Map<String, Integer> userPostCountMap = new HashMap<String, Integer>();
	protected static int postCounter = 0;
	
	/**
	 * A method that determines how many total post pages there are by parsing one of the html pages.
	 * @param htmlFile
	 * @return
	 */
	public static int getTotalNumberOfPages(File htmlFile) {
		try {
			String pageText = getPageInfo(htmlFile);//Page 1 of 6
			String[] values = pageText.trim().split(" ");
			//TODO: jsoup parses &nbsp; to a weird space character that isn't replaced with .trim()
			String value = "";
			boolean next = false;
			for(String v : values) {
				if(next) {
					value = v;
					break;
				}
				if("of".equals(v)) {
					next = true;
					continue;
				}
			}
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
			//TODO: jsoup parses &nbsp; to a weird space character that isn't replaced with .trim()
			String value = "";
			boolean next = false;
			for(int i=values.length-1; i>0; i--) {
				if(next) {
					value = values[i];
					break;
				}
				if("of".equals(values[i])) {
					next = true;
					continue;
				}
			}
			return Integer.valueOf(value);
		} catch (NumberFormatException e) {
			logger.log(Level.SEVERE, "Error getting current page number.", e);
		}
		return -1;
	}
	
	public static String getUserName(Element element) {
		//TODO: jsoup parses &nbsp; to a weird space character that isn't replaced with .trim()
		String username = parseForTagClass(TagClass.USER_NAME, element);
		String[] parts = username.split(" ");
		return parts[0];
	}
	
	/**
	 * A method that return the page info String such as: Page 1 of 6
	 * @param htmlFile
	 * @return
	 */
	static String getPageInfo(File htmlFile) {
		try {
			Document doc = Jsoup.parse(htmlFile, "UTF-8");
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
				dto.setUserName(getUserName(element));
				dto.setPostDate(parseForPostDate(element));
				dto.setPostNumber(getPostNumber());
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
	
	protected static int getPostNumber() {
		postCounter++;
		return postCounter;
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
		removeChildElementsByTagName(TagName.blockquote, content);
		return content.text();
	}

	protected static void removeChildElementsByTagName(TagName tagName, Element element) {
		Elements results = element.getElementsByTag(tagName.name());
		for(Element result : results) {
			result.remove();
		}
	}

	protected static Date parseForPostDate(Element element) {
		try {
			Element time = element.getElementsByTag(TagName.time.name()).first();
			String value = time.attr(TagAttribute.POST_TIME.getKey());
			return postDateFormat.parse(value);
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
	
	protected static String parseForTagClass(TagClass tagClass, Element element) {
		Elements results = element.getElementsByClass(tagClass.getName());
		return results.first().text();
	}
	
	protected static String parseForTagName(TagName tagName, Element element) {
		Elements results = element.getElementsByTag(tagName.name());
		return results.first().text();
	}
	
	/*
	 * An enum used to track the html element name used for finding an element.
	 */
	public enum TagName {
		time, blockquote;
	}

	/*
	 * An enum used to track the html element class name used for finding an element.
	 */
	public enum TagClass {
		//example: <li class="pagejump clickable pj0228983001" id="anonymous_element_1"> ... Page 6 of 6 ... </li>
		TOTAL_PAGES("ipsPagination_pageJump"),
		USER_POST("cPost"),
		USER_NAME("cAuthorPane_author"),
        PHOTO_LINK("ipsAttachLink"), //the <a> tag
        PHOTO_IMG("ipsImage"), //the <img> tag inside the <a>
        MOVIE_LINK("ipsEmbeddedVideo");
		
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
		//<time datetime="2016-10-19T20:47:19Z">October 19, 2016</time>
		POST_TIME("datetime", ""),
//		POST_NUMBER("itemprop", "replyToUrl"),
		POST_TEXT("data-role", "commentContent");
		
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
