package br.com.alltallent.config;

import br.com.alltallent.model.Funcionario;
import br.com.alltallent.model.Perfil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class CustomUserDetailsTest {

    @Test
    void testRoles() {
        Funcionario f = new Funcionario();
        f.setEmail("test@test.com");
        f.setSenhaHash("hash");
        
        // No perfil
        CustomUserDetails user1 = new CustomUserDetails(f);
        assertTrue(user1.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER")));

        // Perfil 1 (Admin)
        Perfil p1 = new Perfil();
        p1.setCodigo(1);
        f.setPerfil(p1);
        CustomUserDetails user2 = new CustomUserDetails(f);
        assertTrue(user2.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER")));

        // Perfil 2 (Gestor)
        Perfil p2 = new Perfil();
        p2.setCodigo(2);
        f.setPerfil(p2);
        CustomUserDetails user3 = new CustomUserDetails(f);
        assertTrue(user3.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER")));

        // Perfil 3 (Colaborador)
        Perfil p3 = new Perfil();
        p3.setCodigo(3);
        f.setPerfil(p3);
        CustomUserDetails user4 = new CustomUserDetails(f);
        assertTrue(user4.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
    }
}
