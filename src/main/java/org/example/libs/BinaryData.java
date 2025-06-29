package org.example.libs;

import java.io.IOException;

public interface BinaryData {
    /**
     * Декодирует бинарные данные
     * @param data входные бинарные данные
     * @throws IOException в случае ошибки декодирования
     */
    void decode(byte[] data) throws IOException;

    /**
     * Кодирует данные в бинарный формат
     * @return закодированные бинарные данные
     * @throws IOException в случае ошибки кодирования
     */
    byte[] encode() throws IOException;

    /**
     * Возвращает длину данных в байтах
     * @return длина данных
     */
    int length();
}

