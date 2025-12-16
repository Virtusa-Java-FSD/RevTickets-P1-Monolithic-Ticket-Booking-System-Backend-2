# Seat Booking System Implementation

## Overview
This implementation provides a complete seat booking system where:
- Seats start as empty/available
- When users book seats, they are marked as booked
- Booked seats persist and show as unavailable on subsequent visits

## Backend APIs Added

### 1. Get Booked Seats
```
GET /api/shows/{showId}/booked-seats
```
Returns list of booked seat numbers for a show.

### 2. Get Seat Status
```
GET /api/shows/{showId}/seat-status
```
Returns complete seat information:
```json
{
  "totalSeats": 36,
  "availableSeats": 30,
  "bookedSeats": ["A1", "A2", "B3", "C4", "D5", "A8"]
}
```

### 3. Check Seat Availability
```
POST /api/shows/{showId}/check-seats
Content-Type: application/json

["A1", "A2", "B3"]
```
Returns:
```json
{
  "available": false,
  "unavailableSeats": ["A1", "B3"],
  "message": "Some seats are already booked"
}
```

### 4. Create Booking (Enhanced)
```
POST /api/bookings
Content-Type: application/json

{
  "showId": 1,
  "seats": ["A1", "A2"],
  "totalPrice": 300,
  "userId": 1
}
```

## Frontend Integration

### 1. Load Seat Status on Page Load
```javascript
async function loadSeatStatus() {
    const response = await fetch(`/api/shows/${showId}/seat-status`);
    const data = await response.json();
    
    // Update UI with booked seats
    data.bookedSeats.forEach(seat => {
        markSeatAsBooked(seat);
    });
}
```

### 2. Check Availability Before Booking
```javascript
async function bookSeats(selectedSeats) {
    // First check availability
    const checkResponse = await fetch(`/api/shows/${showId}/check-seats`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(selectedSeats)
    });
    
    const checkData = await checkResponse.json();
    
    if (!checkData.available) {
        alert(`Seats unavailable: ${checkData.unavailableSeats.join(', ')}`);
        return;
    }
    
    // Proceed with booking...
}
```

### 3. Book Seats
```javascript
async function createBooking(seats) {
    const bookingData = {
        showId: showId,
        seats: seats,
        totalPrice: seats.length * ticketPrice,
        userId: currentUserId
    };
    
    const response = await fetch('/api/bookings', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(bookingData)
    });
    
    if (response.ok) {
        // Booking successful - refresh seat status
        loadSeatStatus();
    }
}
```

## Database Schema

The system uses the existing `Show` model with:
- `bookedSeats`: List of booked seat identifiers
- `availableSeats`: Count of remaining available seats
- `totalSeats`: Total seat capacity

## How It Works

1. **Initial State**: All seats are available (empty `bookedSeats` list)

2. **Seat Selection**: Frontend allows users to select available seats

3. **Availability Check**: Before booking, system checks if selected seats are still available

4. **Booking Process**: 
   - Validates seat availability
   - Adds seats to `bookedSeats` list
   - Decrements `availableSeats` count
   - Creates booking record

5. **Persistence**: Booked seats remain in database and show as unavailable on subsequent visits

## Testing the System

1. Start your Spring Boot application
2. Open `seat-booking-example.html` in a browser
3. Select seats and book them
4. Refresh the page - booked seats should remain marked as unavailable
5. Try to book already booked seats - should show error message

## Frontend CSS Classes

```css
.seat.available { /* Green - clickable */ }
.seat.selected { /* Blue - user selected */ }
.seat.booked { /* Red - unavailable */ }
```

This implementation ensures that:
- ✅ Seats start empty
- ✅ Booked seats are persisted
- ✅ Booked seats show as unavailable on redirect/refresh
- ✅ Real-time availability checking
- ✅ Prevents double booking