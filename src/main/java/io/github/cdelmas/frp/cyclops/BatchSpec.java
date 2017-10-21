package io.github.cdelmas.frp.cyclops;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Wither;

import java.nio.file.Path;

@AllArgsConstructor
@Getter
public class BatchSpec {
    private Path path;
    @Wither
    private long index;
    private int size;
}
