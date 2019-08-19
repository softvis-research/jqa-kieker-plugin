package org.jqassistant.contrib.plugin.kieker.api.model;

import com.buschmais.jqassistant.plugin.common.api.model.NamedDescriptor;
import com.buschmais.xo.neo4j.api.annotation.Indexed;
import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Property;
import com.buschmais.xo.neo4j.api.annotation.Relation;

import java.util.Set;

/**
 * Defines the node for a Method.
 */
@Label("Method")
public interface MethodDescriptor extends KiekerDescriptor, NamedDescriptor {

    @Relation.Outgoing
    Set<CallsDescriptor> getCallee();

    @Relation.Incoming
    Set<CallsDescriptor> getCaller();

    @Property("signature")
    @Indexed
    void setSignature(String signature);

    String getSignature();

    void setBeforeTimestamp(long beforeTimestamp);

    long getBeforeTimestamp();

    void setAfterTimestamp(long afterTimestamp);

    long getAfterTimestamp();

    void setDuration(long duration);

    long getDuration();
}
