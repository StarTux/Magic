package com.winthier.magic;

import com.winthier.custom.CustomConfig;
import com.winthier.custom.item.CustomItem;
import com.winthier.custom.item.ItemContext;
import com.winthier.custom.util.Msg;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

@Getter
public class WandItem implements CustomItem {
    final MagicPlugin plugin;
    final String customId = "magic:wand";
    ItemStack itemStack = null;
    final Spell spell; // Debug

    WandItem(MagicPlugin plugin) {
        this.plugin = plugin;
        // Wand
        ItemStack item = new ItemStack(Material.BLAZE_ROD);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(Msg.format("&rMagic Wand"));
        meta.addEnchant(Enchantment.DURABILITY, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);
        this.itemStack = item;
        spell = new FlamethrowerSpell(plugin); // Debug
    }

    @Override
    public ItemStack spawnItemStack(int amount, CustomConfig config) {
        return itemStack.clone();
    }

    @EventHandler(priority=EventPriority.LOWEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        ItemContext context = ItemContext.of(event);
        ItemMeta meta = context.getItem().getItemMeta();
        context.getItem().setItemMeta(meta);
        switch (event.getAction()) {
        case LEFT_CLICK_AIR:
        case LEFT_CLICK_BLOCK:
            spell.castSpell(WandConfig.of(context.getConfig()), event.getPlayer(), event.getClickedBlock(), null);
            break;
        case RIGHT_CLICK_AIR:
        case RIGHT_CLICK_BLOCK:
            break;
        }
    }

    @EventHandler(priority=EventPriority.LOWEST)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
    }

    @EventHandler(priority=EventPriority.LOWEST)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        ItemContext context = ItemContext.of(event);
        spell.castSpell(WandConfig.of(context.getConfig()), context.getPlayer(), null, event.getEntity());
    }
}
