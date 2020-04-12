package org.jqassistant.contrib.plugin.kieker.api.model;

import com.buschmais.jqassistant.plugin.common.api.model.NamedDescriptor;
import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Relation;

import java.util.List;

/**
 * Defines the node for a Trace of a record.
 */
@Label("Trace")
public interface TraceDescriptor extends NamedDescriptor, KiekerDescriptor {

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
}
