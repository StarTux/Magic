package com.winthier.magic;

import java.util.Random;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;

public final class Util {
    private Util() { }

    public static final Random RANDOM = new Random(System.currentTimeMillis());

    public static Block findTargetBlock(Player player, int maxDistance) {
        Location location = player.getEyeLocation();
        Block center = location.getBlock();
        int sq = maxDistance * maxDistance;
        BlockIterator iter = new BlockIterator(location);
        while (iter.hasNext()) {
            Block block = iter.next();
            if (block.getType() != Material.AIR) return block;
            for (Entity e: block.getWorld().getNearbyEntities(block.getLocation().add(0.5, 0.5, 0.5), 1.0, 1.0, 1.0)) {
                if (e != player && e instanceof LivingEntity) return block;
            }
            int dx = center.getX() - block.getX();
            int dy = center.getY() - block.getY();
            int dz = center.getZ() - block.getZ();
            if (dx * dx + dy * dy + dz * dz > sq) return null;
        }
        return null;
    }
}
