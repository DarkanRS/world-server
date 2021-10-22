package com.rs.game.player.content.randomevents;

import com.rs.game.npc.others.OwnedNPC;
import com.rs.game.player.Player;
import com.rs.game.player.content.dialogue.Conversation;
import com.rs.game.player.content.dialogue.Dialogue;
import com.rs.game.player.content.dialogue.HeadE;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ButtonClickEvent;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.handlers.ButtonClickHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class SandwichLady extends OwnedNPC {
	
	private int ticks = 0;
	private boolean claimed = false;

	public SandwichLady(Player owner) {
		super(owner, 8629, new WorldTile(owner), false);
		this.setRun(true);
		this.setNextFaceEntity(owner);
		setAutoDespawnAtDistance(false);
		teleToOwner();
	}
	
	@Override
	public void processNPC() {
		super.processNPC();
		if (getOwner().isDead() || !withinDistance(getOwner(), 16)) {
			finish();
			return;
		}
		entityFollow(getOwner(), false, 0);
		if (!claimed && (getOwner().getInterfaceManager().containsChatBoxInter() || getOwner().getInterfaceManager().containsScreenInter()))
			return;
		ticks++;
		if (ticks == 1) {
			setNextSpotAnim(new SpotAnim(1605));
			forceTalk("Sandwich delivery for " + getOwner().getDisplayName() + "!");
			setNextFaceEntity(getOwner());
		} else if (ticks == 30)
			forceTalk("All types of sandwiches, " + getOwner().getDisplayName() + ".");
		else if (ticks == 60)
			forceTalk("Come on " + getOwner().getDisplayName() + ", I made these specifically!!");
		else if (ticks == 90)
			forceTalk("You think I made these just for fun?!!?");
		else if (ticks == 120)
			forceTalk("You better start showing some manners young " + (getOwner().getAppearance().isMale() ? "man" : "lady") + "!!");
		else if (ticks == 149)
			forceTalk("Let's see how you like this!");
		else if (ticks == 150) {
			setNextAnimation(new Animation(3045));
			final Player owner = getOwner();
			owner.lock();
			owner.setNextAnimation(new Animation(836));
			owner.stopAll();
			owner.fadeScreen(() -> {
				WorldTile tile = RandomEvents.getRandomTile();
				owner.getControllerManager().processMagicTeleport(tile);
				owner.setNextWorldTile(tile);
				owner.setNextAnimation(new Animation(-1));
				owner.unlock();
			});
		} else if (ticks == 153) {
			setNextSpotAnim(new SpotAnim(1605));
			getOwner().setNextAnimation(new Animation(-1));
		} else if (ticks == 155) {
			finish();
		}
	}
	
	public static NPCClickHandler handleTalkTo = new NPCClickHandler(8629) {
		@Override
		public void handle(NPCClickEvent e) {
			if (e.getNPC() instanceof SandwichLady) {
				SandwichLady npc = (SandwichLady) e.getNPC();
				if (npc.ticks >= 149)
					return;
				if (npc.getOwner() != e.getPlayer()) {
					e.getPlayer().startConversation(new Conversation(new Dialogue()
							.addNPC(8629, HeadE.CALM_TALK, "This is for " + npc.getOwner().getDisplayName() + ", not you!")));
					return;
				}
				if (e.getPlayer().inCombat()) {
					e.getPlayer().sendMessage("The sandwich lady gives you a chocolate bar!");
					e.getPlayer().getInventory().addItemDrop(1973, 1);
					npc.forceTalk("Hope that fills you up!");
					npc.ticks = 152;
					return;
				}
				e.getPlayer().startConversation(new Conversation(e.getPlayer())
						.addNPC(8629, HeadE.HAPPY_TALKING, "You look hungry to me. I tell you what - have a chocolate bar on me.")
						.addNext(() -> {
							e.getPlayer().setTempO("sandwichLady", e.getNPC());
							e.getPlayer().getInterfaceManager().sendInterface(297);
						}));
			}
		}
	};
	
	public static ButtonClickHandler handleSandwichInterface = new ButtonClickHandler(297) {
		@Override
		public void handle(ButtonClickEvent e) {
			if (e.getComponentId() >= 10 && e.getComponentId() <= 22) {
				SandwichLady lady = e.getPlayer().getTempO("sandwichLady");
				e.getPlayer().closeInterfaces();
				if (lady == null) {
					e.getPlayer().sendMessage("An error has ocurred.");
					return;
				}
				if (e.getComponentId() == 22) {
					e.getPlayer().sendMessage("The sandwich lady gives you a chocolate bar!");
					e.getPlayer().getInventory().addItemDrop(1973, 1);
					lady.forceTalk("Hope that fills you up!");
					lady.ticks = 152;
				} else {
					e.getPlayer().sendMessage("The sandwich lady knocks you out and you wake up somewhere.. different.");
					lady.forceTalk("Hey, I didn't say you could have that!");
					lady.ticks = 149;
				}
				lady.claimed = true;
			}
		}
	};
}
