package com.utp.cinerama.cinerama.security;

import com.utp.cinerama.cinerama.model.Usuario;
import com.utp.cinerama.cinerama.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Reglas de autorización para recursos de Cliente.
 * Permite que un usuario CLIENTE vea solo su propio clienteId.
 * Los ADMIN pueden ver cualquiera.
 */
@Component("clienteSecurity")
@RequiredArgsConstructor
public class ClienteSecurity {

    private final UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public boolean canAccessCliente(Long clienteId, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated() || clienteId == null) {
            return false;
        }

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        if (isAdmin) {
            return true;
        }

        String username = authentication.getName();
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElse(null);

        if (usuario == null || usuario.getCliente() == null || usuario.getCliente().getId() == null) {
            return false;
        }

        return clienteId.equals(usuario.getCliente().getId());
    }
}
