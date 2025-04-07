// payment.js - Handles payment processing
function initializePaymentModule() {
    // Load data from localStorage or API
    const payments = JSON.parse(localStorage.getItem('payments')) || [];
    const reservations = JSON.parse(localStorage.getItem('reservations')) || [];
    
    // Render payment history
    function renderPayments(startDate = null, endDate = null) {
        const paymentsList = document.getElementById('payments-list');
        paymentsList.innerHTML = '';
        
        let filteredPayments = payments;
        
        if (startDate && endDate) {
            filteredPayments = payments.filter(payment => {
                const paymentDate = new Date(payment.date);
                return paymentDate >= new Date(startDate) && paymentDate <= new Date(endDate);
            });
        }
        
        if (filteredPayments.length === 0) {
            paymentsList.innerHTML = '<p>No payments found.</p>';
            return;
        }
        
        filteredPayments.forEach(payment => {
            const reservation = reservations.find(r => r.id === payment.reservationId);
            
            const paymentElement = document.createElement('div');
            paymentElement.className = 'payment-card';
            
            paymentElement.innerHTML = `
                <div class="payment-header">
                    <h3>Payment #${payment.id}</h3>
                    <span>$${payment.amount.toFixed(2)}</span>
                </div>
                <div class="payment-details">
                    <div>
                        <strong>Date:</strong> ${new Date(payment.date).toLocaleDateString()}
                    </div>
                    <div>
                        <strong>Method:</strong> ${payment.method}
                    </div>
                    <div>
                        <strong>Reservation:</strong> ${reservation ? reservation.spot : 'N/A'}
                    </div>
                    <div>
                        <strong>Status:</strong> ${payment.status}
                    </div>
                    <div>
                        <strong>Transaction ID:</strong> ${payment.transactionId}
                    </div>
                </div>
            `;
            
            paymentsList.appendChild(paymentElement);
        });
    }
    
    // Filter payments by date range
    document.getElementById('filter-payments').addEventListener('click', function() {
        const startDate = document.getElementById('payment-start-date').value;
        const endDate = document.getElementById('payment-end-date').value;
        
        if (startDate && endDate) {
            renderPayments(startDate, endDate);
        } else {
            renderPayments();
        }
    });
    
    // Handle payment form submission
    document.getElementById('payment-form').addEventListener('submit', function(e) {
        e.preventDefault();
        
        const method = document.getElementById('payment-method').value;
        const invoiceNumber = document.getElementById('invoice-number').textContent;
        const amountText = document.getElementById('payment-amount').textContent;
        const amount = parseFloat(amountText.replace('$', ''));
        
        // Find the reservation from the invoice number
        const reservationId = parseInt(invoiceNumber.split('-')[1]) - 1000;
        const reservations = JSON.parse(localStorage.getItem('reservations'));
        const reservationIndex = reservations.findIndex(r => r.id === reservationId);
        
        if (reservationIndex === -1) {
            alert('Reservation not found.');
            return;
        }
        
        // Create payment record
        const payment = {
            id: payments.length + 1,
            date: new Date().toISOString(),
            reservationId,
            amount,
            method,
            status: 'completed',
            transactionId: `TXN-${Math.floor(Math.random() * 1000000)}`
        };
        
        // Update reservation
        reservations[reservationIndex].paid = true;
        
        // Save data
        payments.push(payment);
        localStorage.setItem('payments', JSON.stringify(payments));
        localStorage.setItem('reservations', JSON.stringify(reservations));
        
        // Close modal and refresh
        document.getElementById('payment-modal').style.display = 'none';
        renderPayments();
        initializeReservationModule();
        
        alert('Payment processed successfully!');
    });
    
    // Apply promo code
    document.getElementById('apply-promo').addEventListener('click', function() {
        const promoCode = document.getElementById('promo-code').value;
        const amountElement = document.getElementById('payment-amount');
        let amount = parseFloat(amountElement.textContent.replace('$', ''));
        
        // Simple promo code check
        if (promoCode === 'PARK20') {
            amount = amount * 0.8; // 20% discount
            amountElement.textContent = `$${amount.toFixed(2)}`;
            alert('Promo code applied! 20% discount added.');
        } else if (promoCode) {
            alert('Invalid promo code.');
        }
    });
    
    // Initial render
    renderPayments();
}