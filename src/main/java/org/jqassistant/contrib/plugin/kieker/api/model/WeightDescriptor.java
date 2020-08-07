package org.jqassistant.contrib.plugin.kieker.api.model;

import com.buschmais.xo.neo4j.api.annotation.Property;

/**
 * Defines a descriptor containing weight information.
 */
public interface WeightDescriptor {

    @Property("weight")
    Long getWeight();

    void setWeight(Long weight);

}
