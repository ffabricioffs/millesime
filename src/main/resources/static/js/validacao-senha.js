/**
 * Validador de Senha para Millésime
 * Valida força de senha e correspondência entre password e confirmPassword
 * Requisitos:
 * - Mínimo 1 letra maiúscula
 * - Mínimo 1 letra minúscula
 * - Mínimo 1 número
 * - Mínimo 1 caractere especial
 * - Campos password e confirmPassword idênticos
 */

class ValidadorSenha {
    constructor(senha) {
        this.senha = senha;
    }

    /**
     * Verifica se a senha contém pelo menos uma letra maiúscula
     */
    temMaiuscula() {
        return /[A-Z]/.test(this.senha);
    }

    /**
     * Verifica se a senha contém pelo menos uma letra minúscula
     */
    temMinuscula() {
        return /[a-z]/.test(this.senha);
    }

    /**
     * Verifica se a senha contém pelo menos um número
     */
    temNumero() {
        return /[0-9]/.test(this.senha);
    }

    /**
     * Verifica se a senha contém pelo menos um caractere especial
     */
    temCaractereEspecial() {
        return /[!@#$%^&*()_+\-=\[\]{};:'",.<>?\/\\|`~]/.test(this.senha);
    }

    /**
     * Avalia toda a força da senha
     */
    valida() {
        return (
            this.temMaiuscula() &&
            this.temMinuscula() &&
            this.temNumero() &&
            this.temCaractereEspecial() &&
            this.senha.length >= 8
        );
    }

    /**
     * Retorna objeto com status de cada requisito
     */
    obterStatusRequisitos() {
        return {
            maiuscula: this.temMaiuscula(),
            minuscula: this.temMinuscula(),
            numero: this.temNumero(),
            especial: this.temCaractereEspecial(),
            minimo: this.senha.length >= 8
        };
    }
}

/**
 * Gerenciador de validação de senha para o formulário
 */
class GerenciadorValidacaoSenha {
    constructor() {
        this.passwordInput = document.getElementById('password');
        this.confirmPasswordInput = document.getElementById('confirmPassword');
        this.formulario = document.querySelector('.register-form');
        this.submitBtn = document.querySelector('.register-submit');

        if (this.passwordInput) {
            this.passwordInput.addEventListener('input', () => this.validarSenha());
            this.passwordInput.addEventListener('blur', () => this.validarSenha());
        }

        if (this.confirmPasswordInput) {
            this.confirmPasswordInput.addEventListener('input', () => this.validarCorrespondencia());
            this.confirmPasswordInput.addEventListener('blur', () => this.validarCorrespondencia());
        }

        if (this.formulario) {
            this.formulario.addEventListener('submit', (e) => this.validarNoEnvio(e));
        }

        this.criarMostrador();
    }

    /**
     * Cria o mostrador de requisitos da senha
     */
    criarMostrador() {
        if (!this.passwordInput) return;

        const mostrador = document.createElement('div');
        mostrador.className = 'senha-requisitos';
        mostrador.innerHTML = `
            <div class="senha-requisito-titulo">Requisitos da senha:</div>
            <div class="senha-requisito-item">
                <span class="senha-requisito-icon" data-req="maiuscula">○</span>
                <span class="senha-requisito-text">1 letra maiúscula (A-Z)</span>
            </div>
            <div class="senha-requisito-item">
                <span class="senha-requisito-icon" data-req="minuscula">○</span>
                <span class="senha-requisito-text">1 letra minúscula (a-z)</span>
            </div>
            <div class="senha-requisito-item">
                <span class="senha-requisito-icon" data-req="numero">○</span>
                <span class="senha-requisito-text">1 número (0-9)</span>
            </div>
            <div class="senha-requisito-item">
                <span class="senha-requisito-icon" data-req="especial">○</span>
                <span class="senha-requisito-text">1 caractere especial (!@#$%^&*)</span>
            </div>
            <div class="senha-requisito-item">
                <span class="senha-requisito-icon" data-req="minimo">○</span>
                <span class="senha-requisito-text">Mínimo 8 caracteres</span>
            </div>
        `;

        this.passwordInput.parentNode.appendChild(mostrador);
        this.mostrador = mostrador;
    }

    /**
     * Valida a senha em tempo real
     */
    validarSenha() {
        const senha = this.passwordInput.value;

        if (!senha) {
            this.limparMostrarador();
            return false;
        }

        const validador = new ValidadorSenha(senha);
        const status = validador.obterStatusRequisitos();

        this.atualizarMostrador(status);
        this.validarCorrespondencia();

        return validador.valida();
    }

    /**
     * Atualiza o visual do mostrador de requisitos
     */
    atualizarMostrador(status) {
        if (!this.mostrador) return;

        Object.keys(status).forEach(requisito => {
            const icon = this.mostrador.querySelector(`[data-req="${requisito}"]`);
            if (icon) {
                if (status[requisito]) {
                    icon.textContent = '✓';
                    icon.className = 'senha-requisito-icon senha-requisito-ok';
                } else {
                    icon.textContent = '○';
                    icon.className = 'senha-requisito-icon';
                }
            }
        });
    }

    /**
     * Limpa o mostrador
     */
    limparMostrarador() {
        if (!this.mostrador) return;

        this.mostrador.querySelectorAll('.senha-requisito-icon').forEach(icon => {
            icon.textContent = '○';
            icon.className = 'senha-requisito-icon';
        });
    }

    /**
     * Valida se os dois campos de senha são idênticos
     */
    validarCorrespondencia() {
        const senha = this.passwordInput?.value || '';
        const confirmacao = this.confirmPasswordInput?.value || '';

        let mensagem = document.querySelector('.confirmacao-validation-message');

        if (!confirmacao) {
            if (mensagem) {
                mensagem.textContent = '';
                mensagem.className = 'confirmacao-validation-message';
            }
            return true;
        }

        if (!mensagem) {
            mensagem = document.createElement('small');
            mensagem.className = 'confirmacao-validation-message';
            this.confirmPasswordInput.parentNode.appendChild(mensagem);
        }

        if (senha !== confirmacao) {
            mensagem.textContent = 'As senhas não coincidem';
            mensagem.className = 'confirmacao-validation-message confirmacao-validation-error';
            mensagem.setAttribute('role', 'alert');
            this.confirmPasswordInput.setAttribute('aria-invalid', 'true');
            return false;
        }

        mensagem.textContent = 'Senhas coincidem ✓';
        mensagem.className = 'confirmacao-validation-message confirmacao-validation-success';
        mensagem.setAttribute('role', 'status');
        this.confirmPasswordInput.setAttribute('aria-invalid', 'false');
        return true;
    }

    /**
     * Valida tudo no envio do formulário
     */
    validarNoEnvio(evento) {
        const senha = this.passwordInput?.value || '';
        const confirmacao = this.confirmPasswordInput?.value || '';

        // Validar força da senha
        if (!senha) {
            evento.preventDefault();
            this.mostrarErroSenha('Senha é obrigatória');
            return false;
        }

        const validador = new ValidadorSenha(senha);

        if (!validador.valida()) {
            evento.preventDefault();
            const status = validador.obterStatusRequisitos();
            const requisitosNaoAtendidos = Object.keys(status)
                .filter(req => !status[req])
                .map(req => this.obterTextoRequisito(req));

            this.mostrarErroSenha(`Requisitos não atendidos: ${requisitosNaoAtendidos.join(', ')}`);
            this.passwordInput.focus();
            return false;
        }

        // Validar correspondência
        if (senha !== confirmacao) {
            evento.preventDefault();
            this.mostrarErroConfirmacao('As senhas não coincidem');
            this.confirmPasswordInput.focus();
            return false;
        }

        return true;
    }

    /**
     * Mostra erro de validação da senha
     */
    mostrarErroSenha(texto) {
        let mensagem = document.querySelector('.senha-validation-message-error');

        if (!mensagem) {
            mensagem = document.createElement('small');
            mensagem.className = 'senha-validation-message-error';
            this.passwordInput.parentNode.appendChild(mensagem);
        }

        mensagem.textContent = texto;
        mensagem.setAttribute('role', 'alert');
    }

    /**
     * Mostra erro de correspondência
     */
    mostrarErroConfirmacao(texto) {
        let mensagem = document.querySelector('.confirmacao-validation-message-error');

        if (!mensagem) {
            mensagem = document.createElement('small');
            mensagem.className = 'confirmacao-validation-message-error';
            this.confirmPasswordInput.parentNode.appendChild(mensagem);
        }

        mensagem.textContent = texto;
        mensagem.setAttribute('role', 'alert');
    }

    /**
     * Traduz chave do requisito para texto legível
     */
    obterTextoRequisito(requisito) {
        const textos = {
            maiuscula: 'letra maiúscula',
            minuscula: 'letra minúscula',
            numero: 'número',
            especial: 'caractere especial',
            minimo: 'mínimo 8 caracteres'
        };
        return textos[requisito] || requisito;
    }
}

/**
 * Inicializa o gerenciador quando o DOM estiver pronto
 */
document.addEventListener('DOMContentLoaded', () => {
    new GerenciadorValidacaoSenha();
});
