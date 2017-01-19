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

@Getter
class FlamethrowerSpell extends AbstractRaySpell {
    final SpellType spellType = SpellType.FLAMETHROWER;
    final int RADIUS = 4;

    FlamethrowerSpell(MagicPlugin plugin) {
        super(plugin);
    }

    @Override
    public int getManaCost(WandConfig config) {
        return 4;
    }

    @Override
    void hitSpell(WandConfig config, Player player, Block block, Location location) {
        burnBlocks(player, block);
        burnEntities(player, location);
        playEffect(location);
    }

    private void burnBlocks(Player player, Block center) {
        final int AMOUNT = 64;
        for (int i = 0; i < AMOUNT; ++i) {
            Block block = center.getRelative(plugin.random.nextInt(RADIUS) - plugin.random.nextInt(RADIUS),
                                             plugin.random.nextInt(RADIUS) - plugin.random.nextInt(RADIUS),
                                             plugin.random.nextInt(RADIUS) - plugin.random.nextInt(RADIUS));
            if (block.getType() == Material.AIR &&
                GenericEventsPlugin.getInstance().playerCanBuild(player, block)) {
                block.setType(Material.FIRE);
            }
        }
    }

    private void burnEntities(Player player, Location location) {
        double DST = (double)RADIUS;
        for (Entity entity: location.getWorld().getNearbyEntities(location, DST, DST, DST)) {
            if (!GenericEventsPlugin.getInstance().playerCanDamageEntity(player, entity)) continue;
            if (!(entity instanceof LivingEntity)) return;
            entity.setFireTicks(Math.max(entity.getFireTicks(), 10*20));
        }
    }

    private void playEffect(Location location) {
        double DST = (double)RADIUS;
        double HDST = DST * 0.5;
        location.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, location, 16, HDST, HDST, HDST);
        location.getWorld().playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.2f);
    }
}
