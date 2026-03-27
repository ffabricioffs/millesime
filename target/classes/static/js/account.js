/**
 * Millésime - Account Page
 * Controla a alternância entre formulários de Login e Cadastro
 */

document.addEventListener('DOMContentLoaded', function () {
    // Elementos
    const loginColumn = document.querySelector('.account-column-left');
    const registerColumn = document.querySelector('.account-column-right');
    const showRegisterBtn = document.getElementById('show-register');
    const showLoginBtn = document.getElementById('show-login');
    
    // Verificar se estamos em mobile ou desktop
    function isDesktop() {
        return window.innerWidth > 768;
    }
    
    /**
     * Estado inicial
     */
    function initializeState() {
        if (isDesktop()) {
            // Em desktop: ambas as colunas visíveis
            loginColumn.classList.add('active');
            registerColumn.classList.add('active');
        } else {
            // Em mobile: apenas login visível
            loginColumn.classList.add('active');
            registerColumn.classList.remove('active');
        }
    }
    
    /**
     * Mostra formulário de login
     */
    function showLoginForm() {
        loginColumn.classList.add('active');
        registerColumn.classList.remove('active');
    }
    
    /**
     * Mostra formulário de cadastro
     */
    function showRegisterForm() {
        registerColumn.classList.add('active');
        loginColumn.classList.remove('active');
    }
    
    /**
     * Event listeners para alternância em mobile
     */
    if (showRegisterBtn) {
        showRegisterBtn.addEventListener('click', function (e) {
            e.preventDefault();
            if (!isDesktop()) {
                showRegisterForm();
                window.scrollTo({ top: 0, behavior: 'smooth' });
            }
        });
    }
    
    if (showLoginBtn) {
        showLoginBtn.addEventListener('click', function (e) {
            e.preventDefault();
            if (!isDesktop()) {
                showLoginForm();
                window.scrollTo({ top: 0, behavior: 'smooth' });
            }
        });
    }
    
    /**
     * Handle responsividade
     */
    window.addEventListener('resize', function () {
        if (isDesktop()) {
            // Em desktop, mostrar ambas as colunas
            loginColumn.classList.add('active');
            registerColumn.classList.add('active');
        } else {
            // Em mobile, manter apenas login visível
            loginColumn.classList.add('active');
            registerColumn.classList.remove('active');
        }
    });
    
    // Inicializar
    initializeState();
});
