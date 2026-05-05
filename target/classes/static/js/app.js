// ============================
// Category Tab / Pill Filtering
// ============================
document.addEventListener('DOMContentLoaded', () => {

    const pills = document.querySelectorAll('.category-pill');
    const sections = document.querySelectorAll('.menu-section');

    pills.forEach(pill => {
        pill.addEventListener('click', () => {
            pills.forEach(p => p.classList.remove('active'));
            pill.classList.add('active');
            const cat = pill.dataset.category;
            sections.forEach(sec => {
                sec.style.display = (cat === 'all' || sec.dataset.category === cat) ? '' : 'none';
            });
        });
    });

    // ============================
    // Order Page – Interactive Cart
    // ============================
    const orderRows = document.querySelectorAll('.item-row');
    if (orderRows.length === 0) return;

    const cartItemsEl  = document.getElementById('cartItems');
    const cartSummary  = document.getElementById('cartSummary');
    const emptyCartBtn = document.getElementById('emptyCartBtn');
    const cartTotalEl  = document.getElementById('cartTotal');

    const cart = {}; // { id: { name, price, qty } }

    orderRows.forEach(row => {
        const id    = row.dataset.id;
        const name  = row.dataset.name;
        const price = parseFloat(row.dataset.price);
        const input = row.querySelector('.qty-input');
        const plus  = row.querySelector('.qty-btn.plus');
        const minus = row.querySelector('.qty-btn.minus');

        plus.addEventListener('click', () => {
            let qty = parseInt(input.value) + 1;
            if (qty > 20) return;
            input.value = qty;
            row.classList.toggle('has-qty', qty > 0);
            updateCart(id, name, price, qty);
        });

        minus.addEventListener('click', () => {
            let qty = Math.max(0, parseInt(input.value) - 1);
            input.value = qty;
            row.classList.toggle('has-qty', qty > 0);
            updateCart(id, name, price, qty);
        });
    });

    function updateCart(id, name, price, qty) {
        if (qty === 0) delete cart[id];
        else cart[id] = { name, price, qty };
        renderCart();
    }

    function renderCart() {
        const isEmpty = Object.keys(cart).length === 0;

        if (isEmpty) {
            cartItemsEl.innerHTML = `
                <div class="cart-empty-state">
                    <i class="bi bi-bag"></i>
                    <p class="mb-0" style="font-size:0.9rem;">Your cart is empty.<br/>Add items from the menu!</p>
                </div>`;
            cartSummary.style.display  = 'none';
            emptyCartBtn.style.display = '';
            return;
        }

        let html = '';
        let total = 0;
        for (const item of Object.values(cart)) {
            const sub = item.price * item.qty;
            total += sub;
            html += `<div class="cart-item-row">
                <span class="cart-item-name">${item.qty}× ${item.name}</span>
                <span class="cart-item-price">$${sub.toFixed(2)}</span>
            </div>`;
        }

        cartItemsEl.innerHTML = html;
        cartTotalEl.textContent = '$' + total.toFixed(2);
        cartSummary.style.display  = '';
        emptyCartBtn.style.display = 'none';
    }
});
