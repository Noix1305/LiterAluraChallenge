package com.example.aluracursos.literalura.model;

import com.example.aluracursos.literalura.model.Autor;
import jakarta.persistence.*;

import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "libros")
public class Libro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @Column(unique = true, length = 512)
    private String titulo;

    private String idioma;

    @ManyToOne
    @JoinColumn(name = "autor_id")  // Esto agrega la columna de referencia al autor en la tabla de libros
    private Autor autor;
    private int numeroDescargas;

    public Libro() {
    }

    public Libro(DatosLibro datosLibro, Autor autor) {
        this.titulo = datosLibro.titulo();
        this.idioma = String.join(", ", datosLibro.idiomas());
        this.numeroDescargas = datosLibro.numeroDescargas();
        this.autor = autor;  // Asignar autor al libro
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }


    public Autor getAutor() {
        return autor;
    }

    public void setAutor(Autor autor) {
        this.autor = autor;
    }

    public String getIdioma() {
        return idioma;
    }

    public void setIdioma(String idioma) {
        this.idioma = idioma;
    }

    public int getNumeroDescargas() {
        return numeroDescargas;
    }

    public void setNumeroDescargas(int numeroDescargas) {
        this.numeroDescargas = numeroDescargas;
    }

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public void mostrarInformacionLibro() {
        System.out.println("\nLibro encontrado: " + this.getTitulo() + "\nAutor: " + this.getAutor().getNombre());
        System.out.println("Idioma: " + this.getIdioma() + "\nDescargas: " + this.getNumeroDescargas() + "\n ");
    }

}
