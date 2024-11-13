package com.example.aluracursos.literalura;

import com.example.aluracursos.literalura.principal.Principal;
import com.example.aluracursos.literalura.repositorio.AutorRepository;
import com.example.aluracursos.literalura.repositorio.LibroRepository;
import com.example.aluracursos.literalura.service.AutorService;
import com.example.aluracursos.literalura.service.LibroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LiteraluraApplication implements CommandLineRunner {


    @Autowired
    private LibroService libroService;
    @Autowired
    AutorService autorService;

    public static void main(String[] args) {
        SpringApplication.run(LiteraluraApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        Principal principal = new Principal(libroService,autorService);
        principal.muestraElMenu();


    }

}
