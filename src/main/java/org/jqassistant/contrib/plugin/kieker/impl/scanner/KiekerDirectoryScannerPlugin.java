package org.jqassistant.contrib.plugin.kieker.impl.scanner;

import com.buschmais.jqassistant.core.scanner.api.DefaultScope;
import com.buschmais.jqassistant.core.scanner.api.ScannerContext;
import com.buschmais.jqassistant.core.scanner.api.ScannerPlugin;
import com.buschmais.jqassistant.core.scanner.api.Scope;
import com.buschmais.jqassistant.plugin.common.api.model.DirectoryDescriptor;
import com.buschmais.jqassistant.plugin.common.api.scanner.AbstractDirectoryScannerPlugin;
import kieker.analysisteetime.plugin.reader.filesystem.fsReader.FSDirectoryReader;
import kieker.common.util.filesystem.FSUtil;
import org.jqassistant.contrib.plugin.kieker.api.model.RecordDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

@ScannerPlugin.Requires(DirectoryDescriptor.class)
public class KiekerDirectoryScannerPlugin extends AbstractDirectoryScannerPlugin<RecordDescriptor> {
    private static final Logger LOGGER = LoggerFactory.getLogger(KiekerDirectoryScannerPlugin.class);

    @Override
    public boolean accepts(File item, String path, Scope scope) throws IOException {
        return new File(item.getAbsolutePath() + File.separator + FSUtil.MAP_FILENAME).exists();
    }

    /**
     * Return the scope the plugin expects for execution.
     *
     * @return The scope.
     */
    @Override
    protected Scope getRequiredScope() {
        return DefaultScope.NONE;
    }

    /**
     * Return the descriptor representing the artifact.
     *
     * @param container      The container.
     * @param scannerContext The scanner context.
     * @return The artifact descriptor.
     */
    @Override
    protected RecordDescriptor getContainerDescriptor(File container, ScannerContext scannerContext) {
        LOGGER.info("Kieker plugin scans records in '{}'", container.getAbsolutePath());

        // Get store, and record descriptor from scanner
        final DirectoryDescriptor directoryDescriptor = scannerContext.getCurrentDescriptor();
        final RecordDescriptor recordDescriptor = scannerContext.getStore().addDescriptorType(directoryDescriptor, RecordDescriptor.class);

        // Set record receiver that maps read records to corresponding descriptors
        KiekerRecordReceiver kiekerRecordReceiver = new KiekerRecordReceiver(new KiekerHelper(scannerContext, recordDescriptor));

        // Set filesystem directory reader (reads *.map, *.dat, *.bin, *.xz files in a directory)
        // Todo use another reader
        FSDirectoryReader fsDirectoryReader = new FSDirectoryReader(container, kiekerRecordReceiver, true);
        fsDirectoryReader.run();

        return recordDescriptor;
    }

    /**
     * Create a scope depending on the container type, e.g. a JAR file should
     * return classpath scope.
     *
     * @param container           The container.
     * @param containerDescriptor The container descriptor.
     * @param scannerContext
     */
    @Override
    protected void enterContainer(File container, RecordDescriptor containerDescriptor, ScannerContext scannerContext) throws IOException {

    }

    /**
     * Destroy the container dependent scope.
     *
     * @param container           The container.
     * @param containerDescriptor The container descriptor
     * @param scannerContext
     */
    @Override
    protected void leaveContainer(File container, RecordDescriptor containerDescriptor, ScannerContext scannerContext) throws IOException {

    }
}
