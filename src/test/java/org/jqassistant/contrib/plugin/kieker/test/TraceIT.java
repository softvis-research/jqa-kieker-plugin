package org.jqassistant.contrib.plugin.kieker.test;

import com.buschmais.jqassistant.core.scanner.api.DefaultScope;
import com.buschmais.jqassistant.plugin.common.test.AbstractPluginIT;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class TraceIT extends AbstractPluginIT {

    @Test
    @Disabled
    public void testTrace() {
        final String TEST_DIRECTORY_PATH = "src/test/resources/bookstore";
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

        // test property values
        TestResult testResultProperties = query(
            "MATCH (n:Trace) Where n.threadId=1 RETURN n.threadId, n.traceId, n.hostname, n.parentOrderId, n.sessionId, n.parentTraceId, n.loggingTimestamp");
        // threadId is "1"
        assertThat(testResultProperties.getColumn("n.threadId").get(0).toString(), equalTo("1"));
        // traceId is "3881283897249497088"
        assertThat(testResultProperties.getColumn("n.traceId").get(0).toString(), equalTo("3881283897249497088"));
        // hostname is ""
        assertThat(testResultProperties.getColumn("n.hostname").get(0).toString(), equalTo("SE"));
        // parentOrderId is "-1"
        assertThat(testResultProperties.getColumn("n.parentOrderId").get(0).toString(), equalTo("-1"));
        // sessionId is "<no-session-id>"
        assertThat(testResultProperties.getColumn("n.sessionId").get(0).toString(), equalTo("<no-session-id>"));
        // parentTraceId is "3881283897249497088"
        assertThat(testResultProperties.getColumn("n.parentTraceId").get(0).toString(), equalTo("3881283897249497088"));
        // loggingTimestamp is "1412763178834633375"
        assertThat(testResultProperties.getColumn("n.loggingTimestamp").get(0).toString(),
            equalTo("1412763178834633375"));
        store.commitTransaction();
    }
}
