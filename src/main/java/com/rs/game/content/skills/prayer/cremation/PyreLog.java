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
package com.rs.game.content.skills.prayer.cremation;

import java.util.*;

public enum PyreLog {
	NORMAL(5, 50, 1511, 3438, 2, 30468, 30478, 4094, 4100, Corpse.LOAR, Corpse.PHRIN),
	OAK(20, 70, 1521, 3440, 2, 30469, 30479, 4095, 4101, Corpse.LOAR, Corpse.PHRIN),
	WILLOW(35, 100, 1519, 3442, 3, 30470, 30480, 4096, 4102, Corpse.LOAR, Corpse.PHRIN, Corpse.RIYL),
	TEAK(40, 120, 6333, 6211, 3, 30471, 30481, 9006, 9008, Corpse.LOAR, Corpse.PHRIN, Corpse.RIYL, Corpse.VYRE),
	ARCTIC(47, 158, 10810, 10808, 3, 30472, 30482, 21271, 21272, Corpse.LOAR, Corpse.PHRIN, Corpse.RIYL, Corpse.VYRE),
	MAPLE(50, 175, 1517, 3444, 3, 30473, 30483, 4097, 4103, Corpse.LOAR, Corpse.PHRIN, Corpse.RIYL, Corpse.VYRE),
	MAHOGANY(55, 210, 6332, 6213, 3, 30474, 30484, 9007, 9009, Corpse.LOAR, Corpse.PHRIN, Corpse.RIYL, Corpse.VYRE),
	EUCALYPTUS(63, 246.5, 12581, 12583, 4, 30476, 30485, 29166, 29167, Corpse.LOAR, Corpse.PHRIN, Corpse.RIYL, Corpse.VYRE, Corpse.ASYN),
	YEW(65, 255, 1515, 3446, 4, 30475, 30486, 4098, 4104, Corpse.LOAR, Corpse.PHRIN, Corpse.RIYL, Corpse.VYRE, Corpse.ASYN),
	MAGIC(80, 404.5, 1513, 3448, 4, 30477, 30487, 29181, 29182, Corpse.LOAR, Corpse.PHRIN, Corpse.RIYL, Corpse.VYRE, Corpse.ASYN, Corpse.FIYR);

	private static Map<Integer, PyreLog> BASELOG_MAP = new HashMap<>();
	private static Map<Integer, PyreLog> ID_MAP = new HashMap<>();

	static {
		for (PyreLog l : PyreLog.values()) {
			BASELOG_MAP.put(l.baseLog, l);
			ID_MAP.put(l.itemId, l);
		}
	}

	public static PyreLog forBaseLog(int base) {
		return BASELOG_MAP.get(base);
	}

	public static PyreLog forId(int base) {
		return ID_MAP.get(base);
	}

	public final int level, baseLog, itemId, oilDoses, vyreNoCorpse, vyreCorpse, shadeNoCorpse, shadeCorpse;
	public final double xp;
	private final Set<Corpse> validCorpseTypes;

	private PyreLog(int level, double xp, int baseLog, int itemId, int oilDoses, int vyreNoCorpse, int vyreCorpse, int shadeNoCorpse, int shadeCorpse, Corpse... validCorpseTypes) {
		this.level = level;
		this.xp = xp;
		this.baseLog = baseLog;
		this.itemId = itemId;
		this.oilDoses = oilDoses;
		this.vyreNoCorpse = vyreNoCorpse;
		this.vyreCorpse = vyreCorpse;
		this.shadeNoCorpse = shadeNoCorpse;
		this.shadeCorpse = shadeCorpse;
		this.validCorpseTypes = new HashSet<>(Arrays.asList(validCorpseTypes));
	}

	public int getCreationXP() {
		return oilDoses * 20;
	}

	public boolean validCorpse(Corpse corpse) {
		return validCorpseTypes.contains(corpse);
	}
}
