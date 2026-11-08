package com.facilita.facilita_backend.controller;

import com.facilita.facilita_backend.model.Produto;
import com.facilita.facilita_backend.model.Usuario;
import com.facilita.facilita_backend.repository.ProdutoRepository;
import com.facilita.facilita_backend.repository.UsuarioRepository;

import java.util.Map;
import java.util.List;
import java.util.UUID;

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

    @PatchMapping("/venda")
    public ResponseEntity<String> realizarVenda(@RequestBody List<java.util.Map<String, Object>> itensVendidos) {
        // vê quem é o lojista autenticado pelo token
        String emailUsuario = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuarioLogado = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        // processa cada item do carrinho para abater do estoque
        for (Map<String, Object> item: itensVendidos) {
            String idStr = (String) item.get("id");
            UUID produtoId = UUID.fromString(idStr);
            int quantidadeVendida = (int) item.get("cartQty");

            Produto produto = produtoRepository.findById(produtoId)
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

            // valida se o produto pertence ao lojista
            if (!produto.getUsuario().getId().equals(usuarioLogado.getId())) {
                return ResponseEntity.status(403).body("Acesso negado.");
            }

            // valida também se há estoque suficiente
            if (produto.getQuantidadeEstoque() < quantidadeVendida) {
                return ResponseEntity.badRequest().body("Estoque insuficiente para o produto: " + produto.getNome());
            }

            // abate a quantidade vendida
            produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() - quantidadeVendida);
            produtoRepository.save(produto);
        }

        return ResponseEntity.ok("Venda realizada e estoque atualizado com sucesso!");
    }
}
