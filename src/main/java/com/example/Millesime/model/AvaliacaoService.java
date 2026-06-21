package com.example.Millesime.model;

import com.example.Millesime.dto.AvaliacaoResponse;
import com.example.Millesime.exception.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AvaliacaoService {

    private static final Logger log = LoggerFactory.getLogger(AvaliacaoService.class);
    private final AvaliacaoDAO avaliacaoDAO;

    public AvaliacaoService(AvaliacaoDAO avaliacaoDAO) {
        this.avaliacaoDAO = avaliacaoDAO;
    }

    @Transactional
    public void criar(UUID produtoId, UUID clienteId, String clienteNome, int nota, String comentario) throws Exception {
        if (nota < 1 || nota > 5) {
            throw new ValidationException("A nota deve ser entre 1 e 5.");
        }
        try {
            if (avaliacaoDAO.contarPorProdutoECliente(produtoId, clienteId) > 0) {
                throw new ValidationException("Voc\u00ea j\u00e1 avaliou este produto.");
            }
            Avaliacao a = new Avaliacao();
            a.setProdutoId(produtoId);
            a.setClienteId(clienteId);
            a.setClienteNome(clienteNome);
            a.setNota(nota);
            a.setComentario(comentario);
            avaliacaoDAO.salvar(a);
        } catch (SQLException e) {
            String state = e.getSQLState();
            if ("23505".equals(state)) {
                throw new ValidationException("Voc\u00ea j\u00e1 avaliou este produto.");
            }
            log.error("Erro ao salvar avaliacao", e);
            throw new Exception("Erro ao salvar avaliação.", e);
        }
    }

    public List<AvaliacaoResponse> listarPorProduto(UUID produtoId) throws Exception {
        return listarPorProduto(produtoId, 1, 50);
    }

    public List<AvaliacaoResponse> listarPorProduto(UUID produtoId, int page, int size) throws Exception {
        try {
            int safePage = Math.max(1, page);
            int safeSize = Math.max(1, Math.min(size, 100));
            return avaliacaoDAO.listarPorProdutoPaginado(produtoId, safePage, safeSize).stream()
                    .map(this::toResponse)
                    .collect(Collectors.toList());
        } catch (SQLException e) {
            log.error("Erro ao listar avaliacoes do produto {}", produtoId, e);
            throw new Exception("Erro ao listar avaliações.", e);
        }
    }

    public double calcularMedia(UUID produtoId) throws Exception {
        try {
            return avaliacaoDAO.mediaPorProduto(produtoId);
        } catch (SQLException e) {
            log.error("Erro ao calcular media do produto {}", produtoId, e);
            return 0.0;
        }
    }

    public int contarPorProduto(UUID produtoId) throws Exception {
        try {
            return avaliacaoDAO.contarPorProduto(produtoId);
        } catch (SQLException e) {
            log.error("Erro ao contar avaliacoes do produto {}", produtoId, e);
            return 0;
        }
    }

    private AvaliacaoResponse toResponse(Avaliacao a) {
        AvaliacaoResponse r = new AvaliacaoResponse();
        r.setId(a.getId());
        r.setClienteNome(a.getClienteNome());
        r.setNota(a.getNota());
        r.setComentario(a.getComentario());
        r.setDataFormatada(a.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        return r;
    }
}
