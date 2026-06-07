package br.com.alltallent.config;

import br.com.alltallent.model.Area;
import br.com.alltallent.model.Funcionario;
import br.com.alltallent.model.Perfil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CustomUserDetailsTest {

    @Test
    void testRolesAndGetters() {
        Funcionario f = new Funcionario();
        f.setEmail("test@test.com");
        f.setSenhaHash("hash");
        f.setCodigo(123);
        Area a = new Area();
        a.setCodigo(10);
        f.setArea(a);
        
        // No perfil
        CustomUserDetails user1 = new CustomUserDetails(f);
        assertTrue(user1.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));
        assertEquals("test@test.com", user1.getUsername());
        assertEquals("hash", user1.getPassword());
        assertEquals(123, user1.getCodigo());
        assertEquals(10, user1.getAreaId());
        assertTrue(user1.isAccountNonExpired());
        assertTrue(user1.isAccountNonLocked());
        assertTrue(user1.isCredentialsNonExpired());
        assertTrue(user1.isEnabled());

        // Perfil 1 (Admin)
        Perfil p1 = new Perfil();
        p1.setCodigo(1);
        f.setPerfil(p1);
        CustomUserDetails user2 = new CustomUserDetails(f);
        assertTrue(user2.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")));
        assertTrue(user2.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_GESTOR")));
        assertTrue(user2.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));

        // Perfil 2 (Gestor)
        Perfil p2 = new Perfil();
        p2.setCodigo(2);
        f.setPerfil(p2);
        CustomUserDetails user3 = new CustomUserDetails(f);
        assertTrue(user3.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_GESTOR")));
        assertTrue(user3.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));

        // Perfil 3 (Colaborador)
        Perfil p3 = new Perfil();
        p3.setCodigo(3);
        f.setPerfil(p3);
        CustomUserDetails user4 = new CustomUserDetails(f);
        assertTrue(user4.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));
    }
}
