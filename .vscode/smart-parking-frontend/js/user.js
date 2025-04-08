// User and Vehicle Management Module
function initializeUserModule() {
    // Load user profile
    loadUserProfile();
    
    // Load vehicles
    loadVehicles();
    
    // Set up event listeners
    document.getElementById('vehicle-form').addEventListener('submit', addVehicle);
    document.getElementById('profile-form').addEventListener('submit', updateProfile);
}

function loadUserProfile() {
    // In a real app, this would come from an API
    const user = {
        name: 'John Doe',
        email: 'john.doe@example.com',
        phone: '555-123-4567'
    };
    
    document.getElementById('profile-name').value = user.name;
    document.getElementById('profile-email').value = user.email;
    document.getElementById('profile-phone').value = user.phone;
}

function updateProfile(e) {
    e.preventDefault();
    
    const name = document.getElementById('profile-name').value;
    const email = document.getElementById('profile-email').value;
    const phone = document.getElementById('profile-phone').value;
    const password = document.getElementById('profile-password').value;
    const confirm = document.getElementById('profile-confirm').value;
    
    if (password && password !== confirm) {
        alert('Passwords do not match!');
        return;
    }
    
    // In a real app, we would send this to the API
    console.log('Updating profile:', { name, email, phone, password });
    alert('Profile updated successfully!');
}

function loadVehicles() {
    const vehiclesList = document.getElementById('vehicles-list');
    vehiclesList.innerHTML = '';
    
    // Get vehicles from localStorage (or API in real implementation)
    const vehicles = JSON.parse(localStorage.getItem('vehicles')) || [];
    
    // Also populate vehicle dropdown in reservation form
    const vehicleDropdown = document.getElementById('reservation-vehicle');
    vehicleDropdown.innerHTML = '<option value="">Select Vehicle</option>';
    
    vehicles.forEach(vehicle => {
        // Add to vehicles list
        const vehicleCard = document.createElement('div');
        vehicleCard.className = 'vehicle-card';
        vehicleCard.innerHTML = `
            <div class="vehicle-header">
                <h3>${vehicle.make} ${vehicle.model}</h3>
                <span class="vehicle-type">${vehicle.type}</span>
            </div>
            <div class="vehicle-details">
                <div><strong>License Plate:</strong> ${vehicle.plate}</div>
                <div><strong>Color:</strong> ${vehicle.color}</div>
            </div>
            <div class="vehicle-actions">
                <button class="btn btn-small btn-outline">Edit</button>
                <button class="btn btn-small" onclick="deleteVehicle(${vehicle.id})">Delete</button>
            </div>
        `;
        vehiclesList.appendChild(vehicleCard);
        
        // Add to dropdown
        const option = document.createElement('option');
        option.value = vehicle.id;
        option.textContent = `${vehicle.make} ${vehicle.model} (${vehicle.plate})`;
        vehicleDropdown.appendChild(option);
    });
}

function addVehicle(e) {
    e.preventDefault();
    
    const plate = document.getElementById('vehicle-plate').value;
    const make = document.getElementById('vehicle-make').value;
    const model = document.getElementById('vehicle-model').value;
    const color = document.getElementById('vehicle-color').value;
    const type = document.getElementById('vehicle-type').value;
    
    // In a real app, we would validate inputs first
    
    // Get existing vehicles
    const vehicles = JSON.parse(localStorage.getItem('vehicles')) || [];
    
    // Add new vehicle
    const newVehicle = {
        id: vehicles.length > 0 ? Math.max(...vehicles.map(v => v.id)) + 1 : 1,
        plate,
        make,
        model,
        color,
        type
    };
    
    vehicles.push(newVehicle);
    localStorage.setItem('vehicles', JSON.stringify(vehicles));
    
    // Refresh the list
    loadVehicles();
    
    // Close modal and reset form
    document.getElementById('vehicle-modal').style.display = 'none';
    e.target.reset();
    
    alert('Vehicle added successfully!');
}

function deleteVehicle(id) {
    if (confirm('Are you sure you want to delete this vehicle?')) {
        // Get existing vehicles
        let vehicles = JSON.parse(localStorage.getItem('vehicles')) || [];
        
        // Filter out the vehicle to delete
        vehicles = vehicles.filter(vehicle => vehicle.id !== id);
        
        // Save back to localStorage
        localStorage.setItem('vehicles', JSON.stringify(vehicles));
        
        // Refresh the list
        loadVehicles();
        
        alert('Vehicle deleted successfully!');
    }
}