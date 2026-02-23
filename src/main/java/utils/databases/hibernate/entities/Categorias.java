package utils.databases.hibernate.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
public class Categorias {
    //Mapear el ID como fk de la tabla Productos
    @Getter @Setter @Id
    private Long ID;

    @JoinColumn(name = "ID")
    @OneToOne(mappedBy = "categoria") // Relación uno a uno con la entidad Productos, donde una categoría tiene un producto asociado.
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter @Setter
    private Categorias categoria;

    @Getter @Setter @Column(name = "Nombre")
    private String nombre;
}