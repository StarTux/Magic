package com.winthier.magic;

import com.winthier.custom.CustomPlugin;
import com.winthier.custom.item.CustomItem;
import com.winthier.custom.item.ItemContext;
import com.winthier.custom.item.UncraftableItem;
import com.winthier.custom.util.Msg;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

@Getter
public final class WandItem implements CustomItem, UncraftableItem {
    private final MagicPlugin plugin;
    private final String customId = "magic:wand";
    private final ItemStack itemStack;
    private static final long COOLDOWN = 10000L;

    WandItem(MagicPlugin plugin) {
        this.plugin = plugin;
        ItemStack item = new ItemStack(Material.BLAZE_ROD);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(Msg.format("&rMagic Wand"));
        meta.addEnchant(Enchantment.DURABILITY, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);
        this.itemStack = item;
    }

    @Override
    public ItemStack spawnItemStack(int amount) {
        return itemStack.clone();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerInteract(PlayerInteractEvent event, ItemContext context) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        switch (event.getAction()) {
        case LEFT_CLICK_AIR:
        case LEFT_CLICK_BLOCK:
            event.setCancelled(true);
            castSpell(event.getPlayer(), context, event.getClickedBlock(), null);
            break;
        case RIGHT_CLICK_AIR:
        case RIGHT_CLICK_BLOCK:
            event.setCancelled(true);
            WandMenu menu = new WandMenu(plugin, event.getPlayer(), WandConfig.of(context.getItemStack()));
            CustomPlugin.getInstance().getInventoryManager().openInventory(event.getPlayer(), menu);
            break;
        default:
            return;
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event, ItemContext context) {
        event.setCancelled(true);
        if (!(event.getDamager() instanceof Player)) return;
        castSpell((Player)event.getDamager(), context, null, event.getEntity());
    }

    void castSpell(Player player, ItemContext context, Block block, Entity entity) {
        WandConfig wandConfig = WandConfig.of(context.getItemStack());
        SpellType spellType = wandConfig.getSelectedSpell();
        if (spellType == null) {
            player.playSound(player.getEyeLocation(), Sound.BLOCK_DISPENSER_FAIL, 1.0f, 1.7f);
            Msg.sendActionBar(player, "&4No spell selected!");
        } else {
            long now = System.currentTimeMillis();
            long lastUse = wandConfig.getLastUse();
            if (lastUse + COOLDOWN > now) {
                player.playSound(player.getEyeLocation(), Sound.BLOCK_DISPENSER_FAIL, 1.0f, 1.7f);
                long seconds = (lastUse + COOLDOWN - now - 1) / 1000L + 1;
                Msg.sendActionBar(player, "&4Wand on cooldown for %d seconds!", seconds);
            } else if (spellType.getInstance(plugin).castSpell(wandConfig, player, block, entity)) {
                player.playSound(player.getEyeLocation(), Sound.ENTITY_WITCH_THROW, 1.0f, 2.0f);
                wandConfig.setLastUse(now);
            } else {
                player.playSound(player.getEyeLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 1.0f, 2.0f);
            }
        }
    }
}
