package com.winthier.magic;

import com.winthier.custom.util.Dirty;
import lombok.RequiredArgsConstructor;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor
public final class WandConfig {
    private static final String KEY_SELECTED_SPELL = "spell";
    private static final String KEY_WAND_LEVEL = "level";
    private static final String KEY_FREE_LEVELS = "free_levels";
    private final Dirty.TagWrapper config;

    public static WandConfig of(ItemStack item) {
        return new WandConfig(Dirty.TagWrapper.itemConfigOf(item));
    }

    public int getSpellLevel(SpellType spell) {
        return config.getInt(spell.key);
    }

    public void setSpellLevel(SpellType spell, int level) {
        config.setInt(spell.key, level);
    }

    public int getWandLevel() {
        return config.getInt(KEY_WAND_LEVEL);
    }

    public void setWandLevel(int level) {
        config.setInt(KEY_WAND_LEVEL, level);
    }

    public int getFreeLevels() {
        return config.getInt(KEY_FREE_LEVELS);
    }

    public void setFreeLevels(int freeLevels) {
        config.setInt(KEY_FREE_LEVELS, freeLevels);
    }

    public SpellType getSelectedSpell() {
        String value = config.getString(KEY_SELECTED_SPELL);
        if (value == null) return null;
        try {
            return SpellType.valueOf(value);
        } catch (IllegalArgumentException iae) {
            return null;
        }
    }

    public void setSelectedSpell(SpellType spellType) {
        config.setString(KEY_SELECTED_SPELL, spellType.name());
    }
}
