package org.jqassistant.contrib.plugin.kieker.api.model;

import com.buschmais.jqassistant.core.store.api.model.Descriptor;
import com.buschmais.xo.neo4j.api.annotation.Relation;

@Relation("CALLS")
public interface CallsDescriptor extends Descriptor {

    @Relation.Outgoing
    MethodDescriptor getCallee();

    @Relation.Incoming
    MethodDescriptor getCaller();

    long getTraceId();

    void setTraceId(long traceId);

    long getTimestamp();

    void setTimestamp(long timestamp);

    void setCallOrderIndex(int callOrderIndex);

    int getCallOrderIndex();

}
