package com.example.aluracursos.literalura.repositorio;

import com.example.aluracursos.literalura.model.DatosLibro;
import com.example.aluracursos.literalura.model.Libro;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;
import java.util.Optional;

public interface LibroRepository extends JpaRepository<Libro, Long> {
    Optional<Libro> findByTitulo(String nombreLibro);

    // Método para obtener todos los libros por idioma
    List<Libro> findByIdioma(String idioma);

    List<Libro> findTop10ByOrderByNumeroDescargasDesc();

}
