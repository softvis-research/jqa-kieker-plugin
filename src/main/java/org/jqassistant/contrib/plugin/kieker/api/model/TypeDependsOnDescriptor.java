package org.jqassistant.contrib.plugin.kieker.api.model;

import com.buschmais.jqassistant.core.store.api.model.Descriptor;
import com.buschmais.xo.neo4j.api.annotation.Relation;
import com.buschmais.xo.neo4j.api.annotation.Relation.Incoming;
import com.buschmais.xo.neo4j.api.annotation.Relation.Outgoing;

@Relation("DEPENDS_ON")
public interface TypeDependsOnDescriptor extends Descriptor, WeightDescriptor {

    @Incoming
    TypeDescriptor getDependency();

    @Outgoing
    TypeDescriptor getDependent();
}
