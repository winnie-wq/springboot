package org.example.medicinalplant.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class R<T> {

    private int code;
    private T data;
    private String message;

    public static <T> R<T> ok(T data) {
        return new R<>(200, data, "success");
    }

    public static <T> R<T> ok() {
        return new R<>(200, null, "success");
    }

    public static <T> R<T> fail(String message) {
        return new R<>(500, null, message);
    }

    public static <T> R<T> fail(int code, String message) {
        return new R<>(code, null, message);
    }
}
