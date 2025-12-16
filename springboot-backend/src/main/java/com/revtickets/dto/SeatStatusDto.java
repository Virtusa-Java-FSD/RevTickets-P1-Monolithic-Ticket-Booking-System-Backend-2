package com.revtickets.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SeatStatusDto {
    private String seatNumber;
    private boolean isBooked;
    private String status; // "AVAILABLE", "BOOKED", "SELECTED"
}