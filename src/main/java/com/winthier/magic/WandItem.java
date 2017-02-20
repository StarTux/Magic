package com.winthier.magic;

import com.winthier.custom.CustomConfig;
import com.winthier.custom.CustomPlugin;
import com.winthier.custom.item.CustomItem;
import com.winthier.custom.item.ItemContext;
import com.winthier.custom.util.Msg;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

@Getter
public class WandItem implements CustomItem {
    final MagicPlugin plugin;
    final String customId = "magic:wand";
    ItemStack itemStack = null;

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
    public ItemStack spawnItemStack(int amount, CustomConfig config) {
        return itemStack.clone();
    }

    @EventHandler(priority=EventPriority.LOWEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        ItemContext context = ItemContext.of(event);
        switch (event.getAction()) {
        case LEFT_CLICK_AIR:
        case LEFT_CLICK_BLOCK:
            WandConfig wandConfig = WandConfig.of(context.config);
            SpellType spellType = wandConfig.getSelectedSpell();
            if (spellType == null) return;
            event.setCancelled(true);
            spellType.getInstance(plugin).castSpell(wandConfig, event.getPlayer(), event.getClickedBlock(), null);
            break;
        case RIGHT_CLICK_AIR:
        case RIGHT_CLICK_BLOCK:
            event.setCancelled(true);
            WandMenu menu = new WandMenu(plugin, event.getPlayer(), WandConfig.of(context.config));
            CustomPlugin.getInstance().getInventoryManager().openInventory(event.getPlayer(), menu);
            break;
        }
    }

    @EventHandler(priority=EventPriority.LOWEST)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
    }

    @EventHandler(priority=EventPriority.LOWEST)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;
        Player player = (Player)event.getDamager();
        ItemContext context = ItemContext.of(event);
        WandConfig wandConfig = WandConfig.of(context.config);
        SpellType spellType = wandConfig.getSelectedSpell();
        if (spellType == null) return;
        event.setCancelled(true);
        spellType.getInstance(plugin).castSpell(wandConfig, player, null, event.getEntity());
    }
}
