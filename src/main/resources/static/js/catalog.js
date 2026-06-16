(function() {
    const productGrid = document.getElementById('product-grid');
    if (!productGrid) return;

    const filterForm = document.querySelector('.catalog-filters form');
    if (filterForm) {
        filterForm.addEventListener('submit', function(e) {
            e.preventDefault();
            var params = buildParams(new FormData(this));
            loadProducts('/catalogo?' + params.toString());
        });
    }

    productGrid.addEventListener('click', function(e) {
        var pagLink = e.target.closest('.catalog-pagination a');
        if (pagLink) {
            e.preventDefault();
            loadProducts(pagLink.href);
            return;
        }
        var clearLink = e.target.closest('.btn-filter-clear');
        if (clearLink) {
            e.preventDefault();
            loadProducts(clearLink.href);
        }
    });

    window.addEventListener('popstate', function(e) {
        if (e.state && e.state.url) {
            loadProducts(e.state.url, true);
        }
    });

    function buildParams(formData) {
        var params = new URLSearchParams();
        for (var entry of formData) {
            if (entry[1]) params.set(entry[0], entry[1]);
        }
        return params;
    }

    function loadProducts(url, replaceStateOnly) {
        fetch(url, { headers: { 'X-Requested-With': 'XMLHttpRequest' } })
            .then(function(r) { return r.text(); })
            .then(function(html) {
                var temp = document.createElement('div');
                temp.innerHTML = html;
                var newGrid = temp.querySelector('#product-grid');
                if (newGrid) {
                    productGrid.innerHTML = newGrid.innerHTML;
                }
                if (!replaceStateOnly) {
                    history.pushState({ url: url }, '', url);
                }
                if (typeof initializeProductInteractions === 'function') {
                    initializeProductInteractions();
                }
            })
            .catch(function() {
                window.location.href = url;
            });
    }
})();
