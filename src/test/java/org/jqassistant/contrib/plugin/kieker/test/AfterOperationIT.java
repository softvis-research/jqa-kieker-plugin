package org.jqassistant.contrib.plugin.kieker.test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.File;

import org.junit.Test;

import com.buschmais.jqassistant.core.scanner.api.DefaultScope;
import com.buschmais.jqassistant.plugin.common.test.AbstractPluginIT;

public class AfterOperationIT extends AbstractPluginIT {

	@Test
	public void testAfterOperation() {
		final String TEST_DIRECTORY_PATH = "src/test/resources/";
		File directory = new File(TEST_DIRECTORY_PATH);
		store.beginTransaction();
		getScanner().scan(directory, TEST_DIRECTORY_PATH, DefaultScope.NONE);

		TestResult testResultAfterOperation = query("MATCH (:AfterOperation)-[:CALLS]->(m:Method) RETURN m");
		// Every AfterOperationEvent calls one method. So 5 AfterOperationEvents call
		// 5 methods.
		assertThat(testResultAfterOperation.getColumn("m").size(), equalTo(5));
		store.commitTransaction();
	}
}
