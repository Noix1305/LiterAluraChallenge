package com.example.aluracursos.literalura.repositorio;

import com.example.aluracursos.literalura.model.Autor;
import com.example.aluracursos.literalura.model.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AutorRepository extends JpaRepository<Autor, Long> {
    // Método para obtener todos los autores
    List<Autor> findAll();

    @Query("SELECT a FROM Autor a " +
            "WHERE LOWER(TRANSLATE(REPLACE(a.nombre, ' ', ''), 'áéíóúÁÉÍÓÚ', 'aeiouaeiou')) " +
            "LIKE LOWER(CONCAT('%', TRANSLATE(REPLACE(:nombre, ' ', ''), 'áéíóúÁÉÍÓÚ', 'aeiouaeiou'), '%')) " +
            "ORDER BY LENGTH(a.nombre) ASC")
    List<Autor> findByNombreAproximado(@Param("nombre") String nombre);


    @Query("SELECT a FROM Autor a WHERE a.nombre = :nombre")
    Optional<Autor> findByNombre(@Param("nombre") String nombre);

    // Método para obtener autores vivos en un determinado año
    @Query("SELECT a FROM Autor a WHERE a.anoNacimiento <= :anio AND (a.anoMuerte >= :anio)")
    List<Autor> findAutoresVivosEnAnio(@Param("anio") int anio);


    List<Autor> findByAnoNacimientoBetween(int anoInicio, int anoFin);
}


