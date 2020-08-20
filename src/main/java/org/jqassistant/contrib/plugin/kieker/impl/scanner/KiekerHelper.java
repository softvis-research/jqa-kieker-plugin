package org.jqassistant.contrib.plugin.kieker.impl.scanner;

import com.buschmais.jqassistant.core.scanner.api.ScannerContext;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import kieker.common.record.flow.trace.TraceMetadata;
import kieker.common.record.flow.trace.operation.AbstractOperationEvent;
import kieker.common.record.flow.trace.operation.AfterOperationEvent;
import kieker.common.record.flow.trace.operation.BeforeOperationEvent;
import kieker.common.record.flow.trace.operation.CallOperationEvent;
import kieker.common.record.misc.KiekerMetadataRecord;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.jqassistant.contrib.plugin.kieker.api.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The Kieker helper creates records, traces, and events.
 *
 * @author Richard Mueller, Dirk Mahler
 */
public class KiekerHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(KiekerHelper.class);
    private ScannerContext scannerContext;
    private RecordDescriptor recordDescriptor;
    private Cache<MethodDescriptorKey, MethodDescriptor> methodDescriptorCache;
    private Cache<CallsDescriptorKey, CallsDescriptor> callsDescriptorCache;
    private Map<Long, TraceDescriptor> traceCache;
    private Map<String, Stack<BeforeOperationEvent>> timestampCache;
    private final String REGEX_FOR_METHOD_NAME = "([a-zA-Z0-9_]+) *\\(";

    public KiekerHelper(ScannerContext scannerContext, RecordDescriptor recordDescriptor) {
        this.scannerContext = scannerContext;
        this.recordDescriptor = recordDescriptor;
        methodDescriptorCache = Caffeine.newBuilder().maximumSize(100000).build();
        callsDescriptorCache = Caffeine.newBuilder().maximumSize(10000000).build();
        traceCache = new HashMap<>();
        timestampCache = new HashMap<>();
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
                MethodDescriptor methodDescriptor = getMethodDescriptor(event.getClassSignature(), event.getOperationSignature());

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
            CallOperationEvent callOperationEvent = (CallOperationEvent) event;
            MethodDescriptor caller = getMethodDescriptor(callOperationEvent.getCallerClassSignature(), callOperationEvent.getCallerOperationSignature());
            MethodDescriptor callee = getMethodDescriptor(callOperationEvent.getCalleeClassSignature(), callOperationEvent.getCalleeOperationSignature());
            // add call
            addCall(caller, callee);
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
        TraceDescriptor traceDescriptor;
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

    private MethodDescriptor getMethodDescriptor(String fqn, String signature) {
        return methodDescriptorCache.get(MethodDescriptorKey.builder().type(fqn).signature(signature).build(), methodDescriptorKey -> {
            Map<String, Object> params = new HashMap<>();
            params.put("fqn", fqn);
            params.put("name", fqn.substring(fqn.lastIndexOf(".") + 1));
            params.put("signature", signature);
            return scannerContext.getStore().executeQuery("MERGE (t:Kieker:Type{fqn:$fqn}) ON CREATE SET t.name=$name MERGE (t)-[:DECLARES]->(m:Kieker:Method{signature:$signature}) RETURN m", params).getSingleResult().get("m", MethodDescriptor.class);
        });
    }

    private CallsDescriptor addCall(MethodDescriptor caller, MethodDescriptor callee) {
        CallsDescriptor callsDescriptor = callsDescriptorCache.get(CallsDescriptorKey.builder().caller(caller).callee(callee).build(), key -> {
            Map<String, Object> params = new HashMap<>();
            params.put("caller", caller);
            params.put("callee", callee);
            return scannerContext.getStore().executeQuery("MATCH (caller:Kieker:Method),(callee:Kieker:Method) WHERE id(caller)=$caller AND id(callee)=$callee MERGE (caller)-[c:CALLS]->(callee) RETURN c", params).getSingleResult().get("c", CallsDescriptor.class);
        });
        Long weight = callsDescriptor.getWeight();
        callsDescriptor.setWeight(weight == null ? 1 : weight + 1);
        return callsDescriptor;
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

    @Builder
    @EqualsAndHashCode
    @ToString
    private static class MethodDescriptorKey {

        private String type;

        private String signature;

    }

    @Builder
    @EqualsAndHashCode
    @ToString
    private static class CallsDescriptorKey {

        private MethodDescriptor caller;

        private MethodDescriptor callee;

    }
}
