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

@Getter @RequiredArgsConstructor
final class FlamethrowerSpell implements Spell {
    private final MagicPlugin plugin;
    private final SpellType spellType = SpellType.FLAMETHROWER;
    private static final int RADIUS = 4;
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
        burnBlocks(player, block);
        burnEntities(player, location);
        playEffect(location);
        return true;
    }

    private void burnBlocks(Player player, Block center) {
        final int amount = 64;
        for (int i = 0; i < amount; ++i) {
            Block block = center.getRelative(Util.RANDOM.nextInt(RADIUS) - Util.RANDOM.nextInt(RADIUS),
                                             Util.RANDOM.nextInt(RADIUS) - Util.RANDOM.nextInt(RADIUS),
                                             Util.RANDOM.nextInt(RADIUS) - Util.RANDOM.nextInt(RADIUS));
            if (block.getType() == Material.AIR
                && GenericEventsPlugin.getInstance().playerCanBuild(player, block)) {
                block.setType(Material.FIRE);
            }
        }
    }

    private void burnEntities(Player player, Location location) {
        double dst = (double)RADIUS;
        for (Entity entity: location.getWorld().getNearbyEntities(location, dst, dst, dst)) {
            if (!GenericEventsPlugin.getInstance().playerCanDamageEntity(player, entity)) continue;
            if (!(entity instanceof LivingEntity)) return;
            entity.setFireTicks(Math.max(entity.getFireTicks(), 10 * 20));
        }
    }

    private void playEffect(Location location) {
        double dst = (double)RADIUS;
        double hdst = dst * 0.5;
        location.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, location, 16, hdst, hdst, hdst);
        location.getWorld().playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.2f);
    }
}
