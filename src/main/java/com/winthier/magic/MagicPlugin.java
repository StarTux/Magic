package com.winthier.magic;

import com.winthier.custom.event.CustomRegisterEvent;
import java.util.Random;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.BlockIterator;

public class MagicPlugin extends JavaPlugin implements Listener {
    final Random random = new Random(System.currentTimeMillis());

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onCustomRegister(CustomRegisterEvent event) {
        event.addItem(new WandItem(this));
    }

    static public Block findTargetBlock(Player player, int maxDistance) {
        Location location = player.getEyeLocation();
        Block center = location.getBlock();
        int sq = maxDistance * maxDistance;
        BlockIterator iter = new BlockIterator(location);
        while (iter.hasNext()) {
            Block block = iter.next();
            if (block.getType() != Material.AIR) return block;
            int dx = center.getX() - block.getX();
            int dy = center.getY() - block.getY();
            int dz = center.getZ() - block.getZ();
            if (dx*dx + dy*dy + dz*dz > sq) return null;
        }
        return null;
    }
}
