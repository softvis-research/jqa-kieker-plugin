package my.project.plugin.scanner.model;

import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Relation;

import java.util.List;

/**
 * TODO
 */
@Label("Trace")
public interface TraceDescriptor extends KiekerDescriptor {

    //TODO implement properties (TraceMetadata)

    @Relation("CONTAINS")
    List<EventDescriptor> getEvents();

    void setTraceId(long traceId);
    long getTraceId();

}
