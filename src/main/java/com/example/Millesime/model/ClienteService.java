package com.example.Millesime.model;

import com.example.Millesime.exception.ResourceNotFoundException;
import com.example.Millesime.exception.ValidationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
@Transactional
public class ClienteService {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final BCryptPasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

    private final ClienteDAO clienteDAO;
    private final PasswordResetTokenDAO passwordResetTokenDAO;
    private final EmailService emailService;

    public ClienteService(ClienteDAO clienteDAO, PasswordResetTokenDAO passwordResetTokenDAO, EmailService emailService) {
        this.clienteDAO = clienteDAO;
        this.passwordResetTokenDAO = passwordResetTokenDAO;
        this.emailService = emailService;
    }

    public void cadastrarCliente(Cliente cliente) throws Exception {
        if (cliente == null) {
            throw new ValidationException("Cliente nao informado.");
        }

        prepararCliente(cliente);
        validarCliente(cliente);

        try {
            if (clienteDAO.buscarPorEmail(cliente.getEmail()) != null) {
                throw new ValidationException("Ja existe um cliente cadastrado com este e-mail.");
            }

            if (clienteDAO.buscarPorCpf(cliente.getCpf()) != null) {
                throw new ValidationException("Ja existe um cliente cadastrado com este CPF.");
            }

            cliente.setSenha(hashPassword(cliente.getSenha()));
            cliente.setAtivo(true);
            clienteDAO.salvar(cliente);
        } catch (SQLException e) {
            throw new Exception("Erro ao cadastrar cliente.", e);
        }
    }

    public Cliente autenticar(String email, String senha) throws Exception {
        if (email == null || email.isBlank() || senha == null || senha.isBlank()) {
            throw new ValidationException("E-mail e senha sao obrigatorios para login.");
        }

        String normalizedEmail = normalizeEmail(email);

        try {
            Cliente cliente = clienteDAO.buscarPorEmail(normalizedEmail);

            if (cliente == null) {
                throw new ValidationException("Cliente nao encontrado para o e-mail informado.");
            }

            if (!cliente.isAtivo()) {
                throw new ValidationException("Cliente inativo.");
            }

            if (!matchesPassword(senha, cliente.getSenha())) {
                throw new ValidationException("Senha invalida.");
            }

            if (!isPasswordEncoded(cliente.getSenha())) {
                cliente.setSenha(hashPassword(senha));
                clienteDAO.atualizar(cliente);
            }

            return cliente;
        } catch (SQLException e) {
            throw new Exception("Erro ao autenticar cliente.", e);
        }
    }

    public void criarTokenRedefinicaoSenha(String email, String baseUrl) throws Exception {
        if (email == null || email.isBlank()) {
            throw new ValidationException("E-mail e obrigatorio para redefinicao de senha.");
        }

        String normalizedEmail = normalizeEmail(email);
        try {
            Cliente cliente = clienteDAO.buscarPorEmail(normalizedEmail);
            if (cliente == null) {
                return; // não expor se o e-mail não existe
            }

            PasswordResetToken token = new PasswordResetToken();
            token.setClienteId(cliente.getId());
            token.setToken(UUID.randomUUID().toString());
            token.setExpiration(LocalDateTime.now().plusHours(2));
            token.setUsed(false);

            passwordResetTokenDAO.salvar(token);

            String linkRedefinicao = String.format("%s/reset-password/confirm?token=%s", baseUrl, token.getToken());
            emailService.enviarEmailRedefinicaoSenha(cliente.getEmail(), linkRedefinicao);
        } catch (SQLException e) {
            throw new Exception("Erro ao criar token de redefinicao de senha.", e);
        }
    }

    public Cliente validarTokenRedefinicaoSenha(String tokenString) throws Exception {
        if (tokenString == null || tokenString.isBlank()) {
            throw new ValidationException("Token de redefinicao de senha invalido.");
        }

        try {
            PasswordResetToken token = passwordResetTokenDAO.buscarPorToken(tokenString);
            if (token == null || token.isUsed() || token.getExpiration().isBefore(LocalDateTime.now())) {
                throw new ValidationException("Token de redefinicao de senha invalido ou expirado.");
            }

            return clienteDAO.buscarPorId(token.getClienteId());
        } catch (SQLException e) {
            throw new Exception("Erro ao validar token de redefinicao de senha.", e);
        }
    }

    public void redefinirSenha(String tokenString, String novaSenha) throws Exception {
        if (tokenString == null || tokenString.isBlank()) {
            throw new ValidationException("Token de redefinicao de senha invalido.");
        }
        if (novaSenha == null || novaSenha.length() < 6) {
            throw new ValidationException("A senha deve ter no minimo 6 caracteres.");
        }

        try {
            PasswordResetToken token = passwordResetTokenDAO.buscarPorToken(tokenString);
            if (token == null || token.isUsed() || token.getExpiration().isBefore(LocalDateTime.now())) {
                throw new ValidationException("Token de redefinicao de senha invalido ou expirado.");
            }

            Cliente cliente = clienteDAO.buscarPorId(token.getClienteId());
            if (cliente == null) {
                throw new ResourceNotFoundException("Cliente nao encontrado.");
            }

            cliente.setSenha(hashPassword(novaSenha));
            clienteDAO.atualizar(cliente);
            passwordResetTokenDAO.marcarComoUsado(token.getId());
        } catch (SQLException e) {
            throw new Exception("Erro ao redefinir senha.", e);
        }
    }

    public void atualizarCadastro(Cliente cliente) throws Exception {
        if (cliente == null || cliente.getId() == null) {
            throw new ValidationException("O id do cliente e obrigatorio para atualizacao.");
        }

        try {
            Cliente existente = clienteDAO.buscarPorId(cliente.getId());
            if (existente == null) {
                throw new ResourceNotFoundException("Cliente nao encontrado.");
            }

            cliente.setEmail(normalizeEmail(cliente.getEmail()));
            cliente.setCpf(onlyDigits(cliente.getCpf()));
            cliente.setAtivo(existente.isAtivo());
            cliente.setDataCadastro(existente.getDataCadastro());

            if (cliente.getSenha() == null || cliente.getSenha().isBlank()) {
                cliente.setSenha(existente.getSenha());
            } else if (!matchesPassword(cliente.getSenha(), existente.getSenha())) {
                cliente.setSenha(hashPassword(cliente.getSenha()));
            }

            validarCliente(cliente);

            Cliente clienteComMesmoEmail = clienteDAO.buscarPorEmail(cliente.getEmail());
            if (clienteComMesmoEmail != null && !cliente.getId().equals(clienteComMesmoEmail.getId())) {
                throw new ValidationException("Ja existe outro cliente cadastrado com este e-mail.");
            }

            Cliente clienteComMesmoCpf = clienteDAO.buscarPorCpf(cliente.getCpf());
            if (clienteComMesmoCpf != null && !cliente.getId().equals(clienteComMesmoCpf.getId())) {
                throw new ValidationException("Ja existe outro cliente cadastrado com este CPF.");
            }

            clienteDAO.atualizar(cliente);
        } catch (SQLException e) {
            throw new Exception("Erro ao atualizar cadastro do cliente.", e);
        }
    }

    public void desativarConta(UUID id) throws Exception {
        if (id == null) {
            throw new ValidationException("O id do cliente e obrigatorio para desativacao.");
        }

        try {
            clienteDAO.deletar(id);
        } catch (SQLException e) {
            throw new Exception("Erro ao desativar conta do cliente.", e);
        }
    }

    public Cliente buscarPorId(UUID id) throws Exception {
        if (id == null) {
            throw new ValidationException("O id do cliente e obrigatorio.");
        }

        try {
            Cliente cliente = clienteDAO.buscarPorId(id);
            if (cliente == null) {
                throw new ResourceNotFoundException("Cliente nao encontrado.");
            }
            return cliente;
        } catch (SQLException e) {
            throw new Exception("Erro ao buscar cliente.", e);
        }
    }

    public List<Cliente> listarTodosClientes() throws Exception {
        try {
            return clienteDAO.listarTodos();
        } catch (SQLException e) {
            throw new Exception("Erro ao listar clientes.", e);
        }
    }

    private void prepararCliente(Cliente cliente) {
        if (cliente.getEmail() != null) {
            cliente.setEmail(normalizeEmail(cliente.getEmail()));
        }

        if (cliente.getCpf() != null) {
            cliente.setCpf(onlyDigits(cliente.getCpf()));
        }
    }

    private void validarCliente(Cliente cliente) {
        if (cliente == null) {
            throw new ValidationException("Cliente nao informado.");
        }

        if (cliente.getNomeCompleto() == null || cliente.getNomeCompleto().isBlank() || cliente.getNomeCompleto().trim().length() < 3) {
            throw new ValidationException("O nome completo e obrigatorio e deve ter pelo menos 3 caracteres.");
        }

        if (cliente.getEmail() == null || cliente.getEmail().isBlank() || !EMAIL_PATTERN.matcher(cliente.getEmail()).matches()) {
            throw new ValidationException("O e-mail informado e invalido.");
        }

        if (cliente.getCpf() == null || cliente.getCpf().isBlank() || !isValidCpf(cliente.getCpf())) {
            throw new ValidationException("O CPF informado e invalido.");
        }

        if (cliente.getSenha() == null || cliente.getSenha().length() < 6) {
            throw new ValidationException("A senha deve ter no minimo 6 caracteres.");
        }

        if (cliente.getDataNascimento() != null) {
            LocalDate dataMinima = LocalDate.now().minusYears(18);
            if (cliente.getDataNascimento().isAfter(dataMinima)) {
                throw new ValidationException("O cliente deve ter pelo menos 18 anos.");
            }
        }
    }

    private String normalizeEmail(String email) {
        if (email == null) {
            return null;
        }
        return email.trim().toLowerCase();
    }

    private String onlyDigits(String value) {
        return value == null ? null : value.replaceAll("\\D", "");
    }

    private boolean isValidCpf(String cpf) {
        if (cpf == null) {
            return false;
        }

        String digits = onlyDigits(cpf);
        if (digits.length() != 11) {
            return false;
        }

        if (digits.chars().distinct().count() == 1) {
            return false;
        }

        int[] numbers = digits.chars().map(c -> c - '0').toArray();
        int dv1 = calculateCpfDigit(numbers, 9);
        int dv2 = calculateCpfDigit(numbers, 10);
        return numbers[9] == dv1 && numbers[10] == dv2;
    }

    private int calculateCpfDigit(int[] numbers, int length) {
        int sum = 0;
        for (int i = 0; i < length; i++) {
            sum += numbers[i] * (length + 1 - i);
        }
        int result = sum % 11;
        return result < 2 ? 0 : 11 - result;
    }

    private boolean matchesPassword(String raw, String encoded) {
        if (raw == null || encoded == null) {
            return false;
        }
        if (isPasswordEncoded(encoded)) {
            return PASSWORD_ENCODER.matches(raw, encoded);
        }
        return raw.equals(encoded);
    }

    private boolean isPasswordEncoded(String senha) {
        return senha != null && senha.startsWith("$2");
    }

    private String hashPassword(String raw) {
        if (raw == null) {
            throw new ValidationException("Senha nao informada.");
        }
        return PASSWORD_ENCODER.encode(raw);
    }
}
