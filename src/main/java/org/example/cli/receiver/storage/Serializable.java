package org.example.cli.receiver.storage;

import com.fasterxml.jackson.core.JsonProcessingException;

@FunctionalInterface
public interface Serializable {
    byte[] toBytes() throws JsonProcessingException;
}
