package com.kachinc.sudokubackend.core;

import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Component;

/**
 * Service for validating if a SudokuBoard is valid
 * 
 * @author chong
 *
 */
@Component
public class SudokuValidator {

	/**
	 * Validate a board
	 * @param board
	 * @return true/false
	 */
	public boolean validate(SudokuBoard board) {

		for (int pos = 0; pos < SudokuConstant.NUMBER_OF_CELLS; pos++) {

			int row = SudokuBoard.posToRowAndCol(pos).getLeft();
			int col = SudokuBoard.posToRowAndCol(pos).getRight();

			if (board.getNoOfEmptyCells() != 0 || !isCellValueValid(board, row, col)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Validate a cell by its value, uniqueness by row, column and 3x3 subarea
	 * @param board
	 * @param row (0..8)
	 * @param col (0..8)
	 * @return true/false
	 */
	public static boolean isCellValueValid(SudokuBoard board, int row, int col) {
		
		if (isCellFormatInvalid(board.getCell(row, col))) {
			return false;
		}

		// Check uniqueness by row
		Set<Integer> rowSet = new HashSet<>();

		for (int j = 0; j < SudokuConstant.CELLS_PER_ROW; j++) {

			Integer cell = board.getCell(row, j);
			if(cell != null && !rowSet.add(cell)) {
				return false;
			}

		}

		// Check uniqueness by col
		Set<Integer> colSet = new HashSet<>();

		for (int i = 0; i < SudokuConstant.CELLS_PER_COL; i++) {

			Integer cell = board.getCell(i, col);
			if(cell != null && !colSet.add(cell)) {
				return false;
			}

		}

		// Check uniqueness by subarea
		Subarea s = Subarea.getSubareaByCell(row, col);
		Set<Integer> subareaSet = new HashSet<>();
		for (int i = s.getiFrom(); i <= s.getiTo(); i++) {
			for (int j = s.getjFrom(); j <= s.getjTo(); j++) {
				
				Integer cell = board.getCell(i, j);
				if(cell != null && !subareaSet.add(cell)) {
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * Return true if cell format is invalid, false if cell format is valid
	 * 
	 * @param cell
	 * @return
	 */
	private static boolean isCellFormatInvalid(Integer cell) {
		return cell == null || cell < SudokuConstant.MIN_CELL_VAL || cell > SudokuConstant.MAX_CELL_VAL;
	}

}
