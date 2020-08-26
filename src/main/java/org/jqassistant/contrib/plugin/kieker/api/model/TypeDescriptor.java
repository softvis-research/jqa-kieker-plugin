package org.jqassistant.contrib.plugin.kieker.api.model;

import com.buschmais.jqassistant.core.store.api.model.FullQualifiedNameDescriptor;
import com.buschmais.jqassistant.plugin.common.api.model.NamedDescriptor;
import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Relation;
import com.buschmais.xo.neo4j.api.annotation.Relation.Incoming;
import com.buschmais.xo.neo4j.api.annotation.Relation.Outgoing;

import java.util.List;

/**
 * Defines the node for a type.
 */
@Label(value = "Type", usingIndexedPropertyOf = FullQualifiedNameDescriptor.class)
public interface TypeDescriptor extends KiekerDescriptor, FullQualifiedNameDescriptor, NamedDescriptor {

    @Relation("DECLARES")
    List<MethodDescriptor> getDeclaredMethods();

    @Outgoing
    List<TypeDependsOnDescriptor> getDependencies();

    @Incoming
    List<TypeDependsOnDescriptor> getDependents();
}
