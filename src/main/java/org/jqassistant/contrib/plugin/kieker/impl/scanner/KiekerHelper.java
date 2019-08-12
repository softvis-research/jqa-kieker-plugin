package org.jqassistant.contrib.plugin.kieker.impl.scanner;

import com.buschmais.jqassistant.core.scanner.api.ScannerContext;
import kieker.common.record.flow.trace.TraceMetadata;
import kieker.common.record.flow.trace.operation.AbstractOperationEvent;
import kieker.common.record.flow.trace.operation.AfterOperationEvent;
import kieker.common.record.flow.trace.operation.BeforeOperationEvent;
import kieker.common.record.misc.KiekerMetadataRecord;
import org.apache.commons.lang.StringUtils;
import org.jqassistant.contrib.plugin.kieker.api.model.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class KiekerHelper {
    private ScannerContext scannerContext = null;
    private RecordDescriptor recordDescriptor = null;
    private Map<String, TypeDescriptor> typeCache = null;

    public KiekerHelper(ScannerContext scannerContext, RecordDescriptor recordDescriptor) {
        this.scannerContext = scannerContext;
        this.recordDescriptor = recordDescriptor;
        typeCache = new HashMap<String, TypeDescriptor>();
    }

    void createRecord(KiekerMetadataRecord record) {
        recordDescriptor.setLoggingTimestamp(record.getLoggingTimestamp());
        recordDescriptor.setVersion(record.getVersion());
        recordDescriptor.setControllerName(record.getControllerName());
        recordDescriptor.setHostname(record.getHostname());
        recordDescriptor.setExperimentId(record.getExperimentId());
        recordDescriptor.setDebugMode(record.isDebugMode());
        recordDescriptor.setTimeOffset(record.getTimeOffset());
        recordDescriptor.setTimeUnit(record.getTimeUnit());
        recordDescriptor.setNumberOfRecords(record.getNumberOfRecords());
    }

    void createTrace(TraceMetadata trace) {
        TraceDescriptor traceDescriptor = scannerContext.getStore().create(TraceDescriptor.class);
        traceDescriptor.setLoggingTimestamp(trace.getLoggingTimestamp());
        traceDescriptor.setTraceId(trace.getTraceId());
        traceDescriptor.setThreadId(trace.getThreadId());
        traceDescriptor.setSessionId(trace.getSessionId());
        traceDescriptor.setHostname(trace.getHostname());
        traceDescriptor.setParentTraceId(trace.getParentTraceId());
        traceDescriptor.setParentOrderId(trace.getParentOrderId());
        recordDescriptor.getTraces().add(traceDescriptor);
    }

    void createEvent(AbstractOperationEvent event) {
        EventDescriptor eventDescriptor = null;
        if (event instanceof BeforeOperationEvent) {
            eventDescriptor = scannerContext.getStore().create(BeforeOperationEventDescriptor.class);
        } else if (event instanceof AfterOperationEvent) {
            eventDescriptor = scannerContext.getStore().create(AfterOperationEventDescriptor.class);
        }
        if (eventDescriptor != null) {
            eventDescriptor.setLoggingTimestamp(event.getLoggingTimestamp());
            eventDescriptor.setTimestamp(event.getTimestamp());
            eventDescriptor.setTraceId(event.getTraceId());
            eventDescriptor.setOrderIndex(event.getOrderIndex());
            TypeDescriptor typeDescriptor = getTypeDescriptor(event.getClassSignature());
            MethodDescriptor methodDescriptor = getMethodDescriptor(event.getOperationSignature(), typeDescriptor);
            eventDescriptor.getMethods().add(methodDescriptor);
            getTrace(event.getTraceId()).getEvents().add(eventDescriptor);
        }
    }

    private TraceDescriptor getTrace(long traceId) {
        TraceDescriptor traceDescriptor = null;
        for (Iterator<TraceDescriptor> iterator = recordDescriptor.getTraces().iterator(); iterator.hasNext(); ) {
            TraceDescriptor trace = iterator.next();
            if (trace.getTraceId() == traceId) {
                traceDescriptor = (TraceDescriptor) trace;
            }
        }
        if (traceDescriptor != null) {
            return traceDescriptor;
        } else {
            traceDescriptor = scannerContext.getStore().create(TraceDescriptor.class);
            traceDescriptor.setTraceId(traceId);
            recordDescriptor.getTraces().add(traceDescriptor);
            return traceDescriptor;
        }
    }

    private TypeDescriptor getTypeDescriptor(String fqn) {
        if (typeCache.containsKey(fqn)) {
            return typeCache.get(fqn);
        } else {
            TypeDescriptor typeDescriptor = scannerContext.getStore().create(TypeDescriptor.class);
            typeDescriptor.setFullQualifiedName(fqn);
            typeDescriptor.setName(fqn.substring(fqn.lastIndexOf(".") + 1));
            typeCache.put(fqn, typeDescriptor);
            return typeDescriptor;
        }
    }

    private MethodDescriptor getMethodDescriptor(String signature, TypeDescriptor parent) {
        MethodDescriptor methodDescriptor = null;
        for (Iterator<MethodDescriptor> iterator = parent.getDeclaredMethods().iterator(); iterator.hasNext(); ) {
            Object member = iterator.next();
            if (member instanceof MethodDescriptor) {
                MethodDescriptor existingMethodDescriptor = (MethodDescriptor) member;
                if (existingMethodDescriptor.getSignature().equals(signature)) {
                    methodDescriptor = existingMethodDescriptor;
                }
            }
        }
        if (methodDescriptor != null) {
            return methodDescriptor;
        } else {
            methodDescriptor = scannerContext.getStore().create(MethodDescriptor.class);
            methodDescriptor.setName(StringUtils.substringBetween(signature, " ", "("));
            methodDescriptor.setSignature(signature);
            parent.getDeclaredMethods().add(methodDescriptor);
            return methodDescriptor;
        }
    }
}
