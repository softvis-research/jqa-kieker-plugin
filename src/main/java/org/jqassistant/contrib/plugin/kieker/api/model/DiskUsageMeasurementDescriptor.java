package org.jqassistant.contrib.plugin.kieker.api.model;

import com.buschmais.jqassistant.plugin.common.api.model.NamedDescriptor;
import com.buschmais.xo.neo4j.api.annotation.Label;

@Label("DiskUsage")
public interface DiskUsageMeasurementDescriptor extends NamedDescriptor, MeasurementDescriptor {

    // String/Path -> handle unescaped backslashes
    void setDeviceName(String deviceName);

    String getDeviceName();

    void setQueue(double queue);

    double getQueue();

    void setReadBytesPerSecond(double readBytesPerSecond);

    double getReadBytesPerSecond();

    void setReadsPerSecond(double readsPerSecond);

    double getReadsPerSecond();

    void setServiceTime(double serviceTime);

    double getServiceTime();

    void setWriteBytesPerSecond(double writeBytesPerSecond);

    double getWriteBytesPerSecond();

    void setWritesPerSecond(double writesPerSecond);

    double getWritesPerSecond();
}
