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

@Getter
class SnowstormSpell extends AbstractRaySpell {
    final SpellType spellType = SpellType.SNOWSTORM;
    final String spellDisplayName = "Snowstorm";
    final int RADIUS = 4;
    final int EFFECT_DURATION = 10;

    SnowstormSpell(MagicPlugin plugin) {
        super(plugin);
    }

    @Override
    public int getManaCost(WandConfig config) {
        return 3;
    }

    @Override
    void hitSpell(WandConfig config, Player player, Block block, Location location) {
        playEffect(player, block, location);
        freezeEntities(player, location);
    }

    private void freezeEntities(Player player, Location location) {
        double DST = (double)RADIUS;
        for (Entity entity: location.getWorld().getNearbyEntities(location, DST, DST, DST)) {
            if (!GenericEventsPlugin.getInstance().playerCanDamageEntity(player, entity)) continue;
            if (!(entity instanceof LivingEntity)) continue;
            if (entity.equals(player)) continue;
            LivingEntity living = (LivingEntity)entity;
            living.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, EFFECT_DURATION * 20, 2));
            living.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, EFFECT_DURATION * 20, 0));
        }
    }

    private void playEffect(final Player player, final Block center, final Location location) {
        double DST = (double)RADIUS;
        double HDST = DST * 0.5;
        new BukkitRunnable() {
            int count = 0;
            @Override public void run() {
                location.getWorld().spawnParticle(Particle.SNOW_SHOVEL, location, 128, HDST, HDST, HDST, 0.0);
                if (count % 2 == 0) {
                    location.getWorld().playSound(location, Sound.ENTITY_LIGHTNING_THUNDER, 1.0f, 2.0f);
                }
                freezeBlocks(player, center);
                if (count++ >= 5) cancel();
            }
        }.runTaskTimer(plugin, 0, 10);
    }

    private void freezeBlocks(Player player, Block center) {
        final int AMOUNT = 8;
        for (int i = 0; i < AMOUNT; ++i) {
            Block block = center.getRelative(plugin.random.nextInt(RADIUS) - plugin.random.nextInt(RADIUS),
                                             plugin.random.nextInt(RADIUS) - plugin.random.nextInt(RADIUS),
                                             plugin.random.nextInt(RADIUS) - plugin.random.nextInt(RADIUS));
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
        } else if (block.getType() == Material.WATER) {
            block.setType(Material.ICE);
        } else if (block.getType() == Material.FIRE) {
            block.setType(Material.AIR);
        } else {
            return;
        }
    }
}
