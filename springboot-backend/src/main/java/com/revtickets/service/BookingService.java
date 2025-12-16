package com.revtickets.service;

import com.revtickets.model.Booking;
import com.revtickets.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private com.revtickets.repository.ShowRepository showRepository;

    @Autowired
    private com.revtickets.repository.UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private com.revtickets.repository.PaymentRepository paymentRepository;

    @Autowired
    private com.revtickets.repository.TicketRepository ticketRepository;

    // @Autowired
    // private DataValidationService dataValidationService;

    public Booking createBooking(Booking booking) {
        // Validation
        if (booking == null) {
            throw new com.revtickets.exception.BookingException("Booking data is required");
        }
        if (booking.getTotalPrice() == null || booking.getTotalPrice() <= 0) {
            throw new com.revtickets.exception.BookingException("Valid total price is required");
        }
        if (booking.getSeats() == null || booking.getSeats().isEmpty()) {
            throw new com.revtickets.exception.BookingException("At least one seat must be selected");
        }
        if (booking.getShow() == null && booking.getEvent() == null && booking.getTravel() == null) {
            throw new com.revtickets.exception.BookingException("Show, Event, or Travel must be specified");
        }
        
        // Store payment ID for later use in ticket creation
        String paymentId = booking.getPaymentId();
        
        // Find payment record if paymentId is provided
        com.revtickets.model.Payment paymentRecord = null;
        if (paymentId != null) {
            paymentRecord = paymentRepository.findByRazorpayPaymentId(paymentId).orElse(null);
            if (paymentRecord != null) {
                paymentRecord.setAmount(booking.getTotalPrice());
                paymentRepository.save(paymentRecord);
            }
        }

        if (booking.getShow() != null && booking.getShow().getId() != null) {
            com.revtickets.model.Show show = showRepository.findById(booking.getShow().getId()).orElse(null);
            if (show == null) {
                throw new com.revtickets.exception.BookingException("Show not found with ID: " + booking.getShow().getId());
            }
                int seatsToBook = booking.getSeats() != null ? booking.getSeats().size() : 0;
                if (show.getAvailableSeats() >= seatsToBook) {
                    show.setAvailableSeats(show.getAvailableSeats() - seatsToBook);
                    
                    if (booking.getSeats() != null) {
                        if (show.getBookedSeats() == null) {
                            show.setBookedSeats(new java.util.ArrayList<>());
                        }
                        // Check for duplicates
                        for (String seat : booking.getSeats()) {
                            if (show.getBookedSeats().contains(seat)) {
                                throw new com.revtickets.exception.BookingException("Seat " + seat + " is already booked");
                            }
                        }
                        show.getBookedSeats().addAll(booking.getSeats());
                    }
                    
                    showRepository.save(show);
                    booking.setShow(show); // Ensure full object is set
                } else {
                    throw new com.revtickets.exception.BookingException("Not enough seats available. Available: " + show.getAvailableSeats() + ", Requested: " + seatsToBook);
                }
        }
        
        // Ensure User is loaded if only ID is passed (though frontend sends object usually, but let's be safe)
        if (booking.getUser() != null && booking.getUser().getId() != null) {
             // If we just have ID in a User object (e.g. from JSON { "user": { "id": 1 } }), 
             // we might want to fetch full User to ensure consistency, 
             // but Hibernate EntityManager.getReference/find usually handles it if cascading.
             // For safety in relationships:
             com.revtickets.model.User user = userRepository.findById(booking.getUser().getId()).orElse(null);
             if (user != null) {
                 booking.setUser(user);
             }
        }

        Booking savedBooking = bookingRepository.save(booking);
        
        // Generate and Save Ticket
        try {
            createTicketFromBooking(savedBooking, paymentId, paymentRecord);
        } catch (Exception e) {
            System.err.println("Failed to generate ticket: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Send e-ticket email
        try {
            if (savedBooking.getUser() != null) {
                // If user object is loaded (it should be if we fetched/saved it)
                if (savedBooking.getUser().getEmail() != null) {
                    emailService.sendBookingConfirmation(savedBooking, savedBooking.getUser().getEmail());
                } else {
                    // Fetch if needed
                     com.revtickets.model.User user = userRepository.findById(savedBooking.getUser().getId()).orElse(null);
                     if (user != null) {
                         emailService.sendBookingConfirmation(savedBooking, user.getEmail());
                     }
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to send booking email, but booking was successful: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Log successful booking creation
        System.out.println("Booking created successfully with ID: " + savedBooking.getId());
        if (paymentId != null) {
            System.out.println("Payment ID: " + paymentId + " linked to booking");
        }
        
        return savedBooking;
    }
    
    private com.revtickets.model.Ticket createTicketFromBooking(Booking booking, String paymentId, com.revtickets.model.Payment paymentRecord) {
        com.revtickets.model.Ticket ticket = new com.revtickets.model.Ticket();
        ticket.setTicketId("TKT-" + System.currentTimeMillis());
        ticket.setUser(booking.getUser());
        ticket.setStatus("CONFIRMED");
        ticket.setTotalAmount(booking.getTotalPrice());
        ticket.setCreatedAt(java.time.LocalDateTime.now());
        
        if (booking.getSeats() != null) {
            ticket.setSeatNumbers(String.join(",", booking.getSeats()));
            ticket.setPassengerCount(booking.getSeats().size());
        }
        
        if (paymentRecord != null) {
            ticket.setOrderId(paymentRecord.getRazorpayOrderId());
            // Link payment to ticket
            paymentRecord.setTicketId(ticket.getTicketId());
            paymentRepository.save(paymentRecord);
        }

        if (booking.getShow() != null) {
            ticket.setTicketType("MOVIE");
            // Assuming Show has relation to Movie, but if not directly accessible we use what we have
             ticket.setTitle("Movie Show"); 
             ticket.setToLocation(booking.getShow().getTheater());
             try {
                java.time.format.DateTimeFormatter timeFormatter = java.time.format.DateTimeFormatter.ofPattern("h:mm a", java.util.Locale.US);
                ticket.setJourneyDateTime(java.time.LocalDateTime.of(
                    java.time.LocalDate.parse(booking.getShow().getShowDate()), 
                    java.time.LocalTime.parse(booking.getShow().getShowTime(), timeFormatter)
                ));
             } catch (Exception e) {
                System.err.println("Warning: Failed to parse show date/time: " + e.getMessage() + ". Using current time.");
                ticket.setJourneyDateTime(java.time.LocalDateTime.now());
             }
        } else if (booking.getTravel() != null) {
            ticket.setTicketType("BUS");
            ticket.setTitle(booking.getTravel().getOperator());
            ticket.setFromLocation(booking.getTravel().getDeparture());
            ticket.setToLocation(booking.getTravel().getArrival());
            // Parse date/time if possible
            // ticket.setJourneyDateTime(...) 
        } else if (booking.getEvent() != null) {
            ticket.setTicketType("EVENT");
            ticket.setTitle(booking.getEvent().getTitle());
            ticket.setToLocation(booking.getEvent().getLocation());
            // ticket.setJourneyDateTime(booking.getEvent().getEventDate()...)
        }
        
        com.revtickets.model.Ticket savedTicket = ticketRepository.save(ticket);
        
        // Store payment ID in ticket for reference
        if (paymentId != null) {
            savedTicket.setOrderId(paymentId);
            ticketRepository.save(savedTicket);
            System.out.println("Payment ID stored in ticket: " + paymentId);
        }
        
        return savedTicket;
    }

    public List<Booking> getBookingsByUserId(Long userId) {
        return bookingRepository.findByUserId(userId);
    }
    
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public Booking getBookingById(Long id) {
        return bookingRepository.findById(id).orElse(null);
    }

    public Booking cancelBooking(Long id) {
        Booking booking = getBookingById(id);
        if (booking != null) {
            booking.setStatus("CANCELLED");
            return bookingRepository.save(booking);
        }
        return null;
    }
    
    public void deleteBooking(Long id) {
        bookingRepository.deleteById(id);
    }
}