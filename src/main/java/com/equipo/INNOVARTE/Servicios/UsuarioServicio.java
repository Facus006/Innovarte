package com.equipo.INNOVARTE.Servicios;

import com.equipo.INNOVARTE.Entidades.Imagen;
import com.equipo.INNOVARTE.Entidades.Usuario;
import com.equipo.INNOVARTE.Enums.Rol;
import com.equipo.INNOVARTE.Errores.MyException;
import com.equipo.INNOVARTE.Repositorios.UsuarioRepositorio;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UsuarioServicio implements UserDetailsService {

    @Autowired
    UsuarioRepositorio usuarioRepo;
    @Autowired
    ImagenServicio imagenservicio;

    @Transactional
    public void registrar(MultipartFile archivo, String nombre, String apellido, String email, String nombreUsuario,
            String password, String password2) throws MyException {

        validar(nombre, apellido, email, nombreUsuario, password, password2);
        validar2(email, nombreUsuario);
        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setApellido(apellido);
        usuario.setEmail(email);
        usuario.setNombreUsuario(nombreUsuario);
        usuario.setPassword(new BCryptPasswordEncoder().encode(password));
        usuario.setRol(Rol.USER);
        usuario.setAlta(true);
        Date fechaRegistro = new Date();
        usuario.setFechaRegistro(fechaRegistro);
        Imagen imagen = imagenservicio.guardar(archivo);
        usuario.setImagen(imagen);
        usuarioRepo.save(usuario);

    }

    private void validar(String nombre, String apellido, String email, String nombreUsuario,
            String password, String password2) throws MyException {

        if (nombre.isEmpty() || nombre == null) {
            throw new MyException("EL nombre no puede estar vacio.");
        }
        if (apellido.isEmpty() || apellido == null) {
            throw new MyException("EL apellido no puede estar vacio.");
        }
        if (email.isEmpty() || email == null) {
            throw new MyException("EL email no puede estar vacio.");
        }
        if (nombreUsuario.isEmpty() || nombreUsuario == null) {
            throw new MyException("EL nombreUsuario no puede estar vacio.");
        }
        if (password.isEmpty() || password == null || password.length() <= 5) {
            throw new MyException("La contraseña no puede estar vacia y tiene que tener mas de 5 caracteres.");
        }
        if (!password.equals(password2)) {
            throw new MyException("Las contraseñas no coiciden.");
        }

    }

    public void validar2(String email, String nombreUsuario) throws MyException {
        if (usuarioRepo.buscarPorEmail(email) != null) {
            throw new MyException("ERROR, EMAIL YA SE ENCUENTRA EN USO!");
        }
        if (usuarioRepo.buscarPorNombreUsuario(nombreUsuario) != null) {
            throw new MyException("ERROR, EL NOMBRE DE USUARIO YA SE ENCUENTRA EN USO!");
        }

    }

    @Transactional
    public void eliminar(String id) throws MyException {

        try {
            Usuario usuario = usuarioRepo.getById(id);
            usuarioRepo.delete(usuario);
        } catch (Exception e) {
            throw new MyException(e.getMessage());
        }

    }

    public Usuario getOne(String id) {
        return usuarioRepo.getOne(id);
    }

    @Transactional(readOnly = true)
    public List<Usuario> listarUsuarios() {

        List<Usuario> usuarios = usuarioRepo.findAll();

        return usuarios;
    }

    @Transactional
    public void cambiarRol(String id, String rol) {
        Optional<Usuario> respuesta = usuarioRepo.findById(id);

        rol = rol.toUpperCase();

        if (respuesta.isPresent()) {
            Usuario usuario = respuesta.get();
            usuario.setRol(Rol.valueOf(rol));
        }
    }

    @Transactional
    public void cambiarEstado(String id) {
        Optional<Usuario> respuesta = usuarioRepo.findById(id);
        if (respuesta.isPresent()) {
            Usuario usuario = respuesta.get();
            Date fechaBaja = new Date();
            usuario.setFechaBaja(fechaBaja);
            usuario.setAlta(!(usuario.isAlta()));
        }
    }

    @Transactional
    public void cambiarFoto(MultipartFile archivo, String idUsuario) throws MyException {

        Optional<Usuario> respuesta = usuarioRepo.findById(idUsuario);
        if (respuesta.isPresent()) {
            Usuario usuario = respuesta.get();

            String idImagen = null;

            if (usuario.getImagen() != null) {
                idImagen = usuario.getImagen().getId();
            }

            Imagen imagen = imagenservicio.actualizar(archivo, idImagen);

            usuario.setImagen(imagen);

            usuarioRepo.save(usuario);

        }

    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Usuario usuario = usuarioRepo.buscarPorEmail(email);

        if (usuario != null && usuario.isAlta() == true) {
            List<GrantedAuthority> permisos = new ArrayList();
            GrantedAuthority p = new SimpleGrantedAuthority("ROLE_" + usuario.getRol().toString());
            permisos.add(p);

            ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpSession session = attr.getRequest().getSession();
            session.setAttribute("usuariosession", usuario);

            return new User(usuario.getEmail(), usuario.getPassword(), permisos);
        } else {
            return null;
        }

    }

}
