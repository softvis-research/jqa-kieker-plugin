package my.project.plugin.scanner.model;

import com.buschmais.jqassistant.core.store.api.model.Descriptor;
import com.buschmais.xo.neo4j.api.annotation.Label;
// tag::class[]
/**
 * Defines the label which is shared by all nodes representing Kieker structures.
 */
@Label("KIEKER")
public interface KiekerDescriptor extends Descriptor {
}
// end::class[]