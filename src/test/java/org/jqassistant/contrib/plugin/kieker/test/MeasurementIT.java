package org.jqassistant.contrib.plugin.kieker.test;

import com.buschmais.jqassistant.core.scanner.api.DefaultScope;
import com.buschmais.jqassistant.plugin.common.test.AbstractPluginIT;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class MeasurementIT extends AbstractPluginIT {

    @Test
    public void testCpuUtilizationMeasurement() {
        final String TEST_DIRECTORY_PATH = "src/test/resources/measurements";
        File directory = new File(TEST_DIRECTORY_PATH);
        store.beginTransaction();
        getScanner().scan(directory, TEST_DIRECTORY_PATH, DefaultScope.NONE);
        TestResult testResult = query("MATCH (r:Record)-[:CONTAINS]->(c:CpuUtilization)" +
            "RETURN c.timestamp AS Timestamp, c.cpuID AS CPU_ID, c.totalUtilization AS CPUUtilization");
        assertThat(testResult.getRows().size(), equalTo(1));
        assertThat(testResult.getColumn("Timestamp").get(0).toString(), equalTo("1593952723894319300"));
        assertThat(testResult.getColumn("CPU_ID").get(0).toString(), equalTo("0"));
        assertThat(testResult.getColumn("CPUUtilization").get(0).toString(), equalTo("0.124"));
    }

    @Test
    public void testDiskUsageMeasurement() {
        final String TEST_DIRECTORY_PATH = "src/test/resources/measurements";
        File directory = new File(TEST_DIRECTORY_PATH);
        store.beginTransaction();
        getScanner().scan(directory, TEST_DIRECTORY_PATH, DefaultScope.NONE);
        TestResult testResult = query("MATCH (r:Record)-[:CONTAINS]->(d:DiskUsage)" +
            "RETURN d.timestamp AS Timestamp, d.deviceName AS Device, d.readBytesPerSecond AS RBps");
        assertThat(testResult.getRows().size(), equalTo(1));
        assertThat(testResult.getColumn("Timestamp").get(0).toString(), equalTo("1593952728396520200"));
        assertThat(testResult.getColumn("Device").get(0).toString(), equalTo("C:\\\\"));
        assertThat(testResult.getColumn("RBps").get(0).toString(), equalTo("16384.0"));
    }

    @Test
    public void testLoadAverageMeasurement() {
        final String TEST_DIRECTORY_PATH = "src/test/resources/measurements";
        File directory = new File(TEST_DIRECTORY_PATH);
        store.beginTransaction();
        getScanner().scan(directory, TEST_DIRECTORY_PATH, DefaultScope.NONE);
        TestResult testResult = query("MATCH (r:Record)-[:CONTAINS]->(l:LoadAverage)" +
            "RETURN l.timestamp AS Timestamp, l.loadAverage1min AS Load");
        assertThat(testResult.getRows().size(), equalTo(1));
        assertThat(testResult.getColumn("Timestamp").get(0).toString(), equalTo("1593952728434977600"));
        assertThat(testResult.getColumn("Load").get(0).toString(), equalTo("0.05"));
    }

    @Test
    public void testMemSwapUsageMeasurement() {
        final String TEST_DIRECTORY_PATH = "src/test/resources/measurements";
        File directory = new File(TEST_DIRECTORY_PATH);
        store.beginTransaction();
        getScanner().scan(directory, TEST_DIRECTORY_PATH, DefaultScope.NONE);
        TestResult testResult = query("MATCH (r:Record)-[:CONTAINS]->(m:MemSwapUsage)" +
            "RETURN m.timestamp AS Timestamp, m.memTotal AS TotalMemory, m.swapUsed AS UsedSwap");
        assertThat(testResult.getRows().size(), equalTo(1));
        assertThat(testResult.getColumn("Timestamp").get(0).toString(), equalTo("1593952723915737300"));
        assertThat(testResult.getColumn("TotalMemory").get(0).toString(), equalTo("17129926656"));
        assertThat(testResult.getColumn("UsedSwap").get(0).toString(), equalTo("15588294656"));
    }

    @Test
    public void testNetworkUtilizationMeasurement() {
        final String TEST_DIRECTORY_PATH = "src/test/resources/measurements";
        File directory = new File(TEST_DIRECTORY_PATH);
        store.beginTransaction();
        getScanner().scan(directory, TEST_DIRECTORY_PATH, DefaultScope.NONE);
        TestResult testResult = query("MATCH (r:Record)-[:CONTAINS]->(n:NetworkUtilization)" +
            "RETURN n.timestamp AS Timestamp, n.interfaceName AS Interface, n.speed AS Speed");
        assertThat(testResult.getRows().size(), equalTo(1));
        assertThat(testResult.getColumn("Timestamp").get(0).toString(), equalTo("1593952728404677600"));
        assertThat(testResult.getColumn("Interface").get(0).toString(), equalTo("eth0"));
        assertThat(testResult.getColumn("Speed").get(0).toString(), equalTo("1.0E8"));
    }

}
