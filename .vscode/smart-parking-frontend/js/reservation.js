// reservation.js - Handles parking spot reservations
function initializeReservationModule() {
    // Load data from localStorage or API
    const reservations = JSON.parse(localStorage.getItem('reservations')) || [];
    const vehicles = JSON.parse(localStorage.getItem('vehicles')) || [];
    
    // Populate vehicle dropdown in reservation form
    const vehicleSelect = document.getElementById('reservation-vehicle');
    vehicleSelect.innerHTML = vehicles.map(vehicle => 
        `<option value="${vehicle.id}">${vehicle.make} ${vehicle.model} (${vehicle.plate})</option>`
    ).join('');
    
    // Render reservations
    function renderReservations(filter = 'all') {
        const reservationsList = document.getElementById('reservations-list');
        reservationsList.innerHTML = '';
        
        let filteredReservations = reservations;
        if (filter !== 'all') {
            filteredReservations = reservations.filter(res => res.status === filter);
        }
        
        if (filteredReservations.length === 0) {
            reservationsList.innerHTML = '<p>No reservations found.</p>';
            return;
        }
        
        filteredReservations.forEach(reservation => {
            const vehicle = vehicles.find(v => v.id === reservation.vehicleId);
            const startDate = new Date(reservation.startTime);
            const endDate = new Date(reservation.endTime);
            
            const reservationElement = document.createElement('div');
            reservationElement.className = 'reservation-card';
            
            reservationElement.innerHTML = `
                <div class="reservation-header">
                    <h3>Reservation #${reservation.id}</h3>
                    <span class="reservation-status status-${reservation.status}">
                        ${reservation.status.charAt(0).toUpperCase() + reservation.status.slice(1)}
                    </span>
                </div>
                <div class="reservation-details">
                    <div>
                        <strong>Vehicle:</strong> ${vehicle.make} ${vehicle.model} (${vehicle.plate})
                    </div>
                    <div>
                        <strong>Spot:</strong> ${reservation.spot}
                    </div>
                    <div>
                        <strong>Date:</strong> ${startDate.toLocaleDateString()}
                    </div>
                    <div>
                        <strong>Time:</strong> ${startDate.toLocaleTimeString()} - ${endDate.toLocaleTimeString()}
                    </div>
                    <div>
                        <strong>Amount:</strong> $${reservation.amount.toFixed(2)}
                    </div>
                </div>
                <div class="reservation-actions">
                    ${reservation.status === 'upcoming' ? 
                        `<button class="btn btn-small" onclick="cancelReservation(${reservation.id})">Cancel</button>` : ''}
                    ${reservation.status === 'completed' && !reservation.paid ? 
                        `<button class="btn btn-small" onclick="payForReservation(${reservation.id})">Pay Now</button>` : ''}
                </div>
            `;
            
            reservationsList.appendChild(reservationElement);
        });
    }
    
    // Filter reservations
    document.getElementById('reservation-filter').addEventListener('change', function() {
        renderReservations(this.value);
    });
    
    // Handle new reservation form
    document.getElementById('reservation-form').addEventListener('submit', function(e) {
        e.preventDefault();
        
        const vehicleId = parseInt(document.getElementById('reservation-vehicle').value);
        const date = document.getElementById('reservation-date').value;
        const startTime = document.getElementById('reservation-start').value;
        const endTime = document.getElementById('reservation-end').value;
        const type = document.getElementById('reservation-type').value;
        
        // In a real app, you would call your backend API to check availability
        const availableSpots = JSON.parse(localStorage.getItem('parkingSpots'))
            .filter(spot => spot.type === type && spot.status === 'available');
        
        const availableSpotsContainer = document.getElementById('available-spots-reservation');
        availableSpotsContainer.innerHTML = '';
        
        if (availableSpots.length === 0) {
            availableSpotsContainer.innerHTML = '<p>No available spots found for the selected criteria.</p>';
            return;
        }
        
        availableSpotsContainer.innerHTML = '<h3>Available Spots:</h3>';
        
        availableSpots.forEach(spot => {
            const spotElement = document.createElement('div');
            spotElement.className = 'parking-spot spot-available';
            spotElement.innerHTML = `
                <div class="spot-type">${spot.type}</div>
                <div class="spot-number">${spot.number}</div>
                <div class="spot-location">${spot.location}</div>
                <button class="btn" onclick="confirmReservation(${spot.id}, ${vehicleId}, '${date}', '${startTime}', '${endTime}')">
                    Reserve This Spot
                </button>
            `;
            availableSpotsContainer.appendChild(spotElement);
        });
    });
    
    // Initial render
    renderReservations();
}

// Confirm reservation
function confirmReservation(spotId, vehicleId, date, startTime, endTime) {
    const parkingSpots = JSON.parse(localStorage.getItem('parkingSpots'));
    const reservations = JSON.parse(localStorage.getItem('reservations'));
    const spot = parkingSpots.find(s => s.id === spotId);
    
    if (!spot || spot.status !== 'available') {
        alert('This spot is no longer available.');
        return;
    }
    
    // Calculate hours for pricing (simplified)
    const start = new Date(`${date}T${startTime}`);
    const end = new Date(`${date}T${endTime}`);
    const hours = (end - start) / (1000 * 60 * 60);
    const amount = hours * 5; // $5 per hour
    
    const newReservation = {
        id: reservations.length + 1,
        vehicleId,
        spot: spot.number,
        startTime: start.toISOString(),
        endTime: end.toISOString(),
        status: 'upcoming',
        amount,
        paid: false
    };
    
    // Update spot status
    spot.status = 'reserved';
    
    // Save data
    reservations.push(newReservation);
    localStorage.setItem('reservations', JSON.stringify(reservations));
    localStorage.setItem('parkingSpots', JSON.stringify(parkingSpots));
    
    // Close modal and refresh
    document.getElementById('reservation-modal').style.display = 'none';
    initializeReservationModule();
    initializeParkingModule();
    
    alert(`Reservation confirmed for spot ${spot.number}!`);
}

// Cancel reservation
function cancelReservation(reservationId) {
    const reservations = JSON.parse(localStorage.getItem('reservations'));
    const parkingSpots = JSON.parse(localStorage.getItem('parkingSpots'));
    
    const reservationIndex = reservations.findIndex(r => r.id === reservationId);
    if (reservationIndex === -1) return;
    
    const reservation = reservations[reservationIndex];
    if (reservation.status !== 'upcoming') {
        alert('Only upcoming reservations can be canceled.');
        return;
    }
    
    // Find and update the parking spot
    const spot = parkingSpots.find(s => s.number === reservation.spot);
    if (spot) {
        spot.status = 'available';
    }
    
    // Update reservation status
    reservation.status = 'cancelled';
    
    // Save data
    localStorage.setItem('reservations', JSON.stringify(reservations));
    localStorage.setItem('parkingSpots', JSON.stringify(parkingSpots));
    
    // Refresh UI
    initializeReservationModule();
    initializeParkingModule();
    
    alert('Reservation canceled successfully.');
}

// Pay for reservation
function payForReservation(reservationId) {
    // In a real app, this would open the payment modal with the reservation details
    const reservation = JSON.parse(localStorage.getItem('reservations'))
        .find(r => r.id === reservationId);
    
    if (!reservation) return;
    
    document.getElementById('payment-modal').style.display = 'flex';
    document.getElementById('invoice-number').textContent = `INV-${1000 + reservationId}`;
    document.getElementById('payment-reservation').textContent = 
        `${reservation.spot} (${new Date(reservation.startTime).toLocaleDateString()})`;
    document.getElementById('payment-amount').textContent = `$${reservation.amount.toFixed(2)}`;
}