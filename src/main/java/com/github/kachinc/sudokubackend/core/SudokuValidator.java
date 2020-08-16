package com.github.kachinc.sudokubackend.core;

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

	public boolean validate(SudokuBoard board) {

		// Check uniqueness by row and cell validity
		for (int i = 0; i < SudokuConstant.CELLS_PER_ROW; i++) {

			Set<Integer> rowSet = new HashSet<>();

			for (int j = 0; j < SudokuConstant.CELLS_PER_COL; j++) {

				Integer cell = board.getCell(i, j);

				if (isCellInvalid(cell)) {
					return false;
				}

				rowSet.add(cell);

			}
			
			if (rowSet.size() != SudokuConstant.CELLS_PER_ROW) {
				return false;
			}
		}
		
		// Check uniqueness by col
		for (int j = 0; j < SudokuConstant.CELLS_PER_COL; j++) {
			
			Set<Integer> colSet = new HashSet<>();
			
			for (int i = 0; i < SudokuConstant.CELLS_PER_ROW; i++) {
				
				Integer cell = board.getCell(i, j);
				colSet.add(cell);
				
			}
			
			if (colSet.size() != SudokuConstant.CELLS_PER_COL) {
				return false;
			}
			
		}
		
		// Check uniqueness by subarea
		
		return true;
	}
	
	/**
	 * Return true if cell is invalid, false if cell is valid
	 * @param cell
	 * @return
	 */
	private boolean isCellInvalid(Integer cell) {
		return cell == null || cell < SudokuConstant.MIN_CELL_VAL || cell > SudokuConstant.MAX_CELL_VAL;
	}

}
