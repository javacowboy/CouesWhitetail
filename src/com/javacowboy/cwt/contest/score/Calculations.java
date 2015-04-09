package com.javacowboy.cwt.contest.score;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Calculations {
	
	List<Double> scoreList = new ArrayList<Double>();
	List<String> badList = new ArrayList<String>();
	Map<Double, String> uniqueMap = new TreeMap<Double, String>();
	int dupGuesses = 0;
	String lowGuess = "";
	String highGuess = "";

	public void run(List<ScorePostInfoDto> results) {
		println("");
		println("Calculations.");
		for (ScorePostInfoDto dto : results) {
			String score = dto.getGuess();
			// println(score + " : " + fractionToDouble(score))
			Double decimal = fractionToDouble(score);
			if (decimal == null) {
				badList.add(score);
			} else {
				scoreList.add(decimal);
				if (uniqueMap.containsKey(decimal)) {
					dupGuesses++;
				}
				uniqueMap.put(decimal, score);
			}
		}
		//print guesses and determine high and low guess
		int uniqueIndex = 0;
		for (Double key : uniqueMap.keySet()) {
			println(uniqueMap.get(key));
			if (uniqueIndex == 0) {
				lowGuess = uniqueMap.get(key);
			}
			if (uniqueIndex == uniqueMap.size() - 1) {
				highGuess = uniqueMap.get(key);
			}
			uniqueIndex++;
		}
		
		println("");
		println("Number of guesses: " + scoreList.size());
		println("Number of duplicate guesses: " + dupGuesses);
		println("The low guess is: " + lowGuess);
		println("The high guess is: " + highGuess);
		println("The average is: " + round(average(scoreList)));
		println("The variance is: " + round(variance(scoreList)));
		println("The standard deviation is: " + round(deviation(scoreList)));

		println("");
		println(badList.size() + " scores were not included in calculations:");
		for(String it : badList) {
			if(it.equals("")){
				it = "No Score";
			}
			println(it);
		}
	}

	private Double fractionToDouble(String value) {
		String[] parts = value.split(" ");
		String whole = parts[0];
		String num = "0";
		String denom = "8";
		if (parts.length > 1) {
			String[] frac = parts[1].split("/");
			num = frac[0];
			denom = frac[1];
		}

		int wholeInt;
		int numInt;
		int denomInt;
		try {
			wholeInt = Integer.parseInt(whole);
			numInt = Integer.parseInt(num);
			denomInt = Integer.parseInt(denom);
		} catch (NumberFormatException e) {
			return null;
		}
		return wholeInt + (numInt / (double)denomInt);
	}
	
	private Double average(List<Double> set){
	    int sum = 0;
	    for(Double it : set) { sum += it; }
	    Double avg = sum / (double)set.size();
	    return avg;
	}
	
	private Double average(double[] set){
	    int sum = 0;
	    for(Double it : set) { sum += it; }
	    Double avg = sum / (double)set.length;
	    return avg;
	}

	private Double variance(List<Double> set){
	    Collections.sort(set);
		//remove the high and the low guess?
		//set.remove(0)
		//set.remove(set.size() -1)
	    Double mean = average(set);
	    double[] diffs = new double[set.size()];
	    int index = 0;
	    for(Double it : set) {
	        double diff = it - mean;
	        diffs[index] = diff * diff;
	        index++;
	    }
	    return average(diffs);
	}

	private Double deviation(List<Double> set){
	    return Math.sqrt(variance(set));
	}
	
	private Double round(Double value) {
		return (double)Math.round(value * 100) / 100;
	}
	
	private void println(String line) {
		System.out.println(line);
	}
}
