package me.zakarya.shop;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class ShopPlugin extends JavaPlugin implements Listener {

    private Economy economy;
    private final Map<String, Inventory> categories = new HashMap<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        if (!setupEconomy()) {
            getLogger().severe("Vault غير موجود!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        Bukkit.getPluginManager().registerEvents(this, this);
        loadCategories();
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) return false;
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        economy = rsp.getProvider();
        return economy != null;
    }

    private void loadCategories() {
        categories.clear();
        FileConfiguration c = getConfig();
        if (!c.isConfigurationSection("categories")) return;

        for (String cat : c.getConfigurationSection("categories").getKeys(false)) {
            Inventory inv = Bukkit.createInventory(null, 54, "§a§l" + cat);
            for (String itemKey : c.getConfigurationSection("categories." + cat).getKeys(false)) {
                Material mat = Material.valueOf(itemKey);
                int price = c.getInt("categories." + cat + "." + itemKey);

                ItemStack item = new ItemStack(mat);
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName("§f" + arabicName(mat));
                meta.setLore(List.of("§7السعر: §a" + price + "$", "§eاضغط للشراء"));
                item.setItemMeta(meta);
                inv.addItem(item);
            }
            categories.put(cat, inv);
        }
    }

    private String arabicName(Material m) {
        return m.name().replace("_", " ");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player p)) return true;

        if (cmd.getName().equalsIgnoreCase("shop")) {
            openMainMenu(p);
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("shopadmin")) {
            if (!p.hasPermission("shop.admin")) {
                p.sendMessage("§cليس لديك صلاحية!");
                return true;
            }

            if (args.length < 1) {
                p.sendMessage("§e/shopadmin addcategory <اسم>");
                p.sendMessage("§e/shopadmin removecategory <اسم>");
                p.sendMessage("§e/shopadmin additem <تصنيف> <سعر>");
                p.sendMessage("§e/shopadmin reload");
                return true;
            }

            switch (args[0].toLowerCase()) {
                case "addcategory" -> {
                    getConfig().createSection("categories." + args[1]);
                    saveConfig();
                    loadCategories();
                    p.sendMessage("§aتم إضافة التصنيف");
                }
                case "removecategory" -> {
                    getConfig().set("categories." + args[1], null);
                    saveConfig();
                    loadCategories();
                    p.sendMessage("§cتم حذف التصنيف");
                }
                case "additem" -> {
                    ItemStack hand = p.getInventory().getItemInMainHand();
                    if (hand.getType() == Material.AIR) {
                        p.sendMessage("§cامسك الأيتم بيدك");
                        return true;
                    }
                    int price = Integer.parseInt(args[2]);
                    getConfig().set("categories." + args[1] + "." + hand.getType().name(), price);
                    saveConfig();
                    loadCategories();
                    p.sendMessage("§aتم إضافة الأيتم للمتجر");
                }
                case "reload" -> {
                    reloadConfig();
                    loadCategories();
                    p.sendMessage("§aتم إعادة تحميل المتجر");
                }
            }
        }
        return true;
    }

    private void openMainMenu(Player p) {
        Inventory main = Bukkit.createInventory(null, 27, "§6§lالمتجر");
        for (String cat : categories.keySet()) {
            ItemStack icon = new ItemStack(Material.CHEST);
            ItemMeta meta = icon.getItemMeta();
            meta.setDisplayName("§a" + cat);
            icon.setItemMeta(meta);
            main.addItem(icon);
        }
        p.openInventory(main);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player p)) return;
        e.setCancelled(true);
        ItemStack clicked = e.getCurrentItem();
        if (clicked == null) return;

        String title = ChatColor.stripColor(e.getView().getTitle());

        if (categories.containsKey(title)) {
            int price = Integer.parseInt(ChatColor.stripColor(clicked.getLore().get(0)).replaceAll("[^0-9]", ""));
            if (!economy.has(p, price)) {
                p.sendMessage("§cرصيدك غير كافٍ!");
                return;
            }
            economy.withdrawPlayer(p, price);
            ItemStack give = clicked.clone();
            give.setAmount(1);
            p.getInventory().addItem(give);
            p.sendMessage("§aتم الشراء!");
        } else if (categories.containsKey(ChatColor.stripColor(clicked.getItemMeta().getDisplayName()))) {
            p.openInventory(categories.get(ChatColor.stripColor(clicked.getItemMeta().getDisplayName())));
        }
    }
}

/* plugin.yml
name: ShopPlugin
version: 3.0
main: me.zakarya.shop.ShopPlugin
api-version: 1.21
commands:
  shop:
  shopadmin:
permissions:
  shop.admin:
    default: op
*/        Bukkit.getPluginManager().registerEvents(this, this);
        loadCategories();
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) return false;
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        economy = rsp.getProvider();
        return economy != null;
    }

    private void loadCategories() {
        FileConfiguration c = getConfig();
        for (String cat : c.getConfigurationSection("categories").getKeys(false)) {
            Inventory inv = Bukkit.createInventory(null, 54, "§a§l" + cat);
            for (String itemKey : c.getConfigurationSection("categories." + cat).getKeys(false)) {
                Material mat = Material.valueOf(itemKey);
                int price = c.getInt("categories." + cat + "." + itemKey);

                ItemStack item = new ItemStack(mat);
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName("§f" + arabicName(mat));
                meta.setLore(List.of("§7السعر: §a" + price + "$", "§eاضغط للشراء"));
                item.setItemMeta(meta);
                inv.addItem(item);
            }
            categories.put(cat, inv);
        }
    }

    private String arabicName(Material m) {
        return m.name().replace("_", " "); // قابل للتوسعة لاحقًا
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player p)) return true;
        Inventory main = Bukkit.createInventory(null, 27, "§6§lالمتجر");
        for (String cat : categories.keySet()) {
            ItemStack icon = new ItemStack(Material.CHEST);
            ItemMeta meta = icon.getItemMeta();
            meta.setDisplayName("§a" + cat);
            icon.setItemMeta(meta);
            main.addItem(icon);
        }
        p.openInventory(main);
        return true;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player p)) return;
        e.setCancelled(true);

        String title = e.getView().getTitle().replace("§a§l", "").replace("§6§l", "");
        ItemStack clicked = e.getCurrentItem();
        if (clicked == null) return;

        if (categories.containsKey(title)) {
            int price = Integer.parseInt(ChatColor.stripColor(clicked.getLore().get(0)).replaceAll("[^0-9]", ""));
            if (!economy.has(p, price)) {
                p.sendMessage("§cرصيدك غير كافٍ!");
                return;
            }
            economy.withdrawPlayer(p, price);
            ItemStack give = clicked.clone();
            give.setAmount(1);
            p.getInventory().addItem(give);
            p.sendMessage("§aتم الشراء!");
        } else if (categories.containsKey(ChatColor.stripColor(clicked.getItemMeta().getDisplayName()))) {
            p.openInventory(categories.get(ChatColor.stripColor(clicked.getItemMeta().getDisplayName())));
        }
    }
}

/* plugin.yml
name: ShopPlugin
version: 2.0
main: me.zakarya.shop.ShopPlugin
api-version: 1.21
commands:
  shop:
*/

/* config.yml مثال
categories:
  Blocks:
    STONE: 5
    DIAMOND_BLOCK: 100
  Tools:
    DIAMOND_SWORD: 150
*/
