package com.example.aluracursos.literalura.service;

import com.example.aluracursos.literalura.model.Autor;
import com.example.aluracursos.literalura.model.DatosLibro;
import com.example.aluracursos.literalura.repositorio.AutorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

@Service
public class AutorService {
    @Autowired
    private AutorRepository autorRepository;
    private Scanner teclado = new Scanner(System.in);

    public Autor obtenerAutorGuardado(DatosLibro datosLibro) {
        // Inicializar el autor como null
        Autor autorGuardado = null;

        // Verificar si hay autores en los datos del libro
        if (datosLibro.autores() != null && !datosLibro.autores().isEmpty()) {
            // Tomamos el primer autor de la lista de autores
            String nombreAutor = datosLibro.autores().get(0).nombre();

            // Verificar si el autor ya existe en la base de datos
            Optional<Autor> autorOptional = autorRepository.findByNombre(nombreAutor);

            if (autorOptional.isPresent()) {
                // Si el autor ya existe, lo obtenemos de la base de datos
                autorGuardado = autorOptional.get();  // Obtener el autor dentro del Optional
            } else {
                // Si el autor no existe, creamos un nuevo autor y lo guardamos
                autorGuardado = new Autor(datosLibro.autores().get(0));
                autorRepository.save(autorGuardado);
            }
        }

        return autorGuardado;
    }

    public List<Autor> obtenerAutoresVivosEnAnio(int anioBusqueda) {
        return autorRepository.findAutoresVivosEnAnio(anioBusqueda);
    }

    public List<Autor> obtenerTodosLosAutores() {
        return autorRepository.findAll();
    }

    public Optional<Autor> buscarAutorPorNombre(String nombre) {
        return autorRepository.findByNombreAproximado(nombre);
    }

    public List<Autor> listarAutoresPorIntervaloDeNacimiento(int anoInicio, int anoFin) {
        return autorRepository.findByAnoNacimientoBetween(anoInicio, anoFin);
    }
}
