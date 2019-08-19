package org.jqassistant.contrib.plugin.kieker.impl.scanner;

import com.buschmais.jqassistant.core.scanner.api.ScannerContext;
import kieker.common.record.flow.trace.TraceMetadata;
import kieker.common.record.flow.trace.operation.AbstractOperationEvent;
import kieker.common.record.flow.trace.operation.AfterOperationEvent;
import kieker.common.record.flow.trace.operation.BeforeOperationEvent;
import kieker.common.record.flow.trace.operation.CallOperationEvent;
import kieker.common.record.misc.KiekerMetadataRecord;
import org.jqassistant.contrib.plugin.kieker.api.model.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The Kieker helper creates records, traces, and call graphs.
 *
 * @author Richard Mueller
 */
public class KiekerHelper {
    private ScannerContext scannerContext = null;
    private RecordDescriptor recordDescriptor = null;
    private Map<String, TypeDescriptor> typeCache = null;
    private Map<Long, TraceDescriptor> traceCache = null;
    private Map<String, Stack<Long>> timestampCache = null;
    private final String REGEX_FOR_METHOD_NAME = "([a-zA-Z0-9_]+) *\\(";


    public KiekerHelper(ScannerContext scannerContext, RecordDescriptor recordDescriptor) {
        this.scannerContext = scannerContext;
        this.recordDescriptor = recordDescriptor;
        typeCache = new HashMap<String, TypeDescriptor>();
        traceCache = new HashMap<Long, TraceDescriptor>();
        timestampCache = new HashMap<String, Stack<Long>>();
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
        traceCache.put(trace.getTraceId(), traceDescriptor);
    }

    void createCallGraph(AbstractOperationEvent event) {
        if (event instanceof BeforeOperationEvent || event instanceof AfterOperationEvent) {
            // get type and method of event
            TypeDescriptor typeDescriptor = getTypeDescriptor(event.getClassSignature());
            MethodDescriptor methodDescriptor = getMethodDescriptor(event.getOperationSignature(), typeDescriptor);

            if (event instanceof BeforeOperationEvent) {
                // push before timestamp to stack
                pushToTimestampStack(event.getOperationSignature(), event.getTimestamp());

                // add first method to trace
                if (event.getOrderIndex() == 0) {
                    // get type and method of event
                    TraceDescriptor traceDescriptor = getTraceDescriptor(event);
                    traceDescriptor.getMethods().add(methodDescriptor);
                }
            } else {
                // set duration
                Long currentDuration = methodDescriptor.getDuration();
                Long newDuration = event.getTimestamp() - popFromTimestampStack(methodDescriptor.getSignature());
                methodDescriptor.setDuration(currentDuration + newDuration);
            }
        } else if (event instanceof CallOperationEvent) {
            // create call relation
            MethodDescriptor callerMethod = getMethodDescriptor(((CallOperationEvent) event).getCallerOperationSignature(), getTypeDescriptor(((CallOperationEvent) event).getCallerClassSignature()));
            MethodDescriptor calleeMethod = getMethodDescriptor(((CallOperationEvent) event).getCalleeOperationSignature(), getTypeDescriptor(((CallOperationEvent) event).getCalleeClassSignature()));
            CallsDescriptor callsDescriptor = scannerContext.getStore().create(callerMethod, CallsDescriptor.class, calleeMethod);
            callsDescriptor.setTraceId(event.getTraceId());
            callsDescriptor.setTimestamp(event.getTimestamp());
            callsDescriptor.setCallOrderIndex(event.getOrderIndex());
        }
    }

    private synchronized void pushToTimestampStack(String signature, Long timestamp) {
        Stack<Long> timestampStack = timestampCache.get(signature);
        // if stack does not exist create it
        if (timestampStack == null) {
            timestampStack = new Stack<Long>();
            timestampStack.push(timestamp);
            timestampCache.put(signature, timestampStack);
        } else {
            // push if item is not already in stack
            if (!timestampStack.contains(timestamp)) timestampStack.push(timestamp);
        }
    }

    private synchronized Long popFromTimestampStack(String signature) {
        Stack<Long> timestampStack = timestampCache.get(signature);
        if (timestampStack != null) {
            return timestampStack.pop();
        } else {
            return 0L;
        }
    }

    private TraceDescriptor getTraceDescriptor(AbstractOperationEvent event) {
        TraceDescriptor traceDescriptor = null;
        if (traceCache.containsKey(event.getTraceId())) {
            traceDescriptor = traceCache.get(event.getTraceId());
        } else {
            traceDescriptor = scannerContext.getStore().create(TraceDescriptor.class);
            traceDescriptor.setTraceId(event.getTraceId());
            recordDescriptor.getTraces().add(traceDescriptor);
            traceCache.put(event.getTraceId(), traceDescriptor);
        }
        return traceDescriptor;
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
            methodDescriptor.setName(getMethodNameFromSignature(signature));
            methodDescriptor.setSignature(signature);
            parent.getDeclaredMethods().add(methodDescriptor);
            return methodDescriptor;
        }
    }

    private String getMethodNameFromSignature(String signature) {
        Pattern pattern = Pattern.compile(REGEX_FOR_METHOD_NAME);
        Matcher matcher = pattern.matcher(signature);
        while (matcher.find()) {
            String matchResult = matcher.group();
            // remove opening parenthesis
            return matchResult.substring(0, matchResult.length() - 1);
        }
        return signature;
    }
}
