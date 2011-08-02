package de.xcraft.engelier.XcraftGate;

import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.weather.WeatherListener;

public class XcraftGateWeatherListener extends WeatherListener {
	private XcraftGate plugin;
	
	public XcraftGateWeatherListener (XcraftGate instance) {
		plugin = instance;
	}
	
	public void onWeatherChange(WeatherChangeEvent event) {
		if (plugin.worlds.get(event.getWorld().getName()) == null) return;
		
		if (!plugin.worlds.get(event.getWorld().getName()).allowWeatherChange)
			event.setCancelled(true);
	}
}
