package de.xcraft.engelier.XcraftGate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
		} else if (o instanceof String) {
			try {
				return Integer.parseInt((String) o);
			} catch (Exception ex) {
				return 0;
			}
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
	
	public static String joinString(String[] array) {
		return joinString(Arrays.asList(array), ",");
	}
	
	public static String joinString(String[] array, String seperator) {
		return joinString(Arrays.asList(array), seperator);
	}

	public static String joinString(List<String> array) {
		return joinString(array, ",");
	}	

	public static String joinString(List<String> array, String seperator) {
		StringBuilder ret = new StringBuilder();
		
		for (String part : array) {
			if (ret.length() > 0) {
				ret.append(seperator);
			}
			
			ret.append(part);
		}
		
		return ret.toString();
	}
	
	public static String joinInteger(Integer[] array, String seperator) {
		List<String> stringlist = new ArrayList<String>();
		
		for (Integer value : array) {
			stringlist.add("" + value);
		}
		
		return joinString(stringlist, seperator);
	}
}
