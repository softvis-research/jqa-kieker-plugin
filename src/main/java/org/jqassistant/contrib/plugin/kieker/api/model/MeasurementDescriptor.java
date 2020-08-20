package org.jqassistant.contrib.plugin.kieker.api.model;

import com.buschmais.xo.neo4j.api.annotation.Label;

@Label("Measurement")
public interface MeasurementDescriptor extends KiekerDescriptor {

    void setTimestamp(long timestamp);

    long getTimestamp();

    void setHostname(String hostname);

    String getHostname();
}
