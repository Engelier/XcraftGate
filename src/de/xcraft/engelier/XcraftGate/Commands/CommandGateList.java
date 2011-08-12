package de.xcraft.engelier.XcraftGate.Commands;

import java.util.List;

import org.bukkit.command.CommandSender;

import de.xcraft.engelier.XcraftGate.XcraftGate;

public class CommandGateList extends CommandHelperGate {

	public CommandGateList(XcraftGate plugin) {
		super(plugin);
	}

	@Override
	public void execute(CommandSender sender, String gateName, List<String> args) {
		this.sender = sender;
		
		Object[] gatesArray = plugin.getGates().namesArray();
		java.util.Arrays.sort(gatesArray);
		
		String gateList = "";
		for (Object gateObj : gatesArray) {
			String thisGateName = (String) gateObj;
			if (gateList.length() + thisGateName.length() + 2 > 255) {
				reply(gateList);
				gateList = "";
			}
			
			if (gateList.length() == 0) {
				gateList = thisGateName;
			} else {
				gateList += ", " + thisGateName;
			}
		}
		reply(gateList);
	}

}
