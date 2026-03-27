package com.example.Millesime.model;

import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

@Service
public class ClienteService {

    private final ClienteDAO clienteDAO;

    public ClienteService(ClienteDAO clienteDAO) {
        this.clienteDAO = clienteDAO;
    }

    public void cadastrarCliente(Cliente cliente) throws Exception {
        validarCliente(cliente);

        try {
            if (clienteDAO.buscarPorEmail(cliente.getEmail()) != null) {
                throw new Exception("Ja existe um cliente cadastrado com este e-mail.");
            }

            if (clienteDAO.buscarPorCpf(cliente.getCpf()) != null) {
                throw new Exception("Ja existe um cliente cadastrado com este CPF.");
            }

            cliente.setAtivo(true);
            clienteDAO.salvar(cliente);
        } catch (SQLException e) {
            throw new Exception("Erro ao cadastrar cliente.", e);
        }
    }

    public Cliente autenticar(String email, String senha) throws Exception {
        try {
            Cliente cliente = clienteDAO.buscarPorEmail(email);

            if (cliente == null) {
                throw new Exception("Cliente nao encontrado para o e-mail informado.");
            }

            if (!cliente.isAtivo()) {
                throw new Exception("Cliente inativo.");
            }

            if (!cliente.getSenha().equals(senha)) {
                throw new Exception("Senha invalida.");
            }

            return cliente;
        } catch (SQLException e) {
            throw new Exception("Erro ao autenticar cliente.", e);
        }
    }

    public void atualizarCadastro(Cliente cliente) throws Exception {
        if (cliente.getId() == null) {
            throw new Exception("O id do cliente e obrigatorio para atualizacao.");
        }

        validarCliente(cliente);

        try {
            Cliente clienteComMesmoEmail = clienteDAO.buscarPorEmail(cliente.getEmail());
            if (clienteComMesmoEmail != null && !cliente.getId().equals(clienteComMesmoEmail.getId())) {
                throw new Exception("Ja existe outro cliente cadastrado com este e-mail.");
            }

            Cliente clienteComMesmoCpf = clienteDAO.buscarPorCpf(cliente.getCpf());
            if (clienteComMesmoCpf != null && !cliente.getId().equals(clienteComMesmoCpf.getId())) {
                throw new Exception("Ja existe outro cliente cadastrado com este CPF.");
            }

            clienteDAO.atualizar(cliente);
        } catch (SQLException e) {
            throw new Exception("Erro ao atualizar cadastro do cliente.", e);
        }
    }

    public void desativarConta(UUID id) throws Exception {
        try {
            clienteDAO.deletar(id);
        } catch (SQLException e) {
            throw new Exception("Erro ao desativar conta do cliente.", e);
        }
    }

    public List<Cliente> listarTodosClientes() throws Exception {
        try {
            return clienteDAO.listarTodos();
        } catch (SQLException e) {
            throw new Exception("Erro ao listar clientes.", e);
        }
    }

    private void validarCliente(Cliente cliente) throws Exception {
        if (cliente == null) {
            throw new Exception("Cliente nao informado.");
        }

        if (cliente.getNomeCompleto() == null || cliente.getNomeCompleto().isBlank()) {
            throw new Exception("O nome completo e obrigatorio.");
        }

        if (cliente.getEmail() == null || cliente.getEmail().isBlank()) {
            throw new Exception("O e-mail e obrigatorio.");
        }

        if (cliente.getCpf() == null || !cliente.getCpf().matches("\\d{11}")) {
            throw new Exception("O CPF deve conter exatamente 11 digitos.");
        }

        if (cliente.getSenha() == null || cliente.getSenha().length() < 6) {
            throw new Exception("A senha deve ter no minimo 6 caracteres.");
        }
    }
}