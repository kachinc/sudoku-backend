package com.kachinc.sudokubackend.core;

/**
 * Class for specifying a subarea <br>
 * the top left subarea has coordinates: <br>
 * iFrom:0 iTo:2 jFrom:0 jTo:2
 * 
 * @author chong
 *
 */
public class Subarea {

	private int iFrom;
	private int iTo;

	private int jFrom;
	private int jTo;

	public Subarea() {
		super();
	}

	public Subarea(int iFrom, int iTo, int jFrom, int jTo) {
		super();
		this.iFrom = iFrom;
		this.iTo = iTo;
		this.jFrom = jFrom;
		this.jTo = jTo;
	}

	public int getiFrom() {
		return iFrom;
	}

	public void setiFrom(int iFrom) {
		this.iFrom = iFrom;
	}

	public int getiTo() {
		return iTo;
	}

	public void setiTo(int iTo) {
		this.iTo = iTo;
	}

	public int getjFrom() {
		return jFrom;
	}

	public void setjFrom(int jFrom) {
		this.jFrom = jFrom;
	}

	public int getjTo() {
		return jTo;
	}

	public void setjTo(int jTo) {
		this.jTo = jTo;
	}
	
	public static boolean cellInSubarea(Subarea subarea, int row, int col) {
		return (subarea.iFrom <= row && row <= subarea.iTo) && (subarea.jFrom <= col && col <= subarea.jTo);
	}
	
	public static Subarea getSubareaByCell(int row, int col) {
		for(Subarea s : SudokuConstant.SUBAREA_LIST){
			if(cellInSubarea(s, row, col)) {
				return s;
			}
		}
		return null;
	}

}
