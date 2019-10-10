package org.jqassistant.contrib.plugin.kieker.api.model;

import com.buschmais.jqassistant.plugin.common.api.model.NamedDescriptor;
import com.buschmais.xo.neo4j.api.annotation.Indexed;
import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Property;
import com.buschmais.xo.neo4j.api.annotation.Relation;

/**
 * Defines the node for a Method.
 */
@Label("Method")
public interface MethodDescriptor extends KiekerDescriptor, NamedDescriptor {

    @Relation.Outgoing
    CallsDescriptor getCallee();

    @Relation.Incoming
    CallsDescriptor getCaller();

    @Property("signature")
    @Indexed
    void setSignature(String signature);

    String getSignature();

    void setDuration(long duration);

    long getDuration();

    void setIncomingCalls(int incomingCalls);

    int getIncomingCalls();

    void setOutgoingCalls(int outgoingCalls);

    int getOutgoingCalls();
}
