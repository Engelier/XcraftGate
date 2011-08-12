package de.xcraft.engelier.XcraftGate;

import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;
import org.bukkit.plugin.Plugin;

import com.nijikokun.bukkit.Permissions.Permissions;

public class ListenerServer extends ServerListener {
	private XcraftGate plugin = null;

	public ListenerServer(XcraftGate instance) {
		plugin = instance;
	}

	@Override
	public void onPluginEnable(PluginEnableEvent event) {
		Plugin thisPlugin = event.getPlugin();

		if (thisPlugin.getDescription().getName().equals("Permissions")) {
			plugin.permissions = ((Permissions) thisPlugin).getHandler();
			plugin.log.info(plugin.getNameBrackets()
					+ "hooked into Permissions "
					+ thisPlugin.getDescription().getVersion());
		}
	}

	@Override
	public void onPluginDisable(PluginDisableEvent event) {
		Plugin thisPlugin = event.getPlugin();

		if (thisPlugin.getDescription().getName().equals("Permissions")) {
			plugin.permissions = null;
			plugin.log.info(plugin.getNameBrackets()
					+ "lost Permissions plugin.");
		}
	}

}
