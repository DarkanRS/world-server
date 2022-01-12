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
package com.rs.net.decoders;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import com.rs.Settings;
import com.rs.cache.Cache;
import com.rs.db.WorldDB;
import com.rs.game.World;
import com.rs.game.player.Player;
import com.rs.lib.Constants;
import com.rs.lib.io.InputStream;
import com.rs.lib.io.IsaacKeyPair;
import com.rs.lib.model.Account;
import com.rs.lib.net.Decoder;
import com.rs.lib.net.Session;
import com.rs.lib.net.decoders.GameDecoder;
import com.rs.lib.net.packets.encoders.WorldLoginDetails;
import com.rs.lib.util.Utils;
import com.rs.net.LobbyCommunicator;
import com.rs.net.encoders.WorldEncoder;
import com.rs.utils.AntiFlood;
import com.rs.utils.MachineInformation;

public final class WorldLoginDecoder extends Decoder {

	public WorldLoginDecoder(Session session) {
		super(session);
	}

	@Override
	public int decode(InputStream stream) {
		session.setDecoder(null);
		int packetId = stream.readUnsignedByte();
		long update = World.getTicksTillUpdate();
		if (update != -1 && update < 20) {
			session.sendClientPacket(14);
			return -1;
		}
		int packetSize = stream.readUnsignedShort();
		if (packetSize != stream.getRemaining()) {
			session.getChannel().close();
			return -1;
		}
		if (stream.readInt() != Constants.CLIENT_BUILD) {
			session.sendClientPacket(6);
			return -1;
		}
		if (packetId == 16 || packetId == 18) // 16 world login
			return decodeWorldLogin(stream);
		session.getChannel().close();
		return -1;
	}

	@SuppressWarnings("unused")
	public int decodeWorldLogin(InputStream stream) {
		if (stream.readInt() != Constants.CUSTOM_CLIENT_BUILD) {
			session.sendClientPacket(6);
			return -1;
		}
		boolean unknownEquals14 = stream.readUnsignedByte() == 1;
		int rsaBlockSize = stream.readUnsignedShort();
		if (rsaBlockSize > stream.getRemaining()) {
			session.sendClientPacket(10);
			return -1;
		}
		byte[] data = new byte[rsaBlockSize];
		stream.readBytes(data, 0, rsaBlockSize);
		InputStream rsaStream = new InputStream(Utils.cryptRSA(data, Constants.RSA_PRIVATE_EXPONENT, Constants.RSA_PRIVATE_MODULUS));
		if (rsaStream.readUnsignedByte() != 10) {
			session.sendClientPacket(10);
			return -1;
		}
		int[] isaacKeys = new int[4];
		for (int i = 0; i < isaacKeys.length; i++)
			isaacKeys[i] = rsaStream.readInt();
		if (rsaStream.readLong() != 0L) { // rsa block check, pass part
			session.sendClientPacket(10);
			return -1;
		}
		String password = rsaStream.readString();
		if (password.length() > 30 || password.length() < 3) {
			session.sendClientPacket(3);
			return -1;
		}
		String unknown = Utils.longToString(rsaStream.readLong());
		rsaStream.readLong(); // random value
		rsaStream.readLong(); // random value
		stream.decodeXTEA(isaacKeys, stream.getOffset(), stream.getLength());
		boolean stringUsername = stream.readUnsignedByte() == 1; // unknown
		String username = Utils.formatPlayerNameForProtocol(stringUsername ? stream.readString() : Utils.longToString(stream.readLong()));
		int displayMode = stream.readUnsignedByte();
		int screenWidth = stream.readUnsignedShort();
		int screenHeight = stream.readUnsignedShort();
		int unknown2 = stream.readUnsignedByte();
		stream.skip(24); // 24bytes directly from a file, no idea whats there
		String settings = stream.readString();
		int affid = stream.readInt();

		int prefSize = stream.readUnsignedByte();
		int[] prefs = new int[prefSize];
		for (int i = 0;i < prefs.length;i++)
			prefs[i] = stream.readUnsignedByte();
		//System.out.println(i+": " + prefs[i]);

		MachineInformation mInformation = null;
		int success = stream.readUnsignedByte();
		if (success != 6)
			System.out.println("Failed to parse machine info");
		int os = stream.readUnsignedByte();
		boolean x64OS = stream.readUnsignedByte() == 1;
		stream.readUnsignedByte();
		int osVendor = stream.readUnsignedByte();
		int javaVersion = stream.readUnsignedByte();
		int javaBuild = stream.readUnsignedByte();
		int javasubBuild = stream.readUnsignedByte();
		stream.readUnsignedByte();
		int maxMem = stream.readUnsignedShort();
		int processors = stream.readUnsignedByte();
		int ram = stream.read24BitInt();
		int cpuClock = stream.readUnsignedShort();
		stream.readJagString();
		stream.readJagString();
		stream.readJagString();
		stream.readJagString();
		int directXDriverDateMonth = stream.readUnsignedByte();
		int directXDriverDateYear = stream.readUnsignedShort();
		String cpuType = stream.readJagString();
		String cpuData = stream.readJagString();
		int cpuCores = stream.readUnsignedByte();
		int rawCPUInformation = stream.readUnsignedByte();
		int[] rawCPUInformationData = new int[3];
		for (int i = 0;i < rawCPUInformationData.length;i++)
			rawCPUInformationData[i] = stream.readInt();
		int rawCPUInformation2 = stream.readInt();
		stream.readInt();
		stream.readLong();
		stream.readString();
		if (stream.readUnsignedByte() == 1)
			stream.readString();
		stream.readUnsignedByte();
		stream.readUnsignedByte();
		stream.readUnsignedByte();
		stream.readByte();
		stream.readInt();
		String token = stream.readString();
		if (!token.equals(Constants.WORLD_TOKEN)) {
			session.sendClientPacket(6);
			return -1;
		}
		stream.readUnsignedByte();
		int worldId = stream.readInt();
		for (int index = 0; index < Cache.STORE.getIndices().length; index++) {
			int crc = Cache.STORE.getIndices()[index] == null ? -1011863738 : Cache.STORE.getIndices()[index].getCRC();
			int receivedCRC = stream.readInt();
			if (crc != receivedCRC && index < 32) {
				System.out.println("CRC mismatch: " + crc + ", " + receivedCRC);
				session.sendClientPacket(6);
				return -1;
			}
		}

		if (World.getPlayers().size() >= Settings.PLAYERS_LIMIT - 10) {
			session.sendClientPacket(7);
			return -1;
		}

		if (AntiFlood.getSessionsIP(session.getIP()) > 3) {
			session.sendClientPacket(9);
			return -1;
		}

		Account a = null;
		try {
			a = LobbyCommunicator.getAccountSync(username, password);
		} catch (InterruptedException | ExecutionException | IOException e) {
			System.err.println("Error connecting to login server!");
			session.sendClientPacket(23);
			return -1;
		}
		final Account account = a;
		if (account == null) {
			session.sendClientPacket(3);
			return -1;
		}

		if (World.containsPlayer(account.getUsername())) {
			session.sendClientPacket(5);
			return -1;
		}

		WorldDB.getPlayers().get(account.getUsername(), player -> {
			if (player == null)
				player = new Player(account);

			if (account.isBanned()) {
				session.sendClientPacket(4);
				return;
			}
			player.init(session, account, displayMode, screenWidth, screenHeight, mInformation);
			session.setIsaac(new IsaacKeyPair(isaacKeys));
			session.write(new WorldLoginDetails(Settings.getConfig().isDebug() ? 2 : player.getRights().getCrown(), player.getIndex(), player.getDisplayName()));
			session.setDecoder(new GameDecoder(session));
			session.setEncoder(new WorldEncoder(player, session));
			player.start();
		});
		return stream.getOffset();
	}

}
