package org.jqassistant.contrib.plugin.kieker.test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.File;

import org.junit.Test;

import com.buschmais.jqassistant.core.scanner.api.DefaultScope;
import com.buschmais.jqassistant.plugin.common.test.AbstractPluginIT;

public class MethodIT extends AbstractPluginIT {

	@Test
	public void testMethod() {
		final String TEST_DIRECTORY_PATH = "src/test/resources/";
		File directory = new File(TEST_DIRECTORY_PATH);
		store.beginTransaction();
		getScanner().scan(directory, TEST_DIRECTORY_PATH, DefaultScope.NONE);

		TestResult testResultMethod = query("MATCH (m:Method) RETURN m");
		// Every Method has a Before- and AfterOperationEvent. With each 5
		// Before-/AfterOper... the file contains 10 Methods.
		assertThat(testResultMethod.getColumn("m").size(), equalTo(10));

		// test property values of two methods
		TestResult testResultProperties = query(
				"MATCH (m:Method) where m.signature=\"public void kieker.examples.monitoring.aspectj.Bookstore.searchBook()\" RETURN m.signature");
		// signature is "3881283897249497088"
		assertThat(testResultProperties.getColumn("m.signature").get(0).toString(),
				equalTo("public void kieker.examples.monitoring.aspectj.Bookstore.searchBook()"));
		// two events have this function -> 1 After- plus 1 BeforeOperationEvent
		assertThat(testResultProperties.getColumn("m.signature").size(), equalTo(2));
		store.commitTransaction();
	}
}
