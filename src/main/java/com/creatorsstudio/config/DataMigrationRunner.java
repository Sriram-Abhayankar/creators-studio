package com.creatorsstudio.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * One-time data migration runner.
 * Copies Purchase_date and Created_at from the Cone table to the Accessory table
 * for existing records where Accessory.Purchase_date is NULL.
 * 
 * This is safe to run multiple times — it only updates records where the target
 * columns are NULL, so it is idempotent.
 * 
 * After all existing data has been migrated, this runner can be safely removed.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataMigrationRunner implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) {
        log.info("=== DATA MIGRATION: Starting Accessory module migration ===");

        try {
            // Step 1: Make old Cone date columns nullable since they are no longer managed by the entity.
            // This allows Hibernate to insert new Cone records without providing these values.
            log.info("=== DATA MIGRATION: Making old Cone date columns nullable ===");
            try {
                jdbcTemplate.execute("ALTER TABLE Cone MODIFY COLUMN Purchase_date datetime(6) NULL");
                jdbcTemplate.execute("ALTER TABLE Cone MODIFY COLUMN Created_at datetime(6) NULL");
                log.info("=== DATA MIGRATION: Cone columns updated to nullable ===");
            } catch (Exception e) {
                log.warn("=== DATA MIGRATION: Could not alter Cone columns (may already be nullable): {} ===", e.getMessage());
            }

            // Step 2: Copy Purchase_date and Created_at from Cone to Accessory for existing records
            Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM Accessory a " +
                "INNER JOIN Cone c ON c.Accessory_id = a.id " +
                "WHERE a.Purchase_date IS NULL AND c.Purchase_date IS NOT NULL",
                Integer.class
            );

            if (count == null || count == 0) {
                log.info("=== DATA MIGRATION: No records need date migration. All Accessory dates are already populated. ===");
            } else {
                log.info("=== DATA MIGRATION: Found {} Accessory records linked to Cone that need date migration ===", count);

                int updatedPurchaseDate = jdbcTemplate.update(
                    "UPDATE Accessory a " +
                    "INNER JOIN Cone c ON c.Accessory_id = a.id " +
                    "SET a.Purchase_date = c.Purchase_date " +
                    "WHERE a.Purchase_date IS NULL AND c.Purchase_date IS NOT NULL"
                );
                log.info("=== DATA MIGRATION: Updated Purchase_date for {} Accessory records ===", updatedPurchaseDate);

                int updatedCreatedAt = jdbcTemplate.update(
                    "UPDATE Accessory a " +
                    "INNER JOIN Cone c ON c.Accessory_id = a.id " +
                    "SET a.Created_at = c.Created_at " +
                    "WHERE a.Created_at IS NULL AND c.Created_at IS NOT NULL"
                );
                log.info("=== DATA MIGRATION: Updated Created_at for {} Accessory records ===", updatedCreatedAt);

                log.info("=== DATA MIGRATION: Date migration complete. {} records processed. ===", count);
            }

            log.info("=== DATA MIGRATION: Accessory module migration finished successfully ===");

        } catch (Exception e) {
            log.error("=== DATA MIGRATION: Migration failed. Error: {} ===", e.getMessage(), e);
            // Do not re-throw — allow the application to start even if migration fails.
            // The migration can be retried on next startup.
        }
    }
}
