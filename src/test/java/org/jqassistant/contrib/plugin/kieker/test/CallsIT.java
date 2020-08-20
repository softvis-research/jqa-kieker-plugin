package org.jqassistant.contrib.plugin.kieker.test;

import com.buschmais.jqassistant.core.scanner.api.DefaultScope;
import com.buschmais.jqassistant.plugin.common.test.AbstractPluginIT;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class CallsIT extends AbstractPluginIT {

    @Test
    public void testTypeDependencies() {
        final String TEST_DIRECTORY_PATH = "src/test/resources/metrics";
        File directory = new File(TEST_DIRECTORY_PATH);
        store.beginTransaction();
        getScanner().scan(directory, TEST_DIRECTORY_PATH, DefaultScope.NONE);
        TestResult testResultMethod = query("MATCH (t1:Type:Kieker)-[d:DEPENDS_ON]->(t2:Type:Kieker) RETURN SUM(d.weight) AS totalWeight");
        assertThat(testResultMethod.getColumn("totalWeight").size(), equalTo(7));
        store.commitTransaction();
    }

    @Test
    public void testPackageDependencies() {
        final String TEST_DIRECTORY_PATH = "src/test/resources/metrics";
        File directory = new File(TEST_DIRECTORY_PATH);
        store.beginTransaction();
        getScanner().scan(directory, TEST_DIRECTORY_PATH, DefaultScope.NONE);
        TestResult testResultMethod = query("MATCH (p1:Package:Kieker)-[d:DEPENDS_ON]->(p2:Package:Kieker) RETURN SUM(d.weight) AS totalWeight");
        assertThat(testResultMethod.getColumn("totalWeight").size(), equalTo(7));
        store.commitTransaction();
    }
}
