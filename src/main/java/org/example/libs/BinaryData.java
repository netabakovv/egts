package org.example.libs;

public interface BinaryData {
    /**
     * Декодирует бинарные данные
     * @param data входные бинарные данные
     * @throws Exception в случае ошибки декодирования
     */
    void decode(byte[] data) throws Exception;

    /**
     * Кодирует данные в бинарный формат
     * @return закодированные бинарные данные
     * @throws Exception в случае ошибки кодирования
     */
    byte[] encode() throws Exception;

    /**
     * Возвращает длину данных в байтах
     * @return длина данных
     */
    short length();
}

