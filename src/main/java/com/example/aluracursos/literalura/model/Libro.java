package com.example.aluracursos.literalura.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Libro(
        @JsonAlias("tittle") String titulo,
        @JsonAlias("languages") String idiomas,
        @JsonAlias("tittle") String ,
        @JsonAlias("tittle") String 

) {
}
