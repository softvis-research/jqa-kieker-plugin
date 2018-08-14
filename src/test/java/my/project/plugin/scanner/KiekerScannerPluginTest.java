package my.project.plugin.scanner;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.util.List;

import org.hamcrest.CoreMatchers;
import org.junit.Test;

import com.buschmais.jqassistant.core.scanner.api.DefaultScope;
import com.buschmais.jqassistant.core.store.api.model.Descriptor;
import com.buschmais.jqassistant.plugin.common.test.AbstractPluginIT;

import my.project.plugin.scanner.model.KiekerColumnDescriptor;
import my.project.plugin.scanner.model.KiekerFileDescriptor;
import my.project.plugin.scanner.model.KiekerRowDescriptor;

// tag::KiekerScannerPluginTest[]
public class KiekerScannerPluginTest extends AbstractPluginIT {

    @Test
    public void scanKiekerFile() {
        store.beginTransaction();
        // Scan the test Kieker file located as resource in the classpath.
        String fileName = "/kieker-20141008-101258830-UTC-000-Thread-1.dat";
        File testFile = new File(getClassesDirectory(KiekerScannerPluginTest.class), fileName);

        // Scan the Kieker file and assert that the returned descriptor is a KiekerFileDescriptor.
        assertThat(getScanner().scan(testFile, fileName, DefaultScope.NONE), CoreMatchers.<Descriptor>instanceOf(KiekerFileDescriptor.class));

        // Determine the KiekerFileDescriptor by executing a Cypher query.
        TestResult testResult = query("MATCH (kiekerFile:KIEKER:File) RETURN kiekerFile");
        List<KiekerFileDescriptor> kiekerFiles = testResult.getColumn("kiekerFile");
        assertThat(kiekerFiles.size(), equalTo(1));

        KiekerFileDescriptor kiekerFile = kiekerFiles.get(0);
        assertThat(kiekerFile.getFileName(), equalTo(fileName));

        // Get rows and verify expected count
        List<KiekerRowDescriptor> rows = kiekerFile.getRows();
        assertThat(rows.size(), equalTo(13));

        // Verify first (and only) row
        KiekerRowDescriptor row0 = rows.get(9);
        assertThat(row0.getLineNumber(), equalTo(5));
        
        // Verify the columns of the first row
        List<KiekerColumnDescriptor> row0Columns = row0.getColumns();
        assertThat(row0Columns.size(), equalTo(7));
        
        // Verify property names and values of the first row
        KiekerColumnDescriptor headerColumn0 = row0Columns.get(0);
        assertThat(headerColumn0.getName(), equalTo("traceId"));
        assertThat(headerColumn0.getValue(), equalTo("3881283897249497088"));

        KiekerColumnDescriptor headerColumn1 = row0Columns.get(1);
        assertThat(headerColumn1.getName(), equalTo("orderIndex"));
        assertThat(headerColumn1.getValue(), equalTo("3"));
        
        KiekerColumnDescriptor headerColumn8 = row0Columns.get(5);
        assertThat(headerColumn8.getName(), equalTo("LoggingTimestamp"));
        assertThat(headerColumn8.getValue(), equalTo("1412763178853643763"));
        
        KiekerColumnDescriptor headerColumn9 = row0Columns.get(6);
        assertThat(headerColumn9.getName(), equalTo("timestamp"));
        assertThat(headerColumn9.getValue(), equalTo("1412763178853641518"));

        store.commitTransaction();
    }
}
// end::KiekerScannerPluginTest[]
