package com.github.bsamartins.spring.boot.beat;

import java.util.function.Consumer;

public interface BeatHandler extends Consumer<Beat<Object>> {
}
