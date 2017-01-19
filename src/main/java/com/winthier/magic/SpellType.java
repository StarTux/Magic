package com.winthier.magic;

public enum SpellType {
    FLAMETHROWER,
    SNOWSTORM,
    ;

    public final String key;

    SpellType() {
        key = name().toLowerCase();
    }
}
