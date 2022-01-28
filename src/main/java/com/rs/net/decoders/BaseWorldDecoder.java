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
//  Copyright (C) 2021 Trenton Kress
//  This file is part of project: Darkan
//
package com.rs.net.decoders;

import com.rs.lib.io.InputStream;
import com.rs.lib.net.Decoder;
import com.rs.lib.net.Encoder;
import com.rs.lib.net.Session;

public final class BaseWorldDecoder extends Decoder {

	public BaseWorldDecoder() {

	}

	public BaseWorldDecoder(Session connection) {
		super(connection);
	}

	@Override
	public final int decode(InputStream stream) {
		session.setDecoder(null);
		int packetId = stream.readUnsignedByte();
		switch (packetId) {
		case 14:
			return decodeLogin(stream);
		default:
			System.out.println("Connection type: " + packetId + " Remaining: " + stream.getRemaining());
			String hex = "";
			for (byte i : stream.getBuffer())
				hex += String.format("%02X", i);
			System.out.println(hex);
			return -1;
		}
	}

	private final int decodeLogin(InputStream stream) {
		if (stream.getRemaining() != 0) {
			session.getChannel().close();
			return -1;
		}
		session.setDecoder(new WorldLoginDecoder(session));
		session.setEncoder(new Encoder(session));
		session.sendLoginStartup();
		return stream.getOffset();
	}
}
