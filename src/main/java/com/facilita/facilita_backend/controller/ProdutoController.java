package com.facilita.facilita_backend.controller;

import com.facilita.facilita_backend.model.Produto;
import com.facilita.facilita_backend.model.Usuario;
import com.facilita.facilita_backend.repository.ProdutoRepository;
import com.facilita.facilita_backend.repository.UsuarioRepository;

import java.util.List;
import java.util.UUID;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController 
@RequestMapping("/api/produtos")
@CrossOrigin(origins = "http://localhost:8443")
public class ProdutoController {
    @Autowired 
    private ProdutoRepository produtoRepository;

    @Autowired 
    private UsuarioRepository usuarioRepository;

    @PostMapping 
    public ResponseEntity<String> criarProduto(@RequestBody Produto produto){
        String emailUsuario = SecurityContextHolder.getContext().getAuthentication().getName();

        Usuario usuarioLogado = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        produto.setUsuario(usuarioLogado);

        produtoRepository.save(produto);

        return ResponseEntity.ok("Produto cadastrado com sucesso!");
    }

    @GetMapping 
    public ResponseEntity<List<Produto>> listarProdutos(){
        String emailUsuario = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuarioLogado = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));
        
        List<Produto> produtos = produtoRepository.findByUsuario(usuarioLogado);

        return ResponseEntity.ok(produtos);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletarProduto(@PathVariable UUID id) {
        
        String emailUsuario = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuarioLogado = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado."));

        if (!produto.getUsuario().getId().equals(usuarioLogado.getId())) {
            return ResponseEntity.status(403).body("Acesso negado. Este produto não pertence ao seu comércio.");
        }

        produtoRepository.delete(produto);

        return ResponseEntity.ok("Produto excluído com sucesso!");
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> atualizarProduto(@PathVariable UUID id, @RequestBody Produto produtoAtualizado) {
        String emailUsuario = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuarioLogado = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        Produto produtoExistente = produtoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado."));

        if (!produtoExistente.getUsuario().getId().equals(usuarioLogado.getId())) {
            return ResponseEntity.status(403).body("Acesso negado. Este produto não pertence ao seu comércio.");
        }

        produtoExistente.setPreco(produtoAtualizado.getPreco());
        produtoExistente.setQuantidadeEstoque(produtoExistente.getQuantidadeEstoque() + produtoAtualizado.getQuantidadeEstoque());
        if (produtoAtualizado.getDataValidade() != null) {
            produtoExistente.setDataValidade(produtoAtualizado.getDataValidade());
        }

        produtoRepository.save(produtoExistente);

        return ResponseEntity.ok("Produto atualizado com sucesso!");
    }
}
