package utils.databases.hibernate.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
public class Clientes {

    @Id @Getter @Setter @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ID;

    @Getter @Setter
    private String Nombre;

    @Getter @Setter
    private String Direccion;

    @Getter @Setter
    private String Localidad;

    @Getter @Setter
    private String Telefono;

    @Getter @Setter
    private String TipoCliente;
}
