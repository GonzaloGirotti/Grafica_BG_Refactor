package utils.databases.hibernate.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Productos {

    @Getter @Setter @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Getter @Setter @Column(name = "Nombre")
    private String nombre;

    // Relación uno a uno con la entidad Categorias, donde un producto tiene una categoría.
    @OneToOne(mappedBy = "categoria")
    @JoinColumn(name = "Categoria_ID")
    @Getter @Setter
    private Categorias categoria;
}
