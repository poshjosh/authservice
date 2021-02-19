package com.looseboxes.service.auth.ext.web.rest.vm;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author hp
 */
public class MessageVM implements Serializable, Message{
    
    private String message;

    @Override
    public String getMessage() {
        return message;
    }

    public MessageVM message(String message) {
        this.setMessage(message);
        return this;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + Objects.hashCode(this.message);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MessageVM other = (MessageVM) obj;
        if (!Objects.equals(this.message, other.message)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "MessageVM{" + "message=" + message + '}';
    }
}
