package com.example.aluracursos.literalura.principal;

import com.example.aluracursos.literalura.model.*;
import com.example.aluracursos.literalura.repositorio.AutorRepository;
import com.example.aluracursos.literalura.repositorio.LibroRepository;
import com.example.aluracursos.literalura.service.ConvierteDatos;
import com.example.aluracursos.literalura.service.LibroApiClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Principal {
    private static final String API_URL = "https://gutendex.com/books/";
    private static final String API_URL_TOPIC = "https://gutendex.com/books/?topic=";

    private Scanner teclado = new Scanner(System.in);
    private LibroApiClient consumoApi = new LibroApiClient();
    private ConvierteDatos conversor = new ConvierteDatos();
    private LibroRepository repositorio;
    private AutorRepository autorRepository;
    private int paginaActual = 0; // Variable para controlar la página actual
    private final int TAMAÑO_PAGINA = 10;

//    String[] posiblesSubjects = {
//            "Adventure",
//            "Fantasy",
//            "Historical Fiction",
//            "Science Fiction",
//            "Romance",
//            "Mystery",
//            "Horror",
//            "Children's Literature",
//            "Biography",
//            "Autobiography",
//            "Poetry",
//            "Philosophy",
//            "Drama",
//            "Non-fiction",
//            "Literary Criticism",
//            "Classics",
//            "Religion",
//            "Art",
//            "Politics",
//            "Economics",
//            "Psychology",
//            "Law",
//            "Technology",
//            "Music",
//            "Travel",
//            "Education",
//            "Health",
//            "Self-Help"
//    };


    public Principal(LibroRepository repository, AutorRepository autorRepository) {
        this.repositorio = repository;
        this.autorRepository = autorRepository;
    }

    public void muestraElMenu() throws JsonProcessingException {
        var opcion = -1;
        while (opcion != 0) {
            var menu = """
                    1 - Buscar libros 
                    2 - Mostrar Todos Los Libros
                    3 - Mostrar Todos Los Autores
                    4 - Buscar Autores Vivos Segun Año
                    
                    0 - Salir
                    """;
            System.out.println(menu);
            opcion = teclado.nextInt();
            teclado.nextLine(); // Limpiar buffer de entrada

            switch (opcion) {
                case 1:
                    obtenerLibrosWeb();
                    break;
                case 2:
                    mostrarTodosLosLibros();
                    break;
                case 3:
                    mostrarTodosLosAutores();
                    break;
                case 4:
                    mostrarAutoresVivosSegunAnio();
                    break;
                case 0:
                    System.out.println("Cerrando la aplicación...");
                    break;
                default:
                    System.out.println("Opción inválida");
            }
        }
    }

    private String obtenerJsonLibro(String nombreLibro) {
        return consumoApi.obtenerDatos(API_URL + "?search=" + nombreLibro);
    }

    private Resultados deserializarResultados(String json) throws JsonProcessingException {
        // Usar el método obtenerDatos de ConvierteDatos para deserializar
        return conversor.obtenerDatos(json, Resultados.class);
    }

    private DatosLibro obtenerLibrosWeb() throws JsonProcessingException {
        // Solicitar el título del libro al usuario
        System.out.println("Ingresa el titulo del libro que deseas buscar: ");
        var nombreLibro = teclado.nextLine();

        // Reemplazar los espacios con %20 para formato de URL
        nombreLibro = nombreLibro.replace(" ", "%20");

        // Obtener el JSON del libro desde la API
        String json = obtenerJsonLibro(nombreLibro);
        System.out.println(json);

        // Deserializar los resultados de la API usando deserializarResultados
        Resultados resultadoApi = deserializarResultados(json);
        System.out.println("Resultados: " + resultadoApi);

        // Verificar si hay resultados disponibles
        if (resultadoApi != null && !resultadoApi.results().isEmpty()) {
            resultadoApi.results().forEach(datosLibro -> {
                // Procesar cada DatosLibro
                System.out.println("DatosLibro: " + datosLibro);

                // Obtener los autores del libro
                Autor autorGuardado = obtenerAutorGuardado(datosLibro);

                // Procesar o crear el libro
                procesarLibro(datosLibro, autorGuardado);
            });
        } else {
            System.out.println("No se encontraron resultados.");
        }

        return null;  // Retorna null si no se requiere un objeto DatosLibro
    }

    // Método para obtener los autores y guardarlos en la base de datos
    private Autor obtenerAutorGuardado(DatosLibro datosLibro) {
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
        } else {
            System.out.println("No se encontraron autores.");
        }

        return autorGuardado;
    }


    // Método para procesar el libro (crear o actualizar)
    @Transactional
    public void procesarLibro(DatosLibro datosLibro, Autor autorGuardado) {
        try {
            // Buscar si el libro ya existe en la base de datos
            Optional<Libro> libroOptional = repositorio.findByTitulo(datosLibro.titulo());

            if (libroOptional.isPresent()) {
                // Si el libro ya existe, obtenerlo y mostrar sus datos
                Libro libro = libroOptional.get();  // Obtener el libro desde el Optional
                libro.mostrarInformacionLibro();

            } else {
                // Si el libro no existe, crear uno nuevo con los datos
                if (autorGuardado != null) {
                    Libro nuevoLibro = new Libro(datosLibro, autorGuardado);
                    repositorio.save(nuevoLibro);
                    System.out.println("Nuevo libro creado: " + nuevoLibro);  // Aquí también usamos toString()

                }
            }

        } catch (DataIntegrityViolationException e) {
            // Captura de errores de integridad de datos (ej. claves duplicadas, valores nulos en campos requeridos)
            System.out.println("Error de integridad de datos: " + e.getMessage());
        } catch (Exception e) {
            // Captura de cualquier otra excepción general
            System.out.println("Error al procesar el libro: " + e.getMessage());
        }
    }


//    public void buscarTemas(String topic) throws JsonProcessingException {
//        Set<String> subjects = new HashSet<>();
//        String url = API_URL_TOPIC + topic;
//
//        while (url != null) {
//            // Hacer la solicitud a la API para obtener los libros con el tema especificado
//            String jsonResponse = consumoApi.obtenerDatos(url);
//
//            // Deserializar la respuesta JSON
//            Resultados resultados = deserializarResultados(jsonResponse);
//
//            // Procesar los libros en los resultados
//            if (resultados != null && !resultados.results().isEmpty()) {
//                for (DatosLibro datosLibro : resultados.results()) {
//                    // Agregar los subjects de cada libro a la lista (sin duplicados)
//                    if (datosLibro.tema() != null) {
//                        subjects.addAll(datosLibro.tema());
//                    }
//                }
//            }
//
//            // Verificar si hay más páginas
//            assert resultados != null;
//            url = resultados.next(); // No es necesario 'resultados.next' como método
//        }
//
//        // Mostrar los subjects encontrados
//        if (!subjects.isEmpty()) {
//            System.out.println("Los temas disponibles para el topic '" + topic + "' son:");
//            for (String subject : subjects) {
//                System.out.println(subject);
//            }
//        } else {
//            System.out.println("No se encontraron temas para el topic '" + topic + "'.");
//        }
//    }

    public void mostrarTodosLosLibros() {
        boolean navegar = true;
        while (navegar) {
            // Obtener los libros paginados para la página actual
            Page<Libro> librosPaginados = obtenerLibrosPaginados(PageRequest.of(paginaActual, TAMAÑO_PAGINA));

            // Mostrar los libros obtenidos
            if (librosPaginados != null && !librosPaginados.isEmpty()) {
                System.out.println("Libros en la página " + (paginaActual + 1) + ":");
                librosPaginados.forEach(libro -> System.out.println(libro.getTitulo()));
            } else {
                System.out.println("No hay libros disponibles.");
            }

            // Opciones para navegar
            System.out.println("\nOpciones:");
            System.out.println("1 - Siguiente página");
            System.out.println("2 - Página anterior");
            System.out.println("0 - Salir de la navegación");

            // Obtener la opción del usuario
            int opcionNavegacion = teclado.nextInt();
            teclado.nextLine(); // Limpiar buffer

            switch (opcionNavegacion) {
                case 1: // Ir a la siguiente página
                    if (librosPaginados.hasNext()) {
                        paginaActual++;
                    } else {
                        System.out.println("Ya estás en la última página.");
                    }
                    break;
                case 2: // Volver a la página anterior
                    if (paginaActual > 0) {
                        paginaActual--;
                    } else {
                        System.out.println("Ya estás en la primera página.");
                    }
                    break;
                case 0: // Salir
                    navegar = false;
                    break;
                default:
                    System.out.println("Opción inválida.");
            }
        }
    }

    public Page<Libro> obtenerLibrosPaginados(Pageable pageable) {
        return repositorio.findAll(pageable);
    }

    public void mostrarTodosLosAutores() {
        List<Autor> autores = obtenerTodosLosAutores();
        System.out.println("Lista de Todos Los Autores: ");
        autores.forEach(autor -> System.out.println("------- " + autor.getNombre()));
    }

    public void mostrarAutoresVivosSegunAnio() {
        System.out.println("Ingresa el año de busqueda: ");
        var anioBusqueda = teclado.nextInt();
        List<Autor> autores = obtenerAutoresVivosEnAnio(anioBusqueda);
        System.out.println("Autores vivos al año " + anioBusqueda + ": ");
        autores.forEach(autor -> System.out.println("------- " + autor.getNombre()));
    }

    public List<Autor> obtenerAutoresVivosEnAnio(int anioBusqueda) {
        return autorRepository.findAutoresVivosEnAnio(anioBusqueda);
    }

    public List<Autor> obtenerTodosLosAutores() {
        return autorRepository.findAll();
    }


}