package com.tritonptms.public_transport_management_system.config;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;

public class StartupPropertyValidator implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        Environment env = applicationContext.getEnvironment();

        // 1. Check if the 'prod' profile is active
        boolean isProd = env.acceptsProfiles(Profiles.of("prod"));

        if (!isProd) {
            // Short explanation: We skip the strict check in non-production environments
            // to allow for more flexible local development setup.
            return;
        }

        System.out.println("\n--- Running Pre-Context Configuration Check (Initializer) ---");

        String dbUrl = env.getProperty("DB_URL");
        String dbUsername = env.getProperty("DB_USERNAME");

        boolean failure = false;

        // Check 1: Ensure URL and Username are not null or empty after Spring
        // processing
        if (dbUrl == null || dbUrl.isBlank()) {
            System.err.println("❌ ERROR: Required property 'DB_URL' is missing or empty in the environment.");
            failure = true;
        }
        if (dbUsername == null || dbUsername.isBlank()) {
            System.err.println("❌ ERROR: Required property 'DB_USERNAME' is missing or empty in the environment.");
            failure = true;
        }

        // Note: We skip checking DB_PASSWORD in this log for security, but its presence
        // is implicit.

        // Check 2: Quick validation of the JDBC URL format (optional but helpful)
        if (!failure && !dbUrl.startsWith("jdbc:postgresql://")) {
            System.err.println("❌ ERROR: DB_URL does not appear to be a PostgreSQL JDBC URL.");
            failure = true;
        }

        if (failure) {
            System.err.println("💥 Fatal: Core database properties are misconfigured. Application terminated.");
            // Force exit BEFORE context is loaded, preventing a cryptic
            // BeanCreationException
            System.exit(1);
        }
        System.out.println("✅ Pre-Context Check: Database configuration properties are present.");
        System.out.println("-----------------------------------------------------------------\n");
    }
}