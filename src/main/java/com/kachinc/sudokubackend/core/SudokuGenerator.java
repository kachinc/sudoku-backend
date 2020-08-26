package com.kachinc.sudokubackend.core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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

	private static Set<Integer> randomIntSet;

	public SudokuGenerator() {
		super();
		randomIntSet = new HashSet<>();
		while (randomIntSet.size() < 9) {
			randomIntSet.add(randomInt(SudokuConstant.MIN_CELL_VAL, SudokuConstant.MAX_CELL_VAL));
		}
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

	public SudokuBoard generateSolution() {

		SudokuBoard board = new SudokuBoard();

		generateFull(board, 0);

		return board;
	}

	/**
	 * Generate a sudoku board with all cells filled, i.e. a solution
	 * 
	 * @return
	 */
	private void generateFull(SudokuBoard board, int posPtr) {

		System.out.println(board.toString());

		int rowPtr = SudokuBoard.posToRowAndCol(posPtr).getLeft();
		int colPtr = SudokuBoard.posToRowAndCol(posPtr).getRight();

		// base case
		if (board.getNoOfEmptyCells() == 0) {
			return;
		}

		// set value
		for (Integer val : randomIntSet) {

			board.setCell(rowPtr, colPtr, val);

			// branch
			if (SudokuValidator.isCellValueValid(board, rowPtr, colPtr)) {
				generateFull(board, posPtr + 1);
			} else {
				board.setCell(rowPtr, colPtr, null);
				generateFull(board, posPtr);
			}
		}

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
