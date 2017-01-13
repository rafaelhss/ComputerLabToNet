package com.text2net.console.generator;

import com.text2net.console.model.Conexao;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.list.SetUniqueList;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;

/**
 * Created by p_8220 on 12/01/2017.
 */

public class PajekFileGenerator {
    List<String> vertices = SetUniqueList.decorate(new ArrayList<String>());
    List<LocalDate> datas = SetUniqueList.decorate(new ArrayList<Date>());
    ArrayList<PajekFileGenerator.Conn> edges = new ArrayList<>();

    public void generate(List<Conexao> conexoes){

        System.out.println("Gerando Rede");
        conexoes.forEach(this::geraConexao);


        StringBuilder net = new StringBuilder();
        net.append("*Vertices " + vertices.size());
        vertices.stream().forEach(v -> net.append(System.lineSeparator() + (vertices.indexOf(v)+1) + " " + v));

        net.append(System.lineSeparator());
        net.append("*Edges");


        ArrayList<LocalDate> datasSort = new ArrayList<>(datas);
        datasSort.forEach(System.out::println);
        //Sorting
        Collections.sort(datasSort, (lhs, rhs) -> {
            if (lhs.isBefore(rhs))
                return -1;
            else if (lhs.isAfter(rhs))
                return 1;
            else
                return 0;
        });
     /*   edges.stream().forEach(v -> net.append(System.lineSeparator() + v.getA() + " " + v.getB() + " "
                + (datasSort.indexOf(v.getDateTime()) + 1) +  "  (" + v.getDateTime()+ ")") );
*/
        edges.stream().forEach(v -> net.append(System.lineSeparator() + v.getA() + " " + v.getB() + " 1 ["
                + (datasSort.indexOf(v.getDateTime()) + 1) + "]") );
        //System.out.println(net.toString());
        try {
            Path p = Paths.get("D:\\Projetos\\ComputerLabToNet\\Network.net");
            Files.write(p, net.toString().getBytes());
            System.out.println("Rede gerada com sucesso: " + p.toString() );
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void geraConexao(Conexao registro) {




        System.out.print(".");
        try {
            String elementA = registro.getElementA().getUsername();

            String elementB = registro.getElementB().getUsername();

            elementA = "\"" +elementA.trim().toLowerCase() + "\"";
            elementB = "\"" +elementB.trim().toLowerCase() + "\"";

            vertices.add(elementA);
            vertices.add(elementB);


            LocalDate data = registro.getData();//sdf.parse(dataStr);
            edges.add(new PajekFileGenerator.Conn(vertices.indexOf(elementA) + 1, vertices.indexOf(elementB) + 1, data));
            datas.add(data);

        }
        catch (Exception e){
            e.printStackTrace();
            System.out.println("Registro invalido:" + registro);
        }

    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Conn {
        private int a;
        private int b;
        private LocalDate dateTime;

    }


}
