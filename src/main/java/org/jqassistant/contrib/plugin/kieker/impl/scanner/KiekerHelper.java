package org.jqassistant.contrib.plugin.kieker.impl.scanner;

import com.buschmais.jqassistant.core.scanner.api.ScannerContext;
import kieker.common.record.flow.trace.TraceMetadata;
import kieker.common.record.flow.trace.operation.AbstractOperationEvent;
import kieker.common.record.flow.trace.operation.AfterOperationEvent;
import kieker.common.record.flow.trace.operation.BeforeOperationEvent;
import kieker.common.record.flow.trace.operation.CallOperationEvent;
import kieker.common.record.misc.KiekerMetadataRecord;
import org.jqassistant.contrib.plugin.kieker.api.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The Kieker helper creates records, traces, and events.
 *
 * @author Richard Mueller
 */
public class KiekerHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(KiekerHelper.class);
    private ScannerContext scannerContext = null;
    private RecordDescriptor recordDescriptor = null;
    private Map<String, TypeDescriptor> typeCache = null;
    private Map<Long, TraceDescriptor> traceCache = null;
    private Map<String, Stack<BeforeOperationEvent>> timestampCache = null;
    private final String REGEX_FOR_METHOD_NAME = "([a-zA-Z0-9_]+) *\\(";


    public KiekerHelper(ScannerContext scannerContext, RecordDescriptor recordDescriptor) {
        this.scannerContext = scannerContext;
        this.recordDescriptor = recordDescriptor;
        typeCache = new HashMap<String, TypeDescriptor>();
        traceCache = new HashMap<Long, TraceDescriptor>();
        timestampCache = new HashMap<String, Stack<BeforeOperationEvent>>();
    }

    void createRecord(KiekerMetadataRecord record) {
        recordDescriptor.setLoggingTimestamp(record.getLoggingTimestamp());
        recordDescriptor.setVersion(record.getVersion());
        recordDescriptor.setControllerName(record.getControllerName());
        recordDescriptor.setHostname(record.getHostname());
        recordDescriptor.setExperimentId(record.getExperimentId());
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
        recordDescriptor.getTraces().add(traceDescriptor);
        traceCache.put(trace.getTraceId(), traceDescriptor);
    }

    void createEvent(AbstractOperationEvent event) {
        if (event instanceof BeforeOperationEvent || event instanceof AfterOperationEvent) {
            if (event instanceof BeforeOperationEvent) {
                // push before timestamp to stack
                pushToEventStack(event.getOperationSignature(), (BeforeOperationEvent) event);
            } else {
                // get type and method of event
                TypeDescriptor typeDescriptor = getTypeDescriptor(event.getClassSignature());
                MethodDescriptor methodDescriptor = getMethodDescriptor(event.getOperationSignature(), typeDescriptor);

                // set before and after timestamp
                ExecutionEventDescriptor executionEventDescriptor = scannerContext.getStore().create(ExecutionEventDescriptor.class);
                BeforeOperationEvent beforeOperationEvent = popFromTimestampStack(methodDescriptor.getSignature());
                if (beforeOperationEvent != null) {
                    executionEventDescriptor.setBeforeOrderIndex(beforeOperationEvent.getOrderIndex());
                    executionEventDescriptor.setBeforeTimestamp(beforeOperationEvent.getTimestamp());
                    executionEventDescriptor.setAfterOrderIndex(event.getOrderIndex());
                    executionEventDescriptor.setAfterTimestamp(event.getTimestamp());
                    executionEventDescriptor.setExecutedMethod(methodDescriptor);
                    getTraceDescriptor(event).getEvents().add(executionEventDescriptor);
                } else {
                    LOGGER.warn("BeforeOperationEvent for method " + methodDescriptor.getSignature() + " missing.");
                }
            }
        } else if (event instanceof CallOperationEvent) {
            // get caller and callee
            MethodDescriptor callerMethod = getMethodDescriptor(((CallOperationEvent) event).getCallerOperationSignature(), getTypeDescriptor(((CallOperationEvent) event).getCallerClassSignature()));
            MethodDescriptor calleeMethod = getMethodDescriptor(((CallOperationEvent) event).getCalleeOperationSignature(), getTypeDescriptor(((CallOperationEvent) event).getCalleeClassSignature()));
            // update number of incoming and outgoing calls
            if (event.getOrderIndex() == 1) {
                callerMethod.setIncomingCalls(callerMethod.getIncomingCalls() + 1);
            }
            callerMethod.setOutgoingCalls(callerMethod.getOutgoingCalls() + 1);
            calleeMethod.setIncomingCalls(calleeMethod.getIncomingCalls() + 1);
            // create call event
            CallEventDescriptor callEventDescriptor = scannerContext.getStore().create(CallEventDescriptor.class);
            callEventDescriptor.setTimestamp(event.getTimestamp());
            callEventDescriptor.setOrderIndex(event.getOrderIndex());
            callEventDescriptor.setCaller(callerMethod);
            callEventDescriptor.setCallee(calleeMethod);
            getTraceDescriptor(event).getEvents().add(callEventDescriptor);
        }
    }

    private synchronized void pushToEventStack(String signature, BeforeOperationEvent event) {
        Stack<BeforeOperationEvent> eventStack = timestampCache.get(signature);
        // if stack does not exist create it
        if (eventStack == null) {
            eventStack = new Stack<BeforeOperationEvent>();
            eventStack.push(event);
            timestampCache.put(signature, eventStack);
        } else {
            // push if item is not already in stack
            if (!eventStack.contains(event)) eventStack.push(event);
        }
    }

    private synchronized BeforeOperationEvent popFromTimestampStack(String signature) {
        Stack<BeforeOperationEvent> eventStack = timestampCache.get(signature);
        if (eventStack != null) {
            return eventStack.pop();
        } else {
            return null;
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
            methodDescriptor.setDuration(0);
            methodDescriptor.setIncomingCalls(0);
            methodDescriptor.setOutgoingCalls(0);
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
