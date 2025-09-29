package com.utp.cinerama.cinerama.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "peliculas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Pelicula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false)
    private String genero;

    @Column(nullable = false)
    private Integer duracion; // Duración en minutos

    @Column(nullable = false)
    private String clasificacion;

    @Column(nullable = false)
    private String sinopsis;
}