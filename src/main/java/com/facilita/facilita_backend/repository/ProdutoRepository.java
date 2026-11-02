package com.facilita.facilita_backend.repository;

import com.facilita.facilita_backend.model.Produto;
import com.facilita.facilita_backend.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface ProdutoRepository extends JpaRepository<Produto, UUID> {
    List<Produto> findByUsuario(Usuario usuario);
}
