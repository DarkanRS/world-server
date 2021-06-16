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
			System.out.println("Connection type: " + packetId);
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
