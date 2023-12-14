package com.equipo.INNOVARTE.Controladores;

import com.equipo.INNOVARTE.Servicios.UsuarioServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/")
public class ControladorPrincipal {

    @Autowired
    UsuarioServicio usuarioServicio;

    @GetMapping("/") //Esta es la pagina principal del usuario que no se logeo
    public String index() {
        return "inicio.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @GetMapping("/inicio") //Esta es la pagina principal del usuario que se logeo
    public String inicio() {
        return "inicio.html";
    }

    @GetMapping("/registrar") // localhost:8080
    public String registrar() {
        return "usuario_registro.html";
    }

    @PostMapping("/registro")
    public String registro(@RequestParam String nombre, @RequestParam String apellido,
            @RequestParam String email, @RequestParam String password,
            String password2, @RequestParam String nombreUsuario,
            String pregunta, String respuesta,
            ModelMap modelo, MultipartFile archivo) {

        try {
            usuarioServicio.registrar(archivo, nombre, apellido, email,
                    nombreUsuario, password, password2);

            modelo.put("exito", "El usuario se registro correctamente.");
            return "inicio.html";
        } catch (Exception ex) {
            modelo.put("error", ex.getMessage());
            modelo.put("nombre", nombre);
            modelo.put("apellido", apellido);
            modelo.put("email", email);
            modelo.put("nombreUsuario", nombreUsuario);
            modelo.put("pregunta", pregunta);
            modelo.put("respuesta", respuesta);
            return "usuario_registro.html";
        }
    }

    @GetMapping("/loguear") // localhost:8080
    public String login(@RequestParam(required = false) String error, ModelMap modelo) {
        if (error != null) {

        }
        return "Login.html";
    }

}
