package com.winthier.magic;

import com.winthier.custom.inventory.AbstractCustomInventory;
import com.winthier.custom.item.ItemContext;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;

@Getter
public class WandMenu extends AbstractCustomInventory {
    class Clickie {
        void click() {
        }
        void shiftClick() {
        }
    }

    final MagicPlugin plugin;
    final Player player;
    final Inventory inventory;
    final WandConfig wandConfig;
    final List<Clickie> clickies = new ArrayList<>();

    WandMenu(MagicPlugin plugin, Player player, WandConfig wandConfig) {
        this.plugin = plugin;
        this.player = player;
        this.wandConfig = wandConfig;
        inventory = Bukkit.getServer().createInventory(player, 18, "Magic Wand");
    }

    void selectSpell(SpellType spellType) {
        ItemContext context = ItemContext.of(player.getInventory().getItemInMainHand());
        if (context == null) return;
        WandConfig wandConfig = WandConfig.of(context.config);
        // TODO check spell level
        wandConfig.setSelectedSpell(spellType);
        context.config.save(context.itemStack);
    }

    void populateInventory() {
        inventory.clear();
        clickies.clear();
        for (final SpellType spellType: SpellType.values()) {
            int index = clickies.size();
            clickies.add(new Clickie() {
                @Override void click() {
                    selectSpell(spellType);
                }
                @Override void shiftClick() {
                    // TODO
                }
            });
            inventory.setItem(index, spellType.getInstance(plugin).getMenuIcon(wandConfig));
        }
    }

    @Override
    public void onInventoryOpen(InventoryOpenEvent event) {
        populateInventory();
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        event.setCancelled(true);
        int index = event.getRawSlot();
        if (index < 0 || index >= clickies.size()) return;
        Clickie clickie = clickies.get(index);
        if (event.isShiftClick()) {
            clickie.shiftClick();
        } else {
            clickie.click();
        }
    }
}
