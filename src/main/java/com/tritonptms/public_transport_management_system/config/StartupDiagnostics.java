package com.tritonptms.public_transport_management_system.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

@Component
@Profile("prod") // Only runs in the 'prod' environment
public class StartupDiagnostics implements CommandLineRunner {

    private final DataSource dataSource;
    private final Environment env;

    public StartupDiagnostics(DataSource dataSource, Environment env) {
        this.dataSource = dataSource;
        this.env = env;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("\n=======================================================");
        System.out.println("🚀 Running Custom Production Startup Diagnostics...");
        System.out.println("=======================================================");

        // --- 1. Environment Variable Checks ---
        checkEnvironmentVariables();

        // --- 2. Database Connection Check ---
        checkDatabaseConnection();

        System.out.println("✅ All critical startup diagnostics passed.");
        System.out.println("=======================================================\n");
    }

    /**
     * Checks if all required environment variables for DB connection are present.
     */
    private void checkEnvironmentVariables() {
        System.out.println("1. Validating External Configuration...");
        String[] requiredVars = { "DB_URL", "DB_USERNAME", "DB_PASSWORD" };
        boolean failure = false;

        for (String var : requiredVars) {
            String value = env.getProperty(var);
            if (value == null || value.isBlank()) {
                System.err.println("❌ CRITICAL ERROR: Required property '" + var + "' is missing or empty.");
                failure = true;
            } else {
                // Log all but the password
                String logValue = var.contains("PASSWORD") ? "*****" : value;
                System.out.println(" - OK: " + var + " -> " + logValue);
            }
        }

        if (failure) {
            System.err.println("\n💥 Fatal: Environment variables are incomplete. Terminating application.");
            // Force application exit with a failure code
            System.exit(1);
        }
    }

    /**
     * Attempts a connection to the database to ensure it's available and
     * credentials are correct.
     */
    private void checkDatabaseConnection() {
        System.out.println("2. Validating Database Connection and Credentials...");
        try (Connection connection = dataSource.getConnection()) {
            if (connection.isValid(5)) { // Check connection validity with a 5-second timeout
                System.out.println(" - OK: Database connection successful. URL: " +
                        env.getProperty("DB_URL"));
            } else {
                throw new SQLException("Connection is not valid after connection pool initialization.");
            }
        } catch (SQLException e) {
            System.err.println("\n❌ CRITICAL ERROR: Database Connection Failed!");
            System.err.println(" - Message: " + e.getMessage());
            System.err.println(
                    " - Hint: Check DB_URL, DB_USERNAME, DB_PASSWORD, and ensure the Docker container is UP.");
            System.err.println("\n💥 Fatal: Cannot establish database connection. Terminating application.");
            // Force application exit with a failure code
            System.exit(1);
        }
    }

    // You can add more checks here (e.g., check for a specific initial user/table);
}