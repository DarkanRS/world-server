package com.rs.tools.old;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.ArrayList;

import com.rs.tools.old.WikiEqupSlotDumper.EquipSlot;
import com.rs.tools.old.WikiEqupSlotDumper.EquipSlot.SlotType;

public class CompleteDump {

	private static ArrayList<EquipSlot> slots = new ArrayList<EquipSlot>();

	public static void addItemsByHand() {
		slots.add(new EquipSlot(818, SlotType.WEAPON_SLOT));
		slots.add(new EquipSlot(1235, SlotType.WEAPON_SLOT));
		slots.add(new EquipSlot(1343, SlotType.WEAPON_SLOT));
		slots.add(new EquipSlot(2609, SlotType.LEGWEAR_SLOT));
		slots.add(new EquipSlot(4181, SlotType.HEAD_SLOT));
		slots.add(new EquipSlot(11367, SlotType.WEAPON_SLOT));
		slots.add(new EquipSlot(16009, SlotType.FEET_SLOT));
		slots.add(new EquipSlot(11698, SlotType.TWO_HANDED));
		slots.add(new EquipSlot(5648, SlotType.WEAPON_SLOT));
		slots.add(new EquipSlot(7388, SlotType.LEGWEAR_SLOT));
		slots.add(new EquipSlot(8839, SlotType.BODY_SLOT));
		slots.add(new EquipSlot(17021, SlotType.WEAPON_SLOT));
		slots.add(new EquipSlot(9139, SlotType.AMMUNITION_SLOT));
		slots.add(new EquipSlot(4335, SlotType.CAPE_SLOT));
		slots.add(new EquipSlot(3839, SlotType.SHIELD_SLOT));
		slots.add(new EquipSlot(9906, SlotType.WEAPON_SLOT));
		slots.add(new EquipSlot(15860, SlotType.WEAPON_SLOT));
		slots.add(new EquipSlot(9907, SlotType.WEAPON_SLOT));
		slots.add(new EquipSlot(9908, SlotType.WEAPON_SLOT));
		slots.add(new EquipSlot(9909, SlotType.WEAPON_SLOT));
		slots.add(new EquipSlot(9910, SlotType.WEAPON_SLOT));
		slots.add(new EquipSlot(9911, SlotType.WEAPON_SLOT));
		slots.add(new EquipSlot(10683, SlotType.BODY_SLOT));
		slots.add(new EquipSlot(10685, SlotType.BODY_SLOT));
		slots.add(new EquipSlot(16016, SlotType.BODY_SLOT));
		slots.add(new EquipSlot(16018, SlotType.BODY_SLOT));
		slots.add(new EquipSlot(10840, SlotType.WEAPON_SLOT));
		slots.add(new EquipSlot(18373, SlotType.WEAPON_SLOT));
		slots.add(new EquipSlot(10863, SlotType.LEGWEAR_SLOT));
		slots.add(new EquipSlot(19612, SlotType.SHIELD_SLOT));
		slots.add(new EquipSlot(10879, SlotType.SHIELD_SLOT));
		slots.add(new EquipSlot(12666, SlotType.HEAD_SLOT));
		slots.add(new EquipSlot(12667, SlotType.HEAD_SLOT));
		slots.add(new EquipSlot(13721, SlotType.WEAPON_SLOT));
		slots.add(new EquipSlot(13738, SlotType.SHIELD_SLOT));
		slots.add(new EquipSlot(13963, SlotType.HEAD_SLOT));
		slots.add(new EquipSlot(16138, SlotType.NECK_SLOT));
		slots.add(new EquipSlot(21485, SlotType.HEAD_SLOT));
		slots.add(new EquipSlot(16139, SlotType.NECK_SLOT));
		slots.add(new EquipSlot(16140, SlotType.NECK_SLOT));
		slots.add(new EquipSlot(16141, SlotType.NECK_SLOT));
		slots.add(new EquipSlot(16213, SlotType.WEAPON_SLOT));
		slots.add(new EquipSlot(20462, SlotType.HANDS_SLOT));
		slots.add(new EquipSlot(17169, SlotType.HANDS_SLOT));
		slots.add(new EquipSlot(19340, SlotType.SHIELD_SLOT));
		slots.add(new EquipSlot(21495, SlotType.WEAPON_SLOT));
		slots.add(new EquipSlot(22958, SlotType.BODY_SLOT));
		slots.add(new EquipSlot(22961, SlotType.HANDS_SLOT));
		slots.add(new EquipSlot(22966, SlotType.HEAD_SLOT));
		slots.add(new EquipSlot(19394, SlotType.NECK_SLOT));
		slots.add(new EquipSlot(19396, SlotType.NECK_SLOT));
		slots.add(new EquipSlot(20790, SlotType.LEGWEAR_SLOT));
		slots.add(new EquipSlot(19419, SlotType.LEGWEAR_SLOT));
		slots.add(new EquipSlot(19707, SlotType.LEGWEAR_SLOT));
		slots.add(new EquipSlot(20135, SlotType.FULL_HELMET));
		slots.add(new EquipSlot(20446, SlotType.BODY_SLOT));
		slots.add(new EquipSlot(20857, SlotType.WEAPON_SLOT));
		slots.add(new EquipSlot(20859, SlotType.WEAPON_SLOT));
		slots.add(new EquipSlot(20953, SlotType.HEAD_SLOT));
		slots.add(new EquipSlot(20954, SlotType.HEAD_SLOT));
		slots.add(new EquipSlot(20955, SlotType.HEAD_SLOT));
		slots.add(new EquipSlot(21334, SlotType.SHIELD_SLOT));
		slots.add(new EquipSlot(18739, SlotType.TWO_HANDED));
		slots.add(new EquipSlot(16939, SlotType.WEAPON_SLOT));
		slots.add(new EquipSlot(21335, SlotType.WEAPON_SLOT));
		slots.add(new EquipSlot(21487, SlotType.FEET_SLOT));
		slots.add(new EquipSlot(21513, SlotType.NECK_SLOT));
		slots.add(new EquipSlot(21519, SlotType.FEET_SLOT));
		slots.add(new EquipSlot(22288, SlotType.AURA_SLOT));
		slots.add(new EquipSlot(22289, SlotType.AURA_SLOT));
		slots.add(new EquipSlot(22290, SlotType.AURA_SLOT));
		slots.add(new EquipSlot(22291, SlotType.AURA_SLOT));
		slots.add(new EquipSlot(22336, SlotType.AMMUNITION_SLOT));
		slots.add(new EquipSlot(22337, SlotType.AMMUNITION_SLOT));
		slots.add(new EquipSlot(22911, SlotType.AURA_SLOT));
		slots.add(new EquipSlot(22912, SlotType.AURA_SLOT));
		slots.add(new EquipSlot(23856, SlotType.AURA_SLOT));
		slots.add(new EquipSlot(23857, SlotType.AURA_SLOT));
		slots.add(new EquipSlot(24172, SlotType.WEAPON_SLOT));
		slots.add(new EquipSlot(24173, SlotType.WEAPON_SLOT));
		slots.add(new EquipSlot(24174, SlotType.WEAPON_SLOT));
		slots.add(new EquipSlot(24186, SlotType.WEAPON_SLOT));
		slots.add(new EquipSlot(24187, SlotType.WEAPON_SLOT));
		slots.add(new EquipSlot(24188, SlotType.WEAPON_SLOT));
		slots.add(new EquipSlot(24189, SlotType.WEAPON_SLOT));
		slots.add(new EquipSlot(24210, SlotType.CAPE_SLOT));
		slots.add(new EquipSlot(24294, SlotType.WEAPON_SLOT));
		slots.add(new EquipSlot(24296, SlotType.HEAD_SLOT));
		slots.add(new EquipSlot(24297, SlotType.HEAD_SLOT));
		slots.add(new EquipSlot(24298, SlotType.HEAD_SLOT));
		slots.add(new EquipSlot(29999, SlotType.CAPE_SLOT));
		slots.add(new EquipSlot(20760, SlotType.CAPE_SLOT));
	}

	public static void main(String[] args) {
		addItemsByHand();
		try {
			RandomAccessFile in = new RandomAccessFile("./slots.s/", "r");
			FileChannel channel = in.getChannel();
			ByteBuffer buffer = channel.map(MapMode.READ_ONLY, 0, channel.size());
			while (buffer.hasRemaining()) {
				int id = buffer.getShort();
				int length = buffer.get() & 0xff;
				byte[] data = new byte[length];
				buffer.get(data, 0, length);
				EquipSlot ep = new EquipSlot(id, SlotType.valueOf(new String(data)));
				if (slots.contains(ep))
					slots.remove(ep);
				slots.add(ep);
			}
			DataOutputStream out = new DataOutputStream(new FileOutputStream("./data/items/equipslots.es/"));
			for (EquipSlot slot : slots) {
				out.writeShort(slot.getId());
				out.write(slot.getType().toString().getBytes());
			}
			out.close();
			in.close();
			System.out.println("Packed Defintions.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
