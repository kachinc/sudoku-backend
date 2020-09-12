package com.kachinc.sudokubackend.controller;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.util.StringUtils;

import com.kachinc.sudokubackend.bean.LoadGameResponseBean;
import com.kachinc.sudokubackend.core.SudokuBoard;
import com.kachinc.sudokubackend.core.SudokuConstant;
import com.kachinc.sudokubackend.core.SudokuGenerator;
import com.kachinc.sudokubackend.core.SudokuValidator;
import com.kachinc.sudokubackend.service.PdfService;

/**
 * Backend controller
 */
@RestController
@RequestMapping("api")
public class SudokuController {

	Logger log = LoggerFactory.getLogger(SudokuController.class);
	
	@Autowired
	private Environment environment;

	@Autowired
	private SudokuGenerator generator;

	@Autowired
	private SudokuValidator validator;
	
	@Autowired
	private PdfService pdfService;

	/**
	 * Generate a solved board. 
	 * 
	 * @return generated solution (format: "_" followed by 81 numbers)
	 */
	@GetMapping("generateSolution")
	@ResponseBody
	String generateSolution() {
		String res = generator.generateSolution().getString();
		log.info("generateSolution {}", res);
		return "_" + res;
	}

	/**
	 * Generate by difficultly between 0 and 1. 0 being a solution while 1 being an
	 * empty board.
	 * 
	 * @param diff (between 0 and 1)
	 * @return generated problem (format: "_" followed by 81 numbers , empty cells are denoted by "-")
	 */
	@GetMapping("generateByDiff")
	@ResponseBody
	String generateByDiff(@RequestParam double diff) {
		String res = generator.generate(diff).getString();
		log.info("generateByDiff {} {}", diff, res);
		return "_" + res;
	}

	/**
	 * Validate a board
	 * @param str (format: 81 characters string)
	 * @return true or false
	 */
	@GetMapping("validate")
	@ResponseBody
	boolean validate(@RequestParam String str) {
		boolean res;
		if (str.length() != SudokuConstant.NUMBER_OF_CELLS) {
			res = false;
		} else {
			SudokuBoard board = new SudokuBoard();
			board.fillByString(str);
			res = validator.validate(board);
		}
		log.info("validate {} {}", str, res);
		return res;
	}
	
	@GetMapping("sudokuPdf")
	void downloadPdf(@RequestParam String str, @RequestParam double difficulty, HttpServletResponse response) throws Exception {
		SudokuBoard board = new SudokuBoard();
		board.fillByString(str);
		pdfService.generatePdf(response.getOutputStream(), board, difficulty);
		response.setContentType("application/pdf");		
	}
	
	@GetMapping("saveGame")
	String saveGame(@RequestParam String boardStrOriginal, @RequestParam String boardStrNow, @RequestParam double difficulty, @RequestParam long elaspedTimeValue) throws Exception {
		String uuid = UUID.randomUUID().toString();
		Properties properties = new Properties();
		properties.put("diff", String.valueOf(difficulty));
		properties.put("boardStrOriginal", boardStrOriginal);
		properties.put("boardStrNow", boardStrNow);
		properties.put("elaspedTimeValue", String.valueOf(elaspedTimeValue));
		try(FileOutputStream fos = new FileOutputStream(environment.getProperty("save.dir.path") + uuid)){
			properties.store(fos, null);
		}
		return uuid;
	}
	
	@GetMapping("loadGame")
	LoadGameResponseBean loadGame(@RequestParam String uuidStr) throws Exception {
		LoadGameResponseBean res = new LoadGameResponseBean();
		uuidStr = StringUtils.trim(uuidStr);
		try {
			UUID.fromString(uuidStr);
			Properties properties = new Properties();
			try (FileInputStream fis = new FileInputStream(environment.getProperty("save.dir.path") + uuidStr)) {
				properties.load(fis);
				res.setUUIDValid(true);
				res.setDifficulty(Double.parseDouble(properties.getProperty("diff")));
				res.setBoardStrOriginal(properties.getProperty("boardStrOriginal"));
				res.setBoardStrNow(properties.getProperty("boardStrNow"));
				res.setElaspedTimeValue(Long.parseLong(properties.getProperty("elaspedTimeValue")));
			}
		} catch (IllegalArgumentException exception) {
			res.setUUIDValid(false);
		}
		return res;
	}

}
