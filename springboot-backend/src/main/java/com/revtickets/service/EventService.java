package com.revtickets.service;

import com.revtickets.model.Event;
import com.revtickets.repository.EventRepository;
import com.revtickets.exception.EventNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Arrays;

@Service
public class EventService {
    
    @Autowired
    private EventRepository eventRepository;
    
    @Autowired
    private com.revtickets.repository.ShowRepository showRepository;
    
    @Autowired
    private com.revtickets.repository.TheaterRepository theaterRepository;
    
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }
    
    public List<Event> getEventsByCategory(String category) {
        return eventRepository.findByCategory(category);
    }
    
    public Event getEventById(Long id) {
        return eventRepository.findById(id)
            .orElseThrow(() -> new EventNotFoundException("Event not found with id: " + id));
    }
    
    public Event createEvent(Event event) {
        Event savedEvent = eventRepository.save(event);
        if ("movie".equals(event.getCategory()) || "concert".equals(event.getCategory())) {
            createShowsForEvent(savedEvent);
        }
        return savedEvent;
    }
    
    public void createShowsForEvent(Event event) {
        List<com.revtickets.model.Theater> theaters = theaterRepository.findByIsActiveTrue();
        if (theaters.isEmpty()) {
            System.out.println("No active theaters found for event: " + event.getId());
            return;
        }
        
        System.out.println("Creating shows for event: " + event.getId() + " with " + theaters.size() + " theaters");
        for (com.revtickets.model.Theater t : theaters) {
            System.out.println("  - Theater: " + t.getName() + " (Active: " + t.getIsActive() + ")");
        }
        
        String[] dates = {
            java.time.LocalDate.now().plusDays(1).toString(),
            java.time.LocalDate.now().plusDays(2).toString(),
            java.time.LocalDate.now().plusDays(3).toString()
        };
        String[] times = {"5:00 PM", "7:00 PM", "9:00 PM"};
        
        List<com.revtickets.model.Show> shows = new java.util.ArrayList<>();
        
        for (String date : dates) {
            for (String time : times) {
                for (com.revtickets.model.Theater theater : theaters) {
                    com.revtickets.model.Show show = new com.revtickets.model.Show();
                    show.setEventId(event.getId());
                    if ("movie".equals(event.getCategory())) {
                        show.setMovieId(event.getId());
                    }
                    show.setShowDate(date);
                    show.setShowTime(time);
                    show.setTotalSeats(100);
                    show.setAvailableSeats(100);
                    show.setPrice(event.getPrice());
                    show.setTheater(theater.getName());
                    shows.add(show);
                }
            }
        }
        
        if (!shows.isEmpty()) {
            showRepository.saveAll(shows);
            System.out.println("Created " + shows.size() + " shows for event: " + event.getId());
        }
    }
    
    public Event updateEvent(Event event) {
        if (!eventRepository.existsById(event.getId())) {
            throw new EventNotFoundException("Event not found with id: " + event.getId());
        }
        Event updatedEvent = eventRepository.save(event);
        if (("movie".equals(event.getCategory()) || "concert".equals(event.getCategory()))) {
            List<com.revtickets.model.Show> existingShows = showRepository.findByEventId(event.getId());
            if (existingShows.isEmpty()) {
                createShowsForEvent(updatedEvent);
            }
        }
        return updatedEvent;
    }
    
    public void deleteEvent(Long id) {
        if (!eventRepository.existsById(id)) {
            throw new EventNotFoundException("Event not found with id: " + id);
        }
        eventRepository.deleteById(id);
    }
    
    public List<Event> searchEvents(String query, String category) {
        List<Event> events = category != null ? getEventsByCategory(category) : getAllEvents();
        return events.stream()
            .filter(event -> event.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                           event.getDescription().toLowerCase().contains(query.toLowerCase()))
            .toList();
    }
    
    public List<Event> filterMovies(String language, Double minRating) {
        List<Event> movies = getEventsByCategory("movie");
        return movies.stream()
            .filter(movie -> (language == null || language.equals(movie.getLanguage())) &&
                           (minRating == null || movie.getRating() >= minRating))
            .toList();
    }
    
    public void seedDatabase() {
        eventRepository.deleteAll();
        
        Event[] events = {
            createMovieEvent("Inception", "A thief who steals corporate secrets through dream-sharing technology.",
                "https://image.tmdb.org/t/p/w500/9gk7adHYeDvHkCSEqAvQNLV5Uge.jpg", 
                8.8, "Sci-Fi, Thriller", 148, "2010-07-16", "English", 250.0),
            createMovieEvent("The Dark Knight", "When the menace known as the Joker wreaks havoc on Gotham.",
                "https://image.tmdb.org/t/p/w500/qJ2tW6WMUDux911r6m7haRef0WH.jpg", 
                9.0, "Action, Crime", 152, "2008-07-18", "English", 300.0),
            createMovieEvent("Pushpa", "A man rises to power by dealing in red sanders.",
                "https://image.tmdb.org/t/p/w500/vVpEOvdxVBP2aV166j5Xlvb5Cdc.jpg", 
                7.5, "Action, Drama", 179, "2021-12-17", "Telugu", 200.0),
            createMovieEvent("RRR", "A fictional story about two revolutionaries.",
                "https://image.tmdb.org/t/p/w500/wR0PIlQGKXRhS0JQUZlVsFf7IL6.jpg", 
                8.0, "Action, Drama", 187, "2022-03-25", "Telugu", 250.0),
            createMovieEvent("3 Idiots", "Two friends are searching for their long lost companion.",
                "https://image.tmdb.org/t/p/w500/66A9MqXOyVFCssoloscw79z8U0Y.jpg", 
                8.4, "Comedy, Drama", 170, "2009-12-25", "Hindi", 220.0),
            
            createConcertEvent("Ed Sheeran World Tour", "Experience the magic of Ed Sheeran live in concert",
                "https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=300&h=400&fit=crop", 
                9.2, Arrays.asList("Pop"), 1500.0),
            createConcertEvent("Coldplay Music of the Spheres", "Coldplay's spectacular world tour with stunning visuals",
                "https://images.unsplash.com/photo-1540039155733-5bb30b53aa14?w=300&h=400&fit=crop", 
                9.5, Arrays.asList("Rock"), 2000.0),
            
            createOtherEvent("Tech Conference 2025", "Leading innovations & tech showcases", "Technology",
                "https://images.unsplash.com/photo-1540575467063-178a50c2df87?w=400", 4.5, 799.0),
            createOtherEvent("Food Festival", "Taste cuisines from around the world", "Food & Beverage",
                "https://images.unsplash.com/photo-1555939594-58d7cb561ad1?w=400", 4.8, 499.0)
        };
        
        List<Event> savedEvents = eventRepository.saveAll(Arrays.asList(events));
        
        seedTheaters();
        
        List<com.revtickets.model.Theater> theaters = theaterRepository.findByIsActiveTrue();
        
        showRepository.deleteAll();
        List<com.revtickets.model.Show> shows = new java.util.ArrayList<>();
        
        String[] dates = {
             java.time.LocalDate.now().plusDays(1).toString(),
             java.time.LocalDate.now().plusDays(2).toString(),
             java.time.LocalDate.now().plusDays(3).toString()
        };
        String[] times = {"5:00 PM", "7:00 PM", "9:00 PM"};

        for (Event event : savedEvents) {
            if ("concert".equals(event.getCategory()) || "movie".equals(event.getCategory())) {
                for (String date : dates) {
                    for (String time : times) {
                        for (com.revtickets.model.Theater theater : theaters) {
                            com.revtickets.model.Show show = new com.revtickets.model.Show();
                            show.setEventId(event.getId());
                            if ("movie".equals(event.getCategory())) {
                                 show.setMovieId(event.getId());
                            }
                            show.setShowDate(date);
                            show.setShowTime(time);
                            show.setTotalSeats(100);
                            show.setAvailableSeats(100);
                            show.setPrice(event.getPrice());
                            show.setTheater(theater.getName());
                            shows.add(show);
                        }
                    }
                }
            }
        }
        showRepository.saveAll(shows);
    }
    
    public void seedTheaters() {
        String[][] theaterData = {
            {"PVR Cinemas", "Mumbai", "Maharashtra"},
            {"INOX", "Delhi", "Delhi"},
            {"Cinepolis", "Bangalore", "Karnataka"},
            {"Main Hall", "Hyderabad", "Telangana"},
            {"IMAX Theater", "Chennai", "Tamil Nadu"},
            {"Carnival Cinemas", "Pune", "Maharashtra"},
            {"Miraj Cinemas", "Kolkata", "West Bengal"},
            {"Asian Cinemas", "Ahmedabad", "Gujarat"},
            {"Fun Cinemas", "Jaipur", "Rajasthan"},
            {"SPI Cinemas", "Coimbatore", "Tamil Nadu"},
            {"Wave Cinemas", "Lucknow", "Uttar Pradesh"},
            {"Mukta A2 Cinemas", "Nagpur", "Maharashtra"},
            {"PVR ICON", "Gurgaon", "Haryana"},
            {"CineMAX", "Chandigarh", "Punjab"},
            {"MovieMax", "Indore", "Madhya Pradesh"},
            {"Rajhans Cinemas", "Surat", "Gujarat"},
            {"City Pride", "Nashik", "Maharashtra"},
            {"Gold Cinemas", "Vadodara", "Gujarat"},
            {"Big Cinemas", "Bhopal", "Madhya Pradesh"},
            {"Sathyam Cinemas", "Vijayawada", "Andhra Pradesh"}
        };
        
        List<com.revtickets.model.Theater> theatersToSave = new java.util.ArrayList<>();
        for (String[] data : theaterData) {
            java.util.Optional<com.revtickets.model.Theater> existing = theaterRepository.findByName(data[0]);
            if (existing.isPresent()) {
                com.revtickets.model.Theater theater = existing.get();
                theater.setCity(data[1]);
                theater.setState(data[2]);
                theater.setLocation(data[1] + ", " + data[2]);
                theater.setIsActive(true);
                theatersToSave.add(theater);
            } else {
                com.revtickets.model.Theater theater = new com.revtickets.model.Theater();
                theater.setName(data[0]);
                theater.setCity(data[1]);
                theater.setState(data[2]);
                theater.setLocation(data[1] + ", " + data[2]);
                theater.setTotalScreens(4 + (int)(Math.random() * 4));
                theater.setIsActive(true);
                theatersToSave.add(theater);
            }
        }
        
        theaterRepository.saveAll(theatersToSave);
        System.out.println("Seeded/Updated " + theatersToSave.size() + " theaters in database");
    }
    
    private Event createMovieEvent(String title, String description, String imageUrl, 
                                 Double rating, String genre, Integer duration, String releaseDate, String language, Double price) {
        Event event = new Event();
        event.setTitle(title);
        event.setDescription(description);
        event.setCategory("movie");
        event.setImageUrl(imageUrl);
        event.setRating(rating);
        event.setGenre(genre);
        event.setDuration(duration);
        event.setReleaseDate(releaseDate);
        event.setLanguage(language);
        event.setPrice(price);
        return event;
    }
    
    private Event createConcertEvent(String title, String description, String imageUrl, 
                                   Double rating, List<String> genres, Double price) {
        Event event = new Event();
        event.setTitle(title);
        event.setDescription(description);
        event.setCategory("concert");
        event.setImageUrl(imageUrl);
        event.setRating(rating);
        event.setGenres(genres);
        event.setPrice(price);
        return event;
    }
    
    private Event createOtherEvent(String title, String description, String industry, String imageUrl, 
                                 Double rating, Double price) {
        Event event = new Event();
        event.setTitle(title);
        event.setDescription(description);
        event.setCategory("other");
        event.setIndustry(industry);
        event.setImageUrl(imageUrl);
        event.setRating(rating);
        event.setPrice(price);
        return event;
    }
}