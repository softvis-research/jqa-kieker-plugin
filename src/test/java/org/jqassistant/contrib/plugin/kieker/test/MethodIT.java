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
		store.commitTransaction();
	}
}
