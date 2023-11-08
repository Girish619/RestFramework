package genericUtility;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;



public class ExcelUtility {
	
	public String getValue(String sheetName, int rowNum, int cellNum) throws EncryptedDocumentException, FileNotFoundException, IOException {
		Workbook workBook = WorkbookFactory.create(new FileInputStream(IpathConstant.excelPath));
		String value = workBook.getSheet(sheetName).getRow(rowNum).getCell(cellNum).getStringCellValue();
		workBook.close();
		return value;
	}
	
}
