package my.project.plugin.scanner.model;

import java.util.List;

import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Relation;

// tag::class[]
/**
 * Represents a row of a Kieker file.
 */
@Label("Row")
public interface KiekerRowDescriptor extends KiekerDescriptor {

    int getLineNumber();

    void setLineNumber(int lineNumber);

    @Relation("HAS_PROPERTY")
    List<KiekerColumnDescriptor> getColumns();

}
// end::class[]