package br.com.fiap.aguiaradar.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardPorEstrategia {
    private String orientacaoEstrategicaId;
    private String categoria;
    private String campanha;
    private long totalProjetos;
    private double investimentoTotal;
    private double retornoTotal;
    private double roiMedioPercentual;
}
