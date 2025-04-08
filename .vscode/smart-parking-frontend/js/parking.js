// Parking Management Module
function initializeParkingModule() {
    // Load parking spots
    loadParkingSpots();
    
    // Set up event listeners
    document.getElementById('refresh-spots').addEventListener('click', loadParkingSpots);
    document.getElementById('spot-search').addEventListener('input', filterParkingSpots);
    document.getElementById('spot-type-filter').addEventListener('change', filterParkingSpots);
}

function loadParkingSpots() {
    const parkingGrid = document.getElementById('parking-grid');
    parkingGrid.innerHTML = '';
    
    // Get spots from localStorage (or API in real implementation)
    const spots = JSON.parse(localStorage.getItem('parkingSpots')) || [];
    
    // Update available spots count on dashboard
    const availableCount = spots.filter(spot => spot.status === 'available').length;
    document.getElementById('available-spots').textContent = availableCount;
    
    // Create spot cards
    spots.forEach(spot => {
        const spotCard = document.createElement('div');
        spotCard.className = `parking-spot spot-${spot.status}`;
        
        let actionButton;
        if (spot.status === 'available') {
            actionButton = `<button class="btn btn-small" onclick="reserveSpot('${spot.number}')">Reserve</button>`;
        } else if (spot.status === 'reserved') {
            actionButton = `<button class="btn btn-small btn-outline" disabled>Reserved</button>`;
        } else {
            actionButton = `<button class="btn btn-small btn-outline" disabled>Occupied</button>`;
        }
        
        spotCard.innerHTML = `
            <div class="spot-type">${spot.type}</div>
            <div class="spot-number">${spot.number}</div>
            <div class="spot-status">${spot.status.charAt(0).toUpperCase() + spot.status.slice(1)}</div>
            <div class="spot-location">${spot.location}</div>
            <div class="spot-actions">${actionButton}</div>
        `;
        
        parkingGrid.appendChild(spotCard);
    });
}

function filterParkingSpots() {
    const searchTerm = document.getElementById('spot-search').value.toLowerCase();
    const typeFilter = document.getElementById('spot-type-filter').value;
    
    const spotCards = document.querySelectorAll('.parking-spot');
    
    spotCards.forEach(card => {
        const spotNumber = card.querySelector('.spot-number').textContent.toLowerCase();
        const spotType = card.querySelector('.spot-type').textContent.toLowerCase();
        const spotLocation = card.querySelector('.spot-location').textContent.toLowerCase();
        
        const matchesSearch = spotNumber.includes(searchTerm) || spotLocation.includes(searchTerm);
        const matchesType = typeFilter === 'all' || spotType.includes(typeFilter.toLowerCase());
        
        if (matchesSearch && matchesType) {
            card.style.display = 'block';
        } else {
            card.style.display = 'none';
        }
    });
}

function reserveSpot(spotNumber) {
    // Open reservation modal with this spot pre-selected
    openModal('reservation-modal');
    // In a real implementation, we would pre-fill the spot selection
    alert(`Reserving spot ${spotNumber}. Please complete the reservation form.`);
}