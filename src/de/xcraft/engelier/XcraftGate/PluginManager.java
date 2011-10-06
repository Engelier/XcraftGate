package de.xcraft.engelier.XcraftGate;

import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;
import com.nijikokun.register.payment.Method;
import com.nijikokun.register.payment.Methods;

public class PluginManager implements Runnable {
	private final XcraftGate core;
	private final org.bukkit.plugin.PluginManager pm;
	
	private Plugin register = null;
	private PermissionHandler permissions = null;
	private Method ecoMethod = null;

	public PluginManager(XcraftGate core) {
		this.core = core;
		this.pm = core.getServer().getPluginManager();
	}
	
	public void registerEvent(Event.Type type, Listener listener, Event.Priority prio) {
		pm.registerEvent(type, listener, prio, core);
	}

	public PermissionHandler getPermissions() {
		return permissions;
	}
	
	public Method getEcoMethod() {
		return ecoMethod;
	}
	
	private void checkPluginPermissions() {
		Plugin permissionsCheck = pm.getPlugin("Permissions");
		if (permissionsCheck != null && permissionsCheck.isEnabled()) {
			permissions = ((Permissions) permissionsCheck).getHandler();
			core.log.info(core.getNameBrackets() + "hooked into Permissions "
					+ permissionsCheck.getDescription().getVersion());
		} else {
			permissions = null;
		}
	}
	
	private void checkPluginRegister() {
		Plugin registerCheck = pm.getPlugin("Register");
		if (registerCheck != null && registerCheck.isEnabled()) {
			register = registerCheck;
			core.log.info(core.getNameBrackets() + "found Register plugin. Trying to find payment method.");
			if (!Methods.hasMethod()) {
				Methods.setMethod(pm);
			
				if (Methods.hasMethod()) {
					ecoMethod = Methods.getMethod();
					core.log.info(core.getNameBrackets() + "found payment method: " + ecoMethod.getName() + " " + ecoMethod.getVersion());
				} else {
					core.log.info(core.getNameBrackets() + "No payment method found, disabling toll collection.");
				}
			} else {
				ecoMethod = Methods.getMethod();
				core.log.info(core.getNameBrackets() + "found payment method: " + ecoMethod.getName() + " " + ecoMethod.getVersion());
			}
		}
	}
	
	public void checkDisabledPlugin(Plugin plugin) {
		if (plugin.getDescription().getName().equals("Permissions")) {
			permissions = null;
			core.log.info(core.getNameBrackets() + "lost permissions plugin - falling back to SuperPerms");
		}

		if (plugin.getDescription().getName().equals("Register")) {
			register = null;
			ecoMethod = null;
			core.log.info(core.getNameBrackets() + "lost Register plugin - toll collection disabled");
		}
		
		if (register != null && register.isEnabled()) {
			if (ecoMethod != null && plugin.getDescription().getName().equals(ecoMethod.getName())) {
				Methods.setMethod(pm);
				if (Methods.hasMethod()) {
					ecoMethod = Methods.getMethod();
					core.log.info(core.getNameBrackets() + "lost economy plugin - using " + ecoMethod.getName() + " " + ecoMethod.getVersion() + " now.");
				} else {
					ecoMethod = null;
					core.log.info(core.getNameBrackets() + "lost economy plugin - toll collection disabled");
				}
			}
		} else {
			ecoMethod = null; // just to be sure
		}
	}
	
	@Override
	public void run() {
		checkPluginPermissions();
		checkPluginRegister();
	}

}
