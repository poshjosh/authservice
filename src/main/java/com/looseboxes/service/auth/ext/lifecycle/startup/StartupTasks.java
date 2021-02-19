package com.looseboxes.service.auth.ext.lifecycle.startup;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * @author hp
 */
@Configuration
public class StartupTasks implements CommandLineRunner{

    @Override
    public void run(String... args) { 

        startupChecks().run(args);
        
        printContentOfCaches().run(args);
        
        printAppInfo().run(args);
    }

    @Bean @Scope("prototype") public StartupChecks startupChecks() {
        return new StartupChecks();
    }

    @Bean @Scope("prototype") public PrintContentOfCaches printContentOfCaches() {
        return new PrintContentOfCaches();
    }

    @Bean @Scope("prototype") public PrintAppInfo printAppInfo() {
        return new PrintAppInfo();
    }
}
