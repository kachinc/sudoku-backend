package com.kachinc.sudokubackend.service;

import java.awt.Color;
import java.io.OutputStream;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.kachinc.sudokubackend.AppConstant;
import com.kachinc.sudokubackend.core.SudokuBoard;
import com.kachinc.sudokubackend.core.SudokuConstant;

import be.quodlibet.boxable.BaseTable;
import be.quodlibet.boxable.Cell;
import be.quodlibet.boxable.HorizontalAlignment;
import be.quodlibet.boxable.Row;
import be.quodlibet.boxable.VerticalAlignment;
import be.quodlibet.boxable.line.LineStyle;

@Component
public class PdfService {
	
	@Autowired
	private Environment env;

	public void generatePdf(OutputStream os, SudokuBoard board, double difficulty) throws Exception {
		geneatePdfPdfBox(os, board, difficulty);
	}

	private void geneatePdfPdfBox(OutputStream os, SudokuBoard board, double difficulty) throws Exception {

		PDDocument document = new PDDocument();

		PDPage page = new PDPage(PDRectangle.A4);
		PDRectangle rect = page.getMediaBox();

		document.addPage(page);

		PDFont titleFont = PDType1Font.HELVETICA_BOLD;
		float titleSize = 24;

		PDFont descrFont = PDType1Font.HELVETICA;
		float descrSize = 18;

		PDFont cellFont = PDType1Font.COURIER_BOLD;
		float cellSize = 28;

		PDPageContentStream contentStream = new PDPageContentStream(document, page);

		contentStream.beginText();
		contentStream.setFont(titleFont, titleSize);
		contentStream.newLineAtOffset(50, rect.getHeight() - 50);
		contentStream.showText("Sudoku Problem");
		contentStream.endText();

		contentStream.beginText();
		contentStream.setFont(descrFont, descrSize);
		contentStream.newLineAtOffset(50, rect.getHeight() - 80);
		contentStream.showText("generated on " + env.getProperty("site.name"));
		contentStream.endText();

		contentStream.beginText();
		contentStream.setFont(descrFont, descrSize);
		contentStream.newLineAtOffset(50, rect.getHeight() - 110);
		contentStream.showText("at "
				+ ZonedDateTime.now().format(DateTimeFormatter.ofPattern(AppConstant.DATE_TIME_FORMAT_WITH_TIMEZONE)));
		contentStream.endText();

		contentStream.beginText();
		contentStream.setFont(descrFont, descrSize);
		contentStream.newLineAtOffset(50, rect.getHeight() - 140);
		contentStream.showText("having a difficulty of " + difficulty + " between 0 and 1");
		contentStream.endText();

		float margin = 50;
		float yStartNewPage = page.getMediaBox().getHeight() - (2 * margin);
		float tableWidth = page.getMediaBox().getWidth() - (2 * margin);

		boolean drawContent = true;
		float bottomMargin = 70;
		float yPosition = rect.getHeight() - 180;

		BaseTable table = new BaseTable(yPosition, yStartNewPage, bottomMargin, tableWidth, margin, document, page,
				true, drawContent);

		float rowHeight = 55;
		float colWidth = 11;

		LineStyle boldLineStyle = new LineStyle(Color.BLACK, 3);

		for (int i = 0; i < SudokuConstant.CELLS_PER_ROW; i++) {
			
			Row<PDPage> row = table.createRow(rowHeight);
			
			for (int j = 0; j < SudokuConstant.CELLS_PER_COL; j++) {

				Integer cell = board.getCell(i, j);
				String cellContent = cell == null ? StringUtils.EMPTY : cell.toString();

				Cell<PDPage> cellObj = row.createCell(colWidth, cellContent);
				cellObj.setFont(cellFont);
				cellObj.setFontSize(cellSize);
				cellObj.setAlign(HorizontalAlignment.CENTER);
				cellObj.setValign(VerticalAlignment.MIDDLE);

				// horizontal borders
				if (i == 0) {
					cellObj.setTopBorderStyle(boldLineStyle);
				}
				
				if (Arrays.asList(2, 5).contains(i)) {
					cellObj.setBottomBorderStyle(boldLineStyle);
				}

				if (i == 8) {
					cellObj.setBottomBorderStyle(boldLineStyle);
				}

				// vertical borders
				if (Arrays.asList(0, 3, 6).contains(j)) {
					cellObj.setLeftBorderStyle(boldLineStyle);
				}

				if (j == 8) {
					cellObj.setRightBorderStyle(boldLineStyle);
				}

			}
		}

		table.draw();

		contentStream.close();

		document.save(os);

		document.close();

	}

//	private ByteArrayOutputStream geneatePdfIText(SudokuBoard board, double difficulty) throws Exception {
//		Document document = new Document();
//
//		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//		PdfWriter.getInstance(document, outputStream);
//
//		document.open();
//
//		Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24, BaseColor.BLACK);
//		Font descrFont = FontFactory.getFont(FontFactory.HELVETICA, 18, BaseColor.BLACK);
//		Font cellFont = FontFactory.getFont(FontFactory.COURIER_BOLD, 24, BaseColor.BLACK);
//
//		PdfPTable table = new PdfPTable(SudokuConstant.CELLS_PER_ROW);
//
//		for (int pos = 0; pos < SudokuConstant.NUMBER_OF_CELLS; pos++) {
//
//			int row = SudokuBoard.posToRowAndCol(pos).getLeft();
//			int col = SudokuBoard.posToRowAndCol(pos).getRight();
//
//			Integer cell = board.getCell(row, col);
//			String cellContent = cell == null ? StringUtils.EMPTY : cell.toString();
//
//			PdfPCell pCell = new PdfPCell(new Phrase(cellContent, cellFont));
//
//			pCell.setBorderWidthTop(AppConstant.PDF_BORDER_WIDTH);
//			pCell.setBorderWidthBottom(AppConstant.PDF_BORDER_WIDTH);
//			pCell.setBorderWidthLeft(AppConstant.PDF_BORDER_WIDTH);
//			pCell.setBorderWidthRight(AppConstant.PDF_BORDER_WIDTH);
//
//			// horizontal borders
//			if (Arrays.asList(0, 3, 6).contains(row)) {
//				pCell.setBorderWidthTop(AppConstant.PDF_BOLD_BORDER_WIDTH);
//			}
//
//			if (row == 8) {
//				pCell.setBorderWidthBottom(AppConstant.PDF_BOLD_BORDER_WIDTH);
//			}
//
//			// vertical borders
//			if (Arrays.asList(0, 3, 6).contains(col)) {
//				pCell.setBorderWidthLeft(AppConstant.PDF_BOLD_BORDER_WIDTH);
//			}
//
//			if (col == 8) {
//				pCell.setBorderWidthRight(AppConstant.PDF_BOLD_BORDER_WIDTH);
//			}
//
//			pCell.setHorizontalAlignment(Element.ALIGN_CENTER);
//			pCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
//			pCell.setFixedHeight(AppConstant.PDF_CELL_HEIGHT);
//
//			table.addCell(pCell);
//		}
//
//		document.add(new Paragraph("Sudoku Problem", titleFont));
//		document.add(new Paragraph("generated on sudoku.kachinc.com", descrFont));
//		document.add(
//				new Paragraph(
//						"at " + ZonedDateTime.now()
//								.format(DateTimeFormatter.ofPattern(AppConstant.DATE_TIME_FORMAT_WITH_TIMEZONE)),
//						descrFont));
//		document.add(new Paragraph("having a difficulty of " + difficulty + " between 0 and 1", descrFont));
//		document.add(new Paragraph("\n\n\n"));
//		document.add(table);
//
//		document.close();
//
//		return outputStream;
//	}

}
