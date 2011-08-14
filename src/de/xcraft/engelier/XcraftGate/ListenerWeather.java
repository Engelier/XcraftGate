package de.xcraft.engelier.XcraftGate;

import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.weather.WeatherListener;

public class ListenerWeather extends WeatherListener {
	private XcraftGate plugin;
	
	public ListenerWeather (XcraftGate instance) {
		plugin = instance;
	}
	
	public void onWeatherChange(WeatherChangeEvent event) {
		if (plugin.getWorlds().get(event.getWorld()) == null) return;
		
		event.setCancelled(!plugin.getWorlds().get(event.getWorld()).isAllowWeatherChange());
	}
}
