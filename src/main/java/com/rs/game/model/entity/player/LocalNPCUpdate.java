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

import com.rs.Settings;
import com.rs.cache.loaders.NPCDefinitions.MovementType;
import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.HitBar;
import com.rs.game.model.entity.npc.NPC;
import com.rs.lib.io.OutputStream;
import com.rs.lib.util.Utils;

import java.util.Iterator;
import java.util.LinkedList;

public final class LocalNPCUpdate {

	private Player player;
	private LinkedList<NPC> localNPCs;

	public void reset() {
		localNPCs.clear();
	}

	public LocalNPCUpdate(Player player) {
		this.player = player;
		localNPCs = new LinkedList<>();
	}

	public void write(OutputStream stream, boolean largeSceneView) {
		OutputStream updateBlockData = new OutputStream();
		processLocalNPCsInform(stream, updateBlockData, largeSceneView);
		stream.writeBytes(updateBlockData.getBuffer(), 0, updateBlockData.getOffset());
	}

	private void processLocalNPCsInform(OutputStream stream, OutputStream updateBlockData, boolean largeSceneView) {
		stream.initBitAccess();
		processInScreenNPCs(stream, updateBlockData, largeSceneView);
		addInScreenNPCs(stream, updateBlockData, largeSceneView);
		if (updateBlockData.getOffset() > 0)
			stream.writeBits(15, 32767);
		stream.finishBitAccess();
	}

	private void processInScreenNPCs(OutputStream stream, OutputStream updateBlockData, boolean largeSceneView) {
		stream.writeBits(8, localNPCs.size());
		for (Iterator<NPC> it = localNPCs.iterator(); it.hasNext();) {
			NPC n = it.next();
			if (n.hasFinished() || !n.withinDistance(player, largeSceneView ? 126 : 14) || n.hasTeleported()) {
				stream.writeBits(1, 1);
				stream.writeBits(2, 3);
				it.remove();
				continue;
			}
			boolean needUpdate = n.needMasksUpdate();
			boolean walkUpdate = n.getNextWalkDirection() != null;
			stream.writeBits(1, (needUpdate || walkUpdate) ? 1 : 0);
			if (walkUpdate) {
				if (n.getNextRunDirection() == null) {
					if (n.getDefinitions().movementType == MovementType.HALF_WALK) {
						stream.writeBits(2, 2);
						stream.writeBits(1, 0);
						stream.writeBits(3, n.getNextWalkDirection().getId());
					} else {
						stream.writeBits(2, 1);
						stream.writeBits(3, n.getNextWalkDirection().getId());
					}
				} else {
					stream.writeBits(2, 2);
					stream.writeBits(1, 1);
					stream.writeBits(3, n.getNextWalkDirection().getId());
					stream.writeBits(3, n.getNextRunDirection().getId());
				}
				stream.writeBits(1, needUpdate ? 1 : 0);
			} else if (needUpdate)
				stream.writeBits(2, 0);
			if (needUpdate)
				appendUpdateBlock(n, updateBlockData, false);
		}
	}

	private void addInScreenNPCs(OutputStream stream, OutputStream updateBlockData, boolean largeSceneView) {
		int radius = largeSceneView ? 126 : 14;                        //TODO is isDead really necessary here?
		for (NPC n : player.queryNearbyNPCsByTileRange(radius, n -> /*!n.isDead() && */!localNPCs.contains(n) && n.withinDistance(player, radius))) {
			if (localNPCs.size() == Settings.LOCAL_NPCS_LIMIT)
				break;
			stream.writeBits(15, n.getIndex());
			boolean needUpdate = n.needMasksUpdate() || n.getLastFaceEntity() != -1;
			int x = n.getX() - player.getX();
			int y = n.getY() - player.getY();
			if (largeSceneView) {
				if (x < 127)
					x += 256;
				if (y < 127)
					y += 256;
			} else {
				if (x < 15)
					x += 32;
				if (y < 15)
					y += 32;
			}
			stream.writeBits(1, needUpdate ? 1 : 0);
			stream.writeBits(largeSceneView ? 8 : 5, y);
			stream.writeBits(3, (n.getFaceAngle() >> 11) - 4);
			stream.writeBits(15, n.getId());
			stream.writeBits(largeSceneView ? 8 : 5, x);
			stream.writeBits(1, n.hasTeleported() ? 1 : 0);
			stream.writeBits(2, n.getPlane());
			localNPCs.add(n);
			if (needUpdate)
				appendUpdateBlock(n, updateBlockData, true);
		}
	}

	private void appendUpdateBlock(NPC n, OutputStream data, boolean added) {
		OutputStream block = new OutputStream();
		int maskData = 0;

		if (n.getNextAnimation() != null) {
			maskData |= 0x10;
			applyAnimationMask(n, block);
		}
		if (n.getBodyMeshModifier() != null) {
			maskData |= 0x100;
			applyBodyMeshModifierMask(n, block);
		}
		if (n.getNextForceMovement() != null) {
			maskData |= 0x400;
			applyForceMovementMask(n, block);
		}
		if (n.getBas() != -1) {
			maskData |= 0x1000;
			block.writeShort(n.getBas() == -2 ? -1 : n.getBas());
		}
		//0x200000 unused outdated varn
		if (n.getNextSpotAnim4() != null) {
			maskData |= 0x1000000;
			applyGraphicsMask4(n, block);
		}
		if (n.getNextFaceEntity() != -2 || (added && n.getLastFaceEntity() != -1)) {
			maskData |= 0x80;
			applyFaceEntityMask(n, block);
		}
		//0x8000 animation related
		if (n.getNextSpotAnim2() != null) {
			maskData |= 0x800;
			applyGraphicsMask2(n, block);
		}
		if (!n.getNextHits().isEmpty() || !n.getNextHitBars().isEmpty()) {
			maskData |= 0x1;
			applyHitMask(n, block);
		}
		if (n.getNextTransformation() != null) {
			maskData |= 0x8;
			applyTransformationMask(n, block);
		}
		if (n.getNextSpotAnim3() != null) {
			maskData |= 0x2000000;
			applyGraphicsMask3(n, block);
		}
		//0x80000 unused double array of size 6 modification?
		//0x40000 mesh modifier for npc chatheads?
		if (n.hasChangedCombatLevel() || (added && n.getCustomCombatLevel() >= 0)) {
			maskData |= 0x10000;
			applyChangeLevelMask(n, block);
		}
		//0x400000 unused outdated varn
		if (n.getNextFaceTile() != null && n.getNextRunDirection() == null && n.getNextWalkDirection() == null) {
			maskData |= 0x4;
			applyFaceTileMask(n, block);
		}
		if (n.getNextBodyGlow() != null) {
			maskData |= 0x20000;
			applyBodyGlowMask(n, block);
		}
		if (n.hasChangedName() || (added && n.getCustomName() != null)) {
			maskData |= 0x800000;
			applyNameChangeMask(n, block);
		}
		if (n.getNextForceTalk() != null) {
			maskData |= 0x2;
			applyForceTalkMask(n, block);
		}
		if (n.getBodyModelRotator() != null) {
			maskData |= 0x4000;
			n.getBodyModelRotator().encodeNPC(block);
		}
		if (n.getNextSpotAnim1() != null) {
			maskData |= 0x20;
			applyGraphicsMask1(n, block);
		}

		if (maskData > 0xff)
			maskData |= 0x40;
		if (maskData > 0xffff)
			maskData |= 0x2000;
		if (maskData > 0xffffff)
			maskData |= 0x100000;

		data.writeByte(maskData);

		if (maskData > 0xff)
			data.writeByte(maskData >> 8);
		if (maskData > 0xffff)
			data.writeByte(maskData >> 16);
		if (maskData > 0xffffff)
			data.writeByte(maskData >> 24);
		data.writeBytes(block.toByteArray());
	}

	private void applyBodyMeshModifierMask(NPC n, OutputStream block) {
		n.getBodyMeshModifier().encode(block);
	}

	private void applyBodyGlowMask(NPC n, OutputStream data) {
		data.write128Byte(n.getNextBodyGlow().getRedAdd());
		data.writeByte(n.getNextBodyGlow().getGreenAdd());
		data.writeByte(n.getNextBodyGlow().getBlueAdd());
		data.write128Byte(n.getNextBodyGlow().getScalar());
		data.writeShortLE(0);
		data.writeShort(n.getNextBodyGlow().getTime());
	}

	private void applyChangeLevelMask(NPC n, OutputStream data) {
		data.writeShort128(n.getCombatLevel());
	}

	private void applyNameChangeMask(NPC npc, OutputStream data) {
		data.writeString(npc.getName());
	}

	private void applyTransformationMask(NPC n, OutputStream data) {
		data.writeBigSmart(n.getNextTransformation().getToNPCId());
	}

	private void applyForceTalkMask(NPC n, OutputStream data) {
		data.writeString(n.getNextForceTalk().getText());
	}

	private void applyForceMovementMask(NPC n, OutputStream data) {
		data.write128Byte(n.getNextForceMovement().getDiffX1());
		data.writeByte128(n.getNextForceMovement().getDiffY1());
		data.writeByteC(n.getNextForceMovement().getDiffX2());
		data.writeByte128(n.getNextForceMovement().getDiffY2());
		data.writeShortLE(n.getNextForceMovement().getStartClientCycles());
		data.writeShortLE(n.getNextForceMovement().getSpeedClientCycles());
		data.writeShortLE128(n.getNextForceMovement().getDirection());
	}

	private void applyFaceTileMask(NPC n, OutputStream data) {
		data.writeShortLE128((n.getNextFaceTile().getX() << 1) + 1);
		data.writeShortLE((n.getNextFaceTile().getY() << 1) + 1);
	}

	private void applyHitMask(NPC n, OutputStream data) {
		data.writeByte128(n.getNextHits().size());
		for (Hit hit : n.getNextHits().toArray(new Hit[n.getNextHits().size()])) {
			boolean interactingWith = hit.interactingWith(player, n);
			if (hit.missed() && !interactingWith) {
				data.writeSmart(32766);
				data.writeByte(hit.getDamage());
			} else if (hit.getSoaking() != null) {
				data.writeSmart(32767);
				data.writeSmart(hit.getMark(player, n));
				data.writeSmart(Utils.clampI(hit.getDamage(), 0, Short.MAX_VALUE/2));
				data.writeSmart(hit.getSoaking().getMark(player, n));
				data.writeSmart(Utils.clampI(hit.getSoaking().getDamage(), 0, Short.MAX_VALUE/2));
			} else {
				data.writeSmart(hit.getMark(player, n));
				data.writeSmart(Utils.clampI(hit.getDamage(), 0, Short.MAX_VALUE/2));
			}
			data.writeSmart(hit.getDelay());
		}
		data.writeByte128(n.getNextHitBars().size());
		for (HitBar bar : n.getNextHitBars()) {
			data.writeSmart(bar.getType());
			int perc = bar.getPercentage();
			int toPerc = bar.getToPercentage();
			boolean display = bar.display(player);
			data.writeSmart(display ? perc != toPerc ? bar.getTimer() : 0 : 32767);
			if (display) {
				data.writeSmart(bar.getDelay());
				data.write128Byte(perc);
				if (toPerc != perc)
					data.writeByte128(toPerc);
			}
		}
	}

	private void applyFaceEntityMask(NPC n, OutputStream data) {
		data.writeShortLE(n.getNextFaceEntity() == -2 ? n.getLastFaceEntity() : n.getNextFaceEntity());
	}

	private void applyAnimationMask(NPC n, OutputStream data) {
		for (int id : n.getNextAnimation().getIds())
			data.writeBigSmart(id);
		data.writeByte(n.getNextAnimation().getSpeed());
	}

	private void applyGraphicsMask4(NPC n, OutputStream data) {
		data.writeShort128(n.getNextSpotAnim4().getId());
		data.writeIntV2(n.getNextSpotAnim4().getSettingsHash());
		data.writeByte128(n.getNextSpotAnim4().getSettings2Hash());
	}

	private void applyGraphicsMask3(NPC n, OutputStream data) {
		data.writeShortLE(n.getNextSpotAnim3().getId());
		data.writeIntLE(n.getNextSpotAnim3().getSettingsHash());
		data.writeByte(n.getNextSpotAnim3().getSettings2Hash());
	}

	private void applyGraphicsMask2(NPC n, OutputStream data) {
		data.writeShortLE(n.getNextSpotAnim2().getId());
		data.writeInt(n.getNextSpotAnim2().getSettingsHash());
		data.writeByte128(n.getNextSpotAnim2().getSettings2Hash());
	}

	private void applyGraphicsMask1(NPC n, OutputStream data) {
		data.writeShort(n.getNextSpotAnim1().getId());
		data.writeIntV2(n.getNextSpotAnim1().getSettingsHash());
		data.writeByteC(n.getNextSpotAnim1().getSettings2Hash());
	}

}
