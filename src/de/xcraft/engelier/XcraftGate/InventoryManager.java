package de.xcraft.engelier.XcraftGate;

import java.io.IOException;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class InventoryManager {
	private static XcraftGate plugin;
	private static YamlConfiguration playerInventorys;
	
	public InventoryManager(XcraftGate plugin) {
		InventoryManager.plugin = plugin;
	}
	
	public void save() {
		try {
			playerInventorys.save(plugin.getConfigFile("playerInventorys.yml"));
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	public void load() {
		playerInventorys = plugin.getConfig(plugin.getConfigFile("playerInventorys.yml"));
	}
	
	public static void changeInventory(Player player, DataWorld from, DataWorld to) {
		saveInventory(player, from, player.getGameMode());
		clearInventory(player);
		loadInventory(player, to, player.getGameMode());
	}
	
	public static void changeInventroy(Player player, GameMode from, GameMode to, DataWorld world) {
		saveInventory(player, world, from);
		clearInventory(player);
		loadInventory(player, world, to);		
	}
	
	private static void saveInventory(Player player, DataWorld world, GameMode mode) {
		ConfigurationSection playerInv = playerInventorys.getConfigurationSection(player.getName() + "." + world.getName() + "." + mode.toString());
		if (playerInv == null) {
			playerInv = playerInventorys.createSection(player.getName() + "." + world.getName() + "." + mode.toString());
		}
		
		ConfigurationSection armor = playerInv.getConfigurationSection("armor");
		if (armor == null) {
			armor = playerInv.createSection("armor");
		}
		
		ConfigurationSection inv = playerInv.getConfigurationSection("inventory");
		if (inv == null) {
			inv = playerInv.createSection("inventory");
		}
		
		playerInv.set("health", player.getHealth());
		playerInv.set("food", player.getFoodLevel());
		playerInv.set("exp_total", player.getTotalExperience());
		playerInv.set("exp_level", player.getLevel());
		playerInv.set("exp_tolvl", player.getExp());
		
		ItemStack[] thisArmor = player.getInventory().getArmorContents();
		ItemStack[] thisInv = player.getInventory().getContents();
		
		for (Integer j = 0; j < 4; j++) {
			armor.set(j.toString(), thisArmor[j]);
		}
		
		for (Integer i = 0; i < 36; i++) {
			inv.set(i.toString(), thisInv[i]);
		}
	}
	
	private static void loadInventory(Player player, DataWorld world, GameMode mode) {
		System.out.println("Loading Inventory " + mode.toString() + " in world " + world.getName() + " for " + player.getName());
		ConfigurationSection playerInv = playerInventorys.getConfigurationSection(player.getName() + "." + world.getName() + "." + mode.toString());
		if (playerInv == null) {
			playerInv = playerInventorys.createSection(player.getName() + "." + world.getName() + "." + mode.toString());
		}
		
		ConfigurationSection armor = playerInv.getConfigurationSection("armor");
		if (armor == null) {
			armor = playerInv.createSection("armor");
		}
		
		ConfigurationSection inv = playerInv.getConfigurationSection("inventory");
		if (inv == null) {
			inv = playerInv.createSection("inventory");
		}
		
		ItemStack[] thisArmor = new ItemStack[4];
		ItemStack[] thisInv = new ItemStack[36];
		
		for (Integer j = 0; j < 4; j++) {
			thisArmor[j] = armor.getItemStack(j.toString(), new ItemStack(Material.AIR));
		}
		player.getInventory().setArmorContents(thisArmor);
		
		for (Integer i = 0; i < 36; i++) {
			thisInv[i] = inv.getItemStack(i.toString(), new ItemStack(Material.AIR));
		}
		player.getInventory().setContents(thisInv);
		
		if (plugin.getConfig().getBoolean("invsep.health")) {
			player.setHealth(playerInv.getInt("health", 20));
		}
		
		if (plugin.getConfig().getBoolean("invsep.food")) {
			player.setFoodLevel(playerInv.getInt("food", 20));
		}
		
		if (plugin.getConfig().getBoolean("invsep.exp")) {
			player.setTotalExperience(playerInv.getInt("exp_total", 0));
			player.setLevel(playerInv.getInt("exp_level", 0));
			player.setExp((float) playerInv.getDouble("exp_tolvl", 0.0));
		}
		
		player.updateInventory();
	}
	
	private static void clearInventory(Player player) {
		player.getInventory().clear();
		player.getInventory().setArmorContents(new ItemStack[4]);
	}
}
