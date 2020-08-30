package com.kachinc.sudokubackend.service;

import java.io.ByteArrayOutputStream;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.kachinc.sudokubackend.AppConstant;
import com.kachinc.sudokubackend.core.SudokuBoard;
import com.kachinc.sudokubackend.core.SudokuConstant;

@Component
public class PdfService {

	public ByteArrayOutputStream generatePdf(SudokuBoard board, double difficulty) throws Exception {
		return geneatePdfPdfBox(board, difficulty);
	}
	
	private ByteArrayOutputStream geneatePdfPdfBox(SudokuBoard board, double difficulty) throws Exception {
		
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		
		
		return outputStream;
	}
	
	private ByteArrayOutputStream geneatePdfIText(SudokuBoard board, double difficulty) throws Exception {
		Document document = new Document();
		
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		PdfWriter.getInstance(document, outputStream);

		document.open();

		Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24, BaseColor.BLACK);
		Font descrFont = FontFactory.getFont(FontFactory.HELVETICA, 18, BaseColor.BLACK);
		Font cellFont = FontFactory.getFont(FontFactory.COURIER_BOLD, 24, BaseColor.BLACK);

		PdfPTable table = new PdfPTable(SudokuConstant.CELLS_PER_ROW);

		for (int pos = 0; pos < SudokuConstant.NUMBER_OF_CELLS; pos++) {

			int row = SudokuBoard.posToRowAndCol(pos).getLeft();
			int col = SudokuBoard.posToRowAndCol(pos).getRight();

			Integer cell = board.getCell(row, col);
			String cellContent = cell == null ? StringUtils.EMPTY : cell.toString();

			PdfPCell pCell = new PdfPCell(new Phrase(cellContent, cellFont));

			pCell.setBorderWidthTop(AppConstant.PDF_BORDER_WIDTH);
			pCell.setBorderWidthBottom(AppConstant.PDF_BORDER_WIDTH);
			pCell.setBorderWidthLeft(AppConstant.PDF_BORDER_WIDTH);
			pCell.setBorderWidthRight(AppConstant.PDF_BORDER_WIDTH);

			// horizontal borders
			if (Arrays.asList(0, 3, 6).contains(row)) {
				pCell.setBorderWidthTop(AppConstant.PDF_BOLD_BORDER_WIDTH);
			}
			
			if(row == 8) {
				pCell.setBorderWidthBottom(AppConstant.PDF_BOLD_BORDER_WIDTH);
			}

			// vertical borders
			if (Arrays.asList(0, 3, 6).contains(col)) {
				pCell.setBorderWidthLeft(AppConstant.PDF_BOLD_BORDER_WIDTH);
			}
			
			if(col == 8) {
				pCell.setBorderWidthRight(AppConstant.PDF_BOLD_BORDER_WIDTH);
			}

			pCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			pCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			pCell.setFixedHeight(AppConstant.PDF_CELL_HEIGHT);

			table.addCell(pCell);
		}

		document.add(new Paragraph("Sudoku Problem", titleFont));
		document.add(new Paragraph("generated on sudoku.kachinc.com", descrFont));
		document.add(new Paragraph(
				"at " + ZonedDateTime.now().format(DateTimeFormatter.ofPattern(AppConstant.DATE_TIME_FORMAT_WITH_TIMEZONE)),
				descrFont));
		document.add(new Paragraph("having a difficulty of " + difficulty + " between 0 and 1", descrFont));
		document.add(new Paragraph("\n\n\n"));
		document.add(table);

		document.close();
		
		return outputStream;
	}

}
