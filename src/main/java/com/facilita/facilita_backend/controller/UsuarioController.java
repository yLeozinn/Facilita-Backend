package com.facilita.facilita_backend.controller;

import com.facilita.facilita_backend.model.Usuario;
import com.facilita.facilita_backend.repository.UsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "http://localhost:8443")
public class UsuarioController {

    @Autowired
    public UsuarioRepository usuarioRepository;

    public static class PerfilDTO {
        public String nome;
        public String email;
        public String nomeEmpresa;
        public String cpf;
        public String cnpj;
        public String celular;  
        public String cep;
        public String uf;       
        public String cidade;     
        public String bairro;       
        public String rua;
        public String numero;

        public PerfilDTO(String nome, String email, String nomeEmpresa, String cpf, String cnpj
            , String celular, String cep, String uf, String cidade, String bairro, String rua, String numero) {
            this.nome = nome;
            this.email = email;
            this.nomeEmpresa = nomeEmpresa;
            this.cpf = cpf;
            this.cnpj = cnpj;
            this.celular = celular;
            this.cep = cep;
            this.uf = uf;
            this.cidade = cidade;
            this.bairro = bairro;
            this.rua = rua;
            this.numero = numero;
        }
    }

    @GetMapping("/me")
    public ResponseEntity<PerfilDTO> getPerfil() {
        // extrai o email validado pelo filtro JWT
        String emailLogado = SecurityContextHolder.getContext().getAuthentication().getName();
        
        // busca os dados no banco
        Usuario usuario = usuarioRepository.findByEmail(emailLogado)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        PerfilDTO perfil = new PerfilDTO(usuario.getNome(), usuario.getEmail(), usuario.getNomeEmpresa(), usuario.getCpf()
                        , usuario.getCnpj(), usuario.getCelular(), usuario.getCep(), usuario.getUf()
                        , usuario.getCidade(), usuario.getBairro(), usuario.getRua(), usuario.getNumero());
        
        return ResponseEntity.ok(perfil);
    }
}