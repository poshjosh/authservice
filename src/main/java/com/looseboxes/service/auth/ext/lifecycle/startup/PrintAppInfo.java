package com.looseboxes.service.auth.ext.lifecycle.startup;

import com.bc.service.util.Util;
import java.net.UnknownHostException;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author hp
 */
public class PrintAppInfo{
    
//    @Autowired private TaskExecutionProperties taskExecutionProperties;

    public void run(String... args) {
        
        this.printSystemProperties();
        
        this.printSystemEnv();
        
        this.printIpAndMemory();
    }
    
    private void printSystemProperties() {
        // DO NOT print values as some may contain sensitive info
        System.out.println("\nPrinting system property names");
        System.out.println(this.nonNullOrEmptyNames(System.getProperties().stringPropertyNames()));
    }

    private void printSystemEnv() {
        // DO NOT print values as some may contain sensitive info
        System.out.println("\nPrinting system environment names");
        System.out.println(this.nonNullOrEmptyNames(System.getenv().keySet()));
    }
 
    private String nonNullOrEmptyNames(Set<String> names) {
        return names.stream()
                .filter(name -> name != null && ! name.isEmpty())
                .filter((name) -> ! isNullOrEmpty(System.getProperty(name)))
                .collect(Collectors.joining(", "));
    }
    
    private boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }
    
    private void printIpAndMemory() {
        System.out.println("\nSystem ip address: " + getIp());
        System.out.print("Max memory: " + Runtime.getRuntime().maxMemory());
        System.out.println(", used memory: " + Util.usedMemory());
    }
    
    private String getIp() {
        try{
            return java.net.InetAddress.getLocalHost().getHostAddress();    
        }catch(UnknownHostException e) {
            System.err.println(e.toString());
            return null;
        }
    }
}
/**
 * 
    private void printTaskExecutionProps() {
        StringBuilder b = new StringBuilder();
        b.append("Thread name prefix: ").append(this.taskExecutionProperties.getThreadNamePrefix());
        final Pool pool = this.taskExecutionProperties.getPool();
        b.append("Core size: ").append(pool.getCoreSize());
        b.append("Keep alive: ").append(pool.getKeepAlive());
        b.append("Max size: ").append(pool.getMaxSize());
        b.append("Queue capacity: ").append(pool.getQueueCapacity());
        final Shutdown sd = this.taskExecutionProperties.getShutdown();
        b.append("Await termination duration: ").append(sd.getAwaitTerminationPeriod());
    }
 * 
 */