package com.winthier.magic;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public interface Spell {
    SpellType getSpellType();
    boolean castSpell(WandConfig config, Player player, Block clickedBlock, Entity clickedEntity);
}
