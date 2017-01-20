package com.winthier.magic;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface Spell {
    SpellType getSpellType();
    ItemStack getMenuIcon(WandConfig config);
    String getSpellDisplayName();
    int getManaCost(WandConfig config);
    boolean castSpell(WandConfig config, Player player, Block clickedBlock, Entity clickedEntity);
}
