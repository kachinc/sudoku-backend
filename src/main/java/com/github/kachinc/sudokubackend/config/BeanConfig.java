package com.github.kachinc.sudokubackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.kachinc.sudoku.SudokuGenerator;
import com.github.kachinc.sudoku.SudokuValidator;

@Configuration
public class BeanConfig {

	@Bean
	public SudokuGenerator sudokuGenerator() {
		return new SudokuGenerator();
	}

	@Bean
	public SudokuValidator sudokuValidator() {
		return new SudokuValidator();
	}

}
