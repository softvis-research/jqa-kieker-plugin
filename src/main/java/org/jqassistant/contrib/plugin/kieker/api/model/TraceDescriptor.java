package org.jqassistant.contrib.plugin.kieker.api.model;

import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Relation;

import java.util.List;

/**
 * Defines the label for a Trace of a record. A Trace contains Events. It stores
 * those properties: traceId, threadId, sessionId, hostname, parentTraceId and
 * parentOrderId
 */
@Label("Trace")
public interface TraceDescriptor extends KiekerDescriptor {

    @Relation("CONTAINS")
    List<EventDescriptor> getEvents();

    void setLoggingTimestamp(long timestamp);

    long getLoggingTimestamp();

    void setTraceId(long traceId);

    long getTraceId();

    void setThreadId(long threadId);

    long getThreadId();

    void setSessionId(String sessionId);

    String getSessionId();

    void setHostname(String hostname);

    String getHostname();

    void setParentTraceId(long parentTraceId);

    long getParentTraceId();

    void setParentOrderId(int parentOrderId);

    int getParentOrderId();
}
