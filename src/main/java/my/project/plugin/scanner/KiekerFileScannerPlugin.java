package my.project.plugin.scanner;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.buschmais.jqassistant.core.scanner.api.Scanner;
import com.buschmais.jqassistant.core.scanner.api.ScannerContext;
import com.buschmais.jqassistant.core.scanner.api.ScannerPlugin.Requires;
import com.buschmais.jqassistant.core.scanner.api.Scope;
import com.buschmais.jqassistant.core.store.api.Store;
import com.buschmais.jqassistant.plugin.common.api.model.FileDescriptor;
import com.buschmais.jqassistant.plugin.common.api.scanner.AbstractScannerPlugin;
import com.buschmais.jqassistant.plugin.common.api.scanner.filesystem.FileResource;
import com.opencsv.CSVReader;

import my.project.plugin.scanner.model.KiekerColumnDescriptor;
import my.project.plugin.scanner.model.KiekerFileDescriptor;
import my.project.plugin.scanner.model.KiekerRowDescriptor;

// tag::class[]
/**
 * A Kieker file scanner plugin.
 */
@Requires(FileDescriptor.class) // The file descriptor is created by the file scanner plugin and enriched by
                                // this one
public class KiekerFileScannerPlugin extends AbstractScannerPlugin<FileResource, KiekerFileDescriptor> {
	String[] kiekerMap = {};
	
    @Override
    //File is a .dat, but file is structured as a CSV file
    public boolean accepts(FileResource item, String path, Scope scope) {
        return path.toLowerCase().endsWith(".dat");
    }

    @Override
    public KiekerFileDescriptor scan(FileResource item, String path, Scope scope, Scanner scanner) throws IOException {
    	//Read properties of all supported MonitoringRecordTypes.
    	MonitoringRecordTypes recordTypes = new MonitoringRecordTypes();
    	recordTypes.ReadRecordXML();
    	//Read MonitoringRecordTypes from the kieker.map file.
    	getKiekerMap(item);
    	String traceType = "";
        ScannerContext context = scanner.getContext();
        final Store store = context.getStore();
        // Open the input stream for reading the file.
        try (InputStream stream = item.createStream()) {
            // Retrieve the scanned file node from the scanner context.
            final FileDescriptor fileDescriptor = context.getCurrentDescriptor();
            // Add the Kieker label.
            final KiekerFileDescriptor kiekerFileDescriptor = store.addDescriptorType(fileDescriptor, KiekerFileDescriptor.class);
            //Parse the stream using OpenCSV.
            //Kieker Files have ";" as seperator, but the default constructor uses ","
            //-> because of this the deprecated constructor with custom seperator is used
            @SuppressWarnings("deprecation")
			CSVReader csvReader = new CSVReader(new InputStreamReader(stream), ';');
            String[] columns;
            int row = 0;
            while ((columns = csvReader.readNext()) != null) {
                // Create the node for a row.
            	KiekerRowDescriptor rowDescriptor = store.create(KiekerRowDescriptor.class);
            	kiekerFileDescriptor.getRows().add(rowDescriptor);
                rowDescriptor.setLineNumber(row);
                for (int i = 0; i < columns.length; i++) {
                	//Get the MonitoringRecordType from the first column.
                	if(i == 0) {
                		traceType = getTraceType(columns[i]); 
                	}
                	// Create the node for a column.
                	KiekerColumnDescriptor columnDescriptor = store.create(KiekerColumnDescriptor.class);
                	rowDescriptor.getColumns().add(columnDescriptor);
                	//First column is always the MonitoringRecordType.
                	//Second column is always the LoggingTimestamp.
                	if(i == 0) {
                		columnDescriptor.setName("MonitoringRecordType");
                		columnDescriptor.setValue(traceType);
                	}else if(i==1) {
                		columnDescriptor.setName("LoggingTimestamp");
                		columnDescriptor.setValue(columns[i]);
                	}else{
                		//"i - 2", because first 2 values are constant and not stored in MonitoringRecordTypes.xml
                		//e.g. third value in .dat equals the first value in the MonitoringRecordTypes.xml
                		columnDescriptor.setName(recordTypes.getPropertyName(traceType, i-2));
                		columnDescriptor.setValue(columns[i]);
                	}
                }
                row++;
            }
            return kiekerFileDescriptor;
        }
    }
    
    private String getTraceType(String input) {
    	Integer traceTypeIndex = 0;
    	//Extract index of MonitoringRecordType -> read after $.
    	traceTypeIndex = Integer.parseInt((input.substring(1)));
		return kiekerMap[traceTypeIndex];
    }
    
    private void getKiekerMap(FileResource item) {
    	ArrayList<String> properties = new ArrayList<String>();
    	//Extract path from item.
    	String path = item.toString();
    	path = path.replace((char) 92, '/');
    	path = path.substring(0, path.lastIndexOf('/'));
    	//Read data from kieker.map.
    	try(BufferedReader br = new BufferedReader(new FileReader(path + "/kieker.map"))) {
    	    for(String line; (line = br.readLine()) != null; ) {
    	    	String property = line.substring(line.lastIndexOf(".")+1);
    	    	properties.add(property);
    	    }
    	} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    	String[] tempMap = new String[properties.size()];
    	tempMap = properties.toArray(tempMap);
    	kiekerMap = tempMap;
    }
}
// end::class[]