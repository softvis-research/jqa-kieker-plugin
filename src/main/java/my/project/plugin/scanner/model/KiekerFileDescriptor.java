package my.project.plugin.scanner.model;

import java.util.List;

import com.buschmais.jqassistant.plugin.common.api.model.FileDescriptor;
import com.buschmais.xo.neo4j.api.annotation.Relation;

// tag::class[]
/**
 * Represents a Kieker file. The labels are inherited from {@link KiekerDescriptor}
 * and {@link FileDescriptor}.
 */
public interface KiekerFileDescriptor extends KiekerDescriptor, FileDescriptor {

    @Relation("HAS_ROW")
    List<KiekerRowDescriptor> getRows();

}
// end::class[]