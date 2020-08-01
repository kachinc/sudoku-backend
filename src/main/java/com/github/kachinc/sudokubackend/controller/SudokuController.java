package com.github.kachinc.sudokubackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.github.kachinc.sudoku.SudokuBoard;
import com.github.kachinc.sudoku.SudokuGenerator;
import com.github.kachinc.sudoku.SudokuValidator;

@RestController
@RequestMapping("api")
public class SudokuController {
	
	@Autowired
	private SudokuGenerator generator;
	
	@Autowired
	private SudokuValidator validator;
	
	@GetMapping("generateSolution")
    @ResponseBody String generateSolution() {
        return generator.generateSolution().getString();
    }
	
	@GetMapping("generateByDiff")
    @ResponseBody String generateByDiff(@RequestParam double diff) {
        return generator.generate(diff).getString();
    }
	
	@GetMapping("validate")
    @ResponseBody boolean validate(@RequestParam String str) {
		SudokuBoard board = new SudokuBoard();
		board.fillByString(str);
        return validator.validate(board);
    }
	
	

}
