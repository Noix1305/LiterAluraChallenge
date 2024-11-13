package com.example.aluracursos.literalura.service;

import com.example.aluracursos.literalura.model.Autor;
import com.example.aluracursos.literalura.model.DatosLibro;
import com.example.aluracursos.literalura.model.Libro;
import com.example.aluracursos.literalura.model.Resultados;
import com.example.aluracursos.literalura.repositorio.AutorRepository;
import com.example.aluracursos.literalura.repositorio.LibroRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class LibroService {
    @Autowired
    private LibroRepository libroRepository;
    @Autowired
    private AutorService autorService;

    private static final String API_URL = "https://gutendex.com/books/";

    private final LibroApiClient consumoApi = new LibroApiClient();
    private final ConvierteDatos conversor = new ConvierteDatos();
    private final Scanner teclado = new Scanner(System.in);


    public List<Libro> mostrarLibrosPorIdioma(String idioma) {
        return libroRepository.findByIdioma(idioma);
        //Retorna una lista de libros filtrada por idioma
    }

    public String obtenerJsonLibro(String nombreLibro) {
        return consumoApi.obtenerDatos(API_URL + "?search=" + nombreLibro);
    }

    public Resultados deserializarResultados(String json) throws JsonProcessingException {
        // Usar el método obtenerDatos de ConvierteDatos para deserializar
        return conversor.obtenerDatos(json, Resultados.class);
    }

    public Page<Libro> obtenerLibrosPaginados(Pageable pageable) {
        return libroRepository.findAll(pageable);
    }

    public List<Libro> obtenerTop10LibrosMasDescargados() {
        return libroRepository.findTop10ByOrderByNumeroDescargasDesc();
    }

    public LibroRepository getLibroRepository() {
        return libroRepository;
    }

    public void setLibroRepository(LibroRepository libroRepository) {
        this.libroRepository = libroRepository;
    }

    public AutorService getAutorService() {
        return autorService;
    }

    public void setAutorService(AutorService autorService) {
        this.autorService = autorService;
    }

    public LibroApiClient getConsumoApi() {
        return consumoApi;
    }

    public ConvierteDatos getConversor() {
        return conversor;
    }

    public Scanner getTeclado() {
        return teclado;
    }
}
