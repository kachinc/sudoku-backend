package com.github.kachinc.sudokubackend.core;

import java.util.Arrays;
import java.util.List;

public class SudokuConstant {

	public static final int CELLS_PER_ROW = 9;
	public static final int CELLS_PER_COL = 9;
	public static final int CELLS_PER_SUBAREA = 9;
	public static final int NUMBER_OF_CELLS = 81;

	public static final int MIN_CELL_VAL = 1;
	public static final int MAX_CELL_VAL = 9;
	public static final int MAX_NUMBER_OF_TRIALS = 100;

	public static final Subarea SUBAREA_TOP_LEFT = new Subarea(0, 2, 0, 2);
	public static final Subarea SUBAREA_TOP_CENTRE = new Subarea(0, 2, 3, 5);
	public static final Subarea SUBAREA_TOP_RIGHT = new Subarea(0, 2, 6, 8);

	public static final Subarea SUBAREA_MIDDLE_LEFT = new Subarea(3, 5, 0, 2);
	public static final Subarea SUBAREA_MIDDLE_CENTRE = new Subarea(3, 5, 3, 5);
	public static final Subarea SUBAREA_MIDDLE_RIGHT = new Subarea(3, 5, 6, 8);

	public static final Subarea SUBAREA_BOTTOM_LEFT = new Subarea(6, 8, 0, 2);
	public static final Subarea SUBAREA_BOTTOM_CENTRE = new Subarea(6, 8, 3, 5);
	public static final Subarea SUBAREA_BOTTOM_RIGHT = new Subarea(6, 8, 6, 8);

	public static final List<Subarea> SUBAREA_LIST = Arrays.asList(SUBAREA_TOP_LEFT, SUBAREA_TOP_CENTRE,
			SUBAREA_TOP_RIGHT, SUBAREA_MIDDLE_LEFT, SUBAREA_MIDDLE_CENTRE, SUBAREA_MIDDLE_RIGHT, SUBAREA_BOTTOM_LEFT,
			SUBAREA_BOTTOM_CENTRE, SUBAREA_BOTTOM_RIGHT);

}
