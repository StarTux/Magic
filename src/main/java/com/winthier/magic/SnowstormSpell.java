package com.winthier.magic;

import com.winthier.generic_events.GenericEventsPlugin;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

@Getter @RequiredArgsConstructor
final class SnowstormSpell implements Spell {
    private final MagicPlugin plugin;
    private final SpellType spellType = SpellType.SNOWSTORM;
    private static final int RADIUS = 4;
    private static final int EFFECT_DURATION = 10;
    private static final int MAX_DISTANCE = 128;

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
            block = Util.findTargetBlock(player, MAX_DISTANCE);
            if (block == null) return false;
            location = block.getLocation().add(0.5, 0.5, 0.5);
        }
        playEffect(player, block, location);
        freezeEntities(player, location);
        return true;
    }

    private void freezeEntities(Player player, Location location) {
        double dst = (double)RADIUS;
        for (Entity entity: location.getWorld().getNearbyEntities(location, dst, dst, dst)) {
            if (!GenericEventsPlugin.getInstance().playerCanDamageEntity(player, entity)) continue;
            if (!(entity instanceof LivingEntity)) continue;
            if (entity.equals(player)) continue;
            LivingEntity living = (LivingEntity)entity;
            living.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, EFFECT_DURATION * 20, 2));
            living.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, EFFECT_DURATION * 20, 0));
        }
    }

    private void playEffect(final Player player, final Block center, final Location location) {
        double dst = (double)RADIUS;
        double hdst = dst * 0.5;
        new BukkitRunnable() {
            private int count = 0;
            @Override public void run() {
                location.getWorld().spawnParticle(Particle.SNOW_SHOVEL, location, 128, hdst, hdst, hdst, 0.0);
                if (count % 2 == 0) {
                    location.getWorld().playSound(location, Sound.ENTITY_LIGHTNING_THUNDER, 1.0f, 2.0f);
                }
                freezeBlocks(player, center);
                if (count++ >= 5) cancel();
            }
        }.runTaskTimer(plugin, 0, 10);
    }

    private void freezeBlocks(Player player, Block center) {
        final int amount = 8;
        for (int i = 0; i < amount; ++i) {
            Block block = center.getRelative(Util.RANDOM.nextInt(RADIUS) - Util.RANDOM.nextInt(RADIUS),
                                             Util.RANDOM.nextInt(RADIUS) - Util.RANDOM.nextInt(RADIUS),
                                             Util.RANDOM.nextInt(RADIUS) - Util.RANDOM.nextInt(RADIUS));
            freezeBlock(player, block);
        }
    }

    private void freezeBlock(Player player, Block block) {
        if (!GenericEventsPlugin.getInstance().playerCanBuild(player, block)) return;
        if (block.getType() == Material.AIR) {
            Material below = block.getRelative(0, -1, 0).getType();
            if (!below.isSolid() || !below.isOccluding()) return;
            block.setType(Material.SNOW);
        } else if (block.getType() == Material.LAVA) {
            Material above = block.getRelative(0, 1, 0).getType();
            if (above != Material.AIR) return;
            block.setType(Material.OBSIDIAN);
        } else if (block.getType() == Material.WATER || block.getType() == Material.STATIONARY_WATER) {
            block.setType(Material.ICE);
        } else if (block.getType() == Material.FIRE) {
            block.setType(Material.AIR);
        } else {
            return;
        }
    }
}
