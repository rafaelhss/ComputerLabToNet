package com.text2net.console.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by p_8220 on 13/01/2017.
 */
@Setter
@Getter
public class Computador {
    private String nome;
    private List<Logon> logons;
}
