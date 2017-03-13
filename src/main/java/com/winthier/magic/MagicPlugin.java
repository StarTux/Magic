package com.winthier.magic;

import com.winthier.custom.event.CustomRegisterEvent;
import java.util.EnumMap;
import java.util.Map;
import lombok.Getter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class MagicPlugin extends JavaPlugin implements Listener {
    private final Map<SpellType, Spell> spells = new EnumMap<>(SpellType.class);
    private final WandItem wandItem = new WandItem(this);

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        for (SpellType spellType: SpellType.values()) spells.put(spellType, spellType.newInstance(this));
    }

    @EventHandler
    public void onCustomRegister(CustomRegisterEvent event) {
        event.addItem(wandItem);
    }

    public Spell getSpell(SpellType spellType) {
        return spells.get(spellType);
    }
}
