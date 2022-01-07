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
package com.rs.game.player.managers;

import java.util.HashMap;
import java.util.Map;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.Hit;
import com.rs.game.player.Equipment;
import com.rs.game.player.Player;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.game.SpotAnim;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ItemClickEvent;
import com.rs.plugin.events.ItemEquipEvent;
import com.rs.plugin.events.XPGainEvent;
import com.rs.plugin.handlers.ItemClickHandler;
import com.rs.plugin.handlers.ItemEquipHandler;
import com.rs.plugin.handlers.XPGainHandler;
import com.rs.utils.Millis;

@PluginEventHandler
public class AuraManager {

	private transient Player player;
	private transient boolean warned;
	private Map<Aura, Long> auraCds;
	private long currActivated;
	private Aura currAura;
	private int jotFlags;

	public enum Aura {
		ODDBALL						(20957, Millis.fromHours(1), 0),
		POISON_PURGE				(20958, Millis.fromMinutes(10), Millis.fromHours(1)),
		FRIEND_IN_NEED				(20963, Millis.fromSeconds(12), Millis.fromMinutes(15)),
		KNOCK_OUT					(20961, Millis.fromHours(1), Millis.fromHours(4)),
		SHARPSHOOTER				(20967, Millis.fromHours(1), Millis.fromHours(3)),
		RUNIC_ACCURACY				(20962, Millis.fromHours(1), Millis.fromHours(3)),
		SUREFOOTED					(20964, Millis.fromMinutes(20), Millis.fromHours(2)),
		REVERENCE					(20965, Millis.fromHours(1), Millis.fromHours(3)),
		CALL_OF_THE_SEA				(20966, Millis.fromHours(1), Millis.fromHours(3)),
		JACK_OF_TRADES				(20959, Millis.fromHours(3), -1),
		GREATER_POISON_PURGE		(22268, Millis.fromMinutes(20), Millis.fromHours(1)),
		GREATER_RUNIC_ACCURACY		(22270, Millis.fromHours(1), Millis.fromHours(3)),
		GREATER_SHARPSHOOTER		(22272, Millis.fromHours(1), Millis.fromHours(3)),
		GREATER_CALL_OF_THE_SEA		(22274, Millis.fromHours(1), Millis.fromHours(3)),
		GREATER_REVERENCE			(22276, Millis.fromHours(1), Millis.fromHours(3)),
		GREATER_SUREFOOTED			(22278, Millis.fromMinutes(40), Millis.fromHours(2)),
		LUMBERJACK					(22280, Millis.fromHours(1), Millis.fromHours(3)),
		GREATER_LUMBERJACK			(22282, Millis.fromHours(1), Millis.fromHours(3)),
		QUARRYMASTER				(22284, Millis.fromHours(1), Millis.fromHours(3)),
		GREATER_QUARRYMASTER		(22286, Millis.fromHours(1), Millis.fromHours(3)),
		FIVE_FINGER_DISCOUNT		(22288, Millis.fromHours(1), Millis.fromHours(3)),
		GREATER_FIVE_FINGER_DISCOUNT(22290, Millis.fromHours(1), Millis.fromHours(3)),
		RESOURCEFUL					(22292, Millis.fromHours(1), Millis.fromHours(3)),
		EQUILIBRIUM					(22294, Millis.fromHours(2), Millis.fromHours(4)),
		INSPIRATION					(22296, Millis.fromHours(1), Millis.fromHours(3)),
		VAMPYRISM					(22298, Millis.fromHours(1), Millis.fromHours(3)),
		PENANCE						(22300, Millis.fromHours(1), Millis.fromHours(3)),
		WISDOM						(22302, Millis.fromMinutes(30), -1),
		AEGIS						(22889, Millis.fromMinutes(30), Millis.fromHours(5)),
		REGENERATION				(22893, Millis.fromHours(1), Millis.fromHours(3)),
		DARK_MAGIC					(22891, Millis.fromHours(1), Millis.fromHours(3)),
		BERSERKER					(22897, Millis.fromMinutes(30), Millis.fromHours(5)),
		ANCESTOR_SPIRITS			(22895, Millis.fromMinutes(30), Millis.fromHours(5)),
		GREENFINGERS				(22883, Millis.fromMinutes(20), Millis.fromHours(1)),
		GREATER_GREENFINGERS		(22885, Millis.fromMinutes(20), Millis.fromHours(1)),
		MASTER_GREENFINGERS			(22887, Millis.fromMinutes(20), Millis.fromHours(1)),
		TRACKER						(22927, Millis.fromHours(1), Millis.fromHours(3)),
		GREATER_TRACKER				(22929, Millis.fromHours(1), Millis.fromHours(3)),
		MASTER_TRACKER				(22931, Millis.fromHours(1), Millis.fromHours(3)),
		SALVATION					(22899, Millis.fromHours(1), Millis.fromHours(3)),
		GREATER_SALVATION			(22901, Millis.fromHours(1), Millis.fromHours(3)),
		MASTER_SALVATION			(22903, Millis.fromHours(1), Millis.fromHours(3)),
		CORRUPTION					(22905, Millis.fromHours(1), Millis.fromHours(3)),
		GREATER_CORRUPTION			(22907, Millis.fromHours(1), Millis.fromHours(3)),
		MASTER_CORRUPTION			(22909, Millis.fromHours(1), Millis.fromHours(3)),
		MASTER_FIVE_FINGER_DISCOUNT	(22911, Millis.fromHours(1), Millis.fromHours(3)),
		MASTER_QUARRYMASTER			(22913, Millis.fromHours(1), Millis.fromHours(3)),
		MASTER_LUMBERJACK			(22915, Millis.fromHours(1), Millis.fromHours(3)),
		MASTER_POISON_PURGE			(22917, Millis.fromMinutes(30), Millis.fromHours(1)),
		MASTER_RUNIC_ACCURACY		(22919, Millis.fromHours(1), Millis.fromHours(3)),
		MASTER_SHARPSHOOTER			(22921, Millis.fromHours(1), Millis.fromHours(3)),
		MASTER_CALL_OF_THE_SEA		(22923, Millis.fromHours(1), Millis.fromHours(3)),
		MASTER_REVERENCE			(22925, Millis.fromHours(1), Millis.fromHours(3)),
		MASTER_KNOCK_OUT			(22933, Millis.fromHours(1), Millis.fromHours(4)),
		SUPREME_SALVATION			(23876, Millis.fromHours(1), Millis.fromHours(3)),
		SUPREME_CORRUPTION			(23874, Millis.fromHours(1), Millis.fromHours(3)),
		HARMONY						(23848, Millis.fromHours(1), Millis.fromHours(3)),
		GREATER_HARMONY				(23850, Millis.fromHours(1), Millis.fromHours(3)),
		MASTER_HARMONY				(23852, Millis.fromHours(1), Millis.fromHours(3)),
		SUPREME_HARMONY				(23854, Millis.fromHours(1), Millis.fromHours(3)),
		INVIGORATE					(23840, Millis.fromHours(1), Millis.fromHours(3)),
		GREATER_INVIGORATE			(23842, Millis.fromHours(1), Millis.fromHours(3)),
		MASTER_INVIGORATE			(23844, Millis.fromHours(1), Millis.fromHours(3)),
		SUPREME_INVIGORATE			(23846, Millis.fromHours(1), Millis.fromHours(3)),
		SUPREME_FIVE_FINGER_DISCOUNT(23856, Millis.fromHours(1), Millis.fromHours(3)),
		SUPREME_QUARRYMASTER		(23858, Millis.fromHours(1), Millis.fromHours(3)),
		SUPREME_LUMBERJACK			(23860, Millis.fromHours(1), Millis.fromHours(3)),
		SUPREME_POISON_PURGE		(23862, Millis.fromHours(1), Millis.fromHours(1)),
		SUPREME_RUNIC_ACCURACY		(23864, Millis.fromHours(1), Millis.fromHours(3)),
		SUPREME_SHARPSHOOTER		(23866, Millis.fromHours(1), Millis.fromHours(3)),
		SUPREME_CALL_OF_THE_SEA		(23868, Millis.fromHours(1), Millis.fromHours(3)),
		SUPREME_REVERENCE			(23870, Millis.fromHours(1), Millis.fromHours(3)),
		SUPREME_TRACKER				(23872, Millis.fromHours(1), Millis.fromHours(3)),
		SUPREME_GREENFINGERS		(23878, Millis.fromMinutes(20), Millis.fromHours(1));

		private static Map<Integer, Aura> ITEMID_MAP = new HashMap<>();

		static {
			for (Aura a : Aura.values())
				ITEMID_MAP.put(a.itemId, a);
		}

		public static Aura forId(int itemId) {
			return ITEMID_MAP.get(itemId);
		}

		public int itemId;
		private long duration;
		private long cooldown;

		private Aura(int itemId, long duration, long cooldown) {
			this.itemId = itemId;
			this.duration = duration;
			this.cooldown = cooldown;
		}
	}

	public static ItemClickHandler handleAuraOptions = new ItemClickHandler(Aura.ITEMID_MAP.keySet().toArray(), new String[] { "Activate aura", "Activate Aura", "Aura time remaining", "Time-Remaining" }) {
		@Override
		public void handle(ItemClickEvent e) {
			switch(e.getOption()) {
			case "Activate aura":
			case "Activate Aura":
				e.getPlayer().getAuraManager().activate();
				break;
			case "Aura time remaining":
			case "Time-Remaining":
				Aura aura = Aura.forId(e.getItem().getId());
				e.getPlayer().getAuraManager().sendAuraRemainingTime(aura);
				if (aura == Aura.JACK_OF_TRADES)
					e.getPlayer().sendMessage("You have trained " + e.getPlayer().getAuraManager().getJotSkills() + " out of 10 skills so far.");
				break;
			}
		}
	};

	public static ItemEquipHandler handleDequipAura = new ItemEquipHandler(Aura.ITEMID_MAP.keySet().toArray()) {
		@Override
		public void handle(ItemEquipEvent e) {
			if (e.dequip())
				e.getPlayer().getAuraManager().removeAura();
		}
	};

	public static XPGainHandler handleXpGain = new XPGainHandler() {
		@Override
		public void handle(XPGainEvent e) {
			if (!e.getPlayer().getAuraManager().isActivated(Aura.JACK_OF_TRADES))
				return;
			int total = e.getPlayer().getAuraManager().getJotSkills();
			e.getPlayer().getAuraManager().setJotFlag(e.getSkillId());
			if (total != e.getPlayer().getAuraManager().getJotSkills())
				e.getPlayer().sendMessage("You have now gained XP in " + e.getPlayer().getAuraManager().getJotSkills() + " of the 10 required skills.");
		}
	};

	public void clearJotFlags() {
		jotFlags = 0;
	}

	public void setJotFlag(int skillId) {
		int flag = 1 << skillId;
		jotFlags |= flag;
	}

	public int getJotSkills() {
		int skills = 0;
		for (int i = 0;i < Constants.SKILL_NAME.length;i++)
			if ((jotFlags & (1 << i)) != 0)
				skills++;
		return skills;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public long getTimeLeft() {
		long timeLeft = currActivated - System.currentTimeMillis();
		if (timeLeft <= 0)
			return 0;
		return timeLeft;
	}

	public void process() {
		if (currActivated <= 0)
			return;
		if (getTimeLeft() <= 60000 && !warned) {
			player.sendMessage("Your aura will deplete in 1 minute.");
			warned = true;
			return;
		}
		if (System.currentTimeMillis() < currActivated)
			return;
		deactivate();
		player.getAppearance().generateAppearanceData();
	}

	public void removeAura() {
		if (currActivated != 0)
			deactivate();
	}

	public void deactivate() {
		if (currAura == null)
			return;
		if (currAura == Aura.JACK_OF_TRADES)
			player.setDailyB("usedJoT", true);
		else if (currAura == Aura.WISDOM)
			player.setDailyB("usedWisdom", true);
		else
			subtractCooldownTime(currAura, getTimeLeft());
		currAura = null;
		currActivated = 0;
		warned = false;
		player.sendMessage("Your aura has depleted.");
	}

	public void subtractCooldownTime(Aura aura, long time) {
		if (onCooldown(aura))
			auraCds.put(aura, auraCds.get(aura) - time);
	}

	public void putOnCooldown(Aura aura) {
		if (auraCds == null)
			auraCds = new HashMap<>();
		auraCds.put(aura, System.currentTimeMillis() + aura.cooldown + aura.duration);
	}

	public boolean onCooldown(Aura aura) {
		return getCooldownTime(aura) > 0;
	}

	public long getCooldownTime(Aura aura) {
		if (auraCds == null)
			auraCds = new HashMap<>();
		if (auraCds.get(aura) == null)
			return 0;
		if (aura == Aura.JACK_OF_TRADES)
			return player.getDailyB("usedJoT") ? Long.MAX_VALUE : 0;
		if (aura == Aura.WISDOM)
			return player.getDailyB("usedWisdom") ? Long.MAX_VALUE : 0;
		return auraCds.get(aura) - System.currentTimeMillis();
	}

	public void activate() {
		Item item = player.getEquipment().getItem(Equipment.AURA);
		if (item == null)
			return;
		Aura aura = Aura.forId(item.getId());
		if (aura == null)
			return;
		player.stopAll(false);
		if (currActivated != 0) {
			player.sendMessage("Your aura is already activated.");
			return;
		}
		if ((player.getDailyB("usedJoT") && aura == Aura.JACK_OF_TRADES) || (player.getDailyB("usedWisdom") && aura == Aura.WISDOM)) {
			player.sendMessage("Your aura has not recharged yet.");
			return;
		}
		if (getCooldownTime(aura) > 0) {
			player.sendMessage("Your aura has not recharged yet.");
			return;
		}
		currAura = aura;
		currActivated = System.currentTimeMillis() + aura.duration;
		player.setNextAnimation(new Animation(2231));
		player.setNextSpotAnim(new SpotAnim(getActivateSpotAnim(aura)));
		player.getAppearance().generateAppearanceData();
		putOnCooldown(aura);
	}

	public int getActivateSpotAnim(Aura aura) {
		if (ItemDefinitions.getDefs(aura.itemId).getName().startsWith("Master "))
			return 1764;
		if (ItemDefinitions.getDefs(aura.itemId).getName().startsWith("Supreme "))
			return 1763;
		return 370;
	}

	public void sendAuraRemainingTime() {
		sendAuraRemainingTime(Aura.forId(player.getEquipment().getAuraId()));
	}

	public void sendAuraRemainingTime(Aura aura) {
		if (aura == null)
			return;
		if (currActivated <= 0) {
			long cooldown = getCooldownTime(aura);
			if (cooldown > 0) {
				if (cooldown == Long.MAX_VALUE)
					player.sendMessage("Your aura will reset tomorrow.");
				else
					player.sendMessage("Currently recharging. <col=ff0000>" + formatTime(cooldown / 1000) + " remaining.");
				return;
			}
			player.sendMessage("Your aura has finished recharging. It is ready to use.");
			return;
		}
		player.sendMessage("Currently active. <col=00ff00>" + formatTime(getTimeLeft() / 1000) + " remaining");
	}

	public String formatTime(long seconds) {
		long minutes = seconds / 60;
		long hours = minutes / 60;
		minutes -= hours * 60;
		seconds -= (hours * 60 * 60) + (minutes * 60);
		String minutesString = (minutes < 10 ? "0" : "") + minutes;
		String secondsString = (seconds < 10 ? "0" : "") + seconds;
		return hours + ":" + minutesString + ":" + secondsString;
	}

	public void sendTimeRemaining(Aura aura) {
		long cooldown = getCooldownTime(aura);
		if (cooldown < System.currentTimeMillis()) {
			player.sendMessage("Your aura has finished recharging. It is ready to use.");
			return;
		}
		player.sendMessage("Currently recharging. <col=ff0000>" + formatTime((cooldown - System.currentTimeMillis()) / 1000) + " remaining.");
	}

	public boolean isActivated(Aura... auras) {
		for (Aura aura : auras)
			if (isActivated(aura))
				return true;
		return false;
	}

	public boolean isActivated(Aura aura) {
		Aura worn = Aura.forId(player.getEquipment().getAuraId());
		return worn == aura && currActivated != 0;
	}

	public boolean isActive() {
		return currActivated != 0;
	}

	public void onIncomingHit(Hit hit) {
		if (isActivated(Aura.PENANCE))
			player.getPrayer().restorePrayer(hit.getDamage() * 0.2);
	}

	public void onOutgoingHit(Hit hit) {
		if (isActivated(Aura.INSPIRATION) && hit.getDamage() > 0)
			useInspiration();
		if (isActivated(Aura.VAMPYRISM))
			player.heal((int) (hit.getDamage() * 0.05));
	}

	public void useInspiration() {
		int atts = player.getTempAttribs().getI("InspirationAura", 0);
		atts++;
		if (atts == 5) {
			atts = 0;
			player.getCombatDefinitions().restoreSpecialAttack(1);
		}
		player.getTempAttribs().setI("InspirationAura", atts);
	}

	public int getAuraModelId() {
		Item weapon = player.getEquipment().getItem(Equipment.WEAPON);
		if (weapon == null)
			return 8719;
		String name = weapon.getDefinitions().getName().toLowerCase();
		if (name.contains("dagger"))
			return 8724;
		if (name.contains("whip"))
			return 8725;
		if (name.contains("2h sword") || name.contains("godsword"))
			return 8773;
		if (name.contains("sword") || name.contains("scimitar") || name.contains("korasi"))
			return 8722;
		return 8719;
	}

	public int getAuraModelId2() {
		Aura aura = Aura.forId(player.getEquipment().getAuraId());
		if (aura == null)
			return -1;
		switch (aura) {
		case CORRUPTION:
			return 16449;
		case SALVATION:
			return 16465;
		case HARMONY:
			return 68605;
		case GREATER_CORRUPTION:
			return 16464;
		case GREATER_SALVATION:
			return 16524;
		case GREATER_HARMONY:
			return 68610;
		case MASTER_CORRUPTION:
			return 16429;
		case MASTER_SALVATION:
			return 16450;
		case MASTER_HARMONY:
			return 68607;
		case SUPREME_CORRUPTION:
			return 68615;
		case SUPREME_SALVATION:
			return 68611;
		case SUPREME_HARMONY:
			return 68613;
		default:
			return -1;
		}
	}

	public static boolean isWingedAura(Aura aura) {
		switch (aura) {
		case CORRUPTION:
		case SALVATION:
		case HARMONY:
		case GREATER_CORRUPTION:
		case GREATER_SALVATION:
		case GREATER_HARMONY:
		case MASTER_CORRUPTION:
		case MASTER_SALVATION:
		case MASTER_HARMONY:
		case SUPREME_CORRUPTION:
		case SUPREME_SALVATION:
		case SUPREME_HARMONY:
			return true;
		default:
			return false;
		}
	}

	public double getThievingMul() {
		if (currAura == null)
			return 1.0;
		switch(currAura) {
		case FIVE_FINGER_DISCOUNT:
			return 1.03;
		case GREATER_FIVE_FINGER_DISCOUNT:
			return 1.05;
		case MASTER_FIVE_FINGER_DISCOUNT:
			return 1.07;
		case SUPREME_FIVE_FINGER_DISCOUNT:
			return 1.1;
		default:
			return 1.0;
		}
	}

	public double getFishingMul() {
		if (currAura == null)
			return 1.0;
		switch(currAura) {
		case CALL_OF_THE_SEA:
			return 1.03;
		case GREATER_CALL_OF_THE_SEA:
			return 1.05;
		case MASTER_CALL_OF_THE_SEA:
			return 1.07;
		case SUPREME_CALL_OF_THE_SEA:
			return 1.1;
		default:
			return 1.0;
		}
	}

	public double getWoodcuttingMul() {
		if (currAura == null)
			return 1.0;
		switch(currAura) {
		case LUMBERJACK:
			return 1.03;
		case GREATER_LUMBERJACK:
			return 1.05;
		case MASTER_LUMBERJACK:
			return 1.07;
		case SUPREME_LUMBERJACK:
			return 1.1;
		default:
			return 1.0;
		}
	}

	public double getMiningMul() {
		if (currAura == null)
			return 1.0;
		switch(currAura) {
		case QUARRYMASTER:
			return 1.03;
		case GREATER_QUARRYMASTER:
			return 1.05;
		case MASTER_QUARRYMASTER:
			return 1.07;
		case SUPREME_QUARRYMASTER:
			return 1.1;
		default:
			return 1.0;
		}
	}

	public double getRangeAcc() {
		if (currAura == null)
			return 1.0;
		switch(currAura) {
		case SHARPSHOOTER:
			return 1.03;
		case GREATER_SHARPSHOOTER:
			return 1.05;
		case MASTER_SHARPSHOOTER:
			return 1.07;
		case SUPREME_SHARPSHOOTER:
			return 1.1;
		default:
			return 1.0;
		}
	}

	public double getMagicAcc() {
		if (currAura == null)
			return 1.0;
		switch(currAura) {
		case RUNIC_ACCURACY:
			return 1.03;
		case GREATER_RUNIC_ACCURACY:
			return 1.05;
		case MASTER_RUNIC_ACCURACY:
			return 1.07;
		case SUPREME_RUNIC_ACCURACY:
			return 1.1;
		default:
			return 1.0;
		}
	}

	public double getPrayerResMul() {
		if (currAura == null)
			return 1.0;
		switch(currAura) {
		case REVERENCE:
			return 1.03;
		case GREATER_REVERENCE:
			return 1.05;
		case MASTER_REVERENCE:
			return 1.07;
		case SUPREME_REVERENCE:
			return 1.1;
		default:
			return 1.0;
		}
	}
}
