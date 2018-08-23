package org.jqassistant.contrib.plugin.kieker.test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.File;

import org.junit.Test;

import com.buschmais.jqassistant.core.scanner.api.DefaultScope;
import com.buschmais.jqassistant.plugin.common.test.AbstractPluginIT;

public class BeforeOperationIT extends AbstractPluginIT {

	@Test
	public void testBeforeOperation() {
		final String TEST_DIRECTORY_PATH = "src/test/resources/";
		File directory = new File(TEST_DIRECTORY_PATH);
		store.beginTransaction();
		getScanner().scan(directory, TEST_DIRECTORY_PATH, DefaultScope.NONE);

		TestResult testResultBeforeOperation = query("MATCH (:BeforeOperation)-[:CALLS]->(m:Method) RETURN m");
		// Every BeforeOperationEvent calls one method. So 5 BeforeOperationEvents call
		// 5 methods.
		assertThat(testResultBeforeOperation.getColumn("m").size(), equalTo(5));
		store.commitTransaction();
	}
}
