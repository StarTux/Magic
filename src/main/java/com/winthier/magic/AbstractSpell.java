package com.winthier.magic;

import com.winthier.custom.util.Msg;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

@RequiredArgsConstructor
abstract class AbstractSpell implements Spell {
    final MagicPlugin plugin;

    protected Material getMenuIconMaterial() {
        switch (getSpellType()) {
        case FLAMETHROWER: return Material.FIREWORK_CHARGE;
        case SNOWSTORM: return Material.SNOW_BALL;
        default: return Material.STONE;
        }
    }

    protected ItemStack getRawMenuIcon() {
        Material mat = getMenuIconMaterial();
        return new ItemStack(mat);
    }

    @Override
    public ItemStack getMenuIcon(WandConfig wandConfig) {
        int level = wandConfig.getSpellLevel(getSpellType());
        ItemStack result;
        // if (level <= 0) {
        //     result = new ItemStack(Material.CHEST);
        // } else {
            result = getRawMenuIcon();
            // result.setAmount(level);
        // }
        ItemMeta meta = result.getItemMeta();
        for (ItemFlag flag: ItemFlag.values()) {
            meta.addItemFlags(flag);
        }
        meta.setDisplayName(Msg.format("&r%s", getSpellDisplayName()));
        result.setItemMeta(meta);
        return result;
    }

    @Override
    public String getSpellDisplayName() {
        return Msg.camelCase(getSpellType().name());
    }
}
