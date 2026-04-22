/**
 * Millésime - Contact Page JavaScript
 * Funções para a página de contato
 */

// Aguarda o DOM carregar completamente
document.addEventListener('DOMContentLoaded', function() {
    
    // ========================================
    // FAQ Accordion Functionality
    // ========================================
    initFaqAccordion();
    
    // ========================================
    // Contact Form Validation & Submission
    // ========================================
    initContactForm();
    
    // ========================================
    // Phone Number Mask
    // ========================================
    initPhoneMask();
    
    // ========================================
    // Success Message Handler
    // ========================================
    checkForSuccessMessage();
});

/**
 * Inicializa o accordion das perguntas frequentes
 */
function initFaqAccordion() {
    const faqQuestions = document.querySelectorAll('.faq-question');
    
    if (faqQuestions.length === 0) return;
    
    faqQuestions.forEach(question => {
        question.addEventListener('click', function(e) {
            const faqItem = this.parentElement;
            const isActive = faqItem.classList.contains('active');
            
            // Fecha todos os outros itens do FAQ
            document.querySelectorAll('.faq-item').forEach(item => {
                if (item !== faqItem) {
                    item.classList.remove('active');
                }
            });
            
            // Alterna o item atual
            if (!isActive) {
                faqItem.classList.add('active');
            } else {
                faqItem.classList.remove('active');
            }
        });
    });
}

/**
 * Inicializa a validação do formulário de contato
 */
function initContactForm() {
    const contactForm = document.getElementById('contactForm');
    
    if (!contactForm) return;
    
    contactForm.addEventListener('submit', function(e) {
        e.preventDefault();
        
        // Coleta os valores dos campos
        const nome = document.getElementById('nome').value.trim();
        const email = document.getElementById('email').value.trim();
        const telefone = document.getElementById('telefone').value.trim();
        const assunto = document.getElementById('assunto').value;
        const mensagem = document.getElementById('mensagem').value.trim();
        const privacidade = document.querySelector('input[name="privacidade"]').checked;
        
        // Validação dos campos
        const errors = [];
        
        if (!nome) {
            errors.push('Nome completo é obrigatório');
            showFieldError('nome', 'Nome completo é obrigatório');
        } else {
            clearFieldError('nome');
        }
        
        if (!email) {
            errors.push('E-mail é obrigatório');
            showFieldError('email', 'E-mail é obrigatório');
        } else if (!isValidEmail(email)) {
            errors.push('E-mail inválido');
            showFieldError('email', 'Digite um e-mail válido');
        } else {
            clearFieldError('email');
        }
        
        if (!assunto) {
            errors.push('Assunto é obrigatório');
            showFieldError('assunto', 'Selecione um assunto');
        } else {
            clearFieldError('assunto');
        }
        
        if (!mensagem) {
            errors.push('Mensagem é obrigatória');
            showFieldError('mensagem', 'Digite sua mensagem');
        } else if (mensagem.length < 10) {
            errors.push('Mensagem deve ter pelo menos 10 caracteres');
            showFieldError('mensagem', 'Mensagem muito curta (mínimo 10 caracteres)');
        } else {
            clearFieldError('mensagem');
        }
        
        if (!privacidade) {
            errors.push('Você deve concordar com a Política de Privacidade');
            showFieldError('privacidade', 'Você precisa concordar com os termos');
        } else {
            clearFieldError('privacidade');
        }
        
        // Se houver erros, exibe e cancela o envio
        if (errors.length > 0) {
            showFormMessage(errors.join('<br>'), 'error');
            return;
        }
        
        // Se passou na validação, envia o formulário
        submitForm(contactForm);
    });
    
    // Adiciona validação em tempo real
    addRealTimeValidation();
}

/**
 * Valida formato de e-mail
 */
function isValidEmail(email) {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
}

/**
 * Exibe erro em um campo específico
 */
function showFieldError(fieldId, message) {
    const field = document.getElementById(fieldId);
    if (!field) return;
    
    // Remove erro existente
    clearFieldError(fieldId);
    
    // Adiciona classe de erro ao campo
    field.classList.add('error-field');
    
    // Cria elemento de mensagem de erro
    const errorDiv = document.createElement('div');
    errorDiv.className = 'field-error';
    errorDiv.id = `${fieldId}-error`;
    errorDiv.style.color = '#dc2626';
    errorDiv.style.fontSize = '0.75rem';
    errorDiv.style.marginTop = '0.25rem';
    errorDiv.textContent = message;
    
    // Insere após o campo
    field.parentNode.insertBefore(errorDiv, field.nextSibling);
}

/**
 * Limpa erro de um campo específico
 */
function clearFieldError(fieldId) {
    const field = document.getElementById(fieldId);
    if (field) {
        field.classList.remove('error-field');
    }
    
    const errorElement = document.getElementById(`${fieldId}-error`);
    if (errorElement) {
        errorElement.remove();
    }
}

/**
 * Exibe mensagem de sucesso ou erro do formulário
 */
function showFormMessage(message, type) {
    // Remove mensagem existente
    const existingMessage = document.querySelector('.form-message');
    if (existingMessage) {
        existingMessage.remove();
    }
    
    // Cria nova mensagem
    const messageDiv = document.createElement('div');
    messageDiv.className = `form-message form-message-${type}`;
    messageDiv.innerHTML = message;
    
    // Estilos da mensagem
    messageDiv.style.padding = '1rem';
    messageDiv.style.borderRadius = '8px';
    messageDiv.style.marginBottom = '1rem';
    messageDiv.style.fontSize = '0.875rem';
    
    if (type === 'error') {
        messageDiv.style.backgroundColor = '#fee2e2';
        messageDiv.style.color = '#dc2626';
        messageDiv.style.border = '1px solid #fecaca';
    } else {
        messageDiv.style.backgroundColor = '#dcfce7';
        messageDiv.style.color = '#16a34a';
        messageDiv.style.border = '1px solid #bbf7d0';
    }
    
    // Insere no topo do formulário
    const form = document.getElementById('contactForm');
    form.insertBefore(messageDiv, form.firstChild);
    
    // Remove mensagem de erro após 5 segundos
    if (type === 'error') {
        setTimeout(() => {
            if (messageDiv && messageDiv.parentNode) {
                messageDiv.remove();
            }
        }, 5000);
    }
}

/**
 * Envia o formulário via AJAX
 */
function submitForm(form) {
    const formData = new FormData(form);
    const submitButton = form.querySelector('button[type="submit"]');
    const originalButtonText = submitButton.innerHTML;
    
    // Desabilita o botão e mostra loading
    submitButton.disabled = true;
    submitButton.innerHTML = `
        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" style="animation: spin 1s linear infinite;">
            <circle cx="12" cy="12" r="10"></circle>
            <path d="M12 2v4M12 18v4M4.93 4.93l2.83 2.83M16.24 16.24l2.83 2.83M2 12h4M18 12h4M4.93 19.07l2.83-2.83M16.24 7.76l2.83-2.83"></path>
        </svg>
        Enviando...
    `;
    
    // Envia via fetch
    fetch(form.action, {
        method: 'POST',
        body: formData,
        headers: {
            'X-Requested-With': 'XMLHttpRequest'
        }
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            showFormMessage('Mensagem enviada com sucesso! Entraremos em contato em breve.', 'success');
            form.reset();
            
            // Remove mensagem de sucesso após 3 segundos
            setTimeout(() => {
                const message = document.querySelector('.form-message-success');
                if (message) message.remove();
            }, 3000);
        } else {
            showFormMessage(data.message || 'Erro ao enviar mensagem. Tente novamente.', 'error');
        }
    })
    .catch(error => {
        console.error('Erro:', error);
        showFormMessage('Erro de conexão. Verifique sua internet e tente novamente.', 'error');
    })
    .finally(() => {
        // Reabilita o botão
        submitButton.disabled = false;
        submitButton.innerHTML = originalButtonText;
    });
}

/**
 * Adiciona validação em tempo real nos campos
 */
function addRealTimeValidation() {
    const fields = ['nome', 'email', 'assunto', 'mensagem'];
    
    fields.forEach(fieldId => {
        const field = document.getElementById(fieldId);
        if (field) {
            field.addEventListener('blur', function() {
                validateField(fieldId);
            });
            
            field.addEventListener('input', function() {
                // Limpa erro enquanto digita
                const errorElement = document.getElementById(`${fieldId}-error`);
                if (errorElement) {
                    errorElement.remove();
                    field.classList.remove('error-field');
                }
            });
        }
    });
}

/**
 * Valida um campo específico
 */
function validateField(fieldId) {
    const field = document.getElementById(fieldId);
    if (!field) return false;
    
    const value = field.value.trim();
    let isValid = true;
    let errorMessage = '';
    
    switch(fieldId) {
        case 'nome':
            if (!value) {
                isValid = false;
                errorMessage = 'Nome completo é obrigatório';
            } else if (value.length < 3) {
                isValid = false;
                errorMessage = 'Nome muito curto';
            }
            break;
        case 'email':
            if (!value) {
                isValid = false;
                errorMessage = 'E-mail é obrigatório';
            } else if (!isValidEmail(value)) {
                isValid = false;
                errorMessage = 'Digite um e-mail válido';
            }
            break;
        case 'assunto':
            if (!value) {
                isValid = false;
                errorMessage = 'Selecione um assunto';
            }
            break;
        case 'mensagem':
            if (!value) {
                isValid = false;
                errorMessage = 'Mensagem é obrigatória';
            } else if (value.length < 10) {
                isValid = false;
                errorMessage = 'Mensagem muito curta (mínimo 10 caracteres)';
            }
            break;
    }
    
    if (!isValid) {
        showFieldError(fieldId, errorMessage);
    } else {
        clearFieldError(fieldId);
    }
    
    return isValid;
}

/**
 * Máscara para campo de telefone
 */
function initPhoneMask() {
    const phoneInput = document.getElementById('telefone');
    if (!phoneInput) return;
    
    phoneInput.addEventListener('input', function(e) {
        let value = e.target.value.replace(/\D/g, '');
        
        if (value.length > 0) {
            if (value.length <= 2) {
                value = `(${value}`;
            } else if (value.length <= 6) {
                value = `(${value.slice(0, 2)}) ${value.slice(2)}`;
            } else if (value.length <= 10) {
                value = `(${value.slice(0, 2)}) ${value.slice(2, 6)}-${value.slice(6)}`;
            } else {
                value = `(${value.slice(0, 2)}) ${value.slice(2, 7)}-${value.slice(7, 11)}`;
            }
        }
        
        e.target.value = value;
    });
}

/**
 * Verifica se há mensagem de sucesso na URL (após redirect)
 */
function checkForSuccessMessage() {
    const urlParams = new URLSearchParams(window.location.search);
    if (urlParams.get('success') === 'true') {
        showFormMessage('Mensagem enviada com sucesso! Entraremos em contato em breve.', 'success');
        
        // Limpa a URL
        window.history.replaceState({}, document.title, window.location.pathname);
    }
}

// Adiciona estilo para animação de loading
const style = document.createElement('style');
style.textContent = `
    @keyframes spin {
        from { transform: rotate(0deg); }
        to { transform: rotate(360deg); }
    }
    
    .error-field {
        border-color: #dc2626 !important;
        background-color: #fee2e2 !important;
    }
    
    .form-message {
        animation: slideDown 0.3s ease-out;
    }
    
    @keyframes slideDown {
        from {
            opacity: 0;
            transform: translateY(-10px);
        }
        to {
            opacity: 1;
            transform: translateY(0);
        }
    }
`;
document.head.appendChild(style);