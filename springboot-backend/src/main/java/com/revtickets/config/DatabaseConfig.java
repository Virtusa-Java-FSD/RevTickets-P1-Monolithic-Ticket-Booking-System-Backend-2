package com.revtickets.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseConfig implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        try {
            // Check if ticket_id column exists and modify it to allow NULL
            String checkColumnQuery = "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS " +
                    "WHERE TABLE_SCHEMA = 'revtickets' AND TABLE_NAME = 'payments' AND COLUMN_NAME = 'ticket_id'";
            
            try {
                jdbcTemplate.queryForObject(checkColumnQuery, String.class);
                // Column exists, modify it to allow NULL
                String alterQuery = "ALTER TABLE payments MODIFY COLUMN ticket_id VARCHAR(255) NULL";
                jdbcTemplate.execute(alterQuery);
                System.out.println("Successfully modified ticket_id column to allow NULL values");
            } catch (Exception e) {
                System.out.println("ticket_id column doesn't exist or already nullable: " + e.getMessage());
            }
        } catch (Exception e) {
            System.err.println("Database schema update failed: " + e.getMessage());
        }
    }
}