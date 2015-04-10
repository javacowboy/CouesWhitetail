package com.javacowboy.cwt.contest.generic;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.javacowboy.cwt.contest.IContest;
import com.javacowboy.cwt.core.Constants;
import com.javacowboy.cwt.core.FileManager;
import com.javacowboy.cwt.core.HtmlParser;
import com.javacowboy.cwt.core.PostInfoDto;

public class GenericContest implements IContest {

	Logger logger = Logger.getLogger(this.getClass().getSimpleName());
	
	public static void main(String[] args) {
		GenericContest instance = new GenericContest();
		instance.run();
	}
	
	@Override
	public void run() {
		FileManager.clean();
		FileManager.init();
		File htmlDir = new File(Constants.HTML_DIR);
        //get all html pages
        FileManager.allTopicPagesToFiles(Constants.getTopicUrl(Constants.FORUM_URL), htmlDir);
        //convert from html to dtos
        List<File> pages = FileManager.listFilesAlphaNumeric(htmlDir);
        List<PostInfoDto> posts = new ArrayList<PostInfoDto>();
      	for(File page : pages) {
      		posts.addAll(HtmlParser.getPostInfo(page));
      	}
      	//write the results
      	writeResults(posts);
	}
	
	private void writeResults(List<PostInfoDto> posts) {
		try {
			FileManager.createDirectory(new File(Constants.RESULTS_DIR));
			File resultsFile = new File(Constants.RESULTS_DIR, Constants.RESULTS_FILENAME);
			logger.info("Writing results to: " + resultsFile.getPath());
			FileWriter fstream = new FileWriter(resultsFile);
			BufferedWriter writer = new BufferedWriter(fstream);
			writer.write(PostInfoDto.header);
			writer.newLine();
			for(PostInfoDto post : posts) {
				writer.write(post.toString());
				writer.newLine();
			}
			writer.close();
		}catch (Exception e) {
			logger.log(Level.SEVERE, "Getting guess or writing to results file.", e);
		}
	
	}

}
