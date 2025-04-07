// Main application script
document.addEventListener('DOMContentLoaded', function() {
    // Tab navigation
    const tabs = document.querySelectorAll('nav a');
    tabs.forEach(tab => {
        tab.addEventListener('click', function(e) {
            e.preventDefault();
            // Remove active class from all tabs and content
            tabs.forEach(t => t.classList.remove('active'));
            document.querySelectorAll('.tab-content').forEach(c => c.classList.remove('active'));
            
            // Add active class to clicked tab and corresponding content
            this.classList.add('active');
            const tabId = this.getAttribute('data-tab');
            document.getElementById(tabId).classList.add('active');
        });
    });
    
    // Quick action buttons
    document.getElementById('quick-reserve').addEventListener('click', function() {
        // Switch to reservations tab and open modal
        tabs.forEach(t => t.classList.remove('active'));
        document.querySelectorAll('.tab-content').forEach(c => c.classList.remove('active'));
        document.querySelector('[data-tab="reservations"]').classList.add('active');
        document.getElementById('reservations').classList.add('active');
        document.getElementById('new-reservation-btn').click();
    });
    
    document.getElementById('quick-pay').addEventListener('click', function() {
        // Switch to payments tab
        tabs.forEach(t => t.classList.remove('active'));
        document.querySelectorAll('.tab-content').forEach(c => c.classList.remove('active'));
        document.querySelector('[data-tab="payments"]').classList.add('active');
        document.getElementById('payments').classList.add('active');
    });
    
    document.getElementById('quick-add-vehicle').addEventListener('click', function() {
        // Switch to vehicles tab and open modal
        tabs.forEach(t => t.classList.remove('active'));
        document.querySelectorAll('.tab-content').forEach(c => c.classList.remove('active'));
        document.querySelector('[data-tab="vehicles"]').classList.add('active');
        document.getElementById('vehicles').classList.add('active');
        document.getElementById('add-vehicle-btn').click();
    });
    
    // Modal handling
    const modals = document.querySelectorAll('.modal');
    const openModalButtons = document.querySelectorAll('[data-modal]');
    const closeModalButtons = document.querySelectorAll('.close-modal');
    
    function openModal(modalId) {
        document.getElementById(modalId).style.display = 'flex';
    }
    
    function closeModal(modalId) {
        document.getElementById(modalId).style.display = 'none';
    }
    
    // Open modal when corresponding button is clicked
    document.getElementById('new-reservation-btn').addEventListener('click', () => openModal('reservation-modal'));
    document.getElementById('add-vehicle-btn').addEventListener('click', () => openModal('vehicle-modal'));
    
    // Close modal when X is clicked
    closeModalButtons.forEach(button => {
        button.addEventListener('click', function() {
            this.closest('.modal').style.display = 'none';
        });
    });
    
    // Close modal when clicking outside content
    modals.forEach(modal => {
        modal.addEventListener('click', function(e) {
            if (e.target === this) {
                this.style.display = 'none';
            }
        });
    });
    
    // Payment method toggle
    document.getElementById('payment-method').addEventListener('change', function() {
        const methodFields = document.querySelectorAll('.payment-method-fields');
        methodFields.forEach(field => field.style.display = 'none');
        
        const selectedMethod = this.value;
        if (selectedMethod === 'credit' || selectedMethod === 'debit') {
            document.getElementById('credit-card-fields').style.display = 'block';
        }
    });
    
    // Initialize with some sample data
    initializeSampleData();
});

function initializeSampleData() {
    // Sample vehicles
    const vehicles = [
        { id: 1, plate: 'ABC123', make: 'Toyota', model: 'Camry', color: 'Blue', type: 'car' },
        { id: 2, plate: 'XYZ789', make: 'Honda', model: 'Civic', color: 'Red', type: 'car' }
    ];
    
    // Sample reservations
    const reservations = [
        { 
            id: 1, 
            vehicleId: 1, 
            spot: 'A-12', 
            startTime: '2023-10-15T10:00:00', 
            endTime: '2023-10-15T14:00:00', 
            status: 'completed',
            amount: 12.50
        },
        { 
            id: 2, 
            vehicleId: 2, 
            spot: 'B-05', 
            startTime: '2023-10-20T09:00:00', 
            endTime: '2023-10-20T17:00:00', 
            status: 'upcoming',
            amount: 25.00
        }
    ];
    
    // Sample parking spots
    const parkingSpots = [
        { id: 1, number: 'A-01', type: 'regular', status: 'available', location: 'Floor 1, North Wing' },
        { id: 2, number: 'A-02', type: 'regular', status: 'available', location: 'Floor 1, North Wing' },
        { id: 3, number: 'A-03', type: 'handicapped', status: 'reserved', location: 'Floor 1, North Wing' },
        { id: 4, number: 'A-04', type: 'regular', status: 'occupied', location: 'Floor 1, North Wing' },
        { id: 5, number: 'B-01', type: 'electric', status: 'available', location: 'Floor 1, South Wing' },
        { id: 6, number: 'B-02', type: 'regular', status: 'available', location: 'Floor 1, South Wing' }
    ];
    
    // Store data in localStorage for demo purposes
    localStorage.setItem('vehicles', JSON.stringify(vehicles));
    localStorage.setItem('reservations', JSON.stringify(reservations));
    localStorage.setItem('parkingSpots', JSON.stringify(parkingSpots));
    
    // Initialize other modules
    initializeParkingModule();
    initializeUserModule();
    initializeReservationModule();
    initializePaymentModule();
}