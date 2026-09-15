package br.com.fiap.aguiaradar.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OrientacaoEstrategicaRequest {
    @NotBlank
    private String categoria;

    @NotBlank
    private String campanha;

    private String descricao;

    private Boolean ativo;
}
