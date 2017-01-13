package com.text2net.console.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Created by p_8220 on 12/01/2017.
 */
@Setter
@Getter
@AllArgsConstructor
public class Conexao {
    private Logon elementA;
    private Logon elementB;
    private LocalDate data;
    private int tempo;
    private String laboratorio;
}
