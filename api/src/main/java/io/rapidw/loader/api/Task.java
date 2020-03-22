package io.rapidw.loader.api;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Task {
    public enum Type {
        RUN,
        STOP
    }

    private Type type;
}
