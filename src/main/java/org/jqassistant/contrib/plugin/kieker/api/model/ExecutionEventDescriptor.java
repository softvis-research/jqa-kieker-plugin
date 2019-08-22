package org.jqassistant.contrib.plugin.kieker.api.model;

import com.buschmais.jqassistant.core.store.api.model.Descriptor;
import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Relation;

/**
 * Defines the node for a call event.
 */
@Label("Execution")
public interface ExecutionEventDescriptor extends EventDescriptor, Descriptor {
    @Relation("EXECUTION")
    MethodDescriptor getExecutedMethod();

    void setExecutedMethod(MethodDescriptor executedMethod);

    void setBeforeTimestamp(long beforeTimestamp);

    long getBeforeTimestamp();

    void setAfterTimestamp(long afterTimestamp);

    long getAfterTimestamp();

    void setBeforeOrderIndex(int beforeOrderIndex);

    int getBeforeOrderIndex();

    void setAfterOrderIndex(int afterOrderIndex);

    int getAfterOrderIndex();
}
