package org.jqassistant.contrib.plugin.kieker.api.model;

import com.buschmais.jqassistant.core.store.api.model.Descriptor;
import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Relation;

/**
 * Defines the node for a call event.
 */
@Label("Call")
public interface CallEventDescriptor extends EventDescriptor, Descriptor {
    @Relation("CALLED_BY")
    MethodDescriptor getCaller();

    void setCaller(MethodDescriptor caller);

    @Relation("CALLED")
    MethodDescriptor getCallee();

    void setCallee(MethodDescriptor callee);

    void setTimestamp(long timestamp);

    long getTimestamp();

    void setOrderIndex(int orderIndex);

    int getOrderIndex();
}
