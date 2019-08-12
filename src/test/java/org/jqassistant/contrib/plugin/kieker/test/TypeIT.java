package org.jqassistant.contrib.plugin.kieker.test;

import com.buschmais.jqassistant.core.scanner.api.DefaultScope;
import com.buschmais.jqassistant.plugin.common.test.AbstractPluginIT;
import org.junit.Test;

import java.io.File;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

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
            "Match (t:Type) Where t.fqn = \"kieker.examples.monitoring.aspectj.BookstoreStarter\" return t.fqn");
        // fqn is "kieker.examples.monitoring.aspectj.BookstoreStarter"
        assertThat(testResultProperties.getColumn("t.fqn").get(0).toString(),
            equalTo("kieker.examples.monitoring.aspectj.BookstoreStarter"));
        // 6 methods have this type
        assertThat(testResultProperties.getColumn("t.fqn").size(), equalTo(6));
        store.commitTransaction();

    }
}
