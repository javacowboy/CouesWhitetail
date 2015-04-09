package com.javacowboy.cwt.contest.score;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;

import com.javacowboy.cwt.contest.IContest;
import com.javacowboy.cwt.core.Constants;

/**
 * A class to read in a results file after it has been manually adjusted and run calculations and error checks.
 * @author matthew
 *
 */
public class CalcAndErrorCheckContest implements IContest {
	
	final Logger logger = Logger.getLogger(this.getClass().getSimpleName());
	Calculations calculations = new Calculations();
	ErrorCheck errorCheck = new ErrorCheck();
	
	public static void main(String[] args) {
		CalcAndErrorCheckContest instance = new CalcAndErrorCheckContest();
		instance.run();
	}

	@Override
	public void run() {
		Scanner scanner = new Scanner(System.in);
		promptForResultsFile();
		String fileName = scanner.nextLine();
		if(fileName == null || fileName.trim().isEmpty()) {
			fileName = Constants.RESULTS_FILENAME;
		}
		File file = new File(Constants.RESULTS_DIR, fileName);
		logger.info("Processing file: " + file.getPath());
		List<ScorePostInfoDto> list = getPostInfo(file);
		calculations.run(list);
		errorCheck.check(list);
		logger.info("Complete");
	}
	
	private List<ScorePostInfoDto> getPostInfo(File file) {
		List<ScorePostInfoDto> list = new ArrayList<ScorePostInfoDto>();
		try {
			FileInputStream fstream = new FileInputStream(file);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String line;
			while((line = br.readLine()) != null) {
				//ignore the header line
				if(!ScorePostInfoDto.header.equals(line)) {
					list.add(parseLine(line));
				}
			}
			in.close();
		} catch (FileNotFoundException e) {
			logger.severe("File does not exist. " + e.getMessage());
		} catch (IOException e) {
			logger.severe("Error reading from file. " + e.getMessage());
		}
		return list;
	}

	private ScorePostInfoDto parseLine(String line) {
		ScorePostInfoDto dto = new ScorePostInfoDto();
		String[] values = line.split(",");
		dto.setUserName(values[0]);
		dto.setGuess(values[1]);
		dto.setPageNumber(Integer.valueOf(values[2].replace("page", "")));
		dto.setPostNumber(Integer.valueOf(values[3]));
		dto.setUserPostNumber(Integer.valueOf(values[4]));
		String text = "";
		for(int i=5; i<values.length; i++) {
			text += values[i] + " ";
		}
		dto.setPostContent(text);
		return dto;
	}

	private void promptForResultsFile() {
        System.out.println("Which file should be checked? (Press Enter to accept the default)");
        System.out.println();
        System.out.print("[" + Constants.RESULTS_FILENAME + "]: ");
    }

}
