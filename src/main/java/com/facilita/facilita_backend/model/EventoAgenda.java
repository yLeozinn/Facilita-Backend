package com.facilita.facilita_backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.UUID;

@Data 
@Entity 
@Table(name = "eventos_agenda")
public class EventoAgenda {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 50)
    private String titulo;
    
    @Column(length = 500)
    private String descricao;

    @Column(nullable = false, length = 5)
    private String horario;

    @Column(name = "data_evento", nullable = false)
    private LocalDate dataEvento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
}
