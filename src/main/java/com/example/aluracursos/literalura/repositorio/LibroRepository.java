package com.example.aluracursos.literalura.repositorio;

import com.example.aluracursos.literalura.model.DatosLibro;
import com.example.aluracursos.literalura.model.Libro;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;

public interface LibroRepository extends JpaRepository<Libro, Long>{
    Optional<Libro> findByTitulo(String nombreLibro);

}
