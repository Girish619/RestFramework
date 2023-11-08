package scripts;

import static io.restassured.RestAssured.*;
import static org.testng.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.hamcrest.Matchers;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import genericUtility.ExcelUtility;
import genericUtility.FileUtility;
import genericUtility.IpathConstant;
import genericUtility.JsonLibrary;
import genericUtility.RestAssuredLibrary;
import io.restassured.http.ContentType;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import pojoClass.CreateProjectPojo;

public class CreateProject {
	
	@Test(dataProvider = "data")
	public void createProject(String createdBy, String projectName, String status, String teamSize) throws FileNotFoundException, IOException {
		
		FileUtility fUtil = new FileUtility();
		ExcelUtility eUtil = new ExcelUtility();
		RestAssuredLibrary rLib = new RestAssuredLibrary();
		JsonLibrary jLib = new JsonLibrary();
		
		baseURI = fUtil.readPropertyFile("baseURI");
		port = 8084;
		String createProj = fUtil.readPropertyFile("createProject");
		
		String expectedStatusLine = eUtil.getValue("Header", 1, 0);
		String expectedPragma = eUtil.getValue("Header", 1, 1);
		String expectedVary = eUtil.getValue("Header", 1, 2);
		
		projectName = projectName+jLib.createRandom();
		
		CreateProjectPojo ry = new CreateProjectPojo(createdBy, projectName, status, teamSize);
		
		Response res = given()
		.body(ry)
		.contentType(ContentType.JSON)
		.when()
		.post(createProj);
		
		res.then()
		.assertThat()
		.statusCode(201)
		.time(Matchers.lessThan(3000l), TimeUnit.MILLISECONDS)
		.contentType(ContentType.JSON)
		.header("Vary", expectedVary)
		.header("Pragma", expectedPragma)
		.statusLine(expectedStatusLine)
		.log()
		.all();
		
		String actualCreatedBy = rLib.getJsonData(res, "createdBy");
		String actualProjectName = rLib.getJsonData(res, "projectName");
		String actualStatus = rLib.getJsonData(res, "status");
		assertEquals(createdBy, actualCreatedBy);
		assertEquals(projectName, actualProjectName);
		assertEquals(status, actualStatus);
		System.out.println("TC Pass");
	}
	
	@DataProvider
	public Object[][] data() throws Throwable {
		Workbook workBook = WorkbookFactory.create(new FileInputStream(IpathConstant.excelPath));
		Sheet sheet = workBook.getSheet("project");
		int lastRow = sheet.getLastRowNum();
		int lastCell = sheet.getRow(0).getLastCellNum();
		Object[][] obj = new Object[lastRow][lastCell];
		for (int i = 1; i < lastRow+1; i++) {
			for (int j = 0; j < lastCell; j++) {
				obj[i-1][j] = sheet.getRow(i).getCell(j).getStringCellValue();
			}
		}
		return obj;
	}


	@Test
	public void schemaValidationReqRes() {
		File file = new File("./schemaReqRes.json");
		when()
		.get("https://reqres.in/api/users?page=2")
		.then()
		.assertThat()
		.body(JsonSchemaValidator.matchesJsonSchema(file))
		.log()
		.all();
	}
	
	@Test
	public void schemaValidationRmg() throws FileNotFoundException, IOException {
		File file = new File("./schemaRmg.Json");
		FileUtility fUtil = new FileUtility();
		baseURI = fUtil.readPropertyFile("baseURI");
		port = 8084;
		when()
		.get("/projects/TY_PROJ_12229")
		.then()
		.assertThat()
		.body(JsonSchemaValidator.matchesJsonSchema(file))
		.log()
		.all();
	}
}
