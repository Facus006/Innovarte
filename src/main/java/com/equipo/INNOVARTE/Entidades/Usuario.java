package com.equipo.INNOVARTE.Entidades;

import com.equipo.INNOVARTE.Enums.Rol;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    public String nombre;
    public String apellido;
    private String email;
    private String nombreUsuario;
    private String password;
    private boolean alta;
    private Rol rol;

    @Temporal(TemporalType.DATE)
    private Date fechaRegistro;
    @Temporal(TemporalType.DATE)
    private Date fechaBaja;
    @Temporal(TemporalType.DATE)
    private Date ultimaVez;

    @OneToOne
    private Imagen imagen;

}
