package com.example.aluracursos.literalura.principal;

import com.example.aluracursos.literalura.service.LibroApiClient;

import java.util.Scanner;

public class Principal {
    private Scanner teclado = new Scanner(System.in);
    private LibroApiClient consumoApi = new LibroApiClient();

    public void muestraElMenu() {
        var opcion = -1;
        while (opcion != 0) {
            var menu = """
                    1 - Buscar series 
                    2 - Buscar episodios
                    3 - Mostrar series buscadas
                                  
                    0 - Salir
                    """;
            System.out.println(menu);
            opcion = teclado.nextInt();
            teclado.nextLine();

         switch (opcion) {
                case 1:
                    buscarLibros();
                    break;
//                case 2:
//                    buscarEpisodioPorSerie();
//                    break;
//
               case 0:
                   System.out.println("Cerrando la aplicación...");
                   break;
                default:
                    System.out.println("Opción inválida");
            }
        }

    }

    private void buscarLibros() {

    }
}
