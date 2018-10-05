package com.javacowboy.cwt.photo;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.List;

import org.junit.Test;

import com.javacowboy.cwt.contest.photo.PhotoHtmlParser;
import com.javacowboy.cwt.contest.photo.PhotoPostInfoDto;
import com.javacowboy.cwt.core.Constants;
import com.javacowboy.cwt.core.FileManager;

public class PhotoHtmlParserTest {
	
	@Test
	public void parsePageTest() {
		clean();
		init();
		File htmlFile = getTestFile();
		List<PhotoPostInfoDto> posts = PhotoHtmlParser.getPhotoPostInfo(htmlFile);
		assertEquals(posts.size(), 15);
	}

	private File getTestFile() {
		File htmlDir = new File(Constants.HTML_DIR);
		List<File> pages = FileManager.listFilesAlphaNumeric(htmlDir);
		return pages.get(1);
	}
	
	private void init() {
        FileManager.init();
        FileManager.createDirectory(new File(Constants.IMAGES_DIR));
    }

    private void clean() {
        FileManager.delete(new File(Constants.IMAGES_DIR));
    }

}
