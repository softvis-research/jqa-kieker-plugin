package org.jqassistant.contrib.plugin.kieker.api.model;

import com.buschmais.jqassistant.core.store.api.model.Descriptor;
import com.buschmais.xo.neo4j.api.annotation.Relation;

@Relation("CALLS")
public interface CallsDescriptor extends Descriptor, WeightDescriptor {

    @Relation.Outgoing
    MethodDescriptor getCallee();

    @Relation.Incoming
    MethodDescriptor getCaller();

}
