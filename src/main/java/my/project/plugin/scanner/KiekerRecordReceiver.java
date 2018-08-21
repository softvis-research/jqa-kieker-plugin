package my.project.plugin.scanner;

import com.buschmais.jqassistant.core.store.api.Store;
import kieker.analysis.plugin.reader.util.IMonitoringRecordReceiver;
import kieker.common.record.IMonitoringRecord;
import kieker.common.record.flow.trace.TraceMetadata;
import my.project.plugin.scanner.model.RecordDescriptor;
import my.project.plugin.scanner.model.TraceDescriptor;

public class KiekerRecordReceiver implements IMonitoringRecordReceiver {
    private RecordDescriptor recordDescriptor;
    private Store store;

    public KiekerRecordReceiver(RecordDescriptor recordDescriptor, Store store) {
        this.recordDescriptor = recordDescriptor;
        this.store = store;
    }

    @Override
    public boolean newMonitoringRecord(IMonitoringRecord iMonitoringRecord) {
        if (iMonitoringRecord instanceof TraceMetadata) {
            addTrace((TraceMetadata) iMonitoringRecord);
        }
        // TODO handle KiekerMetadataRecord, BeforeOperationEvent, AfterOperationEvent, [OperationExecutionEvent]
        return true;
    }

    @Override
    public void newEndOfFileRecord() {
    }

    private void addTrace(TraceMetadata trace) {
        TraceDescriptor traceDescriptor = store.create(TraceDescriptor.class);
        // TODO set all properties and relations of trace descriptor
        traceDescriptor.setTraceId(trace.getTraceId());
        recordDescriptor.getTraces().add(traceDescriptor);
    }
}
