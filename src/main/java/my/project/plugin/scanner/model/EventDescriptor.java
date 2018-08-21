package my.project.plugin.scanner.model;

import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Relation;

import java.util.List;

/**
 * TODO
 */
@Label("Event")
public interface EventDescriptor extends KiekerDescriptor {

    //TODO implement properties (TraceMetadata)

    @Relation("CALLS")
    List<MethodDescriptor> getMethods();

}
