package org.example.libs;

public interface SecretKey {
    byte[] decode(byte[] data);
    byte[] encode();
}
