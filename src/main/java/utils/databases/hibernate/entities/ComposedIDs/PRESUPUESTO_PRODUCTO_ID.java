package utils.databases.hibernate.entities.ComposedIDs;

import lombok.*;
import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PRESUPUESTO_PRODUCTO_ID implements Serializable {

    // Debe coincidir en nombre y tipo con el @Id de la entidad PRESUPUESTO_PRODUCTOS
    private Integer ID_PRESUPUESTO_PRODUCTO;

    // IMPORTANTE: Debe llamarse igual que el objeto en la entidad PRESUPUESTO_PRODUCTOS (presupuesto)
    // y debe ser del mismo tipo que el @Id de la clase Presupuestos (Long)
    private Long presupuesto;
}