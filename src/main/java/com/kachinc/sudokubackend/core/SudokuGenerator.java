package com.kachinc.sudokubackend.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

/**
 * Service for generating a sudoku problem
 * 
 * @author chong
 *
 */
@Component
public class SudokuGenerator {
	
	/**
	 * Get a random permutation of 1..9
	 * @return
	 */
	private List<Integer> getRandomIntList(){
		List<Integer> randomIntList = new ArrayList<>();
		for (int i = 1; i <= 9; i++) {
			randomIntList.add(i);
		}
		Collections.shuffle(randomIntList);
		return randomIntList;
		
	}

	/**
	 * Generate a sudoku problem. Difficulty as number of cells to be removed from a
	 * solution.
	 * 
	 * @param difficulty from 0 to 1
	 * @return
	 */
	public SudokuBoard generate(double difficulty) {

		SudokuBoard board = generateSolution();

		removeCellValue(board, (int) Math.round(SudokuConstant.NUMBER_OF_CELLS * difficulty));
		return board;
	}

	/**
	 * Generate a solved board (solution), initial caller of generateFull
	 * @return
	 */
	public SudokuBoard generateSolution() {

		SudokuBoard board = new SudokuBoard();

		while(generateFull(board, 0) == false);

		return board;
	}

	/**
	 * Generate a sudoku board with all cells filled, i.e. a solution, using backtracking algorithm
	 * 
	 * @param posPtr position pointer for a cell, from 0 to 80
	 * 
	 * @return
	 */
	private boolean generateFull(SudokuBoard board, int posPtr) {

		// Get (row,col) representation from posPtr
		int rowPtr = SudokuBoard.posToRowAndCol(posPtr).getLeft();
		int colPtr = SudokuBoard.posToRowAndCol(posPtr).getRight();

		// Base case
		if (board.getNoOfEmptyCells() == 0) {
			return true;
		}

		// Trials to set a cell value
		// Given a random permutation
		for (Integer val : getRandomIntList()) {

			// Set the current value to the cell
			board.setCell(rowPtr, colPtr, val);

			// Validate the recently set value
			// If the current cell is valid, we can proceed to consider the cells ahead
			if (SudokuValidator.isCellValueValid(board, rowPtr, colPtr)) {
				System.out.println("valid");
				// if the next cell and the cells after are valid
				if(generateFull(board, posPtr + 1)) {
					return true;
				} else {
					// try other possible values for the current cell 
					continue;
				}
			} else {
				// if the current cell is invalid, try other possible values
				board.setCell(rowPtr, colPtr, null);
				System.out.println("invalid");
				continue;
			}
		}
		
		// if all 1 to 9 don't work, return false to backtrack
		return false;

	}

	/**
	 * Set filled value of random cells with null
	 * 
	 * @param board
	 * @param numberOfCellsToRemove
	 */
	private void removeCellValue(SudokuBoard board, int numberOfCellsToRemove) {

		if (numberOfCellsToRemove > SudokuConstant.NUMBER_OF_CELLS) {
			numberOfCellsToRemove = SudokuConstant.NUMBER_OF_CELLS;
		}

		Set<Pair<Integer, Integer>> removedCells = new HashSet<>();
		for (int i = 0; i < numberOfCellsToRemove; i++) {

			Pair<Integer, Integer> cell = null;

			do {
				Integer row = Integer.valueOf(randomInt(0, SudokuConstant.CELLS_PER_ROW - 1));
				Integer col = Integer.valueOf(randomInt(0, SudokuConstant.CELLS_PER_COL - 1));
				cell = Pair.of(row, col);
			} while (removedCells.contains(cell));

			board.setCell(cell.getKey(), cell.getValue(), null);
			removedCells.add(cell);

		}
	}

	/**
	 * Generate a random int between min and max
	 * 
	 * @param min
	 * @param max
	 * @return
	 */
	private int randomInt(int min, int max) {
		return ThreadLocalRandom.current().nextInt(min, max + 1);
	}

}
