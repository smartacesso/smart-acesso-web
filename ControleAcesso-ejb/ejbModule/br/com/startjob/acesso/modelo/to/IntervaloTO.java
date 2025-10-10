package br.com.startjob.acesso.modelo.to;

import java.time.LocalTime;

public class IntervaloTO {
    private LocalTime inicio;
    private LocalTime fim;

    public IntervaloTO(LocalTime inicio, LocalTime fim) {
        this.inicio = inicio;
        this.fim = fim;
    }

    public LocalTime getInicio() { return inicio; }
    public LocalTime getFim() { return fim; }
}
