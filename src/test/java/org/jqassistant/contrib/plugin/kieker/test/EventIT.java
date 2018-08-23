package org.jqassistant.contrib.plugin.kieker.test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.File;

import org.junit.Test;

import com.buschmais.jqassistant.core.scanner.api.DefaultScope;
import com.buschmais.jqassistant.plugin.common.test.AbstractPluginIT;

public class EventIT extends AbstractPluginIT {

	@Test
	public void testBeforeOperation() {
		final String TEST_DIRECTORY_PATH = "src/test/resources/";
		File directory = new File(TEST_DIRECTORY_PATH);
		store.beginTransaction();
		getScanner().scan(directory, TEST_DIRECTORY_PATH, DefaultScope.NONE);

		TestResult testResultType = query("MATCH (:Trace)-[:CONTAINS]->(e:Event) RETURN e");
		// Every Method is declared by a Type.
		assertThat(testResultType.getColumn("e").size(), equalTo(10));
		store.commitTransaction();
	}
}
