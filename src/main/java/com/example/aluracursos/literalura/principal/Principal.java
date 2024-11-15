package com.example.aluracursos.literalura.principal;

import com.example.aluracursos.literalura.model.*;
import com.example.aluracursos.literalura.repositorio.AutorRepository;
import com.example.aluracursos.literalura.repositorio.LibroRepository;
import com.example.aluracursos.literalura.service.AutorService;
import com.example.aluracursos.literalura.service.ConvierteDatos;
import com.example.aluracursos.literalura.service.LibroApiClient;
import com.example.aluracursos.literalura.service.LibroService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Principal {
    private Scanner teclado = new Scanner(System.in);
    private LibroService libroService;
    private AutorService autorService;

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


    public Principal(LibroService libroService, AutorService autorService) {
        this.libroService = libroService;
        this.autorService = autorService;
    }

    public void muestraElMenu() throws JsonProcessingException {
        var opcion = -1;
        while (opcion != 0) {
            var menu = """
                    
                    Bienvenido a 'Busca Libros' tu Biblioteca Virtual!
                    
                    1 - Buscar libros por Título
                    2 - Mostrar Todos Los Libros
                    3 - Mostrar Todos Los Autores
                    4 - Buscar Autores Vivos Segun Año
                    5 - Buscar libros por Idioma
                    6 - Extras
                    
                    
                    0 - Salir
                    """;


            opcion = obtenerEntradaEntero(menu);

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
                case 5:
                    buscarLibrosPorIdioma();
                    break;
                case 6:
                    extras();
                    break;
                case 0:
                    System.out.println("Cerrando la aplicación...");
                    break;
                default:
                    System.out.println("Opción inválida");
            }
        }
    }

    private void extras() {
        var opcion = -1;
        while (opcion != 0) {
            var menu3 = """
                    Extras
                    
                    Selecciona la opción:
                    
                    1 - Buscar Autores por Nombre
                    2 - Buscar Autor por rangos de año
                    3 - Listar los 10 libros mas descargados hasta la fecha
                    
                    
                    0 - Volver
                    """;
            opcion = obtenerEntradaEntero(menu3);

            teclado.nextLine();

            switch (opcion) {
                case 1:
                    buscarAutoresPorNombre();
                    opcion = 0;
                    break;
                case 2:
                    buscarAutorPorRangosAnio();
                    opcion = 0;
                    break;
                case 3:
                    listarTop10LibrosDescargados();
                    opcion = 0;
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Opción inválida");
            }

        }
    }

    private void listarTop10LibrosDescargados() {
        List<Libro> top10Libros = libroService.obtenerTop10LibrosMasDescargados();

        if (top10Libros.isEmpty()) {
            System.out.println("No se encontraron libros.");
        } else {
            System.out.println("Top 10 libros más descargados:");
            int i = 1;
            for (Libro libro : top10Libros) {
                String titulo = libro.getTitulo().length() > 80
                        ? libro.getTitulo().substring(0, 80) + "..."
                        : libro.getTitulo(); // Limitar a 80 caracteres
                System.out.println(i + ". Título: " + titulo + ", Descargas: " + libro.getNumeroDescargas());
                i++;
            }
        }
    }


    private void buscarAutorPorRangosAnio() {
        boolean intervaloValido = false;
        int anioInicio = 0;
        int anioFin = 0;

        while (!intervaloValido) {
            anioInicio = obtenerEntradaEntero("Ingresa el año de inicio del intervalo:");
            teclado.nextLine();
            anioFin = obtenerEntradaEntero("Ingresa el año de fin del intervalo:");
            teclado.nextLine(); // Limpiar buffer

            // Validación para verificar que el año de inicio sea menor que el de fin
            if (anioInicio < anioFin) {
                intervaloValido = true; // Cambiamos el flag a true si los años son válidos
            } else {
                System.out.println("Error: El año de inicio debe ser menor que el año de fin. Inténtalo nuevamente.");
            }
        }

        final int finalAnioInicio = anioInicio; // Hacemos la variable final
        final int finalAnioFin = anioFin; // Hacemos la variable final

        List<Autor> autores = autorService.listarAutoresPorIntervaloDeNacimiento(finalAnioInicio, finalAnioFin);

        if (autores.isEmpty()) {
            System.out.println("No se encontraron autores nacidos entre los años " + finalAnioInicio + " y " + finalAnioFin + ".");
        } else {
            System.out.println("Autores nacidos entre " + finalAnioInicio + " y " + finalAnioFin + ":");
            autores.forEach(autor -> {
                System.out.println("Nombre: " + autor.getNombre());
                System.out.println("Año de Nacimiento: " + autor.getAnoNacimiento());
                if (autor.getAnoMuerte() == 0) {
                    System.out.println("Año de Muerte: Desconocido");
                } else if (autor.getAnoMuerte() > finalAnioFin) {
                    System.out.println("Año de Muerte: Aún vivía en esos años, falleció en " + autor.getAnoMuerte());
                } else {
                    System.out.println("Año de Muerte: " + autor.getAnoMuerte());
                }
                System.out.println("----------");
            });
        }
    }


    private void buscarAutoresPorNombre() {

        System.out.println("Ingresa el nombre del autor que deseas buscar: ");
        var nombre = teclado.nextLine();

        Optional<Autor> autor = autorService.buscarAutorPorNombre(nombre);
        if (autor.isPresent()) {
            Autor autorEncontrado = autor.get();
            System.out.println("Nombre: " + autorEncontrado.getNombre());
            System.out.println("Año de Nacimiento: " + autorEncontrado.getAnoNacimiento());
            System.out.println("Año de Muerte: " + (autorEncontrado.getAnoMuerte() != 0 ? autorEncontrado.getAnoMuerte() : "Aún vive"));
        } else {
            System.out.println("Autor no encontrado.");
        }
    }

    private void mostrarAutoresVivosSegunAnio() {

        final int anioBusquedaFinal = obtenerEntradaEntero("Ingresa el año de búsqueda: ");

        List<Autor> autores = autorService.obtenerAutoresVivosEnAnio(anioBusquedaFinal);
        if (autores.isEmpty()) {
            System.out.println("No se encontraron autores vivos al año " + anioBusquedaFinal);
        } else {
            System.out.println("Autores vivos al año " + anioBusquedaFinal + ": ");
            autores.forEach(autor -> {
                int edadEnAnioBusqueda = anioBusquedaFinal - autor.getAnoNacimiento();
                System.out.println("------- " + autor.getNombre() + ", Edad en " + anioBusquedaFinal + ": " + edadEnAnioBusqueda + " años\n");
            });
        }
    }

    public void mostrarTodosLosAutores() {
        List<Autor> autores = autorService.obtenerTodosLosAutores();
        System.out.println("Lista de Todos Los Autores: ");
        autores.forEach(autor -> System.out.println("------- " + autor.getNombre()));
    }


    public void mostrarTodosLosLibros() {
        boolean navegar = true;
        while (navegar) {
            // Obtener los libros paginados para la página actual
            Page<Libro> librosPaginados = libroService.obtenerLibrosPaginados(PageRequest.of(paginaActual, TAMAÑO_PAGINA));

            // Mostrar los libros obtenidos
            if (librosPaginados != null && !librosPaginados.isEmpty()) {
                System.out.println("Libros en la página " + (paginaActual + 1) + ":");
                librosPaginados.forEach(libro -> System.out.println(libro.getTitulo()));
            } else {
                System.out.println("No hay libros disponibles.");
            }

            // Opciones para navegar

            var menu4 = """
                    Opciones:
                    
                    1 - Siguiente página
                    2 - Página anterior
                    
                    0 - Salir de la navegación
                    """;

            int opcionNavegacion = obtenerEntradaEntero(menu4);

            teclado.nextLine(); // Limpiar buffer

            switch (opcionNavegacion) {
                case 1: // Ir a la siguiente página
                    assert librosPaginados != null;
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


    private void buscarLibrosPorIdioma() {
        var idioma = "";
        var opcion = -1;
        while (opcion != 0) {
            var menu2 = """
                    Selecciona la opción del idioma a buscar:
                    
                    1 - Inglés
                    2 - Español
                    3 - Tagalog
                    4 - Francés
                    
                    0 - Volver
                    """;

            // Manejamos la entrada para asegurar que sea un número entero
            opcion = obtenerEntradaEntero(menu2);

            // Limpiar el buffer del teclado
            teclado.nextLine();

            switch (opcion) {
                case 1:
                    idioma = "en";
                    break;
                case 2:
                    idioma = "es";
                    break;
                case 3:
                    idioma = "tl";
                    break;
                case 4:
                    idioma = "fr";
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Opción inválida. Por favor, ingrese un número válido.");
                    opcion = -1; // Reitera el ciclo si se ingresa una opción inválida
            }

            if (!idioma.isEmpty()) {
                this.mostrarEstadisticasDeLibrosPorIdioma(idioma);
                break; // Termina el ciclo después de mostrar los libros
            }
        }
    }

    public void mostrarEstadisticasDeLibrosPorIdioma(String idioma) {

        List<Libro> libros = libroService.mostrarLibrosPorIdioma(idioma);

        var idiomaCompleto = switch (idioma) {
            case "en" -> "inglés";
            case "es" -> "español";
            case "tl" -> "tagalog";
            case "fr" -> "francés";
            default -> idioma; // Usa el idioma como predeterminado si no es inglés o español
        };

        long cantidadLibros = libros.stream()
                .filter(libro -> libro.getIdioma().equalsIgnoreCase(idioma))
                .count();

        AtomicInteger i = new AtomicInteger(1); //Usamos AtomicInteger para permitir la modificación de i dentro de la expresión lambda.

        System.out.println("Cantidad de libros en el idioma " + idiomaCompleto + ": " + cantidadLibros + ".\n");

        libros.stream()
                .sorted((libro1, libro2) -> libro1.getTitulo().compareToIgnoreCase(libro2.getTitulo()))
                .forEach(libro -> {
                    String titulo = libro.getTitulo().length() > 80
                            ? libro.getTitulo().substring(0, 80) + "..." // Limitar a 80 caracteres y agregar "..."
                            : libro.getTitulo();
                    // Mostrar el título y el contador
                    System.out.println("Título " + i.getAndIncrement() + ": " + titulo + ", Descargas: " + libro.getNumeroDescargas());
                    // Imprime el valor actual de i y luego lo incrementa, de modo que cada libro esté numerado secuencialmente en la lista.
                });

        System.out.println("\n");
    }


    public void obtenerLibrosWeb() throws JsonProcessingException {
        // Solicitar el título del libro al usuario
        System.out.println("Ingresa el titulo del libro que deseas buscar: ");
        var nombreLibro = teclado.nextLine();

        // Reemplazar los espacios con %20 para formato de URL
        nombreLibro = nombreLibro.replace(" ", "%20");

        // Obtener el JSON del libro desde la API
        String json = libroService.obtenerJsonLibro(nombreLibro);
        System.out.println(json);

        // Deserializar los resultados de la API usando deserializarResultados
        Resultados resultadoApi = libroService.deserializarResultados(json);

        // Verificar si hay resultados disponibles
        if (resultadoApi != null && !resultadoApi.results().isEmpty()) {
            resultadoApi.results().forEach(datosLibro -> {

                // Obtener los autores del libro
                Autor autorGuardado = autorService.obtenerAutorGuardado(datosLibro);

                // Procesar o crear el libro
                procesarLibro(datosLibro, autorGuardado);
            });
        } else {
            System.out.println("No se encontraron resultados.");
        }
    }


    public void procesarLibro(DatosLibro datosLibro, Autor autorGuardado) {
        try {
            // Buscar si el libro ya existe en la base de datos
            Optional<Libro> libroOptional = libroService.getLibroRepository().findByTitulo(datosLibro.titulo());

            if (libroOptional.isPresent()) {
                // Si el libro ya existe, obtenerlo y mostrar sus datos
                Libro libro = libroOptional.get();  // Obtener el libro desde el Optional
                libro.mostrarInformacionLibro();

            } else {
                // Si el libro no existe, crear uno nuevo con los datos
                if (autorGuardado != null) {
                    Libro nuevoLibro = new Libro(datosLibro, autorGuardado);
                    libroService.getLibroRepository().save(nuevoLibro);
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


    private int obtenerEntradaEntero(String mensaje) {
        int numero = -1;
        boolean esValido = false;
        while (!esValido) {
            try {
                System.out.println(mensaje);
                String entrada = teclado.nextLine(); // Usamos nextLine() para obtener la entrada completa
                numero = Integer.parseInt(entrada); // Intentamos convertirla a un número
                esValido = true; // Si la conversión tiene éxito, la entrada es válida
            } catch (NumberFormatException e) {
                System.out.println("Entrada no válida. Por favor, ingresa un número.");
            }
        }
        return numero;
    }

//Para implementaciones futuras
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


}