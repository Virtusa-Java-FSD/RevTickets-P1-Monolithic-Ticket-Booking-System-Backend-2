package com.revtickets.service;

import com.revtickets.model.Show;
import com.revtickets.repository.ShowRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ShowService {

    @Autowired
    private ShowRepository showRepository;

    public List<Show> getAllShows() {
        return showRepository.findAll();
    }

    public List<Show> getShowsByMovieId(Long movieId) {
        return showRepository.findByMovieId(movieId);
    }
    
    public List<Show> getShowsByEventId(Long eventId) {
        return showRepository.findByEventId(eventId);
    }

    public Show getShowById(Long id) {
        return showRepository.findById(id).orElse(null);
    }

    public Show createShow(Show show) {
        return showRepository.save(show);
    }

    public List<String> getBookedSeats(Long showId) {
        Show show = showRepository.findById(showId).orElse(null);
        if (show != null && show.getBookedSeats() != null) {
            return show.getBookedSeats();
        }
        return new java.util.ArrayList<>();
    }

    public java.util.Map<String, Object> checkSeatAvailability(Long showId, List<String> requestedSeats) {
        Show show = showRepository.findById(showId).orElse(null);
        java.util.Map<String, Object> response = new java.util.HashMap<>();
        
        if (show == null) {
            response.put("available", false);
            response.put("message", "Show not found");
            return response;
        }
        
        List<String> bookedSeats = show.getBookedSeats() != null ? show.getBookedSeats() : new java.util.ArrayList<>();
        List<String> unavailableSeats = new java.util.ArrayList<>();
        
        for (String seat : requestedSeats) {
            if (bookedSeats.contains(seat)) {
                unavailableSeats.add(seat);
            }
        }
        
        boolean allAvailable = unavailableSeats.isEmpty();
        response.put("available", allAvailable);
        response.put("unavailableSeats", unavailableSeats);
        response.put("message", allAvailable ? "All seats are available" : "Some seats are already booked");
        
        return response;
    }
}
