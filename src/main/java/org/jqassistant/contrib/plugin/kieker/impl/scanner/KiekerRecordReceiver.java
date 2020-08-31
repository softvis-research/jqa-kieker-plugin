package org.jqassistant.contrib.plugin.kieker.impl.scanner;

import kieker.analysis.plugin.reader.util.IMonitoringRecordReceiver;
import kieker.common.record.IMonitoringRecord;
import kieker.common.record.flow.trace.operation.AbstractOperationEvent;
import kieker.common.record.misc.KiekerMetadataRecord;
import kieker.common.record.system.*;

/**
 * The record receiver delegates records from filesystem directory reader to
 * the Kieker helper.
 *
 * @author Richard Mueller, Matteo Fischer, Tom Strempel
 */
public class KiekerRecordReceiver implements IMonitoringRecordReceiver {
    private KiekerHelper kiekerHelper = null;

    public KiekerRecordReceiver(KiekerHelper kiekerHelper) {
        this.kiekerHelper = kiekerHelper;
    }

    /*
     * Delegates records to the Kieker helper.
     *
     * @see kieker.analysis.plugin.reader.util.IMonitoringRecordReceiver#
     * newMonitoringRecord(kieker.common.record.IMonitoringRecord)
     *
     * @param iMonitoringRecord Abstract MonitoringRecord from the kieker file.
     *
     * @return true
     */
    @Override
    public boolean newMonitoringRecord(IMonitoringRecord iMonitoringRecord) {
        if (iMonitoringRecord instanceof KiekerMetadataRecord) {
            kiekerHelper.createRecord((KiekerMetadataRecord) iMonitoringRecord);
        } else if (iMonitoringRecord instanceof AbstractOperationEvent) {
            kiekerHelper.createEvent((AbstractOperationEvent) iMonitoringRecord);
        } else if (iMonitoringRecord instanceof CPUUtilizationRecord || iMonitoringRecord instanceof DiskUsageRecord ||
            iMonitoringRecord instanceof LoadAverageRecord || iMonitoringRecord instanceof MemSwapUsageRecord ||
            iMonitoringRecord instanceof NetworkUtilizationRecord) {
            kiekerHelper.createMeasurement(iMonitoringRecord);
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
