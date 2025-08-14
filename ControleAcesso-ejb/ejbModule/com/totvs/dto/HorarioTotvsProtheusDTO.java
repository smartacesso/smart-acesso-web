package com.totvs.dto;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class HorarioTotvsProtheusDTO {
    private Date horaInicio;
    private Date horaFim;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final SimpleDateFormat SDF = new SimpleDateFormat("HH:mm");

    public HorarioTotvsProtheusDTO(String horarioInicio, String horarioFim, int toleranciaMinutos) {
        boolean inicioVazio = horarioInicio != null && horarioInicio.trim().isEmpty();
        boolean fimVazio = horarioFim != null && horarioFim.trim().isEmpty();

        LocalTime inicio = parseHora(horarioInicio);
        LocalTime fim = parseHora(horarioFim);

        // Se veio vazia → dia todo liberado
        if (inicioVazio) {
            inicio = LocalTime.MIN; // 00:00
        }
        if (fimVazio) {
            fim = LocalTime.of(23, 59); // 23:59
        }

        // Se veio "0" → marcar como 00:00 às 00:00
        if ("0".equals(horarioInicio)) {
            inicio = LocalTime.MIN; // 00:00
        }
        if ("0".equals(horarioFim)) {
            fim = LocalTime.MIN.plusMinutes(1); // 00:00
        }

        // Aplica tolerância somente se horários válidos e não dia todo ou zeros
        if (inicio != null && fim != null && !inicioVazio && !fimVazio && !"0".equals(horarioInicio) && !"0".equals(horarioFim)) {
            inicio = inicio.minusMinutes(toleranciaMinutos);
            fim = fim.plusMinutes(toleranciaMinutos);
        }

        this.horaInicio = inicio != null ? converterHorarioParaDate(inicio.format(FORMATTER)) : null;
        this.horaFim = fim != null ? converterHorarioParaDate(fim.format(FORMATTER)) : null;
    }

    private LocalTime parseHora(String hora) {
        if (hora == null || hora.trim().isEmpty()) {
            return null;
        }

        hora = hora.trim().replace('.', ':');

        // Se for "0" → será tratado no construtor
        if ("0".equals(hora)) {
            return LocalTime.MIN; // 00:00
        }

        if (!hora.contains(":")) {
            hora = String.format("%02d:00", Integer.parseInt(hora));
        } else {
            String[] partes = hora.split(":");
            String h = String.format("%02d", Integer.parseInt(partes[0]));
            String m = "00";
            if (partes.length > 1 && !partes[1].isEmpty()) {
                m = String.format("%02d", Integer.parseInt(partes[1]));
            }
            hora = h + ":" + m;
        }

        return LocalTime.parse(hora, FORMATTER);
    }


    public Date getHoraInicio() {
        return horaInicio;
    }

    public Date getHoraFim() {
        return horaFim;
    }

    private Date converterHorarioParaDate(String hora) {
        try {
            return SDF.parse(hora);
        } catch (ParseException e) {
            // Ideal: trocar por log
            e.printStackTrace();
            return null;
        }
    }
}
