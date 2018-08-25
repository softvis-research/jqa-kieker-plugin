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

		// test property values a BeforeOperationEvent
		TestResult testResultProperties = query(
				"MATCH (n:BeforeOperation) Where n.traceId=3881283897249497088 and n.orderIndex=0 RETURN n.traceId, n.orderIndex, n.loggingTimestamp, n.timestamp");
		// traceId is "3881283897249497088"
		assertThat(testResultProperties.getColumn("n.traceId").get(0).toString(), equalTo("3881283897249497088"));
		// orderIndex is "0"
		assertThat(testResultProperties.getColumn("n.orderIndex").get(0).toString(), equalTo("0"));
		// loggingTimestamp is "1412763178849813012"
		assertThat(testResultProperties.getColumn("n.loggingTimestamp").get(0).toString(),
				equalTo("1412763178849813012"));
		// timestamp is "-1"
		assertThat(testResultProperties.getColumn("n.timestamp").get(0).toString(), equalTo("1412763178849798259"));
		store.commitTransaction();
	}
}
