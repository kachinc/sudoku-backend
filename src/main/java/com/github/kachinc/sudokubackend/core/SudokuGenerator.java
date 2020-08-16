package com.github.kachinc.sudokubackend.core;

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
		
		SudokuBoard board = null;

		// generateFull() returns null if it hits a dead-end
		while (board == null) {
			board = generateFull();
		}

		return board;
	}

	/**
	 * Generate a sudoku board with all cells filled, i.e. a solution
	 * 
	 * @return
	 */
	private SudokuBoard generateFull() {

		SudokuBoard board = new SudokuBoard();
		Map<String, Set<Integer>> filledIntByCol = new HashMap<>();

		for (int i = 0; i < SudokuConstant.CELLS_PER_ROW; i++) {

			Set<Integer> filledIntOfCurrentRow = new HashSet<Integer>();

			for (int j = 0; j < SudokuConstant.CELLS_PER_COL; j++) {

				if (filledIntByCol.get(String.valueOf(j)) == null) {
					filledIntByCol.put(String.valueOf(j), new HashSet<Integer>());
				}

				Integer val = randomIntForGen(filledIntOfCurrentRow, filledIntByCol.get(String.valueOf(j)));

				// null is returned if fail to generate a random int
				if (val == null) {
					return null;
				}

				filledIntOfCurrentRow.add(val);
				filledIntByCol.get(String.valueOf(j)).add(val);
				board.setCell(i, j, val);

				for (Subarea s : SudokuConstant.SUBAREA_LIST) {
					if (i == s.getiTo() && j == s.getjTo()) {
						if (!checkSubarea(board, s)) {
							return null;
						}
					}
				}
			}

		}

		return board;
	}

	private boolean checkSubarea(SudokuBoard board, Subarea s) {

		Set<Integer> filledIntOfCurrentSubArea = new HashSet<Integer>();
		for (int i = s.getiFrom(); i <= s.getiTo(); i++) {
			for (int j = s.getjFrom(); j <= s.getjTo(); j++) {
				filledIntOfCurrentSubArea.add(board.getCell(i, j));
			}
		}
		if (filledIntOfCurrentSubArea.size() != SudokuConstant.CELLS_PER_SUBAREA) {
			//return false;
		}

		return true;
	}

	/**
	 * Generate a random integer from 1 to 9 except integers in excludedInt
	 * 
	 * @param excludedInt1
	 * @param excludedInt2
	 * @return
	 */
	private Integer randomIntForGen(Set<Integer> excludedInt1, Set<Integer> excludedInt2) {
		Integer rand = null;
		int trial = 0;
		do {
			rand = Integer.valueOf(randomInt(SudokuConstant.MIN_CELL_VAL, SudokuConstant.MAX_CELL_VAL));

			// return null if all possibilities are exhausted
			// TODO can this be smarter? backtrack?
			if (trial > SudokuConstant.MAX_NUMBER_OF_TRIALS) {
				return null;
			}

			trial++;
		} while (excludedInt1.contains(rand) || excludedInt2.contains(rand));
		return rand;
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

			// contains() works because hashCode for Pair is calculated using both key &
			// value, and hashCode for Integer is the integer value
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
