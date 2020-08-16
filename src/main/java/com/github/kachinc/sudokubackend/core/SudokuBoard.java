package com.github.kachinc.sudokubackend.core;

/**
 * Representation of a Sudoku board of 9x9
 * 
 * @author chong
 *
 */
public class SudokuBoard {

	private Integer[][] board = new Integer[SudokuConstant.CELLS_PER_ROW][SudokuConstant.CELLS_PER_COL];

	public Integer[][] getBoard() {
		return board;
	}

	public void setBoard(Integer[][] board) {
		this.board = board;
	}

	/**
	 * Set value of a cell by coordinate
	 * 
	 * @param row
	 * @param col
	 * @param val
	 */
	public void setCell(int row, int col, Integer val) {
		board[row][col] = val;
	}

	public Integer getCell(int row, int col) {
		return board[row][col];
	}

	/**
	 * Get the board as an 81 character string
	 * 
	 * @return
	 */
	public String getString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < SudokuConstant.CELLS_PER_ROW; i++) {
			for (int j = 0; j < SudokuConstant.CELLS_PER_COL; j++) {
				sb.append(board[i][j] == null ? "-" : board[i][j]);
			}
		}
		return sb.toString();
	}

	/**
	 * Fill the board with an 81 character string
	 */
	public void fillByString(String str) {
		for (int i = 0; i < SudokuConstant.CELLS_PER_ROW; i++) {
			for (int j = 0; j < SudokuConstant.CELLS_PER_COL; j++) {
				setCell(i, j, Character.getNumericValue(str.charAt(j + i * SudokuConstant.CELLS_PER_ROW)));
			}
		}
	}

	/**
	 * In sudoku format of 9 3x3 grids
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < SudokuConstant.CELLS_PER_ROW; i++) {
			for (int j = 0; j < SudokuConstant.CELLS_PER_COL; j++) {
				sb.append(board[i][j] == null ? "-" : board[i][j]);
				sb.append(" ");
				if (j == 2 || j == 5) {
					sb.append("| ");
				}
			}
			sb.append(System.lineSeparator());
			if (i == 2 || i == 5) {
				sb.append("---------------------");
				sb.append(System.lineSeparator());
			}
		}
		return sb.toString();
	}

}
