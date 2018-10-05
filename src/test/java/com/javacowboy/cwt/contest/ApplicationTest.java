package com.javacowboy.cwt.contest;

import java.io.IOException;
import java.net.MalformedURLException;

import org.junit.Test;

public class ApplicationTest {

	@Test
	public void getForumUrlTest() throws MalformedURLException, IOException {
		Application application = new Application();
		String expected = "http://www.coueswhitetail.com/forums/topic/56561-guess-the-score-of-this-white-mountain-apache-coues-buck/";
		
	}
	
	@Test
	public void parseTopicIdTest() {
		Application application = new Application();
		Integer expected = 56561;
		String input = "http://www.coueswhitetail.com/forums/topic/56561-guess-the-score-of-this-white-mountain-apache-coues-buck/";
	}
}
