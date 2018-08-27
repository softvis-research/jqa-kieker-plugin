package org.jqassistant.contrib.plugin.kieker.api.model;

import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Relation;

import java.util.List;

/**
 * Defines the label for a type.
 * A type declares a method.
 * It stores its signature.
 */
@Label("Type")
public interface TypeDescriptor extends KiekerDescriptor {

    void setSignature(String signature);
    String getSignature();

    @Relation("DECLARES")
    List<MethodDescriptor> getMethods();
}
