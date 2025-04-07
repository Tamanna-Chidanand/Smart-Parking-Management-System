// user.js - Handles user profile and authentication
function initializeUserModule() {
    // Load user data from localStorage or API
    const user = JSON.parse(localStorage.getItem('user')) || {
        id: 1,
        name: 'John Doe',
        email: 'john.doe@example.com',
        phone: '555-123-4567'
    };
    
    // Populate profile form
    document.getElementById('profile-name').value = user.name;
    document.getElementById('profile-email').value = user.email;
    document.getElementById('profile-phone').value = user.phone;
    
    // Handle profile update
    document.getElementById('profile-form').addEventListener('submit', function(e) {
        e.preventDefault();
        
        const updatedUser = {
            ...user,
            name: document.getElementById('profile-name').value,
            email: document.getElementById('profile-email').value,
            phone: document.getElementById('profile-phone').value
        };
        
        const password = document.getElementById('profile-password').value;
        const confirmPassword = document.getElementById('profile-confirm').value;
        
        if (password && password !== confirmPassword) {
            alert('Passwords do not match!');
            return;
        }
        
        if (password) {
            updatedUser.password = password; // In a real app, you would hash this
        }
        
        // Save to localStorage (in a real app, this would be an API call)
        localStorage.setItem('user', JSON.stringify(updatedUser));
        alert('Profile updated successfully!');
    });
    
    // Handle logout
    document.getElementById('logout-btn').addEventListener('click', function() {
        // In a real app, you would clear the authentication token
        localStorage.removeItem('authToken');
        window.location.href = 'login.html'; // Redirect to login page
    });
}