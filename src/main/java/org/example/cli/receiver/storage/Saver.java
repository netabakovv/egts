package org.example.cli.receiver.storage;

import java.io.IOException;

@FunctionalInterface
public interface Saver<T extends Serializable> {
    void save(T data) throws IOException;
}
