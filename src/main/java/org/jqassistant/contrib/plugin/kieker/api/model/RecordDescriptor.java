package org.jqassistant.contrib.plugin.kieker.api.model;

import com.buschmais.jqassistant.plugin.common.api.model.FileDescriptor;
import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Relation;

import java.util.List;

/**
 * TODO
 */
@Label("Record")
public interface RecordDescriptor extends KiekerDescriptor, FileDescriptor {

    //TODO implement properties

    @Relation("CONTAINS")
    List<TraceDescriptor> getTraces();

}
