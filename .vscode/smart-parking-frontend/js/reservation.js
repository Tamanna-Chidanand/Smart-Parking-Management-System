// Reservation Management Module
function initializeReservationModule() {
    // Load reservations
    loadReservations();
    
    // Set up event listeners
    document.getElementById('reservation-form').addEventListener('submit', checkAvailability);
    document.getElementById('reservation-filter').addEventListener('change', filterReservations);
}

function loadReservations() {
    const reservationsList = document.getElementById('reservations-list');
    reservationsList.innerHTML = '';
    
    // Get reservations from localStorage (or API in real implementation)
    const reservations = JSON.parse(localStorage.getItem('reservations')) || [];
    const vehicles = JSON.parse(localStorage.getItem('vehicles')) || [];
    
    // Update active reservations count on dashboard
    const activeCount = reservations.filter(r => r.status === 'upcoming' || r.status === 'active').length;
    document.getElementById('active-reservations').textContent = activeCount;
    
    // Filter reservations based on current selection
    const filter = document.getElementById('reservation-filter').value;
    let filteredReservations = reservations;
    
    if (filter === 'upcoming') {
        filteredReservations = reservations.filter(r => r.status === 'upcoming' || r.status === 'active');
    } else if (filter === 'past') {
        filteredReservations = reservations.filter(r => r.status === 'completed' || r.status === 'cancelled');
    } else if (filter === 'cancelled') {
        filteredReservations = reservations.filter(r => r.status === 'cancelled');
    }
    
    if (filteredReservations.length === 0) {
        reservationsList.innerHTML = '<p>No reservations found.</p>';
        return;
    }
    
    filteredReservations.forEach(reservation => {
        const vehicle = vehicles.find(v => v.id === reservation.vehicleId) || {};
        
        const reservationCard = document.createElement('div');
        reservationCard.className = 'reservation-card';
        
        const startDate = new Date(reservation.startTime);
        const endDate = new Date(reservation.endTime);
        
        reservationCard.innerHTML = `
            <div class="reservation-header">
                <h3>Reservation #${reservation.id}</h3>
                <span class="reservation-status status-${reservation.status}">
                    ${reservation.status.charAt(0).toUpperCase() + reservation.status.slice(1)}
                </span>
            </div>
            <div class="reservation-details">
                <div><strong>Vehicle:</strong> ${vehicle.make} ${vehicle.model} (${vehicle.plate})</div>
                <div><strong>Spot:</strong> ${reservation.spot}</div>
                <div><strong>Date:</strong> ${startDate.toLocaleDateString()}</div>
                <div><strong>Time:</strong> ${startDate.toLocaleTimeString()} - ${endDate.toLocaleTimeString()}</div>
                <div><strong>Amount:</strong> $${reservation.amount.toFixed(2)}</div>
            </div>
            <div class="reservation-actions">
                ${reservation.status === 'upcoming' ? `
                    <button class="btn btn-small" onclick="modifyReservation(${reservation.id})">Modify</button>
                    <button class="btn btn-small btn-outline" onclick="cancelReservation(${reservation.id})">Cancel</button>
                    <button class="btn btn-small" onclick="payForReservation(${reservation.id})">Pay Now</button>
                ` : ''}
                ${reservation.status === 'completed' ? `
                    <button class="btn btn-small" disabled>Completed</button>
                ` : ''}
                ${reservation.status === 'cancelled' ? `
                    <button class="btn btn-small" disabled>Cancelled</button>
                ` : ''}
            </div>
        `;
        
        reservationsList.appendChild(reservationCard);
    });
}

function filterReservations() {
    loadReservations();
}

function checkAvailability(e) {
    e.preventDefault();
    
    const vehicleId = parseInt(document.getElementById('reservation-vehicle').value);
    const date = document.getElementById('reservation-date').value;
    const startTime = document.getElementById('reservation-start').value;
    const endTime = document.getElementById('reservation-end').value;
    const spotType = document.getElementById('reservation-type').value;
    
    // Validate inputs
    if (!vehicleId || !date || !startTime || !endTime) {
        alert('Please fill all required fields!');
        return;
    }
    
    // In a real app, we would send this to the API to check availability
    // For demo, we'll just show some available spots
    
    const availableSpotsContainer = document.getElementById('available-spots-reservation');
    availableSpotsContainer.innerHTML = '<h3>Available Spots</h3>';
    
    // Get parking spots from localStorage
    const spots = JSON.parse(localStorage.getItem('parkingSpots')) || [];
    
    // Filter available spots of the selected type
    const availableSpots = spots.filter(spot => 
        spot.status === 'available' && spot.type === spotType
    );
    
    if (availableSpots.length === 0) {
        availableSpotsContainer.innerHTML += '<p>No available spots found for your criteria.</p>';
        return;
    }
    
    availableSpots.forEach(spot => {
        const spotElement = document.createElement('div');
        spotElement.className = 'available-spot';
        spotElement.innerHTML = `
            <div class="spot-info">
                <strong>${spot.number}</strong> - ${spot.type} (${spot.location})
            </div>
            <button class="btn btn-small" onclick="confirmReservation(${spot.id}, ${vehicleId}, '${date}', '${startTime}', '${endTime}')">
                Reserve This Spot
            </button>
        `;
        availableSpotsContainer.appendChild(spotElement);
    });
}

function confirmReservation(spotId, vehicleId, date, startTime, endTime) {
    // In a real app, we would send this to the API
    const spots = JSON.parse(localStorage.getItem('parkingSpots')) || [];
    const spot = spots.find(s => s.id === spotId);
    
    if (!spot) {
        alert('Spot not found!');
        return;
    }
    
    // Create new reservation
    const reservations = JSON.parse(localStorage.getItem('reservations')) || [];
    const newReservation = {
        id: reservations.length > 0 ? Math.max(...reservations.map(r => r.id)) + 1 : 1,
        vehicleId,
        spot: spot.number,
        startTime: `${date}T${startTime}:00`,
        endTime: `${date}T${endTime}:00`,
        status: 'upcoming',
        amount: calculateReservationAmount(startTime, endTime)
    };
    
    reservations.push(newReservation);
    localStorage.setItem('reservations', JSON.stringify(reservations));
    
    // Update spot status
    spot.status = 'reserved';
    localStorage.setItem('parkingSpots', JSON.stringify(spots));
    
    // Close modal and reset form
    document.getElementById('reservation-modal').style.display = 'none';
    document.getElementById('reservation-form').reset();
    
    // Refresh reservations and parking spots
    loadReservations();
    loadParkingSpots();
    
    alert('Reservation created successfully!');
}

function calculateReservationAmount(startTime, endTime) {
    // Simple calculation for demo purposes
    // In a real app, this would be more complex
    const start = new Date(`2000-01-01T${startTime}`);
    const end = new Date(`2000-01-01T${endTime}`);
    const hours = (end - start) / (1000 * 60 * 60);
    
    // $2.50 per hour with $5 minimum
    return Math.max(5, hours * 2.5);
}

function modifyReservation(reservationId) {
    // In a real app, we would implement modification logic
    alert(`Modifying reservation #${reservationId}`);
}

function cancelReservation(reservationId) {
    if (confirm('Are you sure you want to cancel this reservation?')) {
        // Get reservations
        const reservations = JSON.parse(localStorage.getItem('reservations')) || [];
        const reservation = reservations.find(r => r.id === reservationId);
        
        if (reservation) {
            // Update status
            reservation.status = 'cancelled';
            localStorage.setItem('reservations', JSON.stringify(reservations));
            
            // Free up the parking spot
            const spots = JSON.parse(localStorage.getItem('parkingSpots')) || [];
            const spot = spots.find(s => s.number === reservation.spot);
            
            if (spot) {
                spot.status = 'available';
                localStorage.setItem('parkingSpots', JSON.stringify(spots));
            }
            
            // Refresh lists
            loadReservations();
            loadParkingSpots();
            
            alert('Reservation cancelled successfully!');
        }
    }
}

function payForReservation(reservationId) {
    // Get reservation
    const reservations = JSON.parse(localStorage.getItem('reservations')) || [];
    const reservation = reservations.find(r => r.id === reservationId);
    
    if (reservation) {
        // Set payment details in modal
        document.getElementById('invoice-number').textContent = `INV-${reservation.id}`;
        document.getElementById('payment-reservation').textContent = 
            `${reservation.spot} (${new Date(reservation.startTime).toLocaleDateString()}, ` +
            `${new Date(reservation.startTime).toLocaleTimeString()}-${new Date(reservation.endTime).toLocaleTimeString()}`;
        document.getElementById('payment-amount').textContent = `$${reservation.amount.toFixed(2)}`;
        
        // Store reservation ID in modal for later reference
        document.getElementById('payment-modal').dataset.reservationId = reservationId;
        
        // Open payment modal
        openModal('payment-modal');
    }
}