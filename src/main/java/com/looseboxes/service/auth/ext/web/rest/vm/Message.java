package com.looseboxes.service.auth.ext.web.rest.vm;

import java.util.function.Supplier;

/**
 * @author hp
 */
public interface Message extends Supplier<String>{
    
    @Override
    default String get() {
        return getMessage();
    }
    
    default Message and(Message other, String separator) {
        return () -> this.get() + separator + other.get();
    }
    
    String getMessage();
}
