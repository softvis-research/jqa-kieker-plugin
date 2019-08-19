package org.jqassistant.contrib.plugin.kieker.test;

import com.buschmais.jqassistant.core.scanner.api.DefaultScope;
import com.buschmais.jqassistant.plugin.common.test.AbstractPluginIT;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class TypeIT extends AbstractPluginIT {

    @Test
    @Ignore
    public void testType() {
        final String TEST_DIRECTORY_PATH = "src/test/resources/";
        File directory = new File(TEST_DIRECTORY_PATH);
        store.beginTransaction();
        getScanner().scan(directory, TEST_DIRECTORY_PATH, DefaultScope.NONE);

        TestResult testResultType = query("MATCH (:Type)-[:DECLARES]->(m:Method) RETURN m");
        // there are 5 methods
        assertThat(testResultType.getColumn("m").size(), equalTo(5));

        // test property values of a type
        TestResult testResultProperties = query(
            "Match (t:Type) Where t.fqn = \"kieker.examples.monitoring.aspectj.BookstoreStarter\" return t.fqn");
        // fqn is "kieker.examples.monitoring.aspectj.BookstoreStarter"
        assertThat(testResultProperties.getColumn("t.fqn").get(0).toString(),
            equalTo("kieker.examples.monitoring.aspectj.BookstoreStarter"));
        // 1 method has this type
        assertThat(testResultProperties.getColumn("t.fqn").size(), equalTo(1));
        store.commitTransaction();

    }
}
