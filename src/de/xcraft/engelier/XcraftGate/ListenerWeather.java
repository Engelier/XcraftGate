package de.xcraft.engelier.XcraftGate;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.WeatherChangeEvent;

public class ListenerWeather implements Listener {
	private XcraftGate plugin;
	
	public ListenerWeather (XcraftGate instance) {
		plugin = instance;
	}
	
	@EventHandler
	public void onWeatherChange(WeatherChangeEvent event) {
		if (plugin.getWorlds().get(event.getWorld()) == null) return;
		
		event.setCancelled(!plugin.getWorlds().get(event.getWorld()).isAllowWeatherChange());
	}
}
