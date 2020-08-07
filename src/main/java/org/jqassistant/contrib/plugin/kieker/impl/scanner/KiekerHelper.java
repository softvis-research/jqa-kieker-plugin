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
import org.jqassistant.contrib.plugin.kieker.api.model.CallsDescriptor;
import org.jqassistant.contrib.plugin.kieker.api.model.ExecutionEventDescriptor;
import org.jqassistant.contrib.plugin.kieker.api.model.MethodDescriptor;
import org.jqassistant.contrib.plugin.kieker.api.model.RecordDescriptor;
import org.jqassistant.contrib.plugin.kieker.api.model.TraceDescriptor;
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
 * @author Richard Mueller
 */
public class KiekerHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(KiekerHelper.class);
    private ScannerContext scannerContext = null;
    private RecordDescriptor recordDescriptor = null;
    private Cache<MethodDescriptorKey, MethodDescriptor> methodDescriptorCache;
    private Cache<CallKey, CallsDescriptor> callsDescriptorCache;
    private Map<Long, TraceDescriptor> traceCache = null;
    private Map<String, Stack<BeforeOperationEvent>> timestampCache = null;
    //   private Map<String, CallsDescriptor> callsCache = null;
    private final String REGEX_FOR_METHOD_NAME = "([a-zA-Z0-9_]+) *\\(";
    private Integer counter = null;

    public KiekerHelper(ScannerContext scannerContext, RecordDescriptor recordDescriptor) {
        this.scannerContext = scannerContext;
        this.recordDescriptor = recordDescriptor;
        methodDescriptorCache = Caffeine.newBuilder().softValues().build();
        callsDescriptorCache = Caffeine.newBuilder().softValues().build();
        traceCache = new HashMap<Long, TraceDescriptor>();
        timestampCache = new HashMap<String, Stack<BeforeOperationEvent>>();
        counter = 0;
        //     callsCache = new HashMap<String, CallsDescriptor>();
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
        counter++;
        if (counter == 100000) {
            scannerContext.getStore().flush();
            counter = 0;
        }
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

//            // create call
//            addCall(callerMethod, calleeMethod, callerType.getFullQualifiedName(), calleeType.getFullQualifiedName());
//            // update number of incoming and outgoing calls
//            if (event.getOrderIndex() == 1) {
//                callerMethod.setIncomingCalls(callerMethod.getIncomingCalls() + 1);
//            }
//            callerMethod.setOutgoingCalls(callerMethod.getOutgoingCalls() + 1);
//            calleeMethod.setIncomingCalls(calleeMethod.getIncomingCalls() + 1);
//            // create call event
//            CallEventDescriptor callEventDescriptor = scannerContext.getStore().create(CallEventDescriptor.class);
//            callEventDescriptor.setTimestamp(event.getTimestamp());
//            callEventDescriptor.setOrderIndex(event.getOrderIndex());
//            callEventDescriptor.setCaller(callerMethod);
//            callEventDescriptor.setCallee(calleeMethod);
//            getTraceDescriptor(event).getEvents().add(callEventDescriptor);
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
        CallsDescriptor callsDescriptor = callsDescriptorCache.get(CallKey.builder().caller(caller).callee(callee).build(), key -> caller.getCallees().stream()
            .filter(call -> call.getCaller().equals(callee))
            .findAny()
            .orElse(scannerContext.getStore().create(caller, CallsDescriptor.class, callee)));
        Integer weight = callsDescriptor.getWeight();
        callsDescriptor.setWeight(weight != null ? weight + 1 : 1);
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

//    private void addCall(MethodDescriptor callerMethod, MethodDescriptor calleeMethod, String callerTypeFqn, String calleeTypeFqn) {
//        String callsKey = "";
//        if (!callerMethod.getSignature().contains("(")) {
//            // is encrypted
//            callsKey = String.valueOf((callerTypeFqn + callerMethod.getSignature() + calleeTypeFqn + calleeMethod.getSignature()).hashCode());
//        }
//        callsKey = callerTypeFqn + callerMethod.getSignature() + calleeTypeFqn + calleeMethod.getSignature();
//
//        if (callsCache.containsKey(callsKey)) {
//            // increase weight
//            CallsDescriptor callsDescriptor = callsCache.get(callsKey);
//            callsDescriptor.setWeight(callsDescriptor.getWeight() + 1);
//        } else {
//            CallsDescriptor callsDescriptor = scannerContext.getStore().create(callerMethod, CallsDescriptor.class, calleeMethod);
//            callsDescriptor.setWeight(1);
//            callsCache.put(callsKey, callsDescriptor);
//        }
//    }

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
    private static class CallKey {

        private MethodDescriptor caller;

        private MethodDescriptor callee;

    }
}
