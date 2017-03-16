package com.winthier.magic;

import com.winthier.generic_events.GenericEventsPlugin;
import java.util.ArrayList;
import java.util.List;
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
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

@Getter @RequiredArgsConstructor
public final class GustSpell implements Spell {
    private final MagicPlugin plugin;
    private final SpellType spellType = SpellType.GUST;
    private static final int RADIUS = 4;
    private static final int MAX_DISTANCE = 128;

    @Override
    public boolean castSpell(WandConfig config, Player player, Block clickedBlock, Entity clickedEntity) {
        final Block block;
        final Location location;
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
        double dst = (double)RADIUS;
        final List<Location> eyeLocations = new ArrayList<>();
        final List<LivingEntity> entities = new ArrayList<>();
        for (Entity e: location.getWorld().getNearbyEntities(location, dst, dst, dst)) {
            if (!(e instanceof LivingEntity)) continue;
            if (!GenericEventsPlugin.getInstance().playerCanDamageEntity(player, e)) continue;
            if (e == player) continue;
            LivingEntity le = (LivingEntity)e;
            entities.add(le);
            eyeLocations.add(le.getEyeLocation());
        }
        new BukkitRunnable() {
            private int ticks = 0;
            @Override public void run() {
                if (ticks > 20 * 3) {
                    cancel();
                } else if (ticks == 0) {
                    double rad = (double)RADIUS * 0.5;
                    location.getWorld().spawnParticle(Particle.CLOUD, location, RADIUS * RADIUS * RADIUS, rad, rad, rad, 0.5);
                    location.getWorld().playSound(location, Sound.BLOCK_GRASS_STEP, 1.0f, 0.5f);
                    for (LivingEntity entity: entities) {
                        if (!entity.isValid()) continue;
                        Vector velo = new Vector(Util.RANDOM.nextDouble() * 5.0 - 2.5,
                                                 Util.RANDOM.nextDouble() * 5.0,
                                                 Util.RANDOM.nextDouble() * 5.0 - 2.5);
                        entity.setVelocity(entity.getVelocity().add(velo));
                        entity.getWorld().playSound(entity.getEyeLocation(), Sound.ENTITY_FIREWORK_LAUNCH, 1.0f, 0.7f);
                    }
                } else if (ticks < 4) {
                    for (Location eyeLocation: eyeLocations) {
                        eyeLocation.getWorld().spawnParticle(Particle.SWEEP_ATTACK, eyeLocation, 4, 0.5, 0.5, 0.5, 0.0);
                    }
                    location.getWorld().playSound(location, Sound.BLOCK_GRASS_STEP, 1.0f, 0.5f);
                } else {
                    for (LivingEntity entity: entities) {
                        if (!entity.isValid()) continue;
                        Location location = entity.getLocation();
                        location.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, location, 1, 0.0, 0.0, 0.0, 0.0);
                    }
                }
                for (int i = 0; i < 60; ++i) {
                    Block randomBlock = block.getRelative(Util.RANDOM.nextInt(RADIUS * 2) - RADIUS,
                                                          Util.RANDOM.nextInt(RADIUS * 2) - RADIUS,
                                                          Util.RANDOM.nextInt(RADIUS * 2) - RADIUS);
                    switch (randomBlock.getType()) {
                    case FIRE:
                        randomBlock.setType(Material.AIR);
                        break;
                    default:
                        break;
                    }
                }
                ticks += 1;
            }
        }.runTaskTimer(plugin, 1, 1);
        return true;
    }
}
