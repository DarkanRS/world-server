package com.rs.game.model.entity;

import com.rs.lib.io.OutputStream;

public class ModelRotator {
	public static final ModelRotator RESET = new ModelRotator().addRotator(new Rotation().enableAll());
	
	private int[] targetData = new int[15];
	private int[] slotHashes = new int[15];
	private int curr = 0;
	
	public ModelRotator() {
		
	}
	
	public ModelRotator addRotator(Rotation rotator) {
		targetData[curr] = rotator.getData();
		slotHashes[curr++] = rotator.getMask();
		return this;
	}
	
	public void encodeNPC(OutputStream stream) {
		stream.write128Byte(curr);
		for (int i = 0;i < curr;i++) {
			if ((targetData[i] & 0xc000) == 0xc000) {
				stream.writeShortLE128(targetData[i] >> 16);
				stream.writeShort(targetData[i] & 0xFFFF);
			} else
				stream.writeShortLE128(targetData[i]);
			stream.writeShortLE128(slotHashes[i]);
		}		
	}
	
	public void encodePlayer(OutputStream stream) {
		stream.write128Byte(curr);
		for (int i = 0;i < curr;i++) {
			if ((targetData[i] & 0xc000) == 0xc000) {
				stream.writeShort128(targetData[i] >> 16);
				stream.writeShort128(targetData[i] & 0xFFFF);
			} else
				stream.writeShort128(targetData[i]);
			stream.writeShort(slotHashes[i]);
		}		
	}
}
