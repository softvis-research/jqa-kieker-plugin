package my.project.plugin.scanner;

import com.buschmais.jqassistant.core.scanner.api.DefaultScope;
import com.buschmais.jqassistant.plugin.common.test.AbstractPluginIT;
import org.junit.Test;

import java.io.File;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

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
        store.commitTransaction();
    }
}
