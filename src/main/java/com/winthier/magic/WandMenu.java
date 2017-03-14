package com.winthier.magic;

import com.winthier.custom.CustomPlugin;
import com.winthier.custom.inventory.CustomInventory;
import com.winthier.custom.util.Msg;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

@Getter
public final class WandMenu implements CustomInventory {
    private final MagicPlugin plugin;
    private final Player player;
    private final Inventory inventory;
    private final WandConfig wandConfig;
    private final List<Clickie> clickies = new ArrayList<>();

    class Clickie {
        void click() {
        }
        void shiftClick() {
        }
    }

    WandMenu(MagicPlugin plugin, Player player, WandConfig wandConfig) {
        this.plugin = plugin;
        this.player = player;
        this.wandConfig = wandConfig;
        inventory = Bukkit.getServer().createInventory(player, 18, "Magic Wand");
    }

    void selectSpell(SpellType spellType) {
        ItemStack item = player.getInventory().getItemInMainHand();
        if (CustomPlugin.getInstance().getItemManager().getCustomItem(item) != plugin.getWandItem()) return;
        WandConfig itemConfig = WandConfig.of(item);
        // TODO check spell level
        itemConfig.setSelectedSpell(spellType);
    }

    private Material getMenuIconMaterial(SpellType spellType) {
        switch (spellType) {
        case FLAMETHROWER: return Material.FIREWORK_CHARGE;
        case SNOWSTORM: return Material.SNOW_BALL;
        case GUST: return Material.DISPENSER;
        default: return Material.STONE;
        }
    }

    private ItemStack getRawMenuIcon(SpellType spellType) {
        Material mat = getMenuIconMaterial(spellType);
        return new ItemStack(mat);
    }

    private String getSpellDisplayName(SpellType spellType) {
        switch (spellType) {
        case FLAMETHROWER: return "Flamethrower";
        case SNOWSTORM: return "Snowstorm";
        case GUST: return "Gust of Wind";
        default: return "Spell";
        }
    }

    public ItemStack getMenuIcon(SpellType spellType) {
        int level = wandConfig.getSpellLevel(spellType);
        ItemStack result;
        // if (level <= 0) {
        //     result = new ItemStack(Material.CHEST);
        // } else {
        result = getRawMenuIcon(spellType);
            // result.setAmount(level);
        // }
        ItemMeta meta = result.getItemMeta();
        for (ItemFlag flag: ItemFlag.values()) {
            meta.addItemFlags(flag);
        }
        meta.setDisplayName(Msg.format("&r%s", getSpellDisplayName(spellType)));
        result.setItemMeta(meta);
        return result;
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
            inventory.setItem(index, getMenuIcon(spellType));
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
