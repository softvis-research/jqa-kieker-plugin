package my.project.plugin.scanner.model;

import com.buschmais.xo.neo4j.api.annotation.Label;

// tag::class[]
/**
 * Represents a column within a row of a Kieker file.
 */
@Label("Property")
public interface KiekerColumnDescriptor extends KiekerDescriptor {
	/*property name*/
    String getName();
    
    void setName(String value);
    
	/*property value*/
    String getValue();

    void setValue(String value);

    /*int getIndex();

    void setIndex(int index);*/
}
// end::class[]