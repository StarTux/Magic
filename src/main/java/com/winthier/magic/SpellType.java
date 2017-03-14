package com.winthier.magic;

public enum SpellType {
    FLAMETHROWER,
    SNOWSTORM,
    GUST;

    public final String key;

    SpellType() {
        key = name().toLowerCase();
    }

    Spell newInstance(MagicPlugin plugin) {
        switch (this) {
        case FLAMETHROWER: return new FlamethrowerSpell(plugin);
        case SNOWSTORM: return new SnowstormSpell(plugin);
        case GUST: return new GustSpell(plugin);
        default: return null;
        }
    }

    Spell getInstance(MagicPlugin plugin) {
        return plugin.getSpell(this);
    }
}
