package com.kachinc.sudokubackend.bean;

public class LoadGameResponseBean {
	

	private boolean isUUIDValid;
	
	private double difficulty;
	
	private String boardStrOriginal;
	
	private String boardStrNow;
	
	private long elaspedTimeValue;
	

	public boolean isUUIDValid() {
		return isUUIDValid;
	}

	public void setUUIDValid(boolean isUUIDValid) {
		this.isUUIDValid = isUUIDValid;
	}

	public double getDifficulty() {
		return difficulty;
	}

	public void setDifficulty(double difficulty) {
		this.difficulty = difficulty;
	}

	public String getBoardStrOriginal() {
		return boardStrOriginal;
	}

	public void setBoardStrOriginal(String boardStrOriginal) {
		this.boardStrOriginal = boardStrOriginal;
	}

	public String getBoardStrNow() {
		return boardStrNow;
	}

	public void setBoardStrNow(String boardStrNow) {
		this.boardStrNow = boardStrNow;
	}

	public long getElaspedTimeValue() {
		return elaspedTimeValue;
	}

	public void setElaspedTimeValue(long elaspedTimeValue) {
		this.elaspedTimeValue = elaspedTimeValue;
	}
	
	
	
}
