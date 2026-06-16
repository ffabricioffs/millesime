(function() {
    'use strict';

    var btn = document.querySelector('.btn-cancelar-pedido');
    if (!btn) return;
    btn.addEventListener('click', function(e) {
        if (!confirm('Tem certeza que deseja cancelar este pedido?')) {
            e.preventDefault();
        }
    });
})();
