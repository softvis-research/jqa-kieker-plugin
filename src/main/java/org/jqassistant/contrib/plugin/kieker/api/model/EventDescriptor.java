package org.jqassistant.contrib.plugin.kieker.api.model;

import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Relation;

import java.util.List;

/**
 * Defines the label for an event, that took place in a trace.
 * Contains the properties: timestamp, traceId and orderIndex.
 * An event can call a method.
 */
@Label("Event")
public interface EventDescriptor extends KiekerDescriptor {
	
    @Relation("CALLS")
    List<MethodDescriptor> getMethods();

    void setTimestamp(long timestamp);
    long getTimestamp();
    
    void setTraceId(long traceId);
    long getTraceId();
    
    void setOrderIndex(int orderIndex);
    int getOrderIndex();
}