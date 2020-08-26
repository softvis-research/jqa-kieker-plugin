package org.jqassistant.contrib.plugin.kieker.api.model;

import com.buschmais.jqassistant.plugin.common.api.model.NamedDescriptor;
import com.buschmais.xo.neo4j.api.annotation.Label;

@Label("MemSwapUsage")
public interface MemSwapUsageMeasurementDescriptor extends NamedDescriptor, MeasurementDescriptor {

    void setMemFree(long memFree);

    long getMemFree();

    void setMemTotal(long memTotal);

    long getMemTotal();

    void setMemUsed(long memUsed);

    long getMemUsed();

    void setSwapFree(long swapFree);

    long getSwapFree();

    void setSwapTotal(long swapTotal);

    long getSwapTotal();

    void setSwapUsed(long swapUsed);

    long getSwapUsed();
}
