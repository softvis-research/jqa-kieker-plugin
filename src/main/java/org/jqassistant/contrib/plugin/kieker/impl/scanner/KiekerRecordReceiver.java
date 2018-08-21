package org.jqassistant.contrib.plugin.kieker.impl.scanner;

import com.buschmais.jqassistant.core.store.api.Store;
import kieker.analysis.plugin.reader.util.IMonitoringRecordReceiver;
import kieker.common.record.IMonitoringRecord;
import kieker.common.record.flow.trace.TraceMetadata;
import kieker.common.record.flow.trace.operation.AfterOperationEvent;
import kieker.common.record.flow.trace.operation.BeforeOperationEvent;
import kieker.common.record.misc.KiekerMetadataRecord;

import org.jqassistant.contrib.plugin.kieker.api.model.AfterOperationEventDescriptor;
import org.jqassistant.contrib.plugin.kieker.api.model.BeforeOperationEventDescriptor;
import org.jqassistant.contrib.plugin.kieker.api.model.MethodDescriptor;
import org.jqassistant.contrib.plugin.kieker.api.model.RecordDescriptor;
import org.jqassistant.contrib.plugin.kieker.api.model.TraceDescriptor;
import org.jqassistant.contrib.plugin.kieker.api.model.TypeDescriptor;

/**
 * The record receiver maps read records from filesystem directory reader to corresponding descriptors.
 *
 * @author Richard Mueller, Matteo Fischer
 */
public class KiekerRecordReceiver implements IMonitoringRecordReceiver {
    private RecordDescriptor recordDescriptor;
    private Store store;

    public KiekerRecordReceiver(RecordDescriptor recordDescriptor, Store store) {
        this.recordDescriptor = recordDescriptor;
        this.store = store;
    }

    /*
     * Adds a new Trace, Before-/AfterOperationEvent or Metadata to the recordDescriptor.
     * @see kieker.analysis.plugin.reader.util.IMonitoringRecordReceiver#newMonitoringRecord(kieker.common.record.IMonitoringRecord)
     */
    @Override
    public boolean newMonitoringRecord(IMonitoringRecord iMonitoringRecord) {
    	if (iMonitoringRecord instanceof KiekerMetadataRecord){
    		addMetadata((KiekerMetadataRecord) iMonitoringRecord);
    	}else if (iMonitoringRecord instanceof TraceMetadata) {
            addTrace((TraceMetadata) iMonitoringRecord);
        }else if (iMonitoringRecord instanceof BeforeOperationEvent) {
        	addBeforeOperationEvent((BeforeOperationEvent) iMonitoringRecord);
        }else if (iMonitoringRecord instanceof AfterOperationEvent) {
        	addAfterOperationEvent((AfterOperationEvent) iMonitoringRecord);
        }
        // TODO [OperationExecutionEvent]
        return true;
    }

    @Override
    public void newEndOfFileRecord() {
    }
    
    /*
     * Adds the Metadata Information to the recordDescriptor.
     */
    private void addMetadata(KiekerMetadataRecord record) {
    	recordDescriptor.setVersion(record.getVersion());
    	recordDescriptor.setControllerName(record.getControllerName());
    	recordDescriptor.setHostname(record.getHostname());
    	recordDescriptor.setExperimentId(record.getExperimentId());
    	recordDescriptor.setDebugMode(record.isDebugMode());
    	recordDescriptor.setTimeOffset(record.getTimeOffset());
    	recordDescriptor.setTimeUnit(record.getTimeUnit());
    	recordDescriptor.setNumberOfRecords(record.getNumberOfRecords());
    }
    
    /*
     * Adds a TraceDescriptor to the RecordDescriptor.
     * Reads the TraceMetadata properties from the trace and stores them in the TraceDescriptor.
     */
    private void addTrace(TraceMetadata trace) {
        TraceDescriptor traceDescriptor = store.create(TraceDescriptor.class);

        traceDescriptor.setLoggingTimestamp(trace.getLoggingTimestamp());
        traceDescriptor.setTraceId(trace.getTraceId());
        traceDescriptor.setThreadId(trace.getThreadId());
        traceDescriptor.setSessionId(trace.getSessionId());
        traceDescriptor.setHostname(trace.getHostname());
        traceDescriptor.setParentTraceId(trace.getParentTraceId());
        traceDescriptor.setParentOrderId(trace.getParentOrderId());
        
        recordDescriptor.getTraces().add(traceDescriptor);
    }
    
    /*
     * Adds a BeforeOperationEvent with MethodDescriptor to the last Trace of the recordDescriptor.
     * Reads the the Properties of the BeforeOperationEvent and stores them in the BeforeOperationEventDescriptor
     */
    private void addBeforeOperationEvent(BeforeOperationEvent event) {
    	BeforeOperationEventDescriptor eventDescriptor = store.create(BeforeOperationEventDescriptor.class);
        
    	eventDescriptor.setLoggingTimestamp(event.getLoggingTimestamp());
    	eventDescriptor.setTimestamp(event.getTimestamp());
    	eventDescriptor.setTraceId(event.getTraceId());
    	eventDescriptor.setOrderIndex(event.getOrderIndex());
    	
    	//Creates a MethodDescriptor for the EventDescriptor and adds it to it.
    	MethodDescriptor methodDescriptor = store.create(MethodDescriptor.class);
    	methodDescriptor.setSignature(event.getOperationSignature());
    	eventDescriptor.getMethods().add(methodDescriptor);

    	//Creates a TypeDescriptor and adds the MethodDescriptor to it.
    	TypeDescriptor typeDescriptor = store.create(TypeDescriptor.class);
    	typeDescriptor.setSignature(event.getClassSignature());
    	typeDescriptor.getMethods().add(methodDescriptor);
    	
    	
    	//Add the event to the last trace of the event
    	TraceDescriptor trace = recordDescriptor.getTraces().get(recordDescriptor.getTraces().size()-1);
    	trace.getEvents().add(eventDescriptor);
    	
    	//TODO override last trace in recordDescriptor with the new trace(which stores the event)
    }
    
    /*
     * Adds a AfterOperationEvent with MethodDescriptor to the last Trace of the recordDescriptor.
     * Reads the the Properties of the AfterOperationEvent and stores them in the AfterOperationEventDescriptor.
     */
    private void addAfterOperationEvent(AfterOperationEvent event) {
    	AfterOperationEventDescriptor eventDescriptor = store.create(AfterOperationEventDescriptor.class);
        
    	eventDescriptor.setLoggingTimestamp(event.getLoggingTimestamp());
    	eventDescriptor.setTimestamp(event.getTimestamp());
    	eventDescriptor.setTraceId(event.getTraceId());
    	eventDescriptor.setOrderIndex(event.getOrderIndex());

    	//Creates a MethodDescriptor for the EventDescriptor and adds it to it.
    	MethodDescriptor methodDescriptor = store.create(MethodDescriptor.class);
    	methodDescriptor.setSignature(event.getOperationSignature());
    	eventDescriptor.getMethods().add(methodDescriptor);

    	//Creates a TypeDescriptor and adds the MethodDescriptor to it.
    	TypeDescriptor typeDescriptor = store.create(TypeDescriptor.class);
    	typeDescriptor.setSignature(event.getClassSignature());
    	typeDescriptor.getMethods().add(methodDescriptor);
    	
    	
    	//Add the event to the last trace of the event
    	TraceDescriptor trace = recordDescriptor.getTraces().get(recordDescriptor.getTraces().size()-1);
    	trace.getEvents().add(eventDescriptor);
    	
    	//TODO override last trace in recordDescriptor with the new trace(which stores the event)
    }
    
}
