// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.
//
//  Copyright © 2021 Trenton Kress
//  This file is part of project: Darkan
//
package com.rs.net.decoders.handlers;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.rs.lib.net.ClientPacket;
import com.rs.lib.net.packets.Packet;
import com.rs.lib.net.packets.PacketDecoder;
import com.rs.lib.net.packets.PacketHandler;
import com.rs.lib.util.Logger;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.annotations.ServerStartupEvent;

@PluginEventHandler
public class PacketHandlers {

	private static Map<ClientPacket, PacketHandler<?, ? extends Packet>> PACKET_HANDLERS = new HashMap<>();

	@ServerStartupEvent
	public static void loadPacketDecoders() {
		loadHandlersFromPackage("com.rs.net.decoders.handlers.impl");
	}

	@SuppressWarnings("unchecked")
	public static void loadHandlersFromPackage(String pack) {
		try {
			Logger.log("PacketHandlers", "Initializing packet handlers ("+pack+")...");
			ArrayList<Class<?>> classes = Utils.getClasses(pack);

			for (Class<?> clazz : classes)
				mapHandler((PacketHandler<?, ? extends Packet>) clazz.getConstructor().newInstance());

			Set<ClientPacket> missing = new HashSet<>();
			for (ClientPacket packet : ClientPacket.values())
				if (PACKET_HANDLERS.get(packet) == null)
					missing.add(packet);

			int handled = ClientPacket.values().length - missing.size();
			Logger.log("PacketHandlers", "Packet handlers loaded for " + handled + " packets...");
			Logger.log("PacketHandlers", "Packets missing: " + missing);
		} catch (ClassNotFoundException | IOException | IllegalArgumentException | SecurityException | InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			e.printStackTrace();
		}
	}

	public static void mapHandler(PacketHandler<?, ? extends Packet> handler) {
		ParameterizedType type = (ParameterizedType) handler.getClass().getGenericInterfaces()[0];
		Class<?> clazz = (Class<?>) type.getActualTypeArguments()[1];
		PacketDecoder annotation = clazz.getAnnotation(PacketDecoder.class);
		for (ClientPacket packet : annotation.value())
			PACKET_HANDLERS.put(packet, handler);
	}

	@SuppressWarnings("unchecked")
	public static <T> PacketHandler<T, Packet> getHandler(ClientPacket packet) {
		return (PacketHandler<T, Packet>) PACKET_HANDLERS.get(packet);
	}
}
