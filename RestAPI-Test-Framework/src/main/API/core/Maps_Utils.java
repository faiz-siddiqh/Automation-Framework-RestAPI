package core;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.filter.session.SessionFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

public class Maps_Utils {

	public static SessionFilter session = new SessionFilter();

	public void setUp() {
		ApiUtils.setUp("Maps");

	}

	public RequestSpecification getRequestSpec() {
		RestAssured.baseURI = "https://rahulshettyacademy.com/";
		PrintStream log = null;
		try {
			log = new PrintStream(new FileOutputStream("logs.txt"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return new RequestSpecBuilder().setBaseUri("https://rahulshettyacademy.com/").setContentType(ContentType.JSON)
				.addQueryParam("Key", "qaclick123").addFilter(RequestLoggingFilter.logRequestTo(log))
				.addFilter(ResponseLoggingFilter.logResponseTo(log)).build();

	}

	public ResponseSpecification getResponseSpec() {

		return new ResponseSpecBuilder().expectStatusCode(200).expectContentType(ContentType.JSON).build();
	}

}


