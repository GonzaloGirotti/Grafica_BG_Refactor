package entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "PRESUPUESTO_PRODUCTOS")
@IdClass(entities.ComposedIDs.PRESUPUESTO_PRODUCTO_ID.class)
@Getter @Setter
public class PRESUPUESTO_PRODUCTOS {

    @Id
    @Column(name = "ID_PRESUPUESTO_PRODUCTO")
    private Integer ID_PRESUPUESTO_PRODUCTO;

    @Id
    @ManyToOne
    // Eliminamos el campo 'int ID_PRESUPUESTO' para evitar conflictos.
    // Esta relación ahora maneja tanto la PK compuesta como la FK.
    @JoinColumn(name = "ID_PRESUPUESTO", referencedColumnName = "ID")
    private Presupuestos presupuesto;

    @Column(name = "NOMBRE_PRODUCTO") @Getter @Setter
    private String NOMBRE_PRODUCTO;

    @Column(name = "CANTIDAD") @Getter @Setter
    private int CANTIDAD;

    @Column(name = "OBSERVACIONES") @Getter @Setter
    private String OBSERVACIONES;

    @Column(name = "MEDIDAS") @Getter @Setter
    private String MEDIDAS;

    @Column(name = "PRECIO") @Getter @Setter
    private double PRECIO;
}

