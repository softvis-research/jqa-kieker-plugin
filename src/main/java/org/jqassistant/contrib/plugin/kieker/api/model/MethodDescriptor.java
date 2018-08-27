package org.jqassistant.contrib.plugin.kieker.api.model;

import com.buschmais.xo.neo4j.api.annotation.Label;

/**
 * Defines the label for a Method, that was called by an event.
 * A Method is identified by its signature.
 */
@Label("Method")
public interface MethodDescriptor extends KiekerDescriptor {

    void setSignature(String signature);
    String getSignature();

}
