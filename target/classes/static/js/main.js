/**
 * Millésime - Main JavaScript
 * Premium Wine Store Frontend
 */

// ========== DOM Ready ==========
document.addEventListener('DOMContentLoaded', function() {
    initializeApp();
});

function initializeApp() {
    initializeMobileMenu();
    initializeFilters();
    initializeProductInteractions();
    initializeCartFunctionality();
}

// ========== Mobile Menu ==========
function initializeMobileMenu() {
    const mobileMenuToggle = document.getElementById('mobile-menu-toggle');
    if (!mobileMenuToggle) return;

    mobileMenuToggle.addEventListener('click', function() {
        const nav = this.parentElement.parentElement.querySelector('nav');
        if (nav) {
            nav.style.display = nav.style.display === 'none' ? 'block' : 'none';
        }
    });
}

// ========== Filters ==========
function initializeFilters() {
    const filterButtons = document.querySelectorAll('input[type="checkbox"]');
    filterButtons.forEach(button => {
        button.addEventListener('change', function() {
            // Filter logic would go here
            console.log('Filter changed:', this.name, this.value);
        });
    });
}

// ========== Product Interactions ==========
function initializeProductInteractions() {
    // Quantity selector
    const quantityButtons = document.querySelectorAll('button[data-action="decrease"], button[data-action="increase"]');
    quantityButtons.forEach(button => {
        button.addEventListener('click', function() {
            const input = this.parentElement.querySelector('input[type="number"]');
            if (input) {
                const currentValue = parseInt(input.value) || 1;
                if (this.dataset.action === 'increase') {
                    input.value = currentValue + 1;
                } else if (this.dataset.action === 'decrease' && currentValue > 1) {
                    input.value = currentValue - 1;
                }
            }
        });
    });

    // Product image gallery
    const thumbnails = document.querySelectorAll('img[data-gallery]');
    const mainImage = document.querySelector('img[data-main-image]');
    
    thumbnails.forEach(thumbnail => {
        thumbnail.addEventListener('click', function() {
            if (mainImage) {
                mainImage.src = this.src;
                mainImage.alt = this.alt;
            }
            
            // Update active state
            thumbnails.forEach(t => t.style.borderColor = 'var(--border)');
            this.style.borderColor = 'var(--accent)';
        });
    });

    // Add to cart buttons
    const addToCartButtons = document.querySelectorAll('button:contains("Adicionar ao Carrinho")');
    addToCartButtons.forEach(button => {
        button.addEventListener('click', function(e) {
            e.preventDefault();
            addToCart(this);
        });
    });

    // Wishlist buttons
    const wishlistButtons = document.querySelectorAll('button:contains("Lista de Desejos")');
    wishlistButtons.forEach(button => {
        button.addEventListener('click', function(e) {
            e.preventDefault();
            toggleWishlist(this);
        });
    });
}

// ========== Cart Functionality ==========
function initializeCartFunctionality() {
    // Cart count update
    updateCartCount();
}

function addToCart(button) {
    const quantity = button.parentElement.querySelector('input[type="number"]')?.value || 1;
    const productId = button.dataset.productId || 'unknown';
    
    console.log('Adding to cart:', { productId, quantity });
    
    // Show feedback
    const originalText = button.textContent;
    button.textContent = '✓ Adicionado!';
    button.style.backgroundColor = '#22c55e';
    
    setTimeout(() => {
        button.textContent = originalText;
        button.style.backgroundColor = '';
    }, 2000);

    // Update cart count
    updateCartCount();
}

function toggleWishlist(button) {
    button.classList.toggle('active');
    
    if (button.classList.contains('active')) {
        button.style.borderColor = 'var(--accent)';
        button.style.color = 'var(--accent)';
        button.textContent = '♥ Adicionado à Lista de Desejos';
    } else {
        button.style.borderColor = '';
        button.style.color = '';
        button.textContent = '♡ Adicionar à Lista de Desejos';
    }
}

function updateCartCount() {
    // This would typically fetch from the server
    const cartBadge = document.querySelector('.cart-badge');
    if (cartBadge) {
        const count = parseInt(cartBadge.textContent) || 0;
        if (count > 0) {
            cartBadge.style.display = 'flex';
        }
    }
}

// ========== Utilities ==========

// Smooth scroll for anchor links
document.querySelectorAll('a[href^="#"]').forEach(anchor => {
    anchor.addEventListener('click', function(e) {
        const href = this.getAttribute('href');
        if (href !== '#') {
            e.preventDefault();
            const target = document.querySelector(href);
            if (target) {
                target.scrollIntoView({ behavior: 'smooth' });
            }
        }
    });
});

// Format currency
function formatCurrency(value) {
    return new Intl.NumberFormat('pt-BR', {
        style: 'currency',
        currency: 'BRL'
    }).format(value);
}

// Format date
function formatDate(date) {
    return new Intl.DateTimeFormat('pt-BR', {
        year: 'numeric',
        month: 'long',
        day: 'numeric'
    }).format(new Date(date));
}

// Debounce function for search
function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

// Search functionality
const searchInput = document.querySelector('input[type="search"]');
if (searchInput) {
    searchInput.addEventListener('input', debounce(function(e) {
        const query = e.target.value;
        if (query.length > 2) {
            performSearch(query);
        }
    }, 300));
}

function performSearch(query) {
    console.log('Searching for:', query);
    // Search logic would go here
}

// ========== Form Validation ==========
const forms = document.querySelectorAll('form');
forms.forEach(form => {
    form.addEventListener('submit', function(e) {
        if (!validateForm(this)) {
            e.preventDefault();
        }
    });
});

function validateForm(form) {
    const inputs = form.querySelectorAll('input[required], textarea[required], select[required]');
    let isValid = true;

    inputs.forEach(input => {
        if (!input.value.trim()) {
            input.style.borderColor = '#ef4444';
            isValid = false;
        } else {
            input.style.borderColor = '';
        }

        // Email validation
        if (input.type === 'email' && input.value) {
            const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
            if (!emailRegex.test(input.value)) {
                input.style.borderColor = '#ef4444';
                isValid = false;
            }
        }
    });

    return isValid;
}

// ========== Animations ==========
const observerOptions = {
    threshold: 0.1,
    rootMargin: '0px 0px -100px 0px'
};

const observer = new IntersectionObserver(function(entries) {
    entries.forEach(entry => {
        if (entry.isIntersecting) {
            entry.target.style.animation = 'fadeInUp 0.6s ease-out forwards';
            observer.unobserve(entry.target);
        }
    });
}, observerOptions);

document.querySelectorAll('.card-elegant, .section').forEach(element => {
    observer.observe(element);
});

// ========== Accessibility ==========
document.addEventListener('keydown', function(e) {
    // Escape key to close modals
    if (e.key === 'Escape') {
        const modals = document.querySelectorAll('[role="dialog"][open]');
        modals.forEach(modal => modal.close?.());
    }
});

// ========== Console Logging ==========
console.log('%cMillésime - Premium Wine Store', 'font-size: 20px; font-weight: bold; color: #5C2E3A;');
console.log('%cDesign System: Neoclassic Elegant', 'font-size: 14px; color: #D4AF37;');
