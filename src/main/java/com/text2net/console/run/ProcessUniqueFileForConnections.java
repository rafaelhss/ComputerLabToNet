package com.text2net.console.run;


import com.text2net.console.generator.PajekFileGenerator;
import com.text2net.console.model.Computador;
import com.text2net.console.model.Conexao;
import com.text2net.console.model.Logon;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by rafa on 03/04/2016.
 */
@Component
public class ProcessUniqueFileForConnections {

     public static void main(String[] args) {
        System.out.println("ini");


        String filePath = "D:\\Projetos\\ComputerLabToNet\\LogsUnique\\input.txt";
        System.out.println("*************************************************");
         System.out.println("Processando arquivo: " + filePath);
         System.out.println("");
         System.out.println("");
        ApplicationContext context =
                new ClassPathXmlApplicationContext(new String[] {"spring\\app-config.xml"});

        ProcessUniqueFileForConnections main = context.getBean(ProcessUniqueFileForConnections.class);


        long startTime = System.currentTimeMillis();



         System.out.println("###x"+ new Date() + "x### Extraindo conexoes de :" + filePath);

        List<Conexao> conexoes = new ArrayList<>();

       conexoes.addAll(main.getConnections(Paths.get(filePath)));

        new PajekFileGenerator().generate(conexoes);

        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.out.println("fim. elapsed time em milissegundos : " +  elapsedTime);
    }

    private List<Conexao> getConnections(Path fpath) {

        List<Conexao> conexoes = new ArrayList<>();

        try {
            HashMap<String, Computador> computadores = carregaComputadores(fpath);
            computadores.forEach(
                (nome, computador) -> {
                    System.out.println("Processando computador: " + nome);
                    System.out.println("Descobre o computador a direita de " + nome);
                    Computador vizinho = carregaVizinho(computadores, computador);
                    if(vizinho != null){
                        System.out.println("Para cada login em " + nome + ", verifica se há um login em " + vizinho.getNome() + " no mesmo periodo de aula.");
                        computador.getLogons()
                            .forEach(logon -> {
                                vizinho.getLogons()
                                        .forEach(lvizinho -> {
                                            if(verificaPeriodo(logon.getDatahora(), lvizinho.getDatahora())) { // Conexao identificada
                                                conexoes.add(new Conexao(logon, lvizinho, null, 0 , ""));
                                                System.out.println("Conexao gerada: "
                                                        + logon.getUsername() + " "
                                                        + lvizinho.getUsername() + " "
                                                        + logon.getDatahora() + " "
                                                        + lvizinho.getDatahora() + "("
                                                        + logon.getNomeDoComputador() + " "
                                                        + lvizinho.getNomeDoComputador() + ")");
                                            }
                                        });
                            });
                    }
                }
            );
        }
        catch (Throwable e) {
            e.printStackTrace();
        }

        return conexoes;
    }

    protected HashMap<String,Computador> carregaComputadores(Path fpath) {
        HashMap<String,Computador> computadores = new HashMap<>();

        Pattern patternNome = Pattern.compile("Nome do Computador:");
        Pattern patternLogin = Pattern.compile("^[0-9]{1,4}-[0-9]{1,2}-[0-9]{1,2}\\s[0-9]{1,2}:[0-9]{1,2}:[0-9]{1,2}");


        try {
            Computador currentComputador = null;
            List<String> lines = Files.readAllLines(fpath, Charset.forName("ISO-8859-1"));
            for (String line : lines){
                Matcher matcherNome = patternNome.matcher(line);
                if(matcherNome.find()) {
                    String nome = line.substring(matcherNome.end()).trim();
                    Computador computador = computadores.get(nome);
                    if(computador == null) {
                        computador = new Computador();
                        computador.setNome(nome);
                        computador.setLogons(new ArrayList<>());
                        computadores.put(nome, computador);
                    }
                    currentComputador = computador;
                }
                else {
                    Matcher matcherLogin = patternLogin.matcher(line);
                    if (matcherLogin.find()) {
                        String dataLimpa = matcherLogin.group().substring(0,matcherLogin.end());

                        String LOGIN = "login:";
                        String aux = line.substring(line.toLowerCase().indexOf(LOGIN)+ LOGIN.length());

                        String username = aux.substring(0, aux.toLowerCase().indexOf("no endere")).trim();

                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        LocalDateTime dateTime = LocalDateTime.parse(dataLimpa, formatter);

                        Logon novo = new Logon(username, dateTime, fpath.getFileName().toString());
                        currentComputador.getLogons().add(novo);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return computadores;
    }

    protected boolean verificaPeriodo(LocalDateTime datahora1, LocalDateTime datahora2) {
        if(Math.abs(datahora1.until(datahora2, ChronoUnit.DAYS)) == 0 ) //Mesmo dia
        {
            if(getTempo(datahora1) == getTempo(datahora2)){ // mesmo tempo
                return true;
            }
        }
        return false;
    }

    protected int getTempo(LocalDateTime datahora) {
        LocalDateTime inicioPrimeiroTempoManha = LocalDateTime.of(datahora.getYear(), datahora.getMonth(), datahora.getDayOfMonth(), 7, 10, 0);
        LocalDateTime fimPrimeiroTempoManha = LocalDateTime.of(datahora.getYear(), datahora.getMonth(), datahora.getDayOfMonth(), 8, 50, 0);

        LocalDateTime inicioSegundoTempoManha = LocalDateTime.of(datahora.getYear(), datahora.getMonth(), datahora.getDayOfMonth(), 9, 0, 0);
        LocalDateTime fimSegundoTempoManha = LocalDateTime.of(datahora.getYear(), datahora.getMonth(), datahora.getDayOfMonth(), 10, 40, 0);


        LocalDateTime inicioPrimeiroTempoTarde = LocalDateTime.of(datahora.getYear(), datahora.getMonth(), datahora.getDayOfMonth(), 13, 0, 0);
        LocalDateTime fimPrimeiroTempoTarde = LocalDateTime.of(datahora.getYear(), datahora.getMonth(), datahora.getDayOfMonth(), 14, 40, 0);

        LocalDateTime inicioSegundoTempoTarde = LocalDateTime.of(datahora.getYear(), datahora.getMonth(), datahora.getDayOfMonth(), 15, 0, 0);
        LocalDateTime fimSegundoTempoTarde = LocalDateTime.of(datahora.getYear(), datahora.getMonth(), datahora.getDayOfMonth(), 16, 40, 0);


        LocalDateTime inicioPrimeiroTempoNoite = LocalDateTime.of(datahora.getYear(), datahora.getMonth(), datahora.getDayOfMonth(), 19, 10, 0);
        LocalDateTime fimPrimeiroTempoNoite = LocalDateTime.of(datahora.getYear(), datahora.getMonth(), datahora.getDayOfMonth(), 20, 50, 0);

        LocalDateTime inicioSegundoTempoNoite = LocalDateTime.of(datahora.getYear(), datahora.getMonth(), datahora.getDayOfMonth(), 21, 0, 0);
        LocalDateTime fimSegundoTempoNoite = LocalDateTime.of(datahora.getYear(), datahora.getMonth(), datahora.getDayOfMonth(), 22, 40, 0);

        if(datahora.isAfter(inicioPrimeiroTempoManha) && datahora.isBefore(fimPrimeiroTempoManha)){
            return 1;
        }

        if(datahora.isAfter(inicioSegundoTempoManha) && datahora.isBefore(fimSegundoTempoManha)){
            return 2;
        }

        if(datahora.isAfter(inicioPrimeiroTempoTarde) && datahora.isBefore(fimPrimeiroTempoTarde)){
            return 3;
        }

        if(datahora.isAfter(inicioSegundoTempoTarde) && datahora.isBefore(fimSegundoTempoTarde)){
            return 4;
        }

        if(datahora.isAfter(inicioPrimeiroTempoNoite) && datahora.isBefore(fimPrimeiroTempoNoite)){
            return 5;
        }

        if(datahora.isAfter(inicioSegundoTempoNoite) && datahora.isBefore(fimSegundoTempoNoite)){
            return 6;
        }

        return -1;


    }

    protected Computador carregaVizinho(HashMap<String, Computador> computadores, Computador computador) {

        try {
            String nomeDoComputador = computador.getNome();
            String prefixo = nomeDoComputador.substring(0, nomeDoComputador.indexOf("-"));
            String numero = nomeDoComputador.substring(nomeDoComputador.indexOf("-")+1, nomeDoComputador.lastIndexOf("-"));
            String posfixo = nomeDoComputador.substring(nomeDoComputador.lastIndexOf("-"));

            String candidatoVizinho = prefixo + "-" + (Integer.parseInt(numero)+1)  + posfixo;

            System.out.println("prefixo:" + prefixo + " numero:" + numero + " posfixo:" + posfixo);

            System.out.println("nome: " + nomeDoComputador + "   candidatoVizinho: " + candidatoVizinho);

            if (computadores.get(candidatoVizinho) != null) {
                System.out.println("nome: " + nomeDoComputador + "   vizinho encontrado: " + candidatoVizinho);
                return computadores.get(candidatoVizinho);
            }
        }
        catch (Exception e)  {
            e.printStackTrace();
        }
        System.out.println("Nenhum vizinho encontrado para: " + computador.getNome());
        return null;
    }
}
