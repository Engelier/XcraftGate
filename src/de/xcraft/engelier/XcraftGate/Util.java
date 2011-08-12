package de.xcraft.engelier.XcraftGate;

import org.bukkit.Location;

public class Util {
	public static Integer castInt(Object o) {
		if (o == null) {
			return 0;
		} else if (o instanceof Byte) {
			return (int)(Byte)o;
		} else if (o instanceof Integer) {
			return (Integer)o;
		} else if (o instanceof Double) {
			return (int)(double)(Double)o;
		} else if (o instanceof Float) {
			return (int)(float)(Float)o;
		} else if (o instanceof Long) {
			return (int)(long)(Long)o;
		} else {
			return 0;
		}
	}
	
	public static Boolean castBoolean(Object o) {
		if (o == null) {
			return false;
		} else if (o instanceof Boolean) {
			return (Boolean)o;
		} else if (o instanceof String) {
			return ((String)o).equalsIgnoreCase("true") ? true : false;
		} else {
			return false;
		}
	}
	
	public static String getLocationString(Location location) {
		if (location.getWorld() != null) {
			return location.getWorld().getName() + ","
					+ Math.floor(location.getX()) + ","
					+ Math.floor(location.getY()) + ","
					+ Math.floor(location.getZ());
		} else {
			return null;
		}
	}

	public static Location getSaneLocation(Location loc) {
		double x = Math.floor(loc.getX()) + 0.5;
		double y = loc.getY();
		double z = Math.floor(loc.getZ()) + 0.5;
		
		return new Location(loc.getWorld(), x, y, z, loc.getYaw(), loc.getPitch());
	}
}
