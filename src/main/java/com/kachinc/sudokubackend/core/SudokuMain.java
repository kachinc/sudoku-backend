package com.kachinc.sudokubackend.core;

public class SudokuMain {

	public static void main(String[] args) {

		/*
		 * New problem
		 */

		SudokuGenerator gen = new SudokuGenerator(); // TODO can use dependency injection
		SudokuBoard board = gen.generate(0.5);
		System.out.println(board);
		System.out.println(board.getString());

		/*
		 * Test validator
		 */
		SudokuValidator validator = new SudokuValidator();
		SudokuBoard boardToValidate = gen.generateSolution();
		System.out.println(boardToValidate);
		System.out.println(boardToValidate.getString());
		System.out.println(validator.validate(boardToValidate));
		
		/*
		 * Test fill board
		 */
		SudokuBoard boardToFill = new SudokuBoard();
		String str = "247685931193268574952846317739154826465791283826317495681432759318579642574923168";
		boardToFill.fillByString(str);
		System.out.println(str);
		System.out.println(boardToFill);
		

	}

}
