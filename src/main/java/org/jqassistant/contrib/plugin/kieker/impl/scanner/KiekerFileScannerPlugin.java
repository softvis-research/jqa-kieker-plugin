package org.jqassistant.contrib.plugin.kieker.impl.scanner;

import com.buschmais.jqassistant.core.scanner.api.Scanner;
import com.buschmais.jqassistant.core.scanner.api.ScannerContext;
import com.buschmais.jqassistant.core.scanner.api.ScannerPlugin.Requires;
import com.buschmais.jqassistant.core.scanner.api.Scope;
import com.buschmais.jqassistant.plugin.common.api.model.FileDescriptor;
import com.buschmais.jqassistant.plugin.common.api.scanner.AbstractScannerPlugin;
import com.buschmais.jqassistant.plugin.common.api.scanner.filesystem.FileResource;
import kieker.analysisteetime.plugin.reader.filesystem.fsReader.FSDirectoryReader;
import org.jqassistant.contrib.plugin.kieker.api.model.RecordDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;


/**
 * The Kieker file scanner plugin reads Kieker trace logs (*.map and *.dat files) from a directory and stores them
 * in a Neo4j graph database.
 *
 * @author Richard Mueller, Matteo Fischer
 */
@Requires(FileDescriptor.class) // The file descriptor is created by the file scanner plugin and enriched by
// this one
public class KiekerFileScannerPlugin extends AbstractScannerPlugin<FileResource, RecordDescriptor> {
    private static final Logger LOGGER = LoggerFactory.getLogger(KiekerFileScannerPlugin.class);
    private final String JQASSISTANT_PLUGIN_KIEKER_TRACELOG_DIRNAME = "jqassistant.plugin.kieker.tracelog.dirname";
    private String traceDirName = "src/test/resources";

    /* (non-Javadoc)
     * Checks if the kieker file is a .dat or not.
     * @see com.buschmais.jqassistant.core.scanner.api.ScannerPlugin#accepts(java.lang.Object, java.lang.String, com.buschmais.jqassistant.core.scanner.api.Scope)
     */
    @Override
    public boolean accepts(FileResource item, String path, Scope scope) {
        return path.toLowerCase().endsWith(".dat");
    }

    /* (non-Javadoc)
     * Sets the directory for the kieker files to scan.
     * @see com.buschmais.jqassistant.plugin.common.api.scanner.AbstractScannerPlugin#configure()
     */
    @Override
    protected void configure() {
        super.configure();
        if (getProperties().containsKey(JQASSISTANT_PLUGIN_KIEKER_TRACELOG_DIRNAME)) {
            traceDirName = (String) getProperties().get(JQASSISTANT_PLUGIN_KIEKER_TRACELOG_DIRNAME);
        }

        LOGGER.info("Kieker plugin looks for .map and .dat files in directory '{}'", traceDirName);
    }

    /* (non-Javadoc)
     * @see com.buschmais.jqassistant.core.scanner.api.ScannerPlugin#scan(java.lang.Object, java.lang.String, com.buschmais.jqassistant.core.scanner.api.Scope, com.buschmais.jqassistant.core.scanner.api.Scanner)
     */
    @Override
    public RecordDescriptor scan(FileResource item, String path, Scope scope, Scanner scanner) throws IOException {
        LOGGER.info("Kieker plugin scans record '{}'", item.getFile().getName());

        // Get context, store, and record descriptor from scanner
        ScannerContext scannerContext = scanner.getContext();
        final FileDescriptor fileDescriptor = scannerContext.getCurrentDescriptor();
        final RecordDescriptor recordDescriptor = scannerContext.getStore().addDescriptorType(fileDescriptor, RecordDescriptor.class);

        // Set record receiver that maps read records to corresponding descriptors
        KiekerRecordReceiver kiekerRecordReceiver = new KiekerRecordReceiver(new KiekerHelper(scannerContext, recordDescriptor));

        // Set filesystem directory reader (reads *.map and *.dat files in a directory)
        // Todo use another reader
        FSDirectoryReader fsDirectoryReader = new FSDirectoryReader(new File(traceDirName), kiekerRecordReceiver, true);
        fsDirectoryReader.run();

        return recordDescriptor;
    }
}
