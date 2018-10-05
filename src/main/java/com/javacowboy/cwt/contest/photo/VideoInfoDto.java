package com.javacowboy.cwt.contest.photo;

public class VideoInfoDto {
	
	protected String category;
	protected String url;//the url of the original file
	protected String localFilename;//what we name the file when we download it locally

	//helper methods
	public String getFileExtension() {
		return ".html";
	}
	
	//getters and setters
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}

	public String getLocalFilename() {
		return localFilename;
	}

	public void setLocalFilename(String localFilename) {
		this.localFilename = localFilename;
	}
	
}
