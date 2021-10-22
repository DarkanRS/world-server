package com.rs.game.npc.qbd;

import java.util.Iterator;

import com.rs.game.player.Player;
import com.rs.lib.util.Utils;

/**
 * Handles the summoning of the tortured souls.
 * 
 * @author Emperor
 * 
 */
public final class SoulSummonAttack implements QueenAttack {

	/**
	 * The spawn offset locations.
	 */
	private static final int[][] SPAWN_LOCATIONS = { { 31, 35 }, { 33, 35 }, { 34, 33 }, { 31, 29 } };

	@Override
	public int attack(QueenBlackDragon npc, Player victim) {
		for (Iterator<TorturedSoul> it = npc.getSouls().iterator(); it.hasNext();) {
			if (it.next().isDead()) {
				it.remove();
			}
		}
		npc.getTempAttribs().put("_last_soul_summon", npc.getTicks() + Utils.random(41, 100));
		int count = npc.getPhase() - 1;
		if (count == 3) {
			count = 4;
		}
		if (npc.getSouls().size() < count) {
			victim.sendMessage(
					(count - npc.getSouls().size()) < 2 ? "<col=9900CC>The Queen Black Dragon summons one of her captive, tortured souls.</col>" : "<col=9900CC>The Queen Black Dragon summons several of her captive, tortured souls.</col>");
			for (int i = npc.getSouls().size(); i < count; i++) {
				npc.getSouls().add(new TorturedSoul(npc, victim, npc.getBase().transform(SPAWN_LOCATIONS[i][0], SPAWN_LOCATIONS[i][1], 0)));
			}
		}
		for (int i = 0; i < count; i++) {
			if (i >= npc.getSouls().size()) {
				break;
			}
			TorturedSoul s = npc.getSouls().get(i);
			if (s == null || s.isDead()) {
				continue;
			}
			s.specialAttack(npc.getBase().transform(SPAWN_LOCATIONS[i][0], SPAWN_LOCATIONS[i][1], 0));
		}
		return Utils.random(4, 15);
	}

	@Override
	public boolean canAttack(QueenBlackDragon npc, Player victim) {
		Integer last = (Integer) npc.getTempAttribs().get("_last_soul_summon");
		return last == null || last < npc.getTicks();
	}

}