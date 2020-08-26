package org.jqassistant.contrib.plugin.kieker.api.model;

import com.buschmais.jqassistant.plugin.common.api.model.NamedDescriptor;
import com.buschmais.xo.neo4j.api.annotation.Label;

@Label("NetworkUtilization")
public interface NetworkUtilizationMeasurementDescriptor extends NamedDescriptor, MeasurementDescriptor {

    void setInterfaceName(String interfaceName);

    String getInterfaceName();

    void setRxBytesPerSecond(double rxBytesPerSecond);

    double getRxBytesPerSecond();

    void setRxDroppedPerSecond(double rxDroppedPerSecond);

    double getRxDroppedPerSecond();

    void setRxErrorsPerSecond(double rxErrosPerSecond);

    double getRxErrorsPerSecond();

    void setRxFramePerSecond(double rxFramePerSecond);

    double getRxFramePerSecond();

    void setRxOverrunsPerSecond(double rxOverrunsPerSecond);

    double getRxOverrunsPerSecond();

    void setRxPacketsPerSecond(double rxPacketsPerSecond);

    double getRxPacketsPerSecond();

    void setSpeed(double speed);

    double getSpeed();

    void setTxBytesPerSecond(double txBytesPerSecond);

    double getTxBytesPerSecond();

    void setTxCarrierPerSecond(double txCarrierPerSecond);

    double getTxCarrierPerSecond();

    void setTxCollisionsPerSecond(double txCollisionsPerSecond);

    double getTxCollisionsPerSecond();

    void setTxDroppedPerSecond(double txDroppedPerSecond);

    double getTxDroppedPerSecond();

    void setTxErrorsPerSecond(double txErrorsPerSecond);

    double getTxErrorsPerSecond();

    void setTxOverrunsPerSecond(double txOverrunsPerSecond);

    double getTxOverrunsPerSecond();

    void setTxPacketsPerSecond(double txPacketsPerSecond);

    double getTxPacketsPerSecond();
}
