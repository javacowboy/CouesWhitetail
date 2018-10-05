package com.javacowboy.cwt.contest.photo;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.javacowboy.cwt.core.Constants;
import com.javacowboy.cwt.core.FileManager;
import com.javacowboy.cwt.core.HtmlParser;

/**
 * Created by IntelliJ IDEA.
 * User: MatthewRYoung
 */
public class PhotoHtmlParser extends HtmlParser {

    static final Logger logger = Logger.getLogger(PhotoHtmlParser.class.getSimpleName());

    /**
     * A method that scrapes the html for post info
     * @param htmlFile
     * @return
     */
    public static List<PhotoPostInfoDto> getPhotoPostInfo(File htmlFile) {
        //This should do the same thing as the HtmlParser.getPostInfo method does to get post info
        List<PhotoPostInfoDto> list = new ArrayList<PhotoPostInfoDto>();
        try {
            int pageNumber = getCurrentPageNumber(htmlFile);
            Document doc = Jsoup.parse(htmlFile, Charset.defaultCharset().name());
            Elements userPosts = doc.getElementsByClass(TagClass.USER_POST.getName());
            for(Element element : userPosts) {
                PhotoPostInfoDto dto = new PhotoPostInfoDto();
                dto.setPageNumber(pageNumber);
                dto.setUserName(getUserName(element));
                dto.setPostDate(parseForPostDate(element));
                dto.setPostNumber(getPostNumber());
                dto.setUserPostNumber(getUserPostNumber(dto.getUserName()));
                dto.setPostContent(parseForPostContent(element));
                //Here is where we get more info than the HtmlParser class does.
                getImages(dto, element);
                logger.info("Parsed post info for: " + dto.getUserName());
                list.add(dto);
            }

        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error getting post info.", e);
        }

        return list;
    }

    private static void getImages(PhotoPostInfoDto postInfo, Element element) {
    	//images
        Elements results = element.getElementsByClass(TagClass.PHOTO_LINK.getName());
        int counter = 1;
        for (Element photoLink : results) {
            ImageInfoDto imageInfo = new ImageInfoDto();
            imageInfo.setOrigFilename(photoLink.attr("href"));
            Element img = photoLink.getElementsByTag("img").first();
            if (img != null && img.attr("class").contains(TagClass.PHOTO_IMG.getName())) {
                imageInfo.setThumbFilename(img.attr("src"));
            }
            imageInfo.setCategory(postInfo.getCategory());
            imageInfo.setLocalFilename(pullDownImage(postInfo, imageInfo, counter));
            postInfo.getImages().add(imageInfo);
            counter++;
        }
        //youtube and vimeo videos
        results = element.getElementsByClass(TagClass.MOVIE_LINK.getName());
        for(Element param : results) {
	        	VideoInfoDto videoInfo = new VideoInfoDto();
	        	videoInfo.setCategory(postInfo.getCategory());
	        	Element iframe = param.getElementsByTag("iframe").first();
	        	videoInfo.setUrl(iframe.attr("src"));
	        	videoInfo.setLocalFilename(pullDownVideo(postInfo, videoInfo, counter));
	        	postInfo.getVideos().add(videoInfo);
        }
        //amanda might have uploaded the video for another user
        if(postInfo.hasVideos()) {
        	//TODO: 
        	//postInfo.setUserName(postInfo.getUserName() + "For: " + parseOnBehalfOfUsername(postInfo.getPostContent()));
        }
    }

    private static String pullDownVideo(PhotoPostInfoDto postInfo, VideoInfoDto videoInfo, int counter) {
    	String filename = buildFileName(postInfo, videoInfo, counter);
    	File outDir = new File(Constants.IMAGES_DIR, videoInfo.getCategory());
    	File outFile = new File(outDir, filename);
    	if(videoInfo.getUrl().toLowerCase().contains("youtube.com")) {
    		String youtubeHtml = buildYoutubeHtml(videoInfo.getUrl());
    		FileManager.stringToFile(youtubeHtml, outFile);
    	}else {
    		FileManager.urlToFile(videoInfo.getUrl(), outFile);
    	}
    	return filename;
	}

	private static String buildYoutubeHtml(String url) {
		//<meta http-equiv="refresh" content="0; URL='https://youtube.com/someurl'" />
		StringBuilder builder = new StringBuilder();
		builder.append("<html>")
		.append("<head>")
		.append("<meta http-equiv=\"refresh\" content=\"0; URL='").append(url).append("'\" />")
		.append("</head>")
		.append("<body>")
		.append("<div>Redirect To YouTube</div>")
		.append("</body>")
		.append("</html>");
		return builder.toString();
	}

	private static String pullDownImage(PhotoPostInfoDto postInfo, ImageInfoDto imageInfo, int counter) {
        String filename = buildFileName(postInfo, imageInfo, counter);
        File outDir = new File(Constants.IMAGES_DIR, imageInfo.getCategory());
        File outFile = new File(outDir, filename);
        FileManager.urlToFile(imageInfo.getOrigFileName(), outFile);
        return filename;
    }
	
	private static String buildFileName(PhotoPostInfoDto postInfo, VideoInfoDto videoInfo, int counter) {
		return buildFileName(postInfo.getUserName(), videoInfo.getCategory(), postInfo.getUserPostNumber(), counter, videoInfo.getFileExtension());
	}

    private static String buildFileName(PhotoPostInfoDto postInfo, ImageInfoDto imageInfo, int counter) {
    	return buildFileName(postInfo.getUserName(), imageInfo.getCategory(), postInfo.getUserPostNumber(), counter, imageInfo.getFileExtension());
    }
    
    private static String buildFileName(String username, String category, int userPostNumber, int counter, String fileExtension) {
    	String filename = username + "_";
        filename += category;
        filename += "_post" + userPostNumber;
        filename += "_file" + counter;
        filename += fileExtension;
        //fix illegal filenames
        filename = filename.replace("'", "");
        filename = filename.replace(" ", "_");
        //println(filename)
        return filename;
    }

}
