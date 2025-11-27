package com.utp.cinerama.cinerama.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "boletos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"cliente", "funcion", "asiento"})
public class Boleto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "funcion_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Funcion funcion;

    // ⭐ Nuevo: Relación con Asiento
    @OneToOne
    @JoinColumn(name = "asiento_id", nullable = false)
    @JsonIgnoreProperties({"boleto", "funcion"})
    private Asiento asiento;

    @Column(nullable = false)
    private Double precio;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoBoleto estado;

    @Column(nullable = false)
    private LocalDateTime fechaCompra;

    public enum EstadoBoleto {
        RESERVADO,
        PAGADO,
        CANCELADO,
        USADO
    }
}