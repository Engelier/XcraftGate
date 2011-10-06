package de.xcraft.engelier.XcraftGate;

import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.ServerListener;

public class ListenerServer extends ServerListener {
	private XcraftGate plugin = null;

	public ListenerServer(XcraftGate instance) {
		plugin = instance;
	}

	@Override
	public void onPluginDisable(PluginDisableEvent event) {
		plugin.getPluginManager().checkDisabledPlugin(event.getPlugin());
	}

}
