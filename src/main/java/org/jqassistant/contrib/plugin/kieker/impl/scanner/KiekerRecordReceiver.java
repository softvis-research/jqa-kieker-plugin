package org.jqassistant.contrib.plugin.kieker.impl.scanner;

import com.buschmais.jqassistant.core.store.api.Store;
import kieker.analysis.plugin.reader.util.IMonitoringRecordReceiver;
import kieker.common.record.IMonitoringRecord;
import kieker.common.record.flow.trace.TraceMetadata;
import kieker.common.record.flow.trace.operation.AbstractOperationEvent;
import kieker.common.record.misc.KiekerMetadataRecord;
import org.jqassistant.contrib.plugin.kieker.api.model.RecordDescriptor;
import org.jqassistant.contrib.plugin.kieker.api.model.TraceDescriptor;

import java.util.HashMap;
import java.util.Map;

/**
 * The record receiver maps read records from filesystem directory reader to
 * corresponding descriptors.
 *
 * @author Richard Mueller, Matteo Fischer
 */
public class KiekerRecordReceiver implements IMonitoringRecordReceiver {
    private KiekerHelper kiekerHelper = null;
    private RecordDescriptor recordDescriptor;
    private Store store;
    private Map<Long, TraceDescriptor> traces = new HashMap<>();

    public KiekerRecordReceiver(KiekerHelper kiekerHelper) {
        this.kiekerHelper = kiekerHelper;
    }

    /*
     * Adds a new Trace, Before-/AfterOperationEvent or Metadata to the
     * recordDescriptor.
     *
     * @see kieker.analysis.plugin.reader.util.IMonitoringRecordReceiver#
     * newMonitoringRecord(kieker.common.record.IMonitoringRecord)
     *
     * @param iMonitoringRecord Abstract MonitoringRecord from the kieker file.
     *
     * @return Always true.
     */
    @Override
    public boolean newMonitoringRecord(IMonitoringRecord iMonitoringRecord) {
        if (iMonitoringRecord instanceof KiekerMetadataRecord) {
            kiekerHelper.createRecord((KiekerMetadataRecord) iMonitoringRecord);
        } else if (iMonitoringRecord instanceof TraceMetadata) {
            kiekerHelper.createTrace((TraceMetadata) iMonitoringRecord);
        } else if (iMonitoringRecord instanceof AbstractOperationEvent) {
            kiekerHelper.createEvent((AbstractOperationEvent) iMonitoringRecord);
        }
        // TODO further record types
        return true;
    }

    /*
     * @see kieker.analysis.plugin.reader.util.IMonitoringRecordReceiver#
     * newEndOfFileRecord()
     */
    @Override
    public void newEndOfFileRecord() {
    }

}
