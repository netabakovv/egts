package org.example.cli.receiver.storage;

import java.io.IOException;
import java.util.Map;

public interface Connector {
    void init(Map<String, String> config) throws IOException;
    void close() throws IOException;
}
