package org.example.cli.receiver.storage;

import java.io.IOException;
import java.util.Map;

public interface Store<T extends Serializable> extends Connector, Saver<T>{
    default void configure(Map<String, String> config) throws IOException {
        init(config);
    }
}
