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
import com.rs.game.World;
import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.HitBar;
import com.rs.lib.Constants;
import com.rs.lib.io.OutputStream;
import com.rs.lib.util.Utils;

import java.security.MessageDigest;

public final class LocalPlayerUpdate {

	private static final int MAX_PLAYER_ADD = 15;

	private Player player;

	private byte[] slotFlags;

	private Player[] localPlayers;
	private int[] localPlayersIndexes;
	private int localPlayersIndexesCount;

	private int[] outPlayersIndexes;
	private int outPlayersIndexesCount;

	private int[] regionHashes;

	private byte[][] cachedAppearencesHashes;
	private int totalRenderDataSentLength;

	/**
	 * The amount of local players added this tick.
	 */
	private int localAddedPlayers;

	public Player[] getLocalPlayers() {
		return localPlayers;
	}

	public boolean needAppearenceUpdate(int index, byte[] hash) {
		if (totalRenderDataSentLength > ((Constants.PACKET_SIZE_LIMIT - 500) / 2) || hash == null)
			return false;
		return cachedAppearencesHashes[index] == null || !MessageDigest.isEqual(cachedAppearencesHashes[index], hash);
	}

	public LocalPlayerUpdate(Player player) {
		this.player = player;
		slotFlags = new byte[2048];
		localPlayers = new Player[2048];
		localPlayersIndexes = new int[Settings.PLAYERS_LIMIT];
		outPlayersIndexes = new int[2048];
		regionHashes = new int[2048];
		cachedAppearencesHashes = new byte[Settings.PLAYERS_LIMIT][];
	}

	public void init(OutputStream stream) {
		stream.initBitAccess();
		stream.writeBits(30, player.getTileHash());
		localPlayers[player.getIndex()] = player;
		localPlayersIndexes[localPlayersIndexesCount++] = player.getIndex();
		for (int playerIndex = 1; playerIndex < 2048; playerIndex++) {
			if (playerIndex == player.getIndex())
				continue;
			Player player = World.getPlayers().get(playerIndex);
			stream.writeBits(18, regionHashes[playerIndex] = player == null ? 0 : player.getRegionHash());
			outPlayersIndexes[outPlayersIndexesCount++] = playerIndex;

		}
		stream.finishBitAccess();
	}

	private boolean needsRemove(Player p) {
		return (p.hasFinished() || !player.withinDistance(p.getTile(), player.hasLargeSceneView() ? 126 : 14));
	}

	private boolean needsAdd(Player p) {
		return p != null && !p.hasFinished() && player.withinDistance(p.getTile(), player.hasLargeSceneView() ? 126 : 14) && localAddedPlayers < MAX_PLAYER_ADD;
	}

	private void updateRegionHash(OutputStream stream, int lastRegionHash, int currentRegionHash) {
		int lastRegionX = lastRegionHash >> 8;
		int lastRegionY = 0xff & lastRegionHash;
		int lastPlane = lastRegionHash >> 16;
		int currentRegionX = currentRegionHash >> 8;
		int currentRegionY = 0xff & currentRegionHash;
		int currentPlane = currentRegionHash >> 16;
		int planeOffset = currentPlane - lastPlane;
		if (lastRegionX == currentRegionX && lastRegionY == currentRegionY) {
			stream.writeBits(2, 1);
			stream.writeBits(2, planeOffset);
		} else if (Math.abs(currentRegionX - lastRegionX) <= 1 && Math.abs(currentRegionY - lastRegionY) <= 1) {
			int opcode;
			int dx = currentRegionX - lastRegionX;
			int dy = currentRegionY - lastRegionY;
			if (dx == -1 && dy == -1)
				opcode = 0;
			else if (dx == 1 && dy == -1)
				opcode = 2;
			else if (dx == -1 && dy == 1)
				opcode = 5;
			else if (dx == 1 && dy == 1)
				opcode = 7;
			else if (dy == -1)
				opcode = 1;
			else if (dx == -1)
				opcode = 3;
			else if (dx == 1)
				opcode = 4;
			else
				opcode = 6;
			stream.writeBits(2, 2);
			stream.writeBits(5, (planeOffset << 3) + (opcode & 0x7));
		} else {
			int xOffset = currentRegionX - lastRegionX;
			int yOffset = currentRegionY - lastRegionY;
			stream.writeBits(2, 3);
			stream.writeBits(18, (yOffset & 0xff) + ((xOffset & 0xff) << 8) + (planeOffset << 16));
		}
	}

	private void processOutsidePlayers(OutputStream stream, OutputStream updateBlockData, boolean nsn2) {
		stream.initBitAccess();
		int skip = 0;
		localAddedPlayers = 0;
		for (int i = 0; i < outPlayersIndexesCount; i++) {
			int playerIndex = outPlayersIndexes[i];
			if (nsn2 ? (0x1 & slotFlags[playerIndex]) == 0 : (0x1 & slotFlags[playerIndex]) != 0)
				continue;
			if (skip > 0) {
				skip--;
				slotFlags[playerIndex] = (byte) (slotFlags[playerIndex] | 2);
				continue;
			}
			Player p = World.getPlayers().get(playerIndex);
			if (needsAdd(p)) {
				stream.writeBits(1, 1);
				stream.writeBits(2, 0); // request add
				int hash = p.getRegionHash();
				if (hash == regionHashes[playerIndex])
					stream.writeBits(1, 0);
				else {
					stream.writeBits(1, 1);
					updateRegionHash(stream, regionHashes[playerIndex], hash);
					regionHashes[playerIndex] = hash;
				}
				stream.writeBits(6, p.getXInRegion());
				stream.writeBits(6, p.getYInRegion());
				boolean needAppearenceUpdate = needAppearenceUpdate(p.getIndex(), p.getAppearance().getMD5AppeareanceDataHash());
				appendUpdateBlock(p, updateBlockData, needAppearenceUpdate, true);
				stream.writeBits(1, 1);
				localAddedPlayers++;
				localPlayers[p.getIndex()] = p;
				slotFlags[playerIndex] = (byte) (slotFlags[playerIndex] | 2);
			} else {
				int hash = p == null ? regionHashes[playerIndex] : p.getRegionHash();
				if (p != null && hash != regionHashes[playerIndex]) {
					stream.writeBits(1, 1);
					updateRegionHash(stream, regionHashes[playerIndex], hash);
					regionHashes[playerIndex] = hash;
				} else {
					stream.writeBits(1, 0); // no update needed
					for (int i2 = i + 1; i2 < outPlayersIndexesCount; i2++) {
						int p2Index = outPlayersIndexes[i2];
						if (nsn2 ? (0x1 & slotFlags[p2Index]) == 0 : (0x1 & slotFlags[p2Index]) != 0)
							continue;
						Player p2 = World.getPlayers().get(p2Index);
						if (needsAdd(p2) || (p2 != null && p2.getRegionHash() != regionHashes[p2Index]))
							break;
						skip++;
					}
					skipPlayers(stream, skip);
					slotFlags[playerIndex] = (byte) (slotFlags[playerIndex] | 2);
				}
			}
		}
		stream.finishBitAccess();
	}

	private void processLocalPlayers(OutputStream stream, OutputStream updateBlockData, boolean nsn0) {
		stream.initBitAccess();
		int skip = 0;
		for (int i = 0; i < localPlayersIndexesCount; i++) {
			int playerIndex = localPlayersIndexes[i];
			if (nsn0 ? (0x1 & slotFlags[playerIndex]) != 0 : (0x1 & slotFlags[playerIndex]) == 0)
				continue;
			if (skip > 0) {
				skip--;
				slotFlags[playerIndex] = (byte) (slotFlags[playerIndex] | 2);
				continue;
			}
			Player p = localPlayers[playerIndex];
			if (needsRemove(p)) {
				stream.writeBits(1, 1); // needs update
				stream.writeBits(1, 0); // no masks update needeed
				stream.writeBits(2, 0); // request remove
				regionHashes[playerIndex] = p.getLastTile() == null ? p.getRegionHash() : p.getLastTile().getRegionHash();
				int hash = p.getRegionHash();
				if (hash == regionHashes[playerIndex])
					stream.writeBits(1, 0);
				else {
					stream.writeBits(1, 1);
					updateRegionHash(stream, regionHashes[playerIndex], hash);
					regionHashes[playerIndex] = hash;
				}
				localPlayers[playerIndex] = null;
			} else {
				boolean needAppearenceUpdate = needAppearenceUpdate(p.getIndex(), p.getAppearance().getMD5AppeareanceDataHash());
				boolean needUpdate = p.needMasksUpdate() || needAppearenceUpdate;
				if (needUpdate)
					appendUpdateBlock(p, updateBlockData, needAppearenceUpdate, false);
				if (p.hasTeleported()) {
					stream.writeBits(1, 1); // needs update
					stream.writeBits(1, needUpdate ? 1 : 0);
					stream.writeBits(2, 3);
					int xOffset = p.getX() - p.getLastTile().getX();
					int yOffset = p.getY() - p.getLastTile().getY();
					int planeOffset = p.getPlane() - p.getLastTile().getPlane();
					if (Math.abs(p.getX() - p.getLastTile().getX()) <= 14 && Math.abs(p.getY() - p.getLastTile().getY()) <= 14) {
						stream.writeBits(1, 0);
						if (xOffset < 0)
							xOffset += 32;
						if (yOffset < 0)
							yOffset += 32;
						stream.writeBits(12, yOffset + (xOffset << 5) + (planeOffset << 10));
					} else {
						stream.writeBits(1, 1);
						stream.writeBits(30, (yOffset & 0x3fff) + ((xOffset & 0x3fff) << 14) + ((planeOffset & 0x3) << 28));
					}
				} else if (p.getNextWalkDirection() != null) {
					int dx = p.getNextWalkDirection().getDx();
					int dy = p.getNextWalkDirection().getDy();
					boolean running;
					int opcode;
					if (p.getNextRunDirection() != null) {
						dx += p.getNextRunDirection().getDx();
						dy += p.getNextRunDirection().getDy();
						opcode = Utils.getPlayerRunningDirection(dx, dy);
						if (opcode == -1) {
							running = false;
							opcode = Utils.getPlayerWalkingDirection(dx, dy);
						} else
							running = true;
					} else {
						running = false;
						opcode = Utils.getPlayerWalkingDirection(dx, dy);
					}
					stream.writeBits(1, 1);
					if ((dx == 0 && dy == 0)) {
						stream.writeBits(1, 1); // quick fix
						stream.writeBits(2, 0);
						if (!needUpdate) // hasnt been sent yet
							appendUpdateBlock(p, updateBlockData, needAppearenceUpdate, false);
					} else {
						stream.writeBits(1, needUpdate ? 1 : 0);
						stream.writeBits(2, running ? 2 : 1);
						stream.writeBits(running ? 4 : 3, opcode);
					}
				} else if (needUpdate) {
					stream.writeBits(1, 1); // needs update
					stream.writeBits(1, 1);
					stream.writeBits(2, 0);
				} else { // skip
					stream.writeBits(1, 0); // no update needed
					for (int i2 = i + 1; i2 < localPlayersIndexesCount; i2++) {
						int p2Index = localPlayersIndexes[i2];
						if (nsn0 ? (0x1 & slotFlags[p2Index]) != 0 : (0x1 & slotFlags[p2Index]) == 0)
							continue;
						Player p2 = localPlayers[p2Index];
						if (needsRemove(p2) || p2.hasTeleported() || p2.getNextWalkDirection() != null || (p2.needMasksUpdate() || needAppearenceUpdate(p2.getIndex(), p2.getAppearance().getMD5AppeareanceDataHash())))
							break;
						skip++;
					}
					skipPlayers(stream, skip);
					slotFlags[playerIndex] = (byte) (slotFlags[playerIndex] | 2);
				}

			}
		}
		stream.finishBitAccess();
	}

	private void skipPlayers(OutputStream stream, int amount) {
		stream.writeBits(2, amount == 0 ? 0 : amount > 255 ? 3 : (amount > 31 ? 2 : 1));
		if (amount > 0)
			stream.writeBits(amount > 255 ? 11 : (amount > 31 ? 8 : 5), amount);
	}

	private void appendUpdateBlock(Player p, OutputStream data, boolean needAppearenceUpdate, boolean added) {
		OutputStream block = new OutputStream();
		int maskData = 0;

		if (p.getNextBodyGlow() != null) {
			maskData |= 0x20000;
			applyBodyGlowMask(p, block);
		}

		//maskData |= 0x400000; //who knows
		//		block.writeByte(4);
		//		for (int i = 0;i < 4;i++) {
		//			block.writeShortLE(3);
		//			block.writeInt(523562);
		//		}

		if (p.getNextSpotAnim2() != null) {
			maskData |= 0x200;
			applyGraphicsMask2(p, block);
		}

		if (added || (p.getNextFaceTile() != null && p.getNextRunDirection() == null && p.getNextWalkDirection() == null)) {
			maskData |= 0x20;
			applyFaceDirectionMask(p, block);
		}

		//maskData |= 0x800000; //same as 0x400000 but resets data
		//		block.writeByte(4);
		//		for (int i = 0;i < 4;i++) {
		//			block.writeShortLE(3);
		//			block.writeInt(523562);
		//		}

		//maskData |= 0x10000; //map dot edit

		if (p.getBodyModelRotator() != null) {
			maskData |= 0x100000;
			p.getBodyModelRotator().encodePlayer(block);
		}

		if (p.getNextForceTalk() != null) {
			maskData |= 0x4000;
			applyForceTalkMask(p, block);
		}

		//maskData |= 0x80000; //Forcechat with optional display in chatbox (nex cough?)

		if (p.getNextSpotAnim3() != null) {
			maskData |= 0x40000;
			applyGraphicsMask3(p, block);
		}

		//maskData |= 0x8000; //map dot edit

		if (!p.getNextHits().isEmpty() || !p.getNextHitBars().isEmpty()) {
			maskData |= 0x40;
			applyHitsMask(p, block);
		}

		if (needAppearenceUpdate) {
			maskData |= 0x1;
			applyAppearanceMask(p, block);
		}

		if (p.getNextAnimation() != null) {
			maskData |= 0x10;
			applyAnimationMask(p, block);
		}

		if (p.getNextFaceEntity() != -2 || (added && p.getLastFaceEntity() != -1)) {
			maskData |= 0x2;
			applyFaceEntityMask(p, block);
		}

		if (p.getTemporaryMoveType() != null) {
			maskData |= 0x1000;
			applyTemporaryMoveTypeMask(p, block);
		}

		//maskData |= 0x2000; //animation related?

		if (added || p.isUpdateMovementType()) {
			maskData |= 0x4;
			applyMoveTypeMask(p, block);
		}

		if (p.getNextSpotAnim4() != null) {
			maskData |= 0x200000;
			applyGraphicsMask4(p, block);
		}

		if (p.getNextForceMovement() != null) {
			maskData |= 0x800;
			applyForceMovementMask(p, block);
		}

		if (p.getNextSpotAnim1() != null) {
			maskData |= 0x80;
			applyGraphicsMask1(p, block);
		}

		if (maskData >= 256)
			maskData |= 0x8;
		if (maskData >= 65536)
			maskData |= 0x100;
		data.writeByte(maskData);
		if (maskData >= 256)
			data.writeByte(maskData >> 8);
		if (maskData >= 65536)
			data.writeByte(maskData >> 16);
		data.writeBytes(block.toByteArray());
	}

	private void applyBodyGlowMask(Player p, OutputStream data) {
		data.writeByte(p.getNextBodyGlow().getRedAdd());
		data.writeByteC(p.getNextBodyGlow().getGreenAdd());
		data.write128Byte(p.getNextBodyGlow().getBlueAdd());
		data.write128Byte(p.getNextBodyGlow().getScalar());
		data.writeShort128(0);
		data.writeShort(p.getNextBodyGlow().getTime());
	}

	private void applyForceTalkMask(Player p, OutputStream data) {
		data.writeString(p.getNextForceTalk().getText());
	}

	private void applyHitsMask(Player p, OutputStream data) {
		data.write128Byte(p.getNextHits().size());
		for (Hit hit : p.getNextHits()) {
			boolean interactingWith = hit.interactingWith(player, p);
			if (hit.missed() && !interactingWith) {
				data.writeSmart(32766);
				data.writeByteC(hit.getDamage());
			} else if (hit.getSoaking() != null) {
				data.writeSmart(32767);
				data.writeSmart(hit.getMark(player, p));
				data.writeSmart(Utils.clampI(hit.getDamage(), 0, Short.MAX_VALUE/2));
				data.writeSmart(hit.getSoaking().getMark(player, p));
				data.writeSmart(Utils.clampI(hit.getSoaking().getDamage(), 0, Short.MAX_VALUE/2));
			} else {
				data.writeSmart(hit.getMark(player, p));
				data.writeSmart(Utils.clampI(hit.getDamage(), 0, Short.MAX_VALUE/2));
			}
			data.writeSmart(hit.getDelay());
		}
		data.writeByte(p.getNextHitBars().size());
		for (HitBar bar : p.getNextHitBars()) {
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

	private void applyFaceEntityMask(Player p, OutputStream data) {
		data.writeShort128(p.getNextFaceEntity() == -2 ? p.getLastFaceEntity() : p.getNextFaceEntity());
	}

	private void applyFaceDirectionMask(Player p, OutputStream data) {
		data.writeShort128(p.getFaceAngle()); // also works as face tile as dir
		// calced on setnextfacetile
	}

	private void applyMoveTypeMask(Player p, OutputStream data) {
		data.write128Byte(p.getRun() ? 2 : 1);
	}

	private void applyTemporaryMoveTypeMask(Player p, OutputStream data) {
		data.writeByteC(p.getTemporaryMoveType().getId());
	}

	private void applyGraphicsMask1(Player p, OutputStream data) {
		data.writeShortLE(p.getNextSpotAnim1().getId());
		data.writeInt(p.getNextSpotAnim1().getSettingsHash());
		data.writeByte128(p.getNextSpotAnim1().getSettings2Hash());
	}

	private void applyGraphicsMask2(Player p, OutputStream data) {
		data.writeShort(p.getNextSpotAnim2().getId());
		data.writeIntV2(p.getNextSpotAnim2().getSettingsHash());
		data.write128Byte(p.getNextSpotAnim2().getSettings2Hash());
	}

	private void applyGraphicsMask3(Player p, OutputStream data) {
		data.writeShortLE128(p.getNextSpotAnim3().getId());
		data.writeIntV2(p.getNextSpotAnim3().getSettingsHash());
		data.writeByte128(p.getNextSpotAnim3().getSettings2Hash());
	}

	private void applyGraphicsMask4(Player p, OutputStream data) {
		data.writeShortLE(p.getNextSpotAnim4().getId());
		data.writeIntLE(p.getNextSpotAnim4().getSettingsHash());
		data.write128Byte(p.getNextSpotAnim4().getSettings2Hash());
	}

	private void applyAnimationMask(Player p, OutputStream data) {
		for (int id : p.getNextAnimation().getIds())
			data.writeBigSmart(id);
		data.writeByte(p.getNextAnimation().getSpeed());
	}

	private void applyAppearanceMask(Player p, OutputStream data) {
		byte[] renderData = p.getAppearance().getAppeareanceData();
		totalRenderDataSentLength += renderData.length;
		cachedAppearencesHashes[p.getIndex()] = p.getAppearance().getMD5AppeareanceDataHash();
		data.writeByteC(renderData.length);
		data.writeBytes(renderData);
	}

	private void applyForceMovementMask(Player p, OutputStream data) {
		data.writeByteC(p.getNextForceMovement().getDiffX1());
		data.write128Byte(p.getNextForceMovement().getDiffY1());
		data.writeByte128(p.getNextForceMovement().getDiffX2());
		data.writeByteC(p.getNextForceMovement().getDiffY2());
		data.writeShortLE128(p.getNextForceMovement().getStartClientCycles());
		data.writeShortLE(p.getNextForceMovement().getSpeedClientCycles());
		data.writeShort128(p.getNextForceMovement().getDirection());
	}

	public void write(OutputStream stream) {
		OutputStream updateBlockData = new OutputStream();
		processLocalPlayers(stream, updateBlockData, true);
		processLocalPlayers(stream, updateBlockData, false);
		processOutsidePlayers(stream, updateBlockData, true);
		processOutsidePlayers(stream, updateBlockData, false);
		stream.writeBytes(updateBlockData.getBuffer(), 0, updateBlockData.getOffset());
		totalRenderDataSentLength = 0;
		localPlayersIndexesCount = 0;
		outPlayersIndexesCount = 0;
		for (int playerIndex = 1; playerIndex < 2048; playerIndex++) {
			slotFlags[playerIndex] >>= 1;
			Player player = localPlayers[playerIndex];
			if (player == null)
				outPlayersIndexes[outPlayersIndexesCount++] = playerIndex;
			else
				localPlayersIndexes[localPlayersIndexesCount++] = playerIndex;
		}
	}

}