# Payment & Ticket Storage Fix

## Problem Fixed
- Payments were not being stored in the database
- Tickets were not being created after successful payments
- Payment verification was not updating payment records

## Solution Implemented

### 1. Payment Storage Flow

**Step 1: Create Order**
```
POST /api/payment/create-order
```
- Creates Razorpay order
- Stores initial payment record with status "CREATED"

**Step 2: Payment Verification**
```
POST /api/payment/verify
```
- Verifies Razorpay signature
- Updates payment record with payment ID and status "CAPTURED"

**Step 3: Payment Success Recording**
```
POST /api/payment/success
```
- Updates payment status to "SUCCESS"
- Links payment to booking/ticket

### 2. Booking Integration

**Enhanced Booking Flow:**
```
POST /api/bookings
{
  "showId": 1,
  "seats": ["A1", "A2"],
  "totalPrice": 300,
  "paymentId": "pay_xyz123"
}
```

The booking service now:
- Finds payment record by paymentId
- Links payment to booking
- Creates ticket with payment information
- Updates payment record with ticket ID

### 3. New Endpoints Added

**Check Payment Status:**
```
GET /api/payment/status/{paymentId}
```
Returns payment and ticket information.

**List All Payments:**
```
GET /api/payment/all
```
Returns all payments for debugging.

### 4. Database Storage

**Payments Table:**
- `razorpay_order_id`: Order ID from Razorpay
- `razorpay_payment_id`: Payment ID after successful payment
- `amount`: Payment amount
- `status`: CREATED → CAPTURED → SUCCESS
- `ticket_id`: Links to generated ticket

**Tickets Table:**
- `ticket_id`: Unique ticket identifier
- `order_id`: Links to Razorpay order
- `user_id`: User who made the booking
- `total_amount`: Ticket price
- `status`: CONFIRMED/CANCELLED

### 5. Frontend Integration

```javascript
// 1. Create order
const orderResponse = await fetch('/api/payment/create-order', {
    method: 'POST',
    body: JSON.stringify({ amount: 300 })
});

// 2. Open Razorpay checkout
const rzp = new Razorpay({
    key: orderData.keyId,
    order_id: orderData.orderId,
    handler: async function(response) {
        // 3. Verify payment
        await fetch('/api/payment/verify', {
            method: 'POST',
            body: JSON.stringify({
                razorpay_order_id: response.razorpay_order_id,
                razorpay_payment_id: response.razorpay_payment_id,
                razorpay_signature: response.razorpay_signature
            })
        });
        
        // 4. Create booking with payment ID
        await fetch('/api/bookings', {
            method: 'POST',
            body: JSON.stringify({
                showId: 1,
                seats: ['A1', 'A2'],
                totalPrice: 300,
                paymentId: response.razorpay_payment_id
            })
        });
    }
});
```

### 6. Testing

1. **Start your Spring Boot application**
2. **Open `payment-integration-example.html`**
3. **Click "Pay Now" and complete payment**
4. **Check database tables:**
   - `payments` table should have the payment record
   - `tickets` table should have the generated ticket
5. **Verify with API:**
   ```
   GET /api/payment/all
   GET /api/payment/status/{paymentId}
   ```

## Key Changes Made

1. **PaymentController**: Enabled payment storage in create-order and verify endpoints
2. **BookingService**: Enhanced to link payments with bookings and tickets
3. **Payment Flow**: Complete end-to-end payment tracking
4. **Database Integration**: Proper storage of payments and tickets

Now successful payments will be stored in MongoDB/database and tickets will be generated automatically!