package com.text2net.console.run;

import com.text2net.console.model.Computador;
import com.text2net.console.model.Logon;
import org.junit.Test;

import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.*;

/**
 * Created by p_8220 on 11/01/2017.
 */
public class ProcessFilesForConnectionsTest {

    @Test
    public void carregaComputadoresTest() throws Exception {
        ProcessUniqueFileForConnections processUniqueFileForConnections = new ProcessUniqueFileForConnections();
        String filepath = "D:\\Projetos\\ComputerLabToNet\\LogsUnique\\input.txt";
        HashMap<String, Computador> computadores = processUniqueFileForConnections.carregaComputadores(Paths.get(filepath));
        assertEquals(computadores.get("1001-01").getLogons().get(0).getUsername(), "ecarmo");
        assertEquals(computadores.get("8002-17-057218").getLogons().get(0).getUsername(), "ra51500234");
    }

    @Test
    public void getTempoTest() throws Exception {
        ProcessUniqueFileForConnections processFilesForConnections = new ProcessUniqueFileForConnections();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse("2016-08-01 07:13:19", formatter);

        int tempo = processFilesForConnections.getTempo(dateTime);

        assertEquals(1,tempo);
    }

    @Test
    public void verificaPeriodoTest() throws Exception {
        ProcessUniqueFileForConnections processFilesForConnections = new ProcessUniqueFileForConnections();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime data1 = LocalDateTime.parse("2016-08-01 07:13:19", formatter);
        LocalDateTime data2 = LocalDateTime.parse("2016-08-01 08:13:19", formatter);
        LocalDateTime data3 = LocalDateTime.parse("2016-08-02 08:13:19", formatter);

        assertTrue(processFilesForConnections.verificaPeriodo(data1, data2));
        assertFalse(processFilesForConnections.verificaPeriodo(data1, data3));

    }

}