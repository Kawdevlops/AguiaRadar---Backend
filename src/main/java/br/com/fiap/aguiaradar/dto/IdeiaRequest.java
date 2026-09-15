package br.com.fiap.aguiaradar.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class IdeiaRequest {
    @NotBlank
    private String titulo;

    @NotBlank
    private String descricao;

    private String categoria;

    private String orientacaoEstrategicaId;
}
