package com.winthier.magic;

import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
abstract class AbstractRaySpell implements Spell {
    final MagicPlugin plugin;

    public int getMaxDistance(WandConfig config) {
        return 128;
    }

    @Override
    public boolean castSpell(WandConfig config, Player player, Block clickedBlock, Entity clickedEntity) {
        Block block;
        Location location;
        if (clickedBlock != null) {
            block = clickedBlock;
            location = clickedBlock.getLocation().add(0.5, 0.5, 0.5);
        } else if (clickedEntity != null) {
            location = clickedEntity.getLocation();
            block = location.getBlock();
        } else {
            block = plugin.findTargetBlock(player, getMaxDistance(config));
            if (block == null) return false;
            location = block.getLocation().add(0.5, 0.5, 0.5);
        }
        hitSpell(config, player, block, location);
        return true;
    }

    abstract void hitSpell(WandConfig config, Player player, Block block, Location location);
}
