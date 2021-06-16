package com.rs.utils;

import com.rs.game.player.Player;

@SuppressWarnings("unused")
public class MachineInformation {

	private int os;
	private boolean x64Arch;
	private int osVersion;
	private int osVendor;
	private int javaVersion;
	private int javaVersionBuild;
	private int javaVersionBuild2;
	private boolean hasApplet;
	private int heap;
	private int availableProcessors;
	private int ram;
	private int cpuClockFrequency;
	private int cpuInfo3;
	private int cpuInfo4;
	private int cpuInfo5;

	public MachineInformation(int os, boolean x64Arch, int osVersion, int osVendor, int javaVersion, int javaVersionBuild, int javaVersionBuild2, boolean hasApplet, int heap, int availableProcessor, int ram, int cpuClockFrequency, int cpuInfo3,
			int cpuInfo4, int cpuInfo5) {
		this.os = os;
		this.x64Arch = x64Arch;
		this.osVersion = osVersion;
		this.javaVersion = javaVersion;
		this.javaVersionBuild = javaVersionBuild;
		this.javaVersionBuild2 = javaVersionBuild2;
		this.hasApplet = hasApplet;
		this.heap = heap;
		this.availableProcessors = availableProcessor;
		this.ram = ram;
		this.cpuClockFrequency = cpuClockFrequency;
		this.cpuInfo3 = cpuInfo3;
		this.cpuInfo4 = cpuInfo4;
		this.cpuInfo5 = cpuInfo5;
	}

	public String getVersion() {
		return javaVersion + "." + javaVersionBuild + "." + javaVersionBuild2;
	}

	public void sendSuggestions(Player player) {
		String suggestion = null;
		String title = null;
		if (javaVersion < 6) {
			title = "Client Issues";
			suggestion = "You seem to be using java version: " + getVersion() + ".<br>You should update to jre6.";
		}/*
		 * else if(javaVersionBuild != 0 || javaVersionBuild2 < 31) { title =
		 * "Outdated Java Version"; suggestion =
		 * "Your java seems outdated: "+getVersion
		 * ()+".<br>You should update your to 6.0.31.";
		 *//*
			 * }else if (hasApplet && ((availableProcessors <= (x64Arch ? 2 :
			 * 1)) || ram <= (x64Arch ? 1024 : 512) || cpuClockFrequency <=
			 * 1500)) { title = "Weak Specs"; suggestion =
			 * "Your computer seems to have weak specs. You'd better download desktop client for better perfomance."
			 * ; }
			 */
		if (title != null) {
			player.getInterfaceManager().sendInterface(405);
			player.getPackets().setIFText(405, 16, title);
			player.getPackets().setIFText(405, 17, suggestion);
		}
	}

}
