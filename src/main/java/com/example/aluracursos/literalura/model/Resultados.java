package com.example.aluracursos.literalura.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Resultados(
        @JsonAlias("results") List<DatosLibro> results,
        @JsonAlias("count")int count,
        @JsonAlias("next")String next,
        @JsonAlias("previous")String previous
) {

}
