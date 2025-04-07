// parking.js - Handles parking spot management
function initializeParkingModule() {
    // Load parking spots from localStorage or API
    const parkingSpots = JSON.parse(localStorage.getItem('parkingSpots')) || [];
    const parkingGrid = document.getElementById('parking-grid');
    
    // Render parking spots
    function renderParkingSpots(spots = parkingSpots) {
        parkingGrid.innerHTML = '';
        
        spots.forEach(spot => {
            const spotElement = document.createElement('div');
            spotElement.className = `parking-spot spot-${spot.status}`;
            
            spotElement.innerHTML = `
                <div class="spot-type">${spot.type}</div>
                <div class="spot-number">${spot.number}</div>
                <div class="spot-status">${spot.status.charAt(0).toUpperCase() + spot.status.slice(1)}</div>
                <div class="spot-location">${spot.location}</div>
                <div class="spot-actions">
                    ${spot.status === 'available' ? 
                        `<button class="btn" onclick="reserveSpot(${spot.id})">Reserve</button>` : 
                        `<button class="btn btn-outline" disabled>Unavailable</button>`}
                </div>
            `;
            
            parkingGrid.appendChild(spotElement);
        });
    }
    
    // Filter parking spots
    document.getElementById('spot-type-filter').addEventListener('change', function() {
        const type = this.value;
        if (type === 'all') {
            renderParkingSpots();
        } else {
            const filtered = parkingSpots.filter(spot => spot.type === type);
            renderParkingSpots(filtered);
        }
    });
    
    // Search parking spots
    document.getElementById('spot-search').addEventListener('input', function() {
        const query = this.value.toLowerCase();
        const filtered = parkingSpots.filter(spot => 
            spot.location.toLowerCase().includes(query) || 
            spot.number.toLowerCase().includes(query)
        );
        renderParkingSpots(filtered);
    });
    
    // Refresh parking spots
    document.getElementById('refresh-spots').addEventListener('click', function() {
        renderParkingSpots();
        document.getElementById('spot-search').value = '';
        document.getElementById('spot-type-filter').value = 'all';
    });
    
    // Initial render
    renderParkingSpots();
}

// Reserve a parking spot
function reserveSpot(spotId) {
    // In a real app, this would call your backend API
    const parkingSpots = JSON.parse(localStorage.getItem('parkingSpots'));
    const spotIndex = parkingSpots.findIndex(spot => spot.id === spotId);
    
    if (spotIndex !== -1 && parkingSpots[spotIndex].status === 'available') {
        parkingSpots[spotIndex].status = 'reserved';
        localStorage.setItem('parkingSpots', JSON.stringify(parkingSpots));
        
        // Show reservation modal
        document.getElementById('reservation-modal').style.display = 'flex';
        document.getElementById('reservation-vehicle').value = '';
        document.getElementById('reservation-date').valueAsDate = new Date();
        document.getElementById('reservation-start').value = '09:00';
        document.getElementById('reservation-end').value = '17:00';
        document.getElementById('reservation-type').value = parkingSpots[spotIndex].type;
        
        // Refresh parking grid
        initializeParkingModule();
    }
}