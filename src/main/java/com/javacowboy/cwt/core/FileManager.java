package com.javacowboy.cwt.core;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;

public class FileManager {
	
	static final Logger logger = Logger.getLogger(FileManager.class.getSimpleName());

    /**
     * A method for cleaning up common files from the previous run
     */
    public static void clean() {
        delete(new File(Constants.HTML_DIR));
    }

    /**
     * A method for getting ready for a standard contest
     */
    public static void init() {
        createDirectory(new File(Constants.HTML_DIR));
    }
	
	/**
	 * A method for deleting a file.
	 * If the file is a directory, all contents of the directory will be delete then the directory will be deleted.
	 * @param file
	 */
	public static void delete(File file) {
		if(file.exists()) {
			logger.info("Deleting: " + file.getAbsolutePath());
			if(file.isDirectory()) {
				for(File f : file.listFiles()) {
					delete(f);
				}
			}
			file.delete();
		}
	}

	/**
	 * A method for creating a directory and any parent directories
	 * @param directory
	 */
	public static void createDirectory(File directory) {
		logger.info("Creating directory: " + directory.getAbsolutePath());
		directory.mkdirs();
	}
	
	public static List<File> listFilesAlphaNumeric(File directory) {
		List<File> list = new ArrayList<File>(Arrays.asList(directory.listFiles()));
		Collections.sort(list, new AlphanumComparator());
		return list;
	}
	
	/**
	 * A method for pulling the contents of a url to a file
	 * @param url
	 * @param file
	 */
	public static void urlToFile(String url, File file) {
		logger.info("Creating " + file.getName() + " from: " + url);
		try {
			FileUtils.copyURLToFile(new URL(url), file);
		} catch (MalformedURLException e) {
			logger.log(Level.SEVERE, "Bad url: " + url);
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Couldn't save to file: " + file);
		}
	}
	
	public static void allTopicPagesToFiles(String url, File outputDirectory) {
		allTopicPagesToFiles(url, outputDirectory, "page");
	}
	
	/**
	 * A method that gets all pages in a topic/thread and saves them to files. 
	 * @param url - The url of the first page to the topic
	 * @param outputDirectory
	 * @param baseFileName
	 */
	public static void allTopicPagesToFiles(String url, File outputDirectory, String baseFileName) {
		logger.info("Getting html pages");
		int fileCounter = 1;
		File page1 = new File(outputDirectory, getHtmlFileName(baseFileName, fileCounter));
		urlToFile(url, page1);
		int totalPages = HtmlParser.getTotalNumberOfPages(page1);
		//--------------Get all pages--------------------
		logger.info("Getting " + totalPages + " pages.");
		//we already got page1, increment the count
		fileCounter++;
		for(; fileCounter <= totalPages; fileCounter++){
			String nextAddr = getNextPageUrl(url, fileCounter, Constants.FORUM_POSTS_PER_PAGE);
			String fileName = getHtmlFileName(baseFileName, fileCounter);
			urlToFile(nextAddr, new File(outputDirectory, fileName));
		}
	}
	
	/**
	 * A method that saves a String to a File
	 * @param data
	 * @param outFile
	 */
	public static void stringToFile(String data, File outFile) {
		try {
			FileUtils.writeStringToFile(outFile, data, "UTF-8");
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Couldn't save to file: " + outFile, e);
		}
	}
	
	private static String getNextPageUrl(String baseUrl, int currPage, int postsPerPage){
		int offset = (currPage);
		return baseUrl + "?" + Constants.FORUM_PAGE_KEY + "=" + offset;
	}
	
	private static String getHtmlFileName(String baseFileName, int pageNum) {
		return baseFileName + pageNum + ".html";
	}
}
