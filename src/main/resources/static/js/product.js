/**
 * Millésime - Product Page JavaScript
 * Tabs functionality and quantity button fallback
 */

document.addEventListener('DOMContentLoaded', function() {
    initializeTabs();
    if (!window.MillesimeMainQuantityReady) {
        initializeQuantityButtons();
    }
});

function initializeTabs() {
    var tabButtons = document.querySelectorAll('.product-tabs-nav button');
    var tabContents = document.querySelectorAll('.product-tab-content');
    if (!tabButtons.length || !tabContents.length) return;

    tabButtons.forEach(function(btn, idx) {
        btn.addEventListener('click', function() {
            tabButtons.forEach(function(b) {
                b.className = 'product-tab-button';
            });
            this.className = 'product-tab-button-active';
            tabContents.forEach(function(tc) {
                tc.style.display = 'none';
            });
            if (tabContents[idx]) {
                tabContents[idx].style.display = 'block';
            }
        });
    });
}

function initializeQuantityButtons() {
    document.querySelectorAll('.product-quantity-selector').forEach(function(el) {
        var dec = el.querySelector('button[data-action="decrease"]');
        var inc = el.querySelector('button[data-action="increase"]');
        var input = el.querySelector('.product-quantity-input');
        if (dec && inc && input) {
            dec.addEventListener('click', function() {
                var v = parseInt(input.value) || 1;
                if (v > 1) input.value = v - 1;
            });
            inc.addEventListener('click', function() {
                var v = parseInt(input.value) || 1;
                input.value = v + 1;
            });
        }
    });
}
