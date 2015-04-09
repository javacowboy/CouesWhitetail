package com.javacowboy.cwt.core;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PostInfoDto {
	
	public static final String header = Label.buildHeader(Label.USERNAME, Label.POST_DATE, Label.PAGE_NUMBER, Label.POST_NUMBER, Label.USER_POST_NUMBER, Label.POST_CONTENT);
	protected static final SimpleDateFormat formatter = new SimpleDateFormat(Constants.RESULTS_DATE_FORMAT);
	
	public enum Label {
		USERNAME("User"),
		POST_DATE("Post Date"),
		PAGE_NUMBER("Page"),
		POST_NUMBER("Post #"),
		USER_POST_NUMBER("User Post #"),
		POST_CONTENT("Text"),
		GUESS("Guess");
		
		private String label;
		private Label(String label) {
			this.label = label;
		}
		public String getLabel() {
			return label;
		}
		
		public static String buildHeader(Label ... labels) {
			String[] values = new String[labels.length];
			for(int i=0; i<labels.length; i++) {
				values[i] = labels[i].getLabel();
			}
			return buildHeader(values);
		}
		
		public static String buildHeader(String ... labels) {
			StringBuilder builder = new StringBuilder();
			for(String label : labels) {
				builder.append(label)
				.append(",");
			}
			//remove the last comma
			String value = builder.toString();
			value = value.substring(0, value.length() - 1);
			return value;
		}
	}

	protected String userName;
	protected Date postDate;
	protected int pageNumber; //which page this post was found on
	protected int postNumber; //which post number this was in the thread
	protected int userPostNumber = 1; //if the user posted many times in the thread, this indicates which post number is for this user.
	protected String postContent; //the text the user typed in his/her post.

	//constructors
	public PostInfoDto() {}
	
	public PostInfoDto(PostInfoDto dto) {
		this.userName = dto.userName;
		this.postDate = dto.postDate;
		this.pageNumber = dto.pageNumber;
		this.postNumber = dto.postNumber;
		this.userPostNumber = dto.userPostNumber;
		this.postContent = dto.postContent;
	}
	
	//helper methods
	public String quote(String value) {
		if(value == null) {
			return "";
		}
		value = value.replace("\"", "");
		return "\"" + value + "\"";
	}
	
	public String postDateToString() {
		return (postDate == null ? "" : formatter.format(postDate));
	}
	
	//getters and setters
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public Date getPostDate() {
		return postDate;
	}
	public void setPostDate(Date postDate) {
		this.postDate = postDate;
	}
	public int getPageNumber() {
		return pageNumber;
	}
	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}
	public int getPostNumber() {
		return postNumber;
	}
	public void setPostNumber(int postNumber) {
		this.postNumber = postNumber;
	}
	public int getUserPostNumber() {
		return userPostNumber;
	}
	public void setUserPostNumber(int userPostNumber) {
		this.userPostNumber = userPostNumber;
	}
	public String getPostContent() {
		return postContent;
	}
	public void setPostContent(String postContent) {
		this.postContent = postContent;
	}
	
	@Override
	public String toString() {
		return quote(userName) + "," + postDateToString() + ",page" + pageNumber + "," + postNumber + "," + userPostNumber + "," + quote(postContent.trim());
	}
	
}
