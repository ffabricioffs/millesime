(function() {
    'use strict';

    var modalOverlay = null;

    function initPreco() {
        var input = document.getElementById('preco');
        if (!input) return;
        input.addEventListener('keydown', function(e) {
            if (e.key === '-' || e.key === 'e' || e.key === 'E') e.preventDefault();
        });
        input.addEventListener('input', function() {
            var val = this.value;
            if (val.startsWith('-')) {
                this.value = val.replace('-', '');
                return;
            }
            var idx = val.indexOf('.');
            if (idx !== -1 && val.length - idx - 1 > 2) {
                this.value = val.substring(0, idx + 3);
            }
        });
    }

    function initEstoque() {
        var input = document.getElementById('estoque');
        if (!input) return;
        input.addEventListener('keydown', function(e) {
            if (e.key === '-' || e.key === '.' || e.key === ',') e.preventDefault();
        });
        input.addEventListener('input', function() {
            this.value = this.value.replace(/\D/g, '');
        });
    }

    function initCancelar() {
        var link = document.querySelector('.admin-form-actions a[href="/admin/produtos"]');
        if (!link) return;
        link.addEventListener('click', function(e) {
            e.preventDefault();
            showConfirmModal('Cancelar cadastro?', 'Os dados preenchidos ser\u00e3o perdidos. Deseja realmente cancelar?', function() {
                window.location.href = '/admin/produtos';
            });
        });
    }

    function initDeleteForms() {
        document.querySelectorAll('.form-deletar-produto').forEach(function(form) {
            form.addEventListener('submit', function(e) {
                e.preventDefault();
                showConfirmModal('Desativar produto?', 'O produto ser\u00e1 desativado do cat\u00e1logo.', function() {
                    form.submit();
                });
            });
        });
    }

    function ensureModal() {
        if (modalOverlay) return;
        modalOverlay = document.createElement('div');
        modalOverlay.className = 'modal-overlay';
        document.body.appendChild(modalOverlay);
        modalOverlay.addEventListener('click', function(e) {
            if (e.target === modalOverlay) hideModal();
        });
        document.addEventListener('keydown', function(e) {
            if (e.key === 'Escape') hideModal();
        });
    }

    function showConfirmModal(title, message, onConfirm) {
        ensureModal();
        modalOverlay.innerHTML =
            '<div class="modal-confirm">' +
                '<h3>' + title + '</h3>' +
                '<p>' + message + '</p>' +
                '<div class="modal-actions">' +
                    '<button class="btn-premium" id="modal-continuar">Cancelar</button>' +
                    '<button class="btn-premium-outline" id="modal-confirmar">Confirmar</button>' +
                '</div>' +
            '</div>';
        document.getElementById('modal-continuar').addEventListener('click', hideModal);
        document.getElementById('modal-confirmar').addEventListener('click', function() {
            hideModal();
            if (onConfirm) onConfirm();
        });
        modalOverlay.classList.add('active');
    }

    function hideModal() {
        if (modalOverlay) modalOverlay.classList.remove('active');
    }

    document.addEventListener('DOMContentLoaded', function() {
        initPreco();
        initEstoque();
        initCancelar();
        initDeleteForms();
    });
})();
