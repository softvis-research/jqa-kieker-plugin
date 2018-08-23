package org.jqassistant.contrib.plugin.kieker.test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.File;

import org.junit.Test;

import com.buschmais.jqassistant.core.scanner.api.DefaultScope;
import com.buschmais.jqassistant.plugin.common.test.AbstractPluginIT;

public class TraceIT extends AbstractPluginIT {

	@Test
	public void testTrace() {
		final String TEST_DIRECTORY_PATH = "src/test/resources/";
		File directory = new File(TEST_DIRECTORY_PATH);
		store.beginTransaction();
		getScanner().scan(directory, TEST_DIRECTORY_PATH, DefaultScope.NONE);

		TestResult testResultBeforeOperation = query("MATCH (:Trace)-[:CONTAINS]->(b:BeforeOperation) RETURN b");
		// Overall the record contains 5 AfterOperationEvents
		assertThat(testResultBeforeOperation.getColumn("b").size(), equalTo(5));

		TestResult testResultAfterOperation = query("MATCH (:Trace)-[:CONTAINS]->(a:AfterOperation) RETURN a");
		// Overall the record contains 5 AfterOperationEvents
		assertThat(testResultAfterOperation.getColumn("a").size(), equalTo(5));

		// Number of Before- and AfterOperationEvents has to be the same.
		Integer aSize = testResultAfterOperation.getColumn("a").size();
		assertThat(testResultBeforeOperation.getColumn("b").size(), equalTo(aSize));
		store.commitTransaction();
	}
}
