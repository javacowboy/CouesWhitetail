package com.javacowboy.cwt.contest.photo;

import com.javacowboy.cwt.core.PostInfoDto;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: MatthewRYoung
 */
public class PhotoPostInfoDto extends PostInfoDto {

    public static final String header = Label.buildHeader(Label.USERNAME.getLabel(), Label.PAGE_NUMBER.getLabel(), Label.POST_NUMBER.getLabel(),
            Label.USER_POST_NUMBER.getLabel(), "Filename", "Category", "Orig Filename", Label.POST_CONTENT.getLabel());

    List<ImageInfoDto> images = new ArrayList<ImageInfoDto>();
    List<VideoInfoDto> videos = new ArrayList<VideoInfoDto>();

    //constructors
    public PhotoPostInfoDto() {}

    public PhotoPostInfoDto(PostInfoDto dto) {
        super(dto);
    }

    //helper methods
    public boolean hasMedia() {
    	return hasImages() || hasVideos();
    }
    
    public boolean hasImages(){
        return images.isEmpty() ? false : true;
    }
    
    public boolean hasVideos() {
    	return videos.isEmpty() ? false : true;
    }

    public String getCategory() {
        //parse the users comments and stab at a category
        String tmpText = this.getPostContent().toLowerCase();
        if (tmpText.contains("funn")) {
            return "Funniest";
        } else if (tmpText.contains("series")) {
            return "Series";
        } else if (tmpText.contains("non") && tmpText.contains("coues")) {
            return "Non-Coues";
        } else if (tmpText.contains("coues")) {
            return "Coues";
        } else if (tmpText.contains("video")) {
        	return "Video";
        }
        return "Unknown";
    }

    //getters and setters
    public List<ImageInfoDto> getImages() {
        return images;
    }

    public void setImages(List<ImageInfoDto> images) {
        this.images = images;
    }
    
    public List<VideoInfoDto> getVideos() {
		return videos;
	}

	public void setVideos(List<VideoInfoDto> videos) {
		this.videos = videos;
	}

	@Override
    public String toString(){
        if(hasMedia()) {
            StringBuilder builder = new StringBuilder();
            int counter = 0;
            for (ImageInfoDto imageInfo : images) {
                counter++;
                builder.append(userName + ",page" + pageNumber + "," + postNumber + "," + userPostNumber + "," +
                        imageInfo.getLocalFilename() + "," + imageInfo.getCategory() + "," + imageInfo.getOrigFilename() + "," + quote(getPostContent()));
                if (counter != images.size()) {
                    builder.append("\r\n");
                }
            }
            counter = 0;
            for (VideoInfoDto videoInfo : videos) {
            	counter++;
            	builder.append(userName + ",page" + pageNumber + "," + postNumber + "," + userPostNumber + "," +
                        videoInfo.getLocalFilename() + "," + videoInfo.getCategory() + "," + videoInfo.getUrl() + "," + quote(getPostContent()));
            }
            return builder.toString();
        }else {
            return userName + ",page" + pageNumber + "," + postNumber + "," + userPostNumber + ",,,," + quote(getPostContent());
        }
    }
}
