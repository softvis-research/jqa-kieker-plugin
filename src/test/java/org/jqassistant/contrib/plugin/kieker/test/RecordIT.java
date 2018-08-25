package org.jqassistant.contrib.plugin.kieker.test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.File;

import org.junit.Test;

import com.buschmais.jqassistant.core.scanner.api.DefaultScope;
import com.buschmais.jqassistant.plugin.common.test.AbstractPluginIT;

public class RecordIT extends AbstractPluginIT {

	@Test
	public void testRecord() {
		final String TEST_DIRECTORY_PATH = "src/test/resources/";
		File directory = new File(TEST_DIRECTORY_PATH);
		store.beginTransaction();
		getScanner().scan(directory, TEST_DIRECTORY_PATH, DefaultScope.NONE);
		TestResult testResult = query("MATCH (:Record)-[:CONTAINS]->(t:Trace) RETURN t");
		// one record with two traces
		assertThat(testResult.getColumn("t").size(), equalTo(2));

		// test property values
		TestResult testResultProperties = query(
				"MATCH (n:Record) RETURN n.hostname, n.fileName, n.numberOfRecords, n.controllerName, n.timeOffset, n.experimentId, n.version, n.debugMode, n.loggingTimestamp, n.timeUnit");
		// hostname is "SE"
		assertThat(testResultProperties.getColumn("n.hostname").get(0).toString(), equalTo("SE"));
		// fileName is "/kieker-20141008-101258830-UTC-000-Thread-1.dat"
		assertThat(testResultProperties.getColumn("n.fileName").get(0).toString(),
				equalTo("/kieker-20141008-101258830-UTC-000-Thread-1.dat"));
		// numberOfRecords is "1"
		assertThat(testResultProperties.getColumn("n.numberOfRecords").get(0).toString(), equalTo("1"));
		// controllerName is "KIEKER-SINGLETON"
		assertThat(testResultProperties.getColumn("n.controllerName").get(0).toString(), equalTo("KIEKER-SINGLETON"));
		// timeOffset is "0"
		assertThat(testResultProperties.getColumn("n.timeOffset").get(0).toString(), equalTo("0"));
		// experimentId is "1"
		assertThat(testResultProperties.getColumn("n.experimentId").get(0).toString(), equalTo("1"));
		// version is "1.9"
		assertThat(testResultProperties.getColumn("n.version").get(0).toString(), equalTo("1.9"));
		// debugMode is "false"
		assertThat(testResultProperties.getColumn("n.debugMode").get(0).toString(), equalTo("false"));
		// loggingTimestamp is "1412763178835942258"
		assertThat(testResultProperties.getColumn("n.loggingTimestamp").get(0).toString(),
				equalTo("1412763178835942258"));
		// timeUnit is "NANOSECONDS"
		assertThat(testResultProperties.getColumn("n.timeUnit").get(0).toString(), equalTo("NANOSECONDS"));
		store.commitTransaction();
	}
}
