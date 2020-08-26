package org.jqassistant.contrib.plugin.kieker.test;

import com.buschmais.jqassistant.core.scanner.api.DefaultScope;
import com.buschmais.jqassistant.plugin.common.test.AbstractPluginIT;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class MethodIT extends AbstractPluginIT {

    @Test
    public void testMethod() {
        final String TEST_DIRECTORY_PATH = "src/test/resources/bookstore";
        File directory = new File(TEST_DIRECTORY_PATH);
        store.beginTransaction();
        getScanner().scan(directory, TEST_DIRECTORY_PATH, DefaultScope.NONE);

        TestResult testResultMethod = query("MATCH (m:Method) RETURN m");
        // there are 5 methods
        assertThat(testResultMethod.getColumn("m").size(), equalTo(5));

        // test property values of two methods
        TestResult testResultProperties = query(
            "MATCH (m:Method) where m.signature=\"public void kieker.examples.monitoring.aspectj.Bookstore.searchBook()\" RETURN m.signature");
        // signature is "public void
        // kieker.examples.monitoring.aspectj.Bookstore.searchBook()"
        assertThat(testResultProperties.getColumn("m.signature").get(0).toString(),
            equalTo("public void kieker.examples.monitoring.aspectj.Bookstore.searchBook()"));
        // two events have this function -> 1 After- plus 1 BeforeOperationEvent
        assertThat(testResultProperties.getColumn("m.signature").size(), equalTo(1));
        store.commitTransaction();
    }
}
