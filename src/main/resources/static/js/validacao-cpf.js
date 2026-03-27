/**
 * Validador de CPF para Millésime
 * Valida CPF em tempo real no formulário de cadastro
 * Baseado em algoritmo de validação de CPF brasileiro
 */

class ValidaCPF {
    constructor(cpfEnviado) {
        Object.defineProperty(this, 'cpfLimpo', {
            writable: false,
            enumerable: false,
            configurable: false,
            value: cpfEnviado.replace(/\D+/g, '')
        });
    }

    isSequencia() {
        return this.cpfLimpo.charAt(0).repeat(this.cpfLimpo.length) === this.cpfLimpo;
    }

    geraNovoCPF() {
        const cpfSemDigitos = this.cpfLimpo.slice(0, -2);
        const digito1 = ValidaCPF.geraDigito(cpfSemDigitos);
        const digito2 = ValidaCPF.geraDigito(cpfSemDigitos + digito1);
        this.novoCPF = cpfSemDigitos + digito1 + digito2;
    }

    static geraDigito(cpfSemDigitos) {
        let total = 0;
        let reverso = cpfSemDigitos.length + 1;

        for (let stringNumerica of cpfSemDigitos) {
            total += reverso * Number(stringNumerica);
            reverso--;
        }

        const digito = 11 - (total % 11);
        return digito <= 9 ? String(digito) : '0';
    }

    valida() {
        if (!this.cpfLimpo) return false;
        if (typeof this.cpfLimpo !== 'string') return false;
        if (this.cpfLimpo.length !== 11) return false;
        if (this.isSequencia()) return false;
        this.geraNovoCPF();

        return this.novoCPF === this.cpfLimpo;
    }
}

/**
 * Gerenciador de validação de CPF para o formulário
 */
class GerenciadorValidacaoCPF {
    constructor() {
        this.cpfInput = document.getElementById('cpf');
        this.formulario = document.querySelector('.register-form');
        this.submitBtn = document.querySelector('.register-submit');
        
        if (this.cpfInput) {
            this.cpfInput.addEventListener('blur', () => this.validarCPF());
            this.cpfInput.addEventListener('input', () => this.limparMensagem());
        }

        if (this.formulario) {
            this.formulario.addEventListener('submit', (e) => this.validarNoEnvio(e));
        }
    }

    validarCPF() {
        const cpf = this.cpfInput.value.trim();
        
        if (!cpf) {
            this.mostrarMensagem('CPF é obrigatório', 'error');
            return false;
        }

        const validador = new ValidaCPF(cpf);
        
        if (!validador.valida()) {
            this.mostrarMensagem('CPF inválido. Verifique os dígitos inseridos', 'error');
            this.cpfInput.setAttribute('aria-invalid', 'true');
            return false;
        }

        this.mostrarMensagem('CPF válido', 'success');
        this.cpfInput.setAttribute('aria-invalid', 'false');
        return true;
    }

    validarNoEnvio(evento) {
        const cpf = this.cpfInput.value.trim();

        if (!cpf) {
            evento.preventDefault();
            this.mostrarMensagem('CPF é obrigatório', 'error');
            return false;
        }

        const validador = new ValidaCPF(cpf);

        if (!validador.valida()) {
            evento.preventDefault();
            this.mostrarMensagem('CPF inválido. Não foi possível concluir o cadastro', 'error');
            this.cpfInput.focus();
            return false;
        }

        return true;
    }

    mostrarMensagem(texto, tipo) {
        let mensagem = document.querySelector('.cpf-validation-message');

        if (!mensagem) {
            mensagem = document.createElement('small');
            mensagem.className = 'cpf-validation-message';
            this.cpfInput.parentNode.appendChild(mensagem);
        }

        mensagem.textContent = texto;
        mensagem.className = `cpf-validation-message cpf-validation-${tipo}`;
        mensagem.setAttribute('role', tipo === 'error' ? 'alert' : 'status');
    }

    limparMensagem() {
        const mensagem = document.querySelector('.cpf-validation-message');
        if (mensagem) {
            mensagem.textContent = '';
            mensagem.className = 'cpf-validation-message';
        }
    }
}

/**
 * Inicializa o gerenciador quando o DOM estiver pronto
 */
document.addEventListener('DOMContentLoaded', () => {
    new GerenciadorValidacaoCPF();
});
