package org.jqassistant.contrib.plugin.kieker.api.model;

import com.buschmais.jqassistant.plugin.common.api.model.DirectoryDescriptor;
import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Relation;

import java.util.List;

/**
 * Defines the node for a Record stored in a kieker file. A Record contains
 * traces. It stores those properties: version, controllerName, hostname,
 * experimentId, debugMode(Yes or No), timeOffset and numberOfRecords.
 */
@Label("Record")
public interface RecordDescriptor extends KiekerDescriptor, DirectoryDescriptor {

    @Relation("CONTAINS")
    List<TraceDescriptor> getTraces();

    @Relation("CONTAINS")
    List<MeasurementDescriptor> getMeasurements();

    void setLoggingTimestamp(long timestamp);

    long getLoggingTimestamp();

    void setVersion(String version);

    String getVersion();

    void setControllerName(String controllerName);

    String getControllerName();

    void setHostname(String hostname);

    String getHostname();

    void setExperimentId(int experimentId);

    int getExperimentId();

    void setTimeOffset(long timeOffset);

    long getTimeOffset();

    void setTimeUnit(String timeUnit);

    String getTimeUnit();

    void setNumberOfRecords(long numberOfRecords);

    long getNumberOfRecords();
}
