package org.jqassistant.contrib.plugin.kieker.test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.File;

import org.junit.Test;

import com.buschmais.jqassistant.core.scanner.api.DefaultScope;
import com.buschmais.jqassistant.plugin.common.test.AbstractPluginIT;

public class TypeIT extends AbstractPluginIT {

	@Test
	public void testType() {
		final String TEST_DIRECTORY_PATH = "src/test/resources/";
		File directory = new File(TEST_DIRECTORY_PATH);
		store.beginTransaction();
		getScanner().scan(directory, TEST_DIRECTORY_PATH, DefaultScope.NONE);

		TestResult testResultType = query("MATCH (:Type)-[:DECLARES]->(m:Method) RETURN m");
		// Every Method is declared by a Type.
		assertThat(testResultType.getColumn("m").size(), equalTo(10));

		// test property values of a type
		TestResult testResultProperties = query(
				"Match (t:Type) Where t.signature = \"kieker.examples.monitoring.aspectj.BookstoreStarter\" return t.signature");
		// signature is "kieker.examples.monitoring.aspectj.BookstoreStarter"
		assertThat(testResultProperties.getColumn("t.signature").get(0).toString(),
				equalTo("kieker.examples.monitoring.aspectj.BookstoreStarter"));
		// 6 methods have this type
		assertThat(testResultProperties.getColumn("t.signature").size(), equalTo(6));
		store.commitTransaction();

	}
}
