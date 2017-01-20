package com.winthier.magic;

import com.winthier.custom.CustomConfig;
import java.util.Map;
import java.util.EnumMap;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class WandConfig {
    final static String KEY_SELECTED_SPELL = "spell";
    final static String KEY_WAND_LEVEL = "level";
    final static String KEY_FREE_LEVELS = "free_levels";
    final CustomConfig config;

    public static WandConfig of(CustomConfig config) {
        return new WandConfig(config);
    }

    public int getSpellLevel(SpellType spell) {
        return config.getInt(spell.key, 0);
    }

    public void setSpellLevel(SpellType spell, int level) {
        config.set(spell.key, level);
    }

    public int getWandLevel() {
        return config.getInt(KEY_WAND_LEVEL, 0);
    }

    public void setWandLevel(int level) {
        config.set(KEY_WAND_LEVEL, level);
    }

    public int getFreeLevels() {
        return config.getInt(KEY_FREE_LEVELS, 0);
    }

    public void setFreeLevels(int freeLevels) {
        config.set(KEY_FREE_LEVELS, freeLevels);
    }

    public SpellType getSelectedSpell() {
        String value = config.getString(KEY_SELECTED_SPELL, null);
        if (value == null) return null;
        try {
            return SpellType.valueOf(value);
        } catch (IllegalArgumentException iae) {
            return null;
        }
    }

    public void setSelectedSpell(SpellType spellType) {
        config.set(KEY_SELECTED_SPELL, spellType.name());
    }
}
