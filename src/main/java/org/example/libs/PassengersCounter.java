package org.example.libs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PassengersCounter {
    private byte doorNo;
    private byte in;
    private byte out;

    public byte[] encode() {
        if (doorNo == 0) {
            throw new IllegalArgumentException("Попытка закодировать неинициализированную структуру PassengersCounter");
        }
        return new byte[]{in, out};
    }
}
