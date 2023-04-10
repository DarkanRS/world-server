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
package com.rs.net.encoders;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.rs.cache.loaders.interfaces.IFEvents;
import com.rs.game.World;
import com.rs.game.map.Chunk;
import com.rs.game.map.ChunkManager;
import com.rs.game.model.WorldProjectile;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.managers.InterfaceManager.Sub;
import com.rs.game.model.item.ItemsContainer;
import com.rs.game.model.object.GameObject;
import com.rs.game.model.object.ObjectMeshModifier;
import com.rs.lib.game.Animation;
import com.rs.lib.game.GroundItem;
import com.rs.lib.game.HintIcon;
import com.rs.lib.game.Item;
import com.rs.lib.game.PublicChatMessage;
import com.rs.lib.game.QuickChatMessage;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.Tile;
import com.rs.lib.io.OutputStream;
import com.rs.lib.model.Account;
import com.rs.lib.net.Encoder;
import com.rs.lib.net.ServerPacket;
import com.rs.lib.net.Session;
import com.rs.lib.net.packets.encoders.BlockMinimapState;
import com.rs.lib.net.packets.encoders.ChatFilterSettings;
import com.rs.lib.net.packets.encoders.ChatFilterSettingsPriv;
import com.rs.lib.net.packets.encoders.Cutscene;
import com.rs.lib.net.packets.encoders.DrawOrder;
import com.rs.lib.net.packets.encoders.DynamicMapRegion;
import com.rs.lib.net.packets.encoders.ExecuteCS2;
import com.rs.lib.net.packets.encoders.HintArrow;
import com.rs.lib.net.packets.encoders.MapRegion;
import com.rs.lib.net.packets.encoders.NPCUpdate;
import com.rs.lib.net.packets.encoders.OpenURL;
import com.rs.lib.net.packets.encoders.PlayerOption;
import com.rs.lib.net.packets.encoders.PlayerUpdate;
import com.rs.lib.net.packets.encoders.RunEnergy;
import com.rs.lib.net.packets.encoders.RunWeight;
import com.rs.lib.net.packets.encoders.SetCursor;
import com.rs.lib.net.packets.encoders.Sound;
import com.rs.lib.net.packets.encoders.SystemUpdateTimer;
import com.rs.lib.net.packets.encoders.UpdateGESlot;
import com.rs.lib.net.packets.encoders.UpdateItemContainer;
import com.rs.lib.net.packets.encoders.UpdateStat;
import com.rs.lib.net.packets.encoders.camera.CamLookAt;
import com.rs.lib.net.packets.encoders.camera.CamMoveTo;
import com.rs.lib.net.packets.encoders.camera.CamShake;
import com.rs.lib.net.packets.encoders.interfaces.IFCloseSub;
import com.rs.lib.net.packets.encoders.interfaces.IFOpenTop;
import com.rs.lib.net.packets.encoders.interfaces.IFSetAngle;
import com.rs.lib.net.packets.encoders.interfaces.IFSetAnimation;
import com.rs.lib.net.packets.encoders.interfaces.IFSetEvents;
import com.rs.lib.net.packets.encoders.interfaces.IFSetGraphic;
import com.rs.lib.net.packets.encoders.interfaces.IFSetHide;
import com.rs.lib.net.packets.encoders.interfaces.IFSetItem;
import com.rs.lib.net.packets.encoders.interfaces.IFSetModel;
import com.rs.lib.net.packets.encoders.interfaces.IFSetNPCHead;
import com.rs.lib.net.packets.encoders.interfaces.IFSetPlayerHead;
import com.rs.lib.net.packets.encoders.interfaces.IFSetPosition;
import com.rs.lib.net.packets.encoders.interfaces.IFSetTargetParam;
import com.rs.lib.net.packets.encoders.interfaces.IFSetText;
import com.rs.lib.net.packets.encoders.interfaces.opensub.IFOpenSub;
import com.rs.lib.net.packets.encoders.interfaces.opensub.IFOpenSubActiveGroundItem;
import com.rs.lib.net.packets.encoders.interfaces.opensub.IFOpenSubActiveNPC;
import com.rs.lib.net.packets.encoders.interfaces.opensub.IFOpenSubActiveObject;
import com.rs.lib.net.packets.encoders.interfaces.opensub.IFOpenSubActivePlayer;
import com.rs.lib.net.packets.encoders.social.MessageClan;
import com.rs.lib.net.packets.encoders.social.MessageFriendsChat;
import com.rs.lib.net.packets.encoders.social.MessageGame;
import com.rs.lib.net.packets.encoders.social.MessageGame.MessageType;
import com.rs.lib.net.packets.encoders.social.MessagePrivate;
import com.rs.lib.net.packets.encoders.social.MessagePrivateEcho;
import com.rs.lib.net.packets.encoders.social.MessagePublic;
import com.rs.lib.net.packets.encoders.social.QuickChatClan;
import com.rs.lib.net.packets.encoders.social.QuickChatFriendsChat;
import com.rs.lib.net.packets.encoders.social.QuickChatPrivate;
import com.rs.lib.net.packets.encoders.social.QuickChatPrivateEcho;
import com.rs.lib.net.packets.encoders.updatezone.*;
import com.rs.lib.net.packets.encoders.vars.SetVarClan;
import com.rs.lib.net.packets.encoders.vars.SetVarc;
import com.rs.lib.net.packets.encoders.vars.SetVarcString;
import com.rs.lib.net.packets.encoders.vars.SetVarp;
import com.rs.lib.net.packets.encoders.vars.SetVarpBit;
import com.rs.lib.net.packets.encoders.zonespecific.SpotAnimSpecific;
import com.rs.lib.util.MapUtils;
import com.rs.lib.util.MapUtils.Structure;
import com.rs.lib.util.Utils;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;

public class WorldEncoder extends Encoder {

	private Player player;

	public WorldEncoder(Player player, Session session) {
		super(session);
		this.player = player;
	}

	public void sendWindowsPane(int id, int type) {
		session.writeToQueue(new IFOpenTop(id, type));
	}

	public void sendInterface(boolean overlay, int topId, int topChildId, int subId) {
		session.writeToQueue(new IFOpenSub(topId, topChildId, subId, overlay));
	}

	public void sendNPCInterface(NPC npc, boolean overlay, int topId, int topChildId, int subId) {
		session.writeToQueue(new IFOpenSubActiveNPC(topId, topChildId, subId, overlay, npc.getIndex()));
	}

	public void sendObjectInterface(GameObject object, boolean overlay, int topId, int topChildId, int subId) {
		session.writeToQueue(new IFOpenSubActiveObject(topId, topChildId, subId, overlay, object));
	}

	public void sendPlayerInterface(boolean overlay, int topId, int topChildId, int subId) {
		session.writeToQueue(new IFOpenSubActivePlayer(topId, topChildId, subId, overlay, player.getIndex()));
	}

	public void sendGroundItemInterface(GroundItem item, boolean overlay, int topId, int topChildId, int subId) {
		session.writeToQueue(new IFOpenSubActiveGroundItem(topId, topChildId, subId, overlay, item));
	}

	public void closeInterface(int topChildId) {
		session.writeToQueue(new IFCloseSub(topChildId));
	}

	public void setIFGraphic(int interfaceId, int componentId, int spriteId) {
		session.writeToQueue(new IFSetGraphic(interfaceId, componentId, spriteId));
	}

	public void setIFPosition(int interfaceId, int componentId, int x, int y) {
		session.writeToQueue(new IFSetPosition(interfaceId, componentId, x, y));
	}

	public void setIFModel(int interfaceId, int componentId, int modelId) {
		session.writeToQueue(new IFSetModel(interfaceId, componentId, modelId));
	}

	public void setIFHidden(int interfaceId, int componentId, boolean hidden) {
		session.writeToQueue(new IFSetHide(interfaceId, componentId, hidden));
	}

	public void setIFEvents(IFEvents params) {
		session.writeToQueue(new IFSetEvents(params));
	}

	public void setIFRightClickOps(int interfaceId, int componentId, int fromSlot, int toSlot, int... optionsSlots) {
		setIFEvents(new IFEvents(interfaceId, componentId, fromSlot, toSlot).enableRightClickOptions(optionsSlots));
	}

	public void setIFTargetParam(int interfaceId, int componentId, int fromSlot, int toSlot, int targetParam) {
		session.writeToQueue(new IFSetTargetParam(interfaceId, componentId, fromSlot, toSlot, targetParam));
	}

	public void setIFText(int interfaceId, int componentId, String text) {
		session.writeToQueue(new IFSetText(interfaceId, componentId, text));
	}

	public void setIFAnimation(int emoteId, int interfaceId, int componentId) {
		session.writeToQueue(new IFSetAnimation(interfaceId, componentId, emoteId));
	}

	public void setIFAngle(int interfaceId, int componentId, int pitch, int roll, int scale) {
		session.writeToQueue(new IFSetAngle(interfaceId, componentId, pitch, roll, scale));
	}

	public void setIFItem(int interfaceId, int componentId, int id, int amount) {
		session.writeToQueue(new IFSetItem(interfaceId, componentId, id, amount));
	}

	public void setIFPlayerHead(int interfaceId, int componentId) {
		session.writeToQueue(new IFSetPlayerHead(interfaceId, componentId));
	}

	public void setIFNPCHead(int interfaceId, int componentId, int npcId) {
		session.writeToQueue(new IFSetNPCHead(interfaceId, componentId, npcId));
	}

	public void updateGESlot(int slot, int progress, int item, int price, int amount, int currAmount, int totalPrice) {
		session.writeToQueue(new UpdateGESlot(slot, progress, item, price, amount, currAmount, totalPrice));
	}

	public void sendDrawOrder(boolean playerFirst) {
		session.writeToQueue(new DrawOrder(playerFirst));
	}

	public void sendHintIcon(HintIcon icon) {
		session.writeToQueue(new HintArrow(icon));
	}

	public void sendCameraShake(int slotId, int v1, int v2, int v3, int v4) {
		session.writeToQueue(new CamShake(slotId, v1, v2, v3, v4));
	}

	public void sendStopCameraShake() {
		session.writeToQueue(ServerPacket.CAM_RESET_HARD);
	}

	public void sendCameraLook(Tile tile, int viewZ) {
		sendCameraLook(tile.getXInScene(player.getSceneBaseChunkId()), tile.getYInScene(player.getSceneBaseChunkId()), viewZ);
	}

	public void sendCameraLook(Tile tile, int viewZ, int speedToExactDestination, int speedOnRoutePath) {
		sendCameraLook(tile.getXInScene(player.getSceneBaseChunkId()), tile.getYInScene(player.getSceneBaseChunkId()), viewZ, speedToExactDestination, speedOnRoutePath);
	}

	public void sendCameraLook(int viewLocalX, int viewLocalY, int viewZ) {
		sendCameraLook(viewLocalX, viewLocalY, viewZ, -1, -1);
	}

	public void sendCameraLook(int viewLocalX, int viewLocalY, int viewZ, int speedToExactDestination, int speedOnRoutePath) {
		session.writeToQueue(new CamLookAt(viewLocalX, viewLocalY, viewZ, speedToExactDestination, speedOnRoutePath));
	}

	public void sendResetCamera() {
		session.writeToQueue(ServerPacket.CAM_RESET_HARD);
	}

	public void sendResetCameraSoft() {
		session.writeToQueue(ServerPacket.CAM_RESET_SMOOTH);
	}

	public void sendCameraPos(Tile tile, int z) {
		sendCameraPos(tile.getXInScene(player.getSceneBaseChunkId()), tile.getYInScene(player.getSceneBaseChunkId()), z);
	}

	public void sendCameraPos(Tile tile, int z, int speedToTile, int speedToExactDestination) {
		sendCameraPos(tile.getXInScene(player.getSceneBaseChunkId()), tile.getYInScene(player.getSceneBaseChunkId()), z, speedToTile, speedToExactDestination);
	}

	public void sendCameraPos(int moveLocalX, int moveLocalY, int moveZ) {
		sendCameraPos(moveLocalX, moveLocalY, moveZ, -1, -1);
	}

	/**
	 * @param speedToTile defines speed of the camera to the world tile with the default height then the exact position
	 * @param speedToExactDestination defines speed of camera to the exact position specified by previous parameters.
	 */
	public void sendCameraPos(int moveLocalX, int moveLocalY, int moveZ, int speedToTile, int speedToExactDestination) {
		session.writeToQueue(new CamMoveTo(moveLocalX, moveLocalY, moveZ, speedToTile, speedToExactDestination));
	}

	public void sendRunScript(int scriptId, Object... params) {
		List<Object> l = Arrays.asList(params);
		Collections.reverse(l);
		sendRunScriptReverse(scriptId, l.toArray());
	}

	public void setIFSprite(int interfaceId, int componentId, int slotId, int spriteId) {
		sendRunScript(1351, Utils.toInterfaceHash(interfaceId, componentId), slotId, spriteId);
	}

	public void setIFText(int interfaceId, int componentId, int slotId, String text) {
		sendRunScript(6566, Utils.toInterfaceHash(interfaceId, componentId), slotId, text);
	}

	public void sendRunScriptReverse(int scriptId, Object... params) {
		session.writeToQueue(new ExecuteCS2(scriptId, params));
	}

	public void sendVarc(int id, int value) {
		session.writeToQueue(new SetVarc(id, value));
	}

	public void sendVarcString(int id, String string) {
		session.writeToQueue(new SetVarcString(id, string));
	}

	@Deprecated
	public void sendVar(int id, int value) {
		session.writeToQueue(new SetVarp(id, value));
	}

	@Deprecated
	public void sendVarBit(int id, int value) {
		session.writeToQueue(new SetVarpBit(id, value));
	}

	@Deprecated
	public void clearVarps() {
		session.writeToQueue(ServerPacket.CLEAR_VARPS);
	}

	public void sendRunEnergy(double energy) {
		session.writeToQueue(new RunEnergy((int) energy));
	}

	public void refreshWeight(double weight) {
		session.writeToQueue(new RunWeight((int) weight));
	}

	public void sendObjectAnimation(GameObject object, Animation animation) {
		session.writeToQueue(new UpdateZonePartial(player.getSceneBaseChunkId(), object.getTile().getChunkId()));
		session.writeToQueue(new ObjectAnim(object.getTile().getChunkLocalHash(), object, animation));
	}

	public void removeGroundItem(GroundItem item) {
		session.writeToQueue(new UpdateZonePartial(player.getSceneBaseChunkId(), item.getTile().getChunkId()));
		session.writeToQueue(new RemoveGroundItem(item.getTile().getChunkLocalHash(), item));
	}

	public void sendGroundItem(GroundItem item) {
		session.writeToQueue(new UpdateZonePartial(player.getSceneBaseChunkId(), item.getTile().getChunkId()));
		session.writeToQueue(new CreateGroundItem(item.getTile().getChunkLocalHash(), item));
	}

	public void sendSetGroundItemAmount(GroundItem item, int oldAmount) {
		session.writeToQueue(new UpdateZonePartial(player.getSceneBaseChunkId(), item.getTile().getChunkId()));
		session.writeToQueue(new SetGroundItemAmount(item.getTile().getChunkLocalHash(), item, oldAmount));
	}

	public void sendProjectile(WorldProjectile projectile) {
		session.writeToQueue(new UpdateZonePartial(player.getSceneBaseChunkId(), projectile.getFromTile().getChunkId()));
		session.writeToQueue(new ProjAnim(projectile.getFromTile().getChunkLocalHash(), projectile));
	}

	public void sendTileMessage(String message, Tile tile, int delay, int height, int color) {
		session.writeToQueue(new UpdateZonePartial(player.getSceneBaseChunkId(), tile.getChunkId()));
		session.writeToQueue(new TileMessage(tile.getChunkLocalHash(), message, delay, height, color));
	}

	public void sendTileMessage(String message, Tile tile, int color) {
		sendTileMessage(message, tile, 5000, 255, color);
	}

	public void sendRemoveObject(GameObject object) {
		session.writeToQueue(new UpdateZonePartial(player.getSceneBaseChunkId(), object.getTile().getChunkId()));
		session.writeToQueue(new RemoveObject(object.getTile().getChunkLocalHash(), object));
	}

	public void sendAddObject(GameObject object) {
		session.writeToQueue(new UpdateZonePartial(player.getSceneBaseChunkId(), object.getTile().getChunkId()));
		session.writeToQueue(new AddObject(object.getTile().getChunkLocalHash(), object));
		if (object.getMeshModifier() != null)
			sendCustomizeObject(object.getMeshModifier());
	}

	public void sendCustomizeObject(ObjectMeshModifier modifier) {
		session.writeToQueue(new UpdateZonePartial(player.getSceneBaseChunkId(), modifier.getObject().getTile().getChunkId()));
		session.writeToQueue(new CustomizeObject(modifier.getObject().getTile().getChunkLocalHash(), modifier.getObject(), modifier.getModelIds(), modifier.getModifiedColors(), modifier.getModifiedTextures()));
	}

	public void sendMessage(MessageType type, String text, Player p) {
		session.writeToQueue(new MessageGame(type, text, p == null ? null : p.getAccount()));
	}

	public void sendPublicMessage(Player p, PublicChatMessage message) {
		session.writeToQueue(new MessagePublic(p.getIndex(), p.getMessageIcon(), message));
	}

	public void sendPrivateMessage(String username, String message) {
		session.writeToQueue(new MessagePrivate(username, message));
	}

	public void receivePrivateMessage(Account account, String message) {
		session.writeToQueue(new MessagePrivateEcho(account, message));
	}

	public void receivePrivateChatQuickMessage(Account account, QuickChatMessage message) {
		session.writeToQueue(new QuickChatPrivateEcho(account, message));
	}

	public void sendPrivateQuickMessage(String username, QuickChatMessage message) {
		session.writeToQueue(new QuickChatPrivate(username, message));
	}

	public void sendFriendsChatMessage(Account account, String chatName, String message) {
		session.writeToQueue(new MessageFriendsChat(account, chatName, message));
	}

	public void receiveFriendChatQuickMessage(Account account, String chatName, QuickChatMessage message) {
		session.writeToQueue(new QuickChatFriendsChat(account, chatName, message));
	}

	public void receiveClanChatMessage(Account account, String message, boolean guest) {
		session.writeToQueue(new MessageClan(account, message, guest));
	}

	public void receiveClanChatQuickMessage(Account account, QuickChatMessage message, boolean guest) {
		session.writeToQueue(new QuickChatClan(account, message, guest));
	}

	public void sendChatFilterSettings() {
		session.writeToQueue(new ChatFilterSettings(player.getTradeStatus(), player.getPublicStatus()));
	}

	public void sendChatFilterSettingsPrivateChat() {
		session.writeToQueue(new ChatFilterSettingsPriv(player.getAccount().getSocial().getStatus()));
	}

	public void sendDynamicMapRegion(boolean sendLSWP) {
		byte[] lswp = null;
		if (sendLSWP) {
			OutputStream stream = new OutputStream();
			player.getLocalPlayerUpdate().init(stream);
			lswp = stream.toByteArray();
		}
		OutputStream stream = new OutputStream();
		stream.initBitAccess();
		int mapHash = player.getMapSize().size >> 4;
		int[] realRegionIds = new int[4 * mapHash * mapHash];
		int realRegionIdsCount = 0;
		for (int plane = 0; plane < 4 * Chunk.PLANE_INC; plane += Chunk.PLANE_INC) {
			for (int chunkX = (player.getChunkX() - mapHash) * Chunk.X_INC; chunkX <= ((player.getChunkX() + mapHash)) * Chunk.X_INC; chunkX += Chunk.X_INC) {
				for (int chunkY = (player.getChunkY() - mapHash); chunkY <= ((player.getChunkY() + mapHash)); chunkY++) {
					Chunk chunk = ChunkManager.getChunk(chunkX + chunkY + plane);
					if (chunk.getRenderChunkX() == 0 || chunk.getRenderChunkY() == 0)
						stream.writeBits(1, 0);
					else {
						stream.writeBits(1, 1);
						stream.writeBits(26, (chunk.getRotation() << 1) | (chunk.getRenderPlane() << 24) | (chunk.getRenderChunkX() << 14) | (chunk.getRenderChunkY() << 3));
						boolean found = false;
						for (int index = 0; index < realRegionIdsCount; index++) {
							if (realRegionIds[index] == chunk.getRenderRegionId()) {
								found = true;
								break;
							}
						}
						if (!found)
							realRegionIds[realRegionIdsCount++] = chunk.getRenderRegionId();
					}
				}
			}
		}
		stream.finishBitAccess();
		session.writeToQueue(new DynamicMapRegion(lswp, player.getMapSize(), player.getChunkX(), player.getChunkY(), player.isForceNextMapLoadRefresh(), stream.toByteArray(), realRegionIds));
	}

	public void sendMapRegion(boolean sendLSWP) {
		byte[] lswp = null;
		if (sendLSWP) {
			OutputStream stream = new OutputStream();
			player.getLocalPlayerUpdate().init(stream);
			lswp = stream.toByteArray();
		}
		Set<Integer> mapRegionIds = new IntOpenHashSet();
		for (int chunkId : player.getMapChunkIds()) {
			int[] coords = MapUtils.decode(Structure.CHUNK, chunkId);
			mapRegionIds.add(MapUtils.encode(Structure.REGION, coords[0] >> 3, coords[1] >> 3));
		}
		session.writeToQueue(new MapRegion(lswp, player.getMapSize(), player.getChunkX(), player.getChunkY(), player.isForceNextMapLoadRefresh(), mapRegionIds));
	}

	public void sendCutscene(int id) {
		session.writeToQueue(new Cutscene(id, player.getAppearance().getAppeareanceData()));
	}

	@Deprecated
	public void sendPlayerOption(String option, int slot, boolean top) {
		sendPlayerOption(option, slot, top, -1);
	}

	@Deprecated
	public void sendPlayerOption(String option, int slot, boolean top, int cursor) {
		session.writeToQueue(new PlayerOption(option, slot, top, cursor));
	}

	public void sendLocalPlayersUpdate() {
		OutputStream stream = new OutputStream();
		player.getLocalPlayerUpdate().write(stream);
		session.writeToQueue(new PlayerUpdate(stream.toByteArray()));
	}

	public void sendLocalNPCsUpdate() {
		OutputStream stream = new OutputStream();
		player.getLocalNPCUpdate().write(stream, player.hasLargeSceneView());
		session.writeToQueue(new NPCUpdate(player.hasLargeSceneView(), stream.toByteArray()));
	}

	public void sendSpotAnim(SpotAnim spotAnim, Object target) {
		int targetHash = 0;
		if (target instanceof Player p)
			targetHash = p.getIndex() & 0xffff | 1 << 28;
		else if (target instanceof NPC n)
			targetHash = n.getIndex() & 0xffff | 1 << 29;
		else if (target instanceof Tile tile)
			targetHash = tile.getPlane() << 28 | tile.getX() << 14 | tile.getY() & 0x3fff | 1 << 30;
		session.writeToQueue(new SpotAnimSpecific(spotAnim, targetHash));
	}

	public void sendSystemUpdate(int delay) {
		session.writeToQueue(new SystemUpdateTimer(delay));
	}

	public void sendUpdateItems(int key, boolean negativeKey, Item[] items, int... slots) {
		session.writeToQueue(new UpdateItemContainer(key, negativeKey, items, slots));
	}

	public void sendUpdateItems(int key, ItemsContainer<Item> items, int... slots) {
		sendUpdateItems(key, items.array(), slots);
	}

	public void sendUpdateItems(int key, Item[] items, int... slots) {
		sendUpdateItems(key, key < 0, items, slots);
	}

	public void sendItems(int key, boolean negativeKey, Item[] items) {
		session.writeToQueue(new UpdateItemContainer(key, negativeKey, items));
	}

	public void sendItems(int key, ItemsContainer<Item> items) {
		sendItems(key, key < 0, items);
	}

	public void sendItems(int key, boolean negativeKey, ItemsContainer<Item> items) {
		sendItems(key, negativeKey, items.array());
	}

	public void sendItems(int key, Item[] items) {
		sendItems(key, key < 0, items);
	}

	public void sendLogout(boolean lobby) {
		session.writeToQueue(lobby ? ServerPacket.LOGOUT_LOBBY : ServerPacket.LOGOUT_FULL);
		ChannelFuture future = player.getSession().flush();
		if (player.hasFinished())
			if (future != null)
				future.addListener(ChannelFutureListener.CLOSE);
			else
				player.getSession().getChannel().close();
	}

	public void sendSound(Sound sound) {
		session.writeToQueue(sound);
	}

	public void resetSounds() {
		session.writeToQueue(ServerPacket.RESET_SOUNDS);
	}

	public void updateStats(int... skills) {
		UpdateStat[] updateStats = new UpdateStat[skills.length];
		for (int i = 0;i < skills.length;i++)
			updateStats[i] = new UpdateStat(skills[i], (int) player.getSkills().getXp(skills[i]), player.getSkills().getLevel(skills[i]));
		session.writeToQueue(updateStats);
	}

	public void setBlockMinimapState(int state) {
		session.writeToQueue(new BlockMinimapState(state));
	}

	public void sendOpenURL(String url) {
		session.writeToQueue(new OpenURL(url));
	}

	public void sendSetCursor(String walkHereReplace, int cursor) {
		session.writeToQueue(new SetCursor(walkHereReplace, cursor));
	}

	public void sendGameBarStages() {
		sendVar(1054, player.getClanStatus());
		sendVar(1055, player.getAssistStatus());
		sendVar(1056, player.isFilterGame() ? 1 : 0);
		sendVar(2159, player.getAccount().getSocial().getFcStatus());
		sendChatFilterSettings();
		sendChatFilterSettingsPrivateChat();
	}

	public void sendInventoryMessage(int border, int slotId, String message) {
		sendGameMessage(message);
		sendRunScriptReverse(948, border, slotId, message);
	}

	public void sendGameMessage(String text) {
		sendGameMessage(text, false);
	}

	public void sendGameMessage(String text, boolean filter) {
		sendMessage(filter ? MessageType.FILTERABLE : MessageType.UNFILTERABLE, text, null);
	}

	public void sendDevConsoleMessage(String text) {
		sendMessage(MessageType.DEV_CONSOLE, text, null);
	}

	public void sendTradeRequestMessage(Player p) {
		sendMessage(MessageType.TRADE_REQUEST, "wishes to trade with you.", p);
	}

	public void sendClanWarsRequestMessage(Player p) {
		sendMessage(MessageType.CLAN_CHALLENGE_REQUEST, "wishes to challenge your clan to a clan war.", p);
	}

	public void sendDuelChallengeRequestMessage(Player p, boolean friendly) {
		sendMessage(MessageType.DUEL_REQUEST, "wishes to duel with you (" + (friendly ? "friendly" : "stake") + ").", p);
	}

	public void sendDungeonneringRequestMessage(Player p) {
		sendMessage(MessageType.DUNGEONEERING_INVITE, "has invited you to a dungeon party.", p);
	}

	public void sendClanInviteMessage(Player p) {
		sendMessage(MessageType.CLAN_INVITE, p.getDisplayName() + " is inviting you to join their clan.", p);
	}

	public void sendPouchInfusionOptionsScript(boolean dung, int interfaceId, int componentId, int slotLength, int width, int height, String... options) {
		Object[] parameters = new Object[5 + options.length];
		int index = 0;
		if (dung) {
			parameters[index++] = 1159;
			parameters[index++] = 1100;
		} else {
			parameters[index++] = slotLength;
			parameters[index++] = 1;
		}
		for (int count = options.length - 1; count >= 0; count--)
			parameters[index++] = options[count];
		parameters[index++] = height;
		parameters[index++] = width;
		parameters[index++] = interfaceId << 16 | componentId;
		sendRunScriptReverse(757, parameters);
	}

	public void sendScrollInfusionOptionsScript(boolean dung, int interfaceId, int componentId, int slotLength, int width, int height, String... options) {
		Object[] parameters = new Object[5 + options.length];
		int index = 0;
		if (dung) {
			parameters[index++] = 1159;
			parameters[index++] = 1100;
		} else {
			parameters[index++] = slotLength;
			parameters[index++] = 1;
		}
		for (int count = options.length - 1; count >= 0; count--)
			parameters[index++] = options[count];
		parameters[index++] = height;
		parameters[index++] = width;
		parameters[index++] = interfaceId << 16 | componentId;
		sendRunScriptReverse(763, parameters);
	}

	public void sendRunScriptBlank(int scriptId) {
		sendRunScriptReverse(scriptId);
	}

	public void sendGroundItemMessage(GroundItem item, String message) {
		sendGroundItemMessage(0, 0xFFFFFF, item, message);
	}

	public void sendPlayerMessage(int border, int color, String message) {
		sendGameMessage(message);
		sendVarcString(306, message);
		sendVarc(1699, color);
		sendVarc(1700, border);
		sendVarc(1695, 1);
		sendPlayerInterface(true, player.getInterfaceManager().getTopInterface(), Sub.RENDER_SPACE.getComponent(player.resizeable()), 1177);
	}

	public void sendNPCMessage(int border, int color, NPC npc, String message) {
		sendGameMessage(message);
		sendVarcString(306, message);
		sendVarc(1699, color);
		sendVarc(1700, border);
		sendVarc(1695, 1);
		sendNPCInterface(npc, true, player.getInterfaceManager().getTopInterface(), Sub.RENDER_SPACE.getComponent(player.resizeable()), 1177);
	}

	public void sendGroundItemMessage(int border, int color, GroundItem item, String message) {
		sendGameMessage(message);
		sendVarcString(306, message);
		sendVarc(1699, color);
		sendVarc(1700, border);
		sendVarc(1695, 1);
		sendGroundItemInterface(item, true, player.getInterfaceManager().getTopInterface(), Sub.RENDER_SPACE.getComponent(player.resizeable()), 1177);
	}

	public void sendObjectMessage(int border, int color, GameObject object, String message) {
		sendGameMessage(message);
		sendVarcString(306, message);
		sendVarc(1699, color);
		sendVarc(1700, border);
		sendVarc(1695, 1);
		sendObjectInterface(object, true, player.getInterfaceManager().getTopInterface(), Sub.RENDER_SPACE.getComponent(player.resizeable()), 1177);
	}

	public void openGESearch() {
		player.getInterfaceManager().sendChatBoxInterface(7, 389);
		sendRunScriptReverse(570, "Grand Exchange Item Search");
	}

	public void sendInterFlashScript(int interfaceId, int componentId, int width, int height, int slot) {
		Object[] parameters = new Object[4];
		int index = 0;
		parameters[index++] = slot;
		parameters[index++] = height;
		parameters[index++] = width;
		parameters[index++] = interfaceId << 16 | componentId;
		sendRunScriptReverse(143, parameters);
	}

	public void sendInterSetItemsOptionsScript(int interfaceId, int componentId, int key, int width, int height, String... options) {
		sendInterSetItemsOptionsScript(interfaceId, componentId, key, false, width, height, options);
	}

	public void sendInterSetItemsOptionsScript(int interfaceId, int componentId, int key, boolean negativeKey, int width, int height, String... options) {
		Object[] parameters = new Object[6 + options.length];
		int index = 0;
		for (int count = options.length - 1; count >= 0; count--)
			parameters[index++] = options[count];
		parameters[index++] = -1; // dunno but always this
		parameters[index++] = 0;// dunno but always this, maybe startslot?
		parameters[index++] = height;
		parameters[index++] = width;
		parameters[index++] = key;
		parameters[index++] = interfaceId << 16 | componentId;
		sendRunScriptReverse(negativeKey ? 695 : 150, parameters); // scriptid 150 does
		// that the method
		// name says*/
	}

	public void sendInputIntegerScript(String message) {
		sendRunScriptReverse(108, message);
	}

	public void sendInputNameScript(String message) {
		sendRunScriptReverse(109, message);
	}

	public void sendInputLongTextScript(String message) {
		sendRunScriptReverse(110, message);
	}

	public void setClanVar(int varId, Object value) {
		session.writeToQueue(new SetVarClan(varId, value));
	}
}
