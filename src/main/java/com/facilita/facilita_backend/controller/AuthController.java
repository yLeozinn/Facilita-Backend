package com.facilita.facilita_backend.controller;

import com.facilita.facilita_backend.model.Usuario;
import com.facilita.facilita_backend.repository.UsuarioRepository;
import com.facilita.facilita_backend.security.TokenService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:8443")
@RestController 
@RequestMapping("/api/auth")
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public AuthController(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, TokenService tokenService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    @PostMapping("/cadastrar")
    public ResponseEntity<String> cadastrar(@RequestBody Usuario usuario){
        if (usuarioRepository.existsByEmail(usuario.getEmail())){
            return ResponseEntity.badRequest().body("Erro: Este e-mail já está em uso.");
        }
        if (usuarioRepository.existsByCpf(usuario.getCpf())){
            return ResponseEntity.badRequest().body("Erro: Este CPF já está cadastrado.");
        }

        String senhaCriptografada = passwordEncoder.encode(usuario.getSenha());
        usuario.setSenha(senhaCriptografada);

        usuarioRepository.save(usuario);

        return ResponseEntity.ok("Conta criada com sucesso!");
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO request) {
        // busca o usuario no banco pelo email
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(request.email());

        if(usuarioOpt.isEmpty()) {
            return ResponseEntity.status(401).body("Erro: E-mail ou senha incorretos.");
        }

        Usuario usuario = usuarioOpt.get();

        if (passwordEncoder.matches(request.senha(), usuario.getSenha())) {
            String token = tokenService.gerarToken(usuario);

            return ResponseEntity.ok(Map.of("token", token));
        }

        return ResponseEntity.status(401).body("Erro: E-mail ou senha incorretos");
    }


}

record LoginDTO(String email, String senha) {}