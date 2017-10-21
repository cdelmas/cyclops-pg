package io.github.cdelmas.frp.cyclops;

import cyclops.collections.mutable.ListX;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor
@RequiredArgsConstructor
@Getter
public class Batch {
    private ListX<String> lines;
}
