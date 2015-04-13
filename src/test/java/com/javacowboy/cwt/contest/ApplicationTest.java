package com.javacowboy.cwt.contest;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.MalformedURLException;

import org.junit.Test;

public class ApplicationTest {

	@Test
	public void getForumUrlTest() throws MalformedURLException, IOException {
		Application application = new Application();
		String expected = "http://www.coueswhitetail.com/forums/topic/56561-guess-the-score-of-this-white-mountain-apache-coues-buck/";
		
		String input = "56561";
		assertEquals(expected, application.getForumUrl(input));

		input = "http://www.coueswhitetail.com/forums/topic/56561-guess-the-score-of-this-white-mountain-apache-coues-buck";
		assertEquals(expected, application.getForumUrl(input));
		
		input = "http://www.coueswhitetail.com/forums/topic/56561-guess-the";
		assertEquals(expected, application.getForumUrl(input));
		
		input = "http://www.coueswhitetail.com/forums/topic/56561-invalid-text-here";
		assertEquals(expected, application.getForumUrl(input));
		
	}
	
	@Test
	public void parseTopicIdTest() {
		Application application = new Application();
		Integer expected = 56561;
		String input = "http://www.coueswhitetail.com/forums/topic/56561-guess-the-score-of-this-white-mountain-apache-coues-buck/";
		assertEquals(expected, application.parseTopicId(input));
	}
}
