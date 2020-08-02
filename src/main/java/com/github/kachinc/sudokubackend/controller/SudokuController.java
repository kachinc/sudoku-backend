package com.github.kachinc.sudokubackend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.github.kachinc.sudoku.Constant;
import com.github.kachinc.sudoku.SudokuBoard;
import com.github.kachinc.sudoku.SudokuGenerator;
import com.github.kachinc.sudoku.SudokuValidator;

@RestController
@RequestMapping("api")
public class SudokuController {

	Logger log = LoggerFactory.getLogger(SudokuController.class);

	@Autowired
	private SudokuGenerator generator;

	@Autowired
	private SudokuValidator validator;

	@GetMapping("generateSolution")
	@ResponseBody
	String generateSolution() {
		String res = generator.generateSolution().getString();
		log.info("generateSolution {}", res);
		return "_" + res;
	}

	@GetMapping("generateByDiff")
	@ResponseBody
	String generateByDiff(@RequestParam double diff) {
		String res = generator.generate(diff).getString();
		log.info("generateByDiff {} {}", diff, res);
		return "_" + res;
	}

	@GetMapping("validate")
	@ResponseBody
	boolean validate(@RequestParam String str) {
		boolean res;
		if (str.length() != Constant.NUMBER_OF_CELLS) {
			res =  false;
		} else {
			SudokuBoard board = new SudokuBoard();
			board.fillByString(str);
			res = validator.validate(board);
		}
		log.info("validate {} {}", str, res);
		return res;
	}

}
