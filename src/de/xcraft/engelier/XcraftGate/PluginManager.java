package de.xcraft.engelier.XcraftGate;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

public class PluginManager implements Runnable {
	private final XcraftGate core;
	private final org.bukkit.plugin.PluginManager pm;
	
	private Plugin vault = null;
	
	private Permission permission = null;
	private Economy economy = null;
	
	public PluginManager(XcraftGate core) {
		this.core = core;
		this.pm = core.getServer().getPluginManager();
	}
	
	public void registerEvents(Listener listener) {
		pm.registerEvents(listener, core);
	}

	public Permission getPermissions() {
		return permission;
	}
	
	public Economy getEconomy() {
		return economy;
	}
	
	private void checkPluginVault() {
		if (vault != null)
			return;
		
		Plugin vaultCheck = pm.getPlugin("Vault");
		if (vaultCheck != null && vaultCheck.isEnabled()) {
			vault = vaultCheck;
			core.log.info(core.getNameBrackets() + "found Vault plugin.");
			
	        RegisteredServiceProvider<Permission> permissionProvider = core.getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
	        if (permissionProvider != null) {
	            permission = permissionProvider.getProvider();
	            core.log.info(core.getNameBrackets() + "Reported permission provider: " + permission.getName());
	        }

	        RegisteredServiceProvider<Economy> economyProvider = core.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
	        if (economyProvider != null) {
	            economy = economyProvider.getProvider();
	            core.log.info(core.getNameBrackets() + "Reported economy provider: " + economy.getName());
	        }

		}
	}
	
	public void checkDisabledPlugin(Plugin plugin) {
		if (plugin.getDescription().getName().equals("Vault")) {
			permission = null;
			economy = null;
			vault = null;
			core.log.info(core.getNameBrackets() + "lost Vault plugin");
		}

	}
	
	@Override
	public void run() {
		checkPluginVault();
	}

}
