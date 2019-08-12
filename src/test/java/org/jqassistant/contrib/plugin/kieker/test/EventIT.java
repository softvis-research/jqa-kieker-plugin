package org.jqassistant.contrib.plugin.kieker.test;

import com.buschmais.jqassistant.core.scanner.api.DefaultScope;
import com.buschmais.jqassistant.plugin.common.test.AbstractPluginIT;
import org.junit.Test;

import java.io.File;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class EventIT extends AbstractPluginIT {

    @Test
    public void testEvent() {
        final String TEST_DIRECTORY_PATH = "src/test/resources/";
        File directory = new File(TEST_DIRECTORY_PATH);
        store.beginTransaction();
        getScanner().scan(directory, TEST_DIRECTORY_PATH, DefaultScope.NONE);

        TestResult testResultType = query("MATCH (:Trace)-[:CONTAINS]->(e:Event) RETURN e");
        // Every Method is declared by a Type.
        assertThat(testResultType.getColumn("e").size(), equalTo(10));

        // test property values of an event
        TestResult testResultProperties = query(
            "MATCH (n:Event) Where n.traceId= 3881283897249497088 and n.orderIndex = 0 return n.traceId, n.orderIndex, n.loggingTimestamp, n.timestamp");
        // traceId is "3881283897249497088"
        assertThat(testResultProperties.getColumn("n.traceId").get(0).toString(), equalTo("3881283897249497088"));
        // orderIndex is "0"
        assertThat(testResultProperties.getColumn("n.orderIndex").get(0).toString(), equalTo("0"));
        // loggingTimestamp is "1412763178849813012"
        assertThat(testResultProperties.getColumn("n.loggingTimestamp").get(0).toString(),
            equalTo("1412763178849813012"));
        // timestamp is "1412763178849798259"
        assertThat(testResultProperties.getColumn("n.timestamp").get(0).toString(), equalTo("1412763178849798259"));
        store.commitTransaction();
    }
}
