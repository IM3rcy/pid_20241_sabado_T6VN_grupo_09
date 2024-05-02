package com.crud.proyecto.controller;


import com.crud.proyecto.model.entity.Rol;
import com.crud.proyecto.model.entity.Usuario;
import com.crud.proyecto.model.service.UsuarioService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;


@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;
    
    @GetMapping("/principal")
    public String mostrarUsuarios(Model model, HttpServletRequest request) {
    	
        HttpSession session = request.getSession();

        Usuario usuario = (Usuario) session.getAttribute("usuario");
        
    	if(usuario != null) {
            List<Usuario> usuarios = usuarioService.listarUsuarios(usuario.getRol().getNombre());
            model.addAttribute("usuarios", usuarios);
    	}
   
        return "index";
    }
    
    @GetMapping("/buscarUsuario")
    public String buscarUsuario(@RequestParam("query") String query, Model model) {
        List<Usuario> usuarios = usuarioService.buscarUsuarioNombreXApellido(query);
        model.addAttribute("usuarios", usuarios);
        return "index";
    }

    @GetMapping("/formularioUsuario")
    public String mostrarFormularioCreacion(Model model, HttpServletRequest request) {

    HttpSession session = request.getSession();

    Usuario usuario = (Usuario) session.getAttribute("usuario");

    if(usuario != null) {
        String nombreRolActual = usuario.getRol().getNombre();
        List<Rol> rolesDisponibles = obtenerRolesDisponibles(nombreRolActual);
        model.addAttribute("roles", rolesDisponibles);
    }

    model.addAttribute("usuario", new Usuario());
    model.addAttribute("tituloAccion", "Nuevo Usuario");
    model.addAttribute("botonAccion", "Guardar Usuario");
    return "crear"; 
}

    private List<Rol> obtenerRolesDisponibles(String nombreRolActual) {
    switch(nombreRolActual) {
        case "Inversionista":
            return Arrays.asList(new Rol("Jefe de Prestamistas"));
        case "Jefe de Prestamistas":
            return Arrays.asList(new Rol("Prestamista"));
        case "Prestamista":
            return Arrays.asList(new Rol("Prestatario"));
        default:
            return Collections.emptyList();
    }
}

    
    
    @PostMapping("/guardarUsuario")
    public String guardarUsuario(@ModelAttribute("usuario") Usuario usuario) {
        if(usuario != null && usuario.getId() != null && usuario.getId() != 0) {
            usuarioService.update(usuario.getId(), usuario);
        } else {
            usuarioService.save(usuario);
        }
        return "redirect:/usuarios/principal";
    }

    
    
    @GetMapping("/editarUsuario/{id}")
    public String editarUsuario(@PathVariable("id") Long id, Model model) {
        Usuario usuario = usuarioService.findById(id);
        List<Rol> roles = usuarioService.listarRoles();
        
        model.addAttribute("roles", roles);
        model.addAttribute("tituloAccion", "Editar Usuario");
        model.addAttribute("botonAccion", "Actualizar Usuario");
        
        if (usuario != null) {
        	System.out.println("usuarios a edirtar "+ usuario);
            model.addAttribute("usuario", usuario);
            return "crear";
        } else {
            return "redirect:/usuarios/principal"; 
        }
    }

    
    @GetMapping("/eliminarUsuario/{id}")
    public String eliminarUsuario(@PathVariable("id") Long id) {
        usuarioService.delete(id);
        return "redirect:/usuarios/principal";
    }

}
