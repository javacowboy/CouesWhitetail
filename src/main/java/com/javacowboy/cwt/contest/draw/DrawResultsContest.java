package com.javacowboy.cwt.contest.draw;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.javacowboy.cwt.contest.IContest;
import com.javacowboy.cwt.core.Constants;
import com.javacowboy.cwt.core.FileManager;
import com.javacowboy.cwt.core.HtmlParser;
import com.javacowboy.cwt.core.PostInfoDto;

public class DrawResultsContest implements IContest {
	
	int totalRecords = 0;
	int totalMatched = 0;
	
	final Logger logger = Logger.getLogger(this.getClass().getSimpleName());
	
	public static void main(String[] args) {
		DrawResultsContest instance = new DrawResultsContest();
		instance.run();
	}

	@Override
	public void run() {
        FileManager.clean();
        FileManager.init();
		File htmlDir = new File(Constants.HTML_DIR);
		//get all html pages
		FileManager.allTopicPagesToFiles(Constants.FORUM_URL, htmlDir);
		//convert from html to dtos
		List<File> pages = FileManager.listFilesAlphaNumeric(htmlDir);
		List<PostInfoDto> posts = new ArrayList<PostInfoDto>();
		for(File page : pages) {
			posts.addAll(HtmlParser.getPostInfo(page));
		}
		//parse each users post for a guess
		getGuessAndWriteResults(posts);
		logger.info("Complete");
	}

	private void getGuessAndWriteResults(List<PostInfoDto> posts) {
		try {
			FileManager.createDirectory(new File(Constants.RESULTS_DIR));
			File resultsFile = new File(Constants.RESULTS_DIR, Constants.RESULTS_FILENAME);
			logger.info("Writing results to: " + resultsFile.getPath());
			FileWriter fstream = new FileWriter(resultsFile);
			BufferedWriter writer = new BufferedWriter(fstream);
			writer.write(DrawPostInfoDto.header);
			writer.newLine();
			for(PostInfoDto post : posts) {
				DrawPostInfoDto dto = new DrawPostInfoDto(post);
				dto.setGuess(getGuess(dto));
				writer.write(dto.toString());
				writer.newLine();
			}
			writer.close();
		}catch (Exception e) {
			logger.log(Level.SEVERE, "Getting guess or writing to results file.", e);
		}
	}

	private Date getGuess(DrawPostInfoDto postInfo) {
		if (postInfo.getPostContent() == null
				|| postInfo.getPostContent().isEmpty()) {
			return null;
		}

		// parse
		Date date = parseDate(postInfo);
		// println(date.toString() + " : " + text)
		return date;
	}

	private Date parseDate(DrawPostInfoDto postInfo) {
		totalRecords++;
		String text = postInfo.getPostContent();
		text = clean(text);
		Date date = parseDate1(postInfo, text);
		if (date != null) {
			totalMatched++;
			return date;
		}
		date = parseDate2(postInfo, text);
		if (date != null) {
			totalMatched++;
			return date;
		}
		date = parseDate3(postInfo, text);
		if (date != null) {
			totalMatched++;
			return date;
		}
		date = parseDate5(postInfo, text);
		if (date != null) {
			totalMatched++;
			return date;
		}
		date = parseDate6(postInfo, text);
		if (date != null) {
			totalMatched++;
			return date;
		}
		date = parseDate7(postInfo, text);
		if (date != null) {
			totalMatched++;
			return date;
		}
		date = parseDate8(postInfo, text);
		if (date != null) {
			totalMatched++;
			return date;
		}
		date = parseDate9(postInfo, text);
		if (date != null) {
			totalMatched++;
			return date;
		}
		date = parseDate10(postInfo, text);
		if (date != null) {
			totalMatched++;
			return date;
		}

		// println("parsed: " + text + " got: " + date)
		return date;
	}

	Date parseDate1(DrawPostInfoDto postInfo, String text) {
		String format = "MMMMM dd yyyy h:mma";
		return parseDate(postInfo, format, text, false);
	}

	Date parseDate2(DrawPostInfoDto postInfo, String text) {
		String format = "MMMMM dd h:mma";
		return parseDate(postInfo, format, text, true);
	}

	Date parseDate3(DrawPostInfoDto postInfo, String text) {
		String format = "MMMMM dd h:mm";
		return parseDate(postInfo, format, text, true);
	}

	Date parseDate5(DrawPostInfoDto postInfo, String text) {
		String format = "MMMMM dd hmma";
		return parseDate(postInfo, format, text, true);
	}

	Date parseDate6(DrawPostInfoDto postInfo, String text) {
		String format = "MMMMM dd hmm";
		return parseDate(postInfo, format, text, true);
	}

	Date parseDate7(DrawPostInfoDto postInfo, String text) {
		text = text.replace(":", "");
		String format = "MMMMM dd Hmm";
		return parseDate(postInfo, format, text, true);
	}

	Date parseDate8(DrawPostInfoDto postInfo, String text) {
		String format = "MM/dd/yyyy h:mma";
		return parseDate(postInfo, format, text, false);
	}

	Date parseDate9(DrawPostInfoDto postInfo, String text) {
		String format = "MM/dd/yy h:mma";
		return parseDate(postInfo, format, text, false);
	}

	Date parseDate10(DrawPostInfoDto postInfo, String text) {
		String format = "MM/dd h:mma";
		return parseDate(postInfo, format, text, true);
	}

	Date parseDate11(DrawPostInfoDto postInfo, String text) {
		String format = "MM/dd h:mm";
		return parseDate(postInfo, format, text, true);
	}

	@SuppressWarnings("deprecation")
	Date parseDate12(DrawPostInfoDto postInfo, String text) {
		String format = "dd h:mma";
		Date date = parseDate(postInfo, format, text, true);
		if (date != null) {
			date.setMonth(currentMonth());
		}
		return date;
	}

	@SuppressWarnings("deprecation")
	Date parseDate(DrawPostInfoDto postInfo, String format, String text,
			boolean setYear) {
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		Date date = null;
		try {
			date = formatter.parse(text);
			postInfo.setMatched(format);
			postInfo.setGuessString(text);
			if (setYear) {
				date.setYear(currentYear());
			}
		} catch (java.text.ParseException e) {
			date = null;
		}
		return date;
	}

	@SuppressWarnings("deprecation")
	int currentYear() {
		Date today = new Date();
		return today.getYear();
	}

	@SuppressWarnings("deprecation")
	int currentMonth() {
		Date today = new Date();
		return today.getMonth();
	}

	String clean(String text) {
		text = text.trim();
		text = text.toLowerCase();
		text = text.replace("-", "/");
		text = text.replace("7/", "07/"); // July
		text = clearBeforeMonth(text);
		// text = text.replace("i say", "")
		// text = text.replace("lets go with", "")
		// text = text.replace("my guess is", "")
		// text = text.replace("here is my wag", "")
		text = text.replace("about", "");
		text = text.replace("bout", "");
		text = text.replace("high", "");
		text = text.replace("noon", "12:00pm");
		text = text.replace("@", "");
		text = text.replace("at", "");
		text = text.replace("!", "");
		text = text.replace(",", "");
		text = text.replace(".", "");
		text = text.replace("st", "");
		text = text.replace("nd", "");
		text = text.replace("rd", "");
		text = text.replace("th", "");
		// text = text.replace("AM", "am")
		// text = text.replace("PM", "pm")
		text = text.replace(" am", "am");
		text = text.replace(" pm", "pm");
		// text = clearAfterAmPm(text)
		text = fixEvenHours(text);
		return text;
	}

	String clearBeforeMonth(String text) {
		int begin = text.indexOf("july");
		if (begin > 0) {
			return text.substring(begin, text.length());
		}
		begin = text.indexOf("07/");
		if (begin > 0) {
			return text.substring(begin, text.length());
		}
		return text;
	}

	String fixEvenHours(String text) {
		if (!text.contains(":")) {
			for (int i = 0; i < 12; i++) {
				text = text.replace(i + "am", i + ":00");
				text = text.replace(i + "pm", (i + 12) + ":00");
			}
		}
		return text;
	}
	
}
