package org.jqassistant.contrib.plugin.kieker.api.model;

import com.buschmais.xo.neo4j.api.annotation.Label;

/**
 * TODO
 */
@Label("Method")
public interface MethodDescriptor extends KiekerDescriptor {

    void setSignature(String signature);

    String getSignature();

}
