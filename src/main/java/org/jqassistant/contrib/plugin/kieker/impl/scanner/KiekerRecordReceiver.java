package org.jqassistant.contrib.plugin.kieker.impl.scanner;

import kieker.analysis.plugin.reader.util.IMonitoringRecordReceiver;
import kieker.common.record.IMonitoringRecord;
import kieker.common.record.flow.trace.TraceMetadata;
import kieker.common.record.flow.trace.operation.AbstractOperationEvent;
import kieker.common.record.misc.KiekerMetadataRecord;

/**
 * The record receiver maps read records from filesystem directory reader to
 * corresponding descriptors.
 *
 * @author Richard Mueller, Matteo Fischer
 */
public class KiekerRecordReceiver implements IMonitoringRecordReceiver {
    private KiekerHelper kiekerHelper = null;

    public KiekerRecordReceiver(KiekerHelper kiekerHelper) {
        this.kiekerHelper = kiekerHelper;
    }

    /*
     * Creates record, trace, or event nodes and corresponding relationships.
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
            kiekerHelper.createCallGraph((AbstractOperationEvent) iMonitoringRecord);
        }
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
