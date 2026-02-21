package entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import entities.PRESUPUESTO_PRODUCTOS;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Presupuestos {

    @Setter @Getter @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Genera automáticamente un valor único para el ID utilizando la estrategia de identidad de la base de datos
    // Es foreign key de la tabla PRESUPUESTO_PRODUCTOS por lo que se relaciona con el ID_PRESUPUESTO de esa tabla agregando una restricción de clave foránea.

    private Long id;

    @Setter @Getter @Column(name = "Nombre_Cliente")
    private String Nombre_Cliente;

    @Setter @Getter @Column(name = "Fecha")
    private String Fecha;

    @Setter @Getter @Column(name = "Tipo_Cliente")
    private String Tipo_Cliente;

    @Setter @Getter @Column(name = "Numero_Presupuesto")
    private int Numero_Presupuesto;

    @Setter @Getter @Column(name = "Precio_Total")
    private double Precio_Total;

    // Relación uno a muchos con la entidad PRESUPUESTO_PRODUCTOS, donde un presupuesto puede tener múltiples productos asociados.
    // OrphanRemoval = true: Si se elimina un presupuesto, también se eliminarán automáticamente los productos asociados a ese presupuesto.
    // CascadeType.ALL: Todas las operaciones (persistir, fusionar, eliminar, etc.) realizadas en el presupuesto se propagarán a los productos asociados.
    @OneToMany(mappedBy = "presupuesto", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PRESUPUESTO_PRODUCTOS> PRESUPUESTO_PRODUCTOS = new ArrayList<>();

}
