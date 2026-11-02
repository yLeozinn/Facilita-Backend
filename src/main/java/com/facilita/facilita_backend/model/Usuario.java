package com.facilita.facilita_backend.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;
import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;

@Data
@Entity 
@Table(name = "usuarios")
public class Usuario implements UserDetails{
    
    @Id 
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id; // utilizando UUID por segurança, bem mais seguro contra acessos invalidos do que IDs sequenciais

    @Column(nullable = false)
    private String nome;
     
    @Column(nullable = false)
    private String sobrenome;
     
    @Column(nullable = false, unique = true)
    private String cpf;

    @Column(unique = true)
    private String cnpj;
     
    @Column(nullable = false, unique = true)
    private String email;
     
    @Column(nullable = false)
    private String senha;

    @Column(nullable = false)
    private String celular;
    
    // dados de endereço
    @Column(nullable = false, length = 9)
    private String cep;

    @Column(nullable = false, length = 2)
    private String uf; 
     
    @Column(nullable = false)
    private String cidade;
     
    @Column(nullable = false)
    private String bairro;
     
    @Column(nullable = false)
    private String rua;
     
    @Column(nullable = false)
    private String numero;

    private String complemento;

    @Column(name = "nome_comercio", nullable = false)
    private String nomeComercio;

    @Column(name = "chave_pix")
    private String chavePix;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getUsername() {
        return this.email; 
    }

    @Override
    public String getPassword() {
        return this.senha; 
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
     
}
