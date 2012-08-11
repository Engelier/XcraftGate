package de.xcraft.engelier.XcraftGate;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;

public class ListenerServer implements Listener {
	private XcraftGate plugin = null;

	public ListenerServer(XcraftGate instance) {
		plugin = instance;
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPluginDisable(PluginDisableEvent event) {
		plugin.getPluginManager().checkDisabledPlugin(event.getPlugin());
	}

}
