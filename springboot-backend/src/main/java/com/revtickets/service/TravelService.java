package com.revtickets.service;

import com.revtickets.model.Travel;
import com.revtickets.repository.TravelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TravelService {
    
    @Autowired
    private TravelRepository travelRepository;
    
    public List<Travel> searchTravel(String type, String from, String to) {
        if (type != null && from != null && to != null) {
            return travelRepository.findByTypeAndDepartureAndArrival(type, from, to);
        } else if (from != null && to != null) {
            return travelRepository.findByDepartureAndArrival(from, to);
        } else if (type != null) {
            return travelRepository.findByType(type);
        }
        return travelRepository.findAll();
    }
    
    public List<Travel> getFlights(String from, String to) {
        return travelRepository.findByTypeAndDepartureAndArrival("flight", from, to);
    }
    
    public List<Travel> getBuses(String from, String to) {
        return travelRepository.findByTypeAndDepartureAndArrival("bus", from, to);
    }
    
    public List<Travel> getTrains(String from, String to) {
        return travelRepository.findByTypeAndDepartureAndArrival("train", from, to);
    }
    
    // Admin CRUD operations
    public List<Travel> getAllTravels() {
        return travelRepository.findAll();
    }
    
    public Travel createTravel(Travel travel) {
        return travelRepository.save(travel);
    }
    
    public Travel updateTravel(Travel travel) {
        if (!travelRepository.existsById(travel.getId())) {
            throw new RuntimeException("Travel not found with id: " + travel.getId());
        }
        return travelRepository.save(travel);
    }
    
    public void deleteTravel(Long id) {
        if (!travelRepository.existsById(id)) {
            throw new RuntimeException("Travel not found with id: " + id);
        }
        travelRepository.deleteById(id);
    }
    
    public void seedTravelData() {
        travelRepository.deleteAll();
        
        Travel[] travels = {
            // Flights
            createTravel("flight", "IndiGo", "6E-123", "Delhi", "Mumbai", "06:00", "08:30", "2h 30m", 4500.0, 45, 4.2, new String[]{"WiFi", "Meals"}),
            createTravel("flight", "Air India", "AI-456", "Delhi", "Mumbai", "14:00", "16:30", "2h 30m", 5200.0, 32, 4.0, new String[]{"WiFi", "Entertainment"}),
            createTravel("flight", "SpiceJet", "SG-789", "Mumbai", "Bangalore", "09:15", "11:00", "1h 45m", 3800.0, 28, 4.1, new String[]{"Snacks"}),
            
            // Buses
            createTravel("bus", "Volvo", "KA-01-AB-1234", "Delhi", "Mumbai", "20:00", "12:00+1", "16h", 1200.0, 25, 4.3, new String[]{"AC", "WiFi", "Charging Point"}),
            createTravel("bus", "Mercedes", "MH-02-CD-5678", "Mumbai", "Pune", "22:30", "05:30+1", "7h", 800.0, 18, 4.5, new String[]{"AC", "Blanket", "Water"}),
            createTravel("bus", "Scania", "TN-03-EF-9012", "Bangalore", "Chennai", "23:00", "06:00+1", "7h", 900.0, 22, 4.2, new String[]{"AC", "WiFi", "Movies"}),
            
            // Trains
            createTravel("train", "Rajdhani Express", "12951", "Delhi", "Mumbai", "16:55", "08:35+1", "15h 40m", 2500.0, 50, 4.4, new String[]{"AC", "Meals", "Bedding"}),
            createTravel("train", "Shatabdi Express", "12027", "Delhi", "Bhopal", "06:00", "14:25", "8h 25m", 1800.0, 35, 4.3, new String[]{"AC", "Meals"}),
            createTravel("train", "Duronto Express", "12259", "Mumbai", "Bangalore", "21:45", "11:30+1", "13h 45m", 2200.0, 40, 4.1, new String[]{"AC", "Meals"})
        };
        
        for (Travel travel : travels) {
            travelRepository.save(travel);
        }
    }
    
    private Travel createTravel(String type, String operator, String vehicleNumber, String departure, 
                              String arrival, String departureTime, String arrivalTime, String duration, 
                              Double price, Integer availableSeats, Double rating, String[] amenities) {
        Travel travel = new Travel();
        travel.setType(type);
        travel.setOperator(operator);
        travel.setVehicleNumber(vehicleNumber);
        travel.setDeparture(departure);
        travel.setArrival(arrival);
        travel.setDepartureTime(departureTime);
        travel.setArrivalTime(arrivalTime);
        travel.setDuration(duration);
        travel.setPrice(price);
        travel.setAvailableSeats(availableSeats);
        travel.setRating(rating);
        travel.setAmenities(amenities);
        return travel;
    }
}