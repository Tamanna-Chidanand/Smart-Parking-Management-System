// Payment Management Module
function initializePaymentModule() {
    // Load payment history
    loadPayments();
    
    // Set up event listeners
    document.getElementById('payment-form').addEventListener('submit', processPayment);
    document.getElementById('filter-payments').addEventListener('click', filterPayments);
    document.getElementById('apply-promo').addEventListener('click', applyPromoCode);
}

function loadPayments() {
    const paymentsList = document.getElementById('payments-list');
    paymentsList.innerHTML = '';
    
    // Get payments from localStorage (or API in real implementation)
    const payments = JSON.parse(localStorage.getItem('payments')) || [];
    const reservations = JSON.parse(localStorage.getItem('reservations')) || [];
    
    // Update recent payments on dashboard
    const recentAmount = payments.reduce((sum, payment) => sum + payment.amount, 0);
    document.getElementById('recent-payments').textContent = `$${recentAmount.toFixed(2)}`;
    
    // Filter payments based on date range
    const startDateInput = document.getElementById('payment-start-date');
    const endDateInput = document.getElementById('payment-end-date');
    
    let filteredPayments = payments;
    
    if (startDateInput.value && endDateInput.value) {
        const startDate = new Date(startDateInput.value);
        const endDate = new Date(endDateInput.value);
        
        filteredPayments = payments.filter(payment => {
            const paymentDate = new Date(payment.date);
            return paymentDate >= startDate && paymentDate <= endDate;
        });
    }
    
    if (filteredPayments.length === 0) {
        paymentsList.innerHTML = '<p>No payments found.</p>';
        return;
    }
    
    filteredPayments.forEach(payment => {
        const reservation = reservations.find(r => r.id === payment.reservationId) || {};
        
        const paymentCard = document.createElement('div');
        paymentCard.className = 'payment-card';
        
        const paymentDate = new Date(payment.date);
        
        paymentCard.innerHTML = `
            <div class="payment-header">
                <h3>Payment #${payment.id}</h3>
                <span>${paymentDate.toLocaleDateString()}</span>
            </div>
            <div class="payment-details">
                <div><strong>Amount:</strong> $${payment.amount.toFixed(2)}</div>
                <div><strong>Method:</strong> ${payment.method}</div>
                <div><strong>Reservation:</strong> ${reservation.spot || 'N/A'}</div>
                <div><strong>Status:</strong> ${payment.status}</div>
            </div>
        `;
        
        paymentsList.appendChild(paymentCard);
    });
}

function filterPayments() {
    loadPayments();
}

function processPayment(e) {
    e.preventDefault();
    
    const reservationId = parseInt(e.target.closest('.modal').dataset.reservationId);
    const method = document.getElementById('payment-method').value;
    const promoCode = document.getElementById('promo-code').value;
    
    // Get reservation
    const reservations = JSON.parse(localStorage.getItem('reservations')) || [];
    const reservation = reservations.find(r => r.id === reservationId);
    
    if (!reservation) {
        alert('Reservation not found!');
        return;
    }
    
    // Calculate final amount (apply promo if valid)
    let amount = reservation.amount;
    if (promoCode === 'PARK20') {
        amount *= 0.8; // 20% discount
    }
    
    // Create payment record
    const payments = JSON.parse(localStorage.getItem('payments')) || [];
    const newPayment = {
        id: payments.length > 0 ? Math.max(...payments.map(p => p.id)) + 1 : 1,
        reservationId,
        amount,
        method,
        date: new Date().toISOString(),
        status: 'completed'
    };
    
    payments.push(newPayment);
    localStorage.setItem('payments', JSON.stringify(payments));
    
    // Update reservation status
    reservation.status = 'completed';
    localStorage.setItem('reservations', JSON.stringify(reservations));
    
    // Close modal and reset form
    document.getElementById('payment-modal').style.display = 'none';
    e.target.reset();
    
    // Refresh lists
    loadPayments();
    loadReservations();
    
    alert('Payment processed successfully!');
}

function applyPromoCode() {
    const promoCode = document.getElementById('promo-code').value;
    const amountElement = document.getElementById('payment-amount');
    let amount = parseFloat(amountElement.textContent.replace('$', ''));
    
    if (promoCode === 'PARK20') {
        amount *= 0.8; // 20% discount
        amountElement.textContent = `$${amount.toFixed(2)}`;
        alert('Promo code applied! 20% discount added.');
    } else if (promoCode) {
        alert('Invalid promo code!');
    }
}