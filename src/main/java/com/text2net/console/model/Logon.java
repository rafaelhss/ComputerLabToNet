package com.text2net.console.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * Created by p_8220 on 11/01/2017.
 */
@Setter
@Getter
@AllArgsConstructor
public class Logon {
    private String username;
    private LocalDateTime datahora;
    private String nomeDoComputador;
}
