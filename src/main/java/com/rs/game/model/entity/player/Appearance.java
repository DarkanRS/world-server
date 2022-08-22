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
package com.rs.game.model.entity.player;

import java.util.ArrayList;

import com.rs.cache.loaders.EnumDefinitions;
import com.rs.cache.loaders.ItemDefinitions;
import com.rs.cache.loaders.NPCDefinitions;
import com.rs.cache.loaders.StructDefinitions;
import com.rs.game.World;
import com.rs.game.model.entity.BodyGlow;
import com.rs.lib.game.Item;
import com.rs.lib.io.OutputStream;
import com.rs.lib.util.Utils;

public class Appearance {

	private transient int bas;
	private int title;
	private int[] lookI;
	private int[] colour;
	private boolean male;
	private transient boolean glowRed;
	private transient byte[] appearanceData;
	private transient byte[] md5AppearanceDataHash;
	private transient short transformedNpcId;
	private transient boolean hidePlayer;

	public static final int FEMALE_HAIR_STRUCT_LOOKUP = 2341;
	public static final int MALE_HAIR_STRUCT_LOOKUP = 2338;
	public static final int MALE_HAIR_SLOT_LOOKUP = 2339;
	public static final int FEMALE_HAIR_SLOT_LOOKUP = 2342;

	public static final int HAIR_WITH_HAT_PARAM = 790;
	public static final int HAIR_WITH_FACE_MASK_PARAM = 791;

	private transient Player player;

	public Appearance() {
		male = true;
		bas = -1;
		title = -1;
		resetAppearance();
	}

	public void setGlowRed(boolean glowRed) {
		this.glowRed = glowRed;
		generateAppearanceData();
	}

	public void setPlayer(Player player) {
		this.player = player;
		transformedNpcId = -1;
		bas = -1;
		if (lookI == null)
			resetAppearance();
	}

	public void transformIntoNPC(int id) {
		transformedNpcId = (short) id;
		generateAppearanceData();
	}

	public void switchHidden() {
		hidePlayer = !hidePlayer;
		generateAppearanceData();
	}

	public void setHidden(boolean hidden) {
		hidePlayer = hidden;
		generateAppearanceData();
	}

	public boolean isHidden() {
		return hidePlayer;
	}

	public boolean isGlowRed() {
		return glowRed;
	}

	private int getHatHairStyle(int baseStyle, boolean isFaceMask) {
		EnumDefinitions lookup = EnumDefinitions.getEnum(male ? MALE_HAIR_SLOT_LOOKUP : FEMALE_HAIR_SLOT_LOOKUP);
		int slot = lookup.getIntValue(baseStyle);
		EnumDefinitions structLookup = EnumDefinitions.getEnum(male ? MALE_HAIR_STRUCT_LOOKUP : FEMALE_HAIR_STRUCT_LOOKUP);
		int structID = structLookup.getIntValue(slot);
		return StructDefinitions.getStruct(structID).getIntValue(isFaceMask ? HAIR_WITH_FACE_MASK_PARAM : HAIR_WITH_HAT_PARAM, -1);
	}

	public void generateAppearanceData() {
		OutputStream stream = new OutputStream();
		boolean pvpArea = World.isPvpArea(player);
		boolean showSkillTotal = player.getTempAttribs().getB("showSkillTotal") && !pvpArea;
		if (glowRed && player.getNextBodyGlow() == null)
			player.setNextBodyGlow(new BodyGlow(90, 0, 0, 0, 255));
		int flag = 0;
		if (!male)
			flag |= 0x1;
		if (transformedNpcId >= 0 && NPCDefinitions.getDefs(transformedNpcId).aBool4872)
			flag |= 0x2;
		if (showSkillTotal)
			flag |= 0x4;
		if (title != 0 || player.getTitle() != null)
			flag |= isTitleAfter(title) || player.isTitleAfter() ? 0x80 : 0x40; // after/before

		// flag += sizeOffset << 3;
		// flag |= 0x7;

		stream.writeByte(flag);
		if (title != 0 || player.getTitle() != null)
			stream.writeGJString(player.getFormattedTitle());
		stream.writeByte(player.hasSkull() ? player.getSkullId() : -1);
		stream.writeByte(player.getTempAttribs().getI("customHeadIcon", -1) != -1 ? player.getTempAttribs().getI("customHeadIcon") : player.getPrayer().getPrayerHeadIcon());
		stream.writeByte(hidePlayer ? 1 : 0);

		if (transformedNpcId >= 0) {
			stream.writeShort(-1);
			stream.writeShort(transformedNpcId);
			stream.writeByte(0);
		} else {
			for (int index = 0; index < 4; index++) {
				Item item = player.getEquipment().get(index);
				if (item == null)
					stream.writeByte(0);
				else
					stream.writeShort(16384 + item.getId());
			}

			Item item = player.getEquipment().get(Equipment.CHEST);
			stream.writeShort(item == null ? 0x100 + lookI[2] : 16384 + item.getId());

			item = player.getEquipment().get(Equipment.SHIELD);
			if (item == null)
				stream.writeByte(0);
			else
				stream.writeShort(16384 + item.getId());

			item = player.getEquipment().get(Equipment.CHEST);
			if (lookI[3] != -1 && (item == null || !Equipment.hideArms(item)))
				stream.writeShort(0x100 + lookI[3]);
			else if (item != null && !Equipment.hideArms(item))
				stream.writeShort(0x100 + getOldArms());
			else
				stream.writeByte(0);
			item = player.getEquipment().get(Equipment.LEGS);
			stream.writeShort(item == null ? 0x100 + lookI[5] : 16384 + item.getId());

			item = player.getEquipment().get(Equipment.HEAD);
			if (lookI[0] != -1 && (item == null || !Equipment.hideHair(item))) {
				if (item == null)
					stream.writeShort(0x100 + lookI[0]);
				else {
					int hatHairStyle = getHatHairStyle(lookI[0], item.getDefinitions().faceMask());
					if (hatHairStyle != -1)
						stream.writeShort(0x100 + hatHairStyle);
					else
						stream.writeByte(0);
				}
			} else
				stream.writeByte(0);

			item = player.getEquipment().get(Equipment.HANDS);
			stream.writeShort(item == null ? 0x100 + lookI[4] : 16384 + item.getId());

			item = player.getEquipment().get(Equipment.FEET);
			stream.writeShort(item == null ? 0x100 + lookI[6] : 16384 + item.getId());

			item = player.getEquipment().get(male ? Equipment.HEAD : Equipment.CHEST);
			if (male && lookI[1] != -1 && (item == null || (male && !Equipment.hideBeard(item))))
				stream.writeShort(0x100 + lookI[1]);
			else
				stream.writeByte(0);

			item = player.getEquipment().get(Equipment.AURA);
			if (item == null)
				stream.writeByte(0);
			else
				stream.writeShort(16384 + item.getId());

			encodeMeshModifiers(stream);
		}

		for (int element : colour)
			stream.writeByte(element);

		stream.writeShort(getRenderEmote());
		stream.writeString(player.getDisplayName());
		stream.writeByte(pvpArea ? player.getSkills().getCombatLevel() : player.getSkills().getCombatLevelWithSummoning());
		if (showSkillTotal)
			stream.writeShort(player.getSkills().getTotalLevel());
		else {
			stream.writeByte(pvpArea ? player.getSkills().getCombatLevelWithSummoning() : 0);
			stream.writeByte(player.getPvpCombatLevelThreshhold());
		}
		stream.writeByte(transformedNpcId >= 0 ? 1 : 0);
		if (transformedNpcId >= 0) {
			NPCDefinitions defs = NPCDefinitions.getDefs(transformedNpcId);
			stream.writeShort(defs.walkingAnimation);
			stream.writeShort(defs.rotate180Animation);
			stream.writeShort(defs.rotate90RightAnimation);
			stream.writeShort(defs.rotate90LeftAnimation);
			stream.writeByte(defs.specialByte);
		}

		// done separated for safe because of synchronization
		byte[] appeareanceData = new byte[stream.getOffset()];
		System.arraycopy(stream.getBuffer(), 0, appeareanceData, 0, appeareanceData.length);
		byte[] md5Hash = Utils.encryptUsingMD5(appeareanceData);
		appearanceData = appeareanceData;
		md5AppearanceDataHash = md5Hash;
	}

	private ArrayList<MeshModifier> getMeshModifiers() {
		ArrayList<MeshModifier> modifiers = new ArrayList<>();
		int slotFlag = -1;
		for (int slotId = 0; slotId < Equipment.SIZE; slotId++) {
			if (Equipment.DISABLED_SLOTS[slotId] != 0)
				continue;
			slotFlag++;
			if (player.getEquipment().getId(slotId) == -1)
				continue;
			Item item = player.getEquipment().get(slotId);
			if (item == null)
				continue;
			ItemDefinitions defs = item.getDefinitions();
			if (defs == null)
				continue;

			switch(defs.getId()) {
			case 1833:
			case 1835:
			case 1171:
			case 2637:
				MeshModifier mod = new MeshModifier(defs, slotFlag);
				boolean add = false;
				if (item.getMetaDataI("drTOr", -1) > 0) {
					mod.addTextures(item.getMetaDataI("drTOr", -1), item.getMetaDataI("drTOr", -1), item.getMetaDataI("drTOr", -1));
					add = true;
				}
				if (item.getMetaDataI("drCOr", -1) > 0) {
					mod.addColors(item.getMetaDataI("drCOr", -1));
					add = true;
				}
				if (add)
					modifiers.add(mod);
				break;
			case 20767:
			case 20768:
				modifiers.add(new MeshModifier(defs, slotFlag)
						.addColors(player.getMaxedCapeCustomized()));
				break;
			case 20769:
			case 20770:
			case 20771:
			case 20772:
				modifiers.add(new MeshModifier(defs, slotFlag)
						.addColors(player.getCompletionistCapeCustomized()));
				break;
			case 20708:
			case 20709:
				if (player.getClan() != null)
					modifiers.add(new MeshModifier(defs, slotFlag)
							.addColors(player.getClan().getMottifColors())
							.addTextures(player.getClan().getMottifTextures()));
				break;
			}

			if (slotId == Equipment.AURA && player.getAuraManager().isActive())
				modifiers.add(new MeshModifier(defs, slotFlag)
						.addBodyModels(player.getAuraManager().getAuraModelId(), player.getAuraManager().getAuraModelId2()));

		}
		return modifiers;
	}

	private void encodeMeshModifiers(OutputStream stream) {
		int start = stream.getOffset();
		stream.writeShort(0);
		int slotHash = 0;
		for (MeshModifier modifier : getMeshModifiers()) {
			int slot = modifier.encode(stream);
			if (slot != -1)
				slotHash |= 1 << slot;
		}
		int end = stream.getOffset();
		stream.setOffset(start);
		stream.writeShort(slotHash);
		stream.setOffset(end);
	}

	public int getSize() {
		if (transformedNpcId >= 0)
			return NPCDefinitions.getDefs(transformedNpcId).size;
		return 1;
	}

	public void setBAS(int id) {
		bas = id;
		generateAppearanceData();
	}

	public boolean isTitleAfter(int title) {
		if (title >= 32 && title <= 37)
			return true;

		if (title == 64)
			return false;

		if (title >= 58 && title <= 65)
			return true;

		if (title == 40 || title == 43 || title == 45 || title == 47 || title == 49 || title == 53 || title == 55 || title == 56 || title == 72 || title == 73)
			return true;
		return false;
	}

	public int getTransformedNPC() {
		return transformedNpcId;
	}

	public boolean isNPC() {
		return transformedNpcId != -1;
	}

	public int getRenderEmote() {
		if (bas >= 0)
			return bas;
		if (transformedNpcId >= 0)
			return NPCDefinitions.getDefs(transformedNpcId).basId;
		return player.getEquipment().getWeaponBAS();
	}

	public void resetAppearance() {
		lookI = new int[7];
		colour = new int[10];
		male();
	}

	public int getOldArms() {
		return male ? 26 : 61;
	}

	public void male() {
		lookI[0] = 310;
		lookI[1] = 16;
		lookI[2] = 452;
		lookI[3] = -1;
		lookI[4] = 371;
		lookI[5] = 627;
		lookI[6] = 433;
		colour[0] = 12;
		colour[1] = 218;
		colour[2] = 218;
		colour[3] = 180;
		colour[4] = 110;
		colour[5] = 0;
		colour[6] = 0;
		colour[7] = 0;
		colour[8] = 0;
		colour[9] = 0;
		male = true;
	}

	public void female() {
		lookI[0] = 274;
		lookI[1] = -1;
		lookI[2] = 561;
		lookI[3] = -1;
		lookI[4] = 514;
		lookI[5] = 482;
		lookI[6] = 547;
		colour[0] = 12;
		colour[1] = 218;
		colour[2] = 218;
		colour[3] = 180;
		colour[4] = 110;
		colour[5] = 0;
		colour[6] = 0;
		colour[7] = 0;
		colour[8] = 0;
		colour[9] = 0;
		male = false;
	}

	public byte[] getAppeareanceData() {
		return appearanceData;
	}

	public byte[] getMD5AppeareanceDataHash() {
		return md5AppearanceDataHash;
	}

	public boolean isMale() {
		return male;
	}

	public void setLook(int i, int i2) {
		lookI[i] = i2;
		if (i == 2)
			verifyArms();
	}

	public void setColor(int i, int i2) {
		colour[i] = (byte) i2;
	}

	public void setMale(boolean male) {
		this.male = male;
	}

	public void setHairStyle(int i) {
		lookI[0] = i;
	}

	public void setTopStyle(int i) {
		lookI[2] = i;
		verifyArms();
	}

	public int getTopStyle() {
		return lookI[2];
	}

	public void setArmsStyle(int i) {
		lookI[3] = i;
	}

	public int getArmsStyle() {
		return lookI[3];
	}

	public void setWristsStyle(int i) {
		lookI[4] = i;
	}

	public int getWristsStyle() {
		return lookI[4];
	}

	public void setLegsStyle(int i) {
		lookI[5] = i;
	}

	public int getHairStyle() {
		return lookI[0];
	}

	public void setBeardStyle(int i) {
		lookI[1] = i;
	}

	public int getBeardStyle() {
		return lookI[1];
	}

	public void setFacialHair(int i) {
		lookI[1] = i;
	}

	public int getFacialHair() {
		return lookI[1];
	}

	public void setSkinColor(int color) {
		colour[4] = (byte) color;
	}

	public int getSkinColor() {
		return colour[4];
	}

	public void setHairColor(int color) {
		colour[0] = (byte) color;
	}

	public void setTopColor(int color) {
		colour[1] = (byte) color;
	}

	public void setLegsColor(int color) {
		colour[2] = (byte) color;
	}

	public void setBootsStyle(int i) {
		lookI[6] = i;
	}

	public void setBootsColor(int color) {
		colour[3] = (byte) color;
	}

	public int getHairColor() {
		return colour[0];
	}

	public void setTitle(int title) {
		this.title = title;
		generateAppearanceData();
	}

	public int getTitle() {
		return title;
	}

	public void verifyArms() {
		int topStyle = getTopStyle();
		int setId = getSetByStyle(topStyle, 3, !isMale());
		if (setId != -1) {
			StructDefinitions set = StructDefinitions.getStruct(setId);
			setArmsStyle(set.getIntValue(1183, -1));
			setWristsStyle(set.getIntValue(1184, -1));
		} else {
			if (EnumDefinitions.getEnum(isMale() ? 711 : 693).getKeyForValue(getArmsStyle()) == -1)
				setArmsStyle(isMale() ? 26 : 61);
			if (EnumDefinitions.getEnum(isMale() ? 749 : 751).getKeyForValue(getWristsStyle()) == -1)
				setWristsStyle(isMale() ? 34 : 68);
		}
	}

	private static int getSetByStyle(int styleID, int styleSlot, boolean female) {
		EnumDefinitions sets = EnumDefinitions.getEnum(5735);
		for (int slot = sets.getSize() - 1; slot >= 0; slot--) {
			int structId = sets.getIntValue(slot);
			if (structId != -1) {
				StructDefinitions struct = StructDefinitions.getStruct(structId);
				int v7 = 0;
				for (int setId = getSetStruct(struct, 0, female); setId != -1; setId = getSetStruct(struct, v7, female)) {
					StructDefinitions setStyles = StructDefinitions.getStruct(setId);
					switch (styleSlot) {
					case 3:
						if (setStyles.getIntValue(1182, -1) == styleID)
							return setId;
						break;
					case 4:
						if (setStyles.getIntValue(1183, -1) == styleID)
							return setId;
						break;
					case 5:
						if (setStyles.getIntValue(1184, -1) == styleID)
							return setId;
						break;
					case 6:
						if (setStyles.getIntValue(1185, -1) == styleID)
							return setId;
						break;
					default:
						return -1;
					}
					v7++;
				}
			}
		}
		return -1;
	}

	private static int getSetStruct(StructDefinitions struct, int slot, boolean female) {
		switch (slot) {
		case 0:
			return struct.getIntValue(female ? 1175 : 1169, -1);
		case 1:
			return struct.getIntValue(female ? 1176: 1170, -1);
		case 2:
			return struct.getIntValue(female ? 1177 : 1171, -1);
		case 3:
			return struct.getIntValue(female ? 1178 : 1172, -1);
		case 4:
			return struct.getIntValue(female ? 1179 : 1173, -1);
		case 5:
			return struct.getIntValue(female ? 1180 : 1174, -1);
		default:
			return -1;
		}
	}

	public void printDebug() {
		for (int i = 0;i < lookI.length;i++)
			System.out.println("lookI["+i+"] = " + lookI[i] + ";");
		for (int i = 0;i < colour.length;i++)
			System.out.println("colour["+i+"] = " + colour[i] + ";");
	}
}
