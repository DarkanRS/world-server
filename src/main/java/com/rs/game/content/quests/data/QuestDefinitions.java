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
package com.rs.game.content.quests.data;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;

import com.rs.cache.ArchiveType;
import com.rs.cache.Cache;
import com.rs.cache.IndexType;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.io.InputStream;
import com.rs.lib.util.Logger;
import com.rs.lib.util.Utils;

/**
 * @author Trenton
 * Necking myself knowing that all of this was in the cache.
 * Regretting hours of finding varbits by looping for all the quests.
 */
public class QuestDefinitions {

	public enum Type {
		NORMAL,
		HOLIDAY;
	}

	public enum Difficulty {
		NOVICE,
		INTERMEDIATE,
		EXPERIENCED,
		MASTER,
		GRANDMASTER,
		SPECIAL;
	}

	public int id;
	public String name;
	public String sortName;
	public boolean members;
	public int[] _questPrerequisiteIds;
	public int[][] _levelRequirements;
	private QuestInformation extraInfo;
	public int[][] varbitValues;
	public int[][] varValues;
	public Type type = Type.NORMAL;
	public int questpointRequirement;
	public int questpointReward;
	public Difficulty difficulty = Difficulty.NOVICE;
	public HashMap<Integer, Object> params;
	public int graphicId;


	/*
	 * LITERAL JUNK NULL FOR EVERY QUEST
	 */
	private String[] varpRequirementNames;
	private String[] varbitRequirementNames;
	public int[] anIntArray2601;
	private int[] varBitRequirements;
	private int[] minVarBitValue;
	private int[] maxVarBitValue;
	private int[] maxVarpValue;
	private int[] varpRequirements;
	private int[] minVarpValue;

	public static final HashMap<Integer, QuestDefinitions> QUESTS = new HashMap<>();
	private static final HashMap<String, QuestDefinitions> QUESTS_NAME = new HashMap<>();

	public static void main(String[] args) throws IOException {
		//		Cache.init();
		//		for (int i = 0;i < Cache.STORE.getIndex(IndexType.CONFIG).getValidFilesCount(ArchiveType.QUESTS.getId());i++) {
		//			QuestDefinitions def = new QuestDefinitions(i);
		//			//Logger.debug(def.name.toUpperCase().replace(" ", "_").replaceAll("[^A-Za-z0-9_]", "") + "(" + i + "),");
		//			if (i == 0)
		//				Logger.debug(def);
		//		}
	}

	public QuestDefinitions(int id) {
		this.id = id;
		decode(new InputStream(Cache.STORE.getIndex(IndexType.CONFIG).getFile(ArchiveType.QUESTS.getId(), id)));
	}

	public static void init() {
		QUESTS.clear();
		QUESTS_NAME.clear();
		for (int i = 0;i < Cache.STORE.getIndex(IndexType.CONFIG).getValidFilesCount(ArchiveType.QUESTS.getId());i++) {
			QuestDefinitions def = new QuestDefinitions(i);
			if (def != null) {
				QUESTS.put(i, def);
				QUESTS_NAME.put(def.name, def);
			}
		}

		/*
		 * Fix invalid values
		 */
		QuestDefinitions quest;

		//buyers cellars
		quest = QUESTS.get(174);
		quest.varbitValues[0][0] = 7793;
		quest.varbitValues[0][2] = 30;

		//shield of arrav
		quest = QUESTS.get(63);
		quest.varValues[1][1] = 1;

		//underground pass
		quest = QUESTS.get(31);
		quest.varValues[0][1] = 1;

		//as a first resort
		quest = QUESTS.get(41);
		quest.varbitValues[0][1] = 10;

		//death plat
		quest = QUESTS.get(140);
		quest.varValues = null;
		quest.varbitValues = new int[][] { new int[] { 10761, 1, 65 } };

		//workshop I
		quest = QUESTS.get(8);
		quest.varValues[0][1] = (1 << 1);
		quest.varValues[0][2] = (1 << 20);

		//fur n seek
		quest = QUESTS.get(33);
		quest.varbitValues[0][1] = 2;

		//tai bwo wannai
		quest = QUESTS.get(89);
		quest.varValues[0][1] = 3;

		//muspah
		quest = QUESTS.get(18);
		quest.varbitValues[0][1] = 10;

		//deadliest catch
		quest = QUESTS.get(191);
		quest.varbitValues[0][2] = 50;

		//workshop IV
		quest = QUESTS.get(187);
		quest.varbitValues[0][2] = 9;

		//forgiveness of a chaos dwarf
		quest = QUESTS.get(35);
		quest.varbitValues[0][2] = 90;

		//waterfall quest
		quest = QUESTS.get(93);
		quest.varValues[0][2] = 10;

		//perils of ice mountain
		quest = QUESTS.get(109);
		quest.varbitValues[0][2] = 150;

		//regicide
		quest = QUESTS.get(100);
		quest.varValues[0][2] = 15;

		//rocking out
		quest = QUESTS.get(4);
		quest.varbitValues[0][2] = 100;

		//spirit of summer
		quest = QUESTS.get(14);
		quest.varbitValues[0][2] = 100;

		//Black knights fortress, wrong qp, for some reason it was 0
		quest = QUESTS.get(53);
		quest.questpointReward = 3;
	}

	public static QuestDefinitions getQuestDefinitions(int id) {
		return QUESTS.get(id);
	}

	public static QuestDefinitions getQuestDefinitions(String name) {
		return QUESTS_NAME.get(name);
	}

	public void sendStarted(Player player) {
		if (varValues != null)
			for (int[] varValue : varValues)
				player.getVars().setVar(varValue[0], varValue[1]);
		if (varbitValues != null)
			for (int[] varbitValue : varbitValues)
				player.getVars().setVarBit(varbitValue[0], varbitValue[1]);
	}

	public void sendCompleted(Player player) {
		if (varValues != null)
			for (int[] varValue : varValues)
				player.getVars().setVar(varValue[0], varValue[2]);
		if (varbitValues != null)
			for (int[] varbitValue : varbitValues)
				player.getVars().setVarBit(varbitValue[0], varbitValue[2]);
	}

	public void decode(InputStream buffer) {
		while (buffer.getRemaining() > 0) {
			int opcode = buffer.readUnsignedByte();
			if (opcode == 0)
				break;
			if (1 == opcode)
				name = buffer.readJagString();
			else if (2 == opcode)
				sortName = buffer.readJagString();
			else if (3 == opcode) {
				int i_3_ = buffer.readUnsignedByte();
				varValues = new int[i_3_][3];
				for (int i_4_ = 0; i_4_ < i_3_; i_4_++) {
					varValues[i_4_][0] = buffer.readUnsignedShort();
					varValues[i_4_][1] = buffer.readInt();
					varValues[i_4_][2] = buffer.readInt();
				}
			} else if (opcode == 4) {
				int i_5_ = buffer.readUnsignedByte();
				varbitValues = new int[i_5_][3];
				for (int i_6_ = 0; i_6_ < i_5_; i_6_++) {
					varbitValues[i_6_][0] = buffer.readUnsignedShort();
					varbitValues[i_6_][1] = buffer.readInt();
					varbitValues[i_6_][2] = buffer.readInt();
				}
			} else if (5 == opcode)
				buffer.readShort();
			else if (6 == opcode)
				type = Type.values()[buffer.readUnsignedByte()];
			else if (7 == opcode) {
				int diff = buffer.readUnsignedByte();
				if (diff == 250)
					difficulty = Difficulty.SPECIAL;
				else
					difficulty = Difficulty.values()[diff];
			} else if (opcode == 8)
				members = true;
			else if (opcode == 9)
				questpointReward = buffer.readUnsignedByte();
			else if (opcode == 10) {
				int i_7_ = buffer.readUnsignedByte();
				anIntArray2601 = new int[i_7_];
				for (int i_8_ = 0; i_8_ < i_7_; i_8_++)
					anIntArray2601[i_8_] = buffer.readInt();
			} else if (12 == opcode)
				buffer.readInt();
			else if (opcode == 13) {
				int i_9_ = buffer.readUnsignedByte();
				_questPrerequisiteIds = new int[i_9_];
				for (int i_10_ = 0; i_10_ < i_9_; i_10_++)
					_questPrerequisiteIds[i_10_] = buffer.readUnsignedShort();
			} else if (opcode == 14) {
				int i_11_ = buffer.readUnsignedByte();
				_levelRequirements = new int[i_11_][2];
				for (int i_12_ = 0; i_12_ < i_11_; i_12_++) {
					_levelRequirements[i_12_][0] = buffer.readUnsignedByte();
					_levelRequirements[i_12_][1] = buffer.readUnsignedByte();
				}
			} else if (opcode == 15)
				questpointRequirement = buffer.readUnsignedShort();
			else if (opcode == 17)
				graphicId = buffer.readBigSmart();
			else if (18 == opcode) {
				int i_13_ = buffer.readUnsignedByte();
				varpRequirements = new int[i_13_];
				minVarpValue = new int[i_13_];
				maxVarpValue = new int[i_13_];
				varpRequirementNames = new String[i_13_];
				for (int i_14_ = 0; i_14_ < i_13_; i_14_++) {
					varpRequirements[i_14_] = buffer.readInt();
					minVarpValue[i_14_] = buffer.readInt();
					maxVarpValue[i_14_] = buffer.readInt();
					varpRequirementNames[i_14_] = buffer.readString();
				}
			} else if (19 == opcode) {
				int i_15_ = buffer.readUnsignedByte();
				varBitRequirements = new int[i_15_];
				minVarBitValue = new int[i_15_];
				maxVarBitValue = new int[i_15_];
				varbitRequirementNames = new String[i_15_];
				for (int i_16_ = 0; i_16_ < i_15_; i_16_++) {
					varBitRequirements[i_16_] = buffer.readInt();
					minVarBitValue[i_16_] = buffer.readInt();
					maxVarBitValue[i_16_] = buffer.readInt();
					varbitRequirementNames[i_16_] = buffer.readString();
				}
			} else if (249 == opcode) {
				int count = buffer.readUnsignedByte();
				if (null == params)
					params = new HashMap<>(count);
				for (int index = 0; index < count; index++) {
					boolean stringVal = (buffer.readUnsignedByte()) == 1;
					int key = buffer.read24BitInt();
					Object value;
					if (stringVal)
						value = buffer.readString();
					else
						value = buffer.readInt();
					params.put(key, value);
				}
			} else
				Logger.error(QuestDefinitions.class, "decode", "Error unrecognized quest config code: {" + opcode + "}");
		}
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		String newLine = System.getProperty("line.separator");

		result.append(this.getClass().getName());
		result.append(" {");
		result.append(newLine);

		// determine fields declared in this class only (no fields of
		// superclass)
		Field[] fields = this.getClass().getDeclaredFields();

		// print field names paired with their values
		for (Field field : fields) {
			if (Modifier.isStatic(field.getModifiers()))
				continue;
			result.append("  ");
			try {
				result.append(field.getType().getCanonicalName() + " " + field.getName() + ": ");
				result.append(Utils.getFieldValue(this, field));
			} catch (Throwable ex) {
				Logger.handleNoRecord(QuestDefinitions.class, "toString", "Error getting field info:" + field, ex);
			}
			result.append(newLine);
		}
		result.append("}");

		return result.toString();
	}

	public QuestInformation getExtraInfo() {
		return extraInfo;
	}

	public void setExtraInfo(QuestInformation extraInfo) {
		this.extraInfo = extraInfo;
	}
}
