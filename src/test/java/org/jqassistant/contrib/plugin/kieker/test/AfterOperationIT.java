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

		// test property values a AfterOperationEvent
		TestResult testResultProperties = query(
				"MATCH (n:AfterOperation) Where n.traceId=3881283897249497088 and n.orderIndex=2 RETURN n.traceId, n.orderIndex, n.loggingTimestamp, n.timestamp");
		// traceId is "3881283897249497088"
		assertThat(testResultProperties.getColumn("n.traceId").get(0).toString(), equalTo("3881283897249497088"));
		// orderIndex is "2"
		assertThat(testResultProperties.getColumn("n.orderIndex").get(0).toString(), equalTo("2"));
		// loggingTimestamp is "1412763178853435612"
		assertThat(testResultProperties.getColumn("n.loggingTimestamp").get(0).toString(),
				equalTo("1412763178853435612"));
		// timestamp is "1412763178853429518"
		assertThat(testResultProperties.getColumn("n.timestamp").get(0).toString(), equalTo("1412763178853429518"));
		store.commitTransaction();
	}
}
