package my.project.plugin.scanner.model;

import com.buschmais.jqassistant.core.store.api.model.Descriptor;
import com.buschmais.xo.neo4j.api.annotation.Label;

/**
 * TODO
 */
@Label("Method")
public interface MethodDescriptor extends KiekerDescriptor {

    void setSignature(String signature);
    String getSignature();

}
