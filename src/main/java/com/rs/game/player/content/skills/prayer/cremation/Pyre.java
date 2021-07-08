package com.rs.game.player.content.skills.prayer.cremation;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.World;
import com.rs.game.npc.others.OwnedNPC;
import com.rs.game.object.GameObject;
import com.rs.game.object.OwnedObject;
import com.rs.game.player.Player;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.WorldTile;

public class Pyre extends OwnedObject {
	
	private PyreLog log;
	private Corpse corpse;
	private int life;
	private boolean lit;
	private boolean shadePyre;

	public Pyre(Player player, GameObject object, PyreLog log, boolean shadePyre) {
		super(player, object);
		this.id = shadePyre ? log.shadeNoCorpse : log.vyreNoCorpse;
		this.life = 50;
		this.log = log;
		this.shadePyre = shadePyre;
	}
	
	@Override
	public void tick(Player owner) {
		if (life-- <= 0)
			destroy();
	}
	
	public boolean setCorpse(Corpse corpse) {
		if (!log.validCorpse(corpse))
			return false;
		if (corpse == Corpse.VYRE && shadePyre)
			return false;
		this.corpse = corpse;
		setId(shadePyre ? log.shadeCorpse : log.vyreCorpse);
		life = 50;
		return true;
	}
	
	@Override
	public void onDestroy() {
		if (lit)
			return;
		World.addGroundItem(new Item(log.itemId), getCoordFace(), getOwner());
		if (corpse != null)
			World.addGroundItem(new Item(corpse.itemIds[0]), getCoordFace(), getOwner());
	}

	public void light(Player player) {
		life = 50;
		lit = true;
		player.lock();
		player.setNextAnimation(new Animation(16700));
		WorldTasksManager.delay(1, () -> {
			World.sendSpotAnim(player, new SpotAnim(357), getCoordFace());
			new ReleasedSpirit(player, getCoordFace(), shadePyre);
			player.getSkills().addXp(Constants.FIREMAKING, log.xp);
			player.getSkills().addXp(Constants.PRAYER, corpse.xp);
		});
		WorldTasksManager.delay(3, () -> {
			destroy();
		});
		WorldTasksManager.delay(4, () -> {
			player.incrementCount(ItemDefinitions.getDefs(corpse.itemIds[0]).name + " cremated");
			player.unlock();
			GameObject stand = World.getClosestObject(shadePyre ? 4065 : 30488, getCoordFace());
			World.sendSpotAnim(player, new SpotAnim(1605), stand);
			for (Item item : corpse.getKeyDrop(player, log))
				if (item != null)
					World.addGroundItem(item, stand);
		});
	}
	
	public PyreLog getLog() {
		return log;
	}
	
	public boolean isShadePyre() {
		return shadePyre;
	}

	private static class ReleasedSpirit extends OwnedNPC {
		
		private int life;

		public ReleasedSpirit(Player owner, WorldTile tile, boolean shade) {
			super(owner, shade ? 1242 : 7687, tile, false);
			life = shade ? 6 : 12;
		}
		
		@Override
		public void processNPC() {
			if (life-- <= 0)
				finish();
		}
		
	}

}
