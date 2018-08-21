package my.project.plugin.scanner;

import com.buschmais.jqassistant.core.scanner.api.Scanner;
import com.buschmais.jqassistant.core.scanner.api.ScannerContext;
import com.buschmais.jqassistant.core.scanner.api.ScannerPlugin.Requires;
import com.buschmais.jqassistant.core.scanner.api.Scope;
import com.buschmais.jqassistant.core.store.api.Store;
import com.buschmais.jqassistant.plugin.common.api.model.FileDescriptor;
import com.buschmais.jqassistant.plugin.common.api.scanner.AbstractScannerPlugin;
import com.buschmais.jqassistant.plugin.common.api.scanner.filesystem.FileResource;
import kieker.analysisteetime.plugin.reader.filesystem.fsReader.FSDirectoryReader;
import my.project.plugin.scanner.model.RecordDescriptor;

import java.io.File;
import java.io.IOException;


/**
 * A Kieker file scanner plugin.
 */
@Requires(FileDescriptor.class) // The file descriptor is created by the file scanner plugin and enriched by
// this one
public class KiekerFileScannerPlugin extends AbstractScannerPlugin<FileResource, RecordDescriptor> {

    @Override
    //File is a .dat, but file is structured as a CSV file
    public boolean accepts(FileResource item, String path, Scope scope) {
        return path.toLowerCase().endsWith(".dat");
    }

    @Override
    protected void configure() {
        // TODO implement property for directory (JQASSISTANT_PLUGIN_KIEKER_TRACELOG_DIRNAME)
        super.configure();
    }

    @Override
    public RecordDescriptor scan(FileResource item, String path, Scope scope, Scanner scanner) throws IOException {
        // Get context, store, and record descriptor from scanner
        ScannerContext context = scanner.getContext();
        final Store store = context.getStore();
        final FileDescriptor fileDescriptor = context.getCurrentDescriptor();
        final RecordDescriptor recordDescriptor = store.addDescriptorType(fileDescriptor, RecordDescriptor.class);

        // Set record receiver that maps read records to corresponding descriptors
        KiekerRecordReceiver kiekerRecordReceiver = new KiekerRecordReceiver(recordDescriptor, store);

        // Set filesystem directory reader (reads *.map and *.dat files in a directory)
        // TODO use property JQASSISTANT_PLUGIN_KIEKER_TRACELOG_DIRNAME
        FSDirectoryReader fsDirectoryReader = new FSDirectoryReader(new File("src/test/resources"), kiekerRecordReceiver, true);
        fsDirectoryReader.run();

        return recordDescriptor;
    }
}
