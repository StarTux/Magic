package com.winthier.magic;

import com.winthier.custom.CustomPlugin;
import com.winthier.custom.item.CustomItem;
import com.winthier.custom.item.ItemContext;
import com.winthier.custom.util.Msg;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
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
public class WandItem implements CustomItem {
    private final MagicPlugin plugin;
    private final String customId = "magic:wand";
    private final ItemStack itemStack;

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
            WandConfig wandConfig = WandConfig.of(context.getItemStack());
            SpellType spellType = wandConfig.getSelectedSpell();
            if (spellType == null) return;
            event.setCancelled(true);
            spellType.getInstance(plugin).castSpell(wandConfig, event.getPlayer(), event.getClickedBlock(), null);
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
        if (!(event.getDamager() instanceof Player)) return;
        Player player = (Player)event.getDamager();
        WandConfig wandConfig = WandConfig.of(context.getItemStack());
        SpellType spellType = wandConfig.getSelectedSpell();
        if (spellType == null) return;
        event.setCancelled(true);
        spellType.getInstance(plugin).castSpell(wandConfig, player, null, event.getEntity());
    }
}
