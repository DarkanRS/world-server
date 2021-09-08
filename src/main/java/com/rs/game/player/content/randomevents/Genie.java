package com.rs.game.player.content.randomevents;

import com.rs.game.npc.others.OwnedNPC;
import com.rs.game.player.Player;
import com.rs.game.player.content.dialogue.Conversation;
import com.rs.game.player.content.dialogue.Dialogue;
import com.rs.game.player.content.dialogue.HeadE;
import com.rs.game.player.content.dialogue.Options;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class Genie extends OwnedNPC {

	private int ticks = 0;
	private boolean claimed = false;

	public Genie(Player owner) {
		super(owner, 3022, new WorldTile(owner), false);
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
			forceTalk("Hello " + getOwner().getDisplayName() + "!");
			setNextFaceEntity(getOwner());
		} else if (ticks == 30)
			forceTalk("A wish for " + getOwner().getDisplayName() + ".");
		else if (ticks == 60)
			forceTalk("I came from the desert you know...");
		else if (ticks == 90)
			forceTalk("Not just anyone gets a wish");
		else if (ticks == 120)
			forceTalk("Young " + (getOwner().getAppearance().isMale() ? "sir" : "madam") + " these things are quite rare.");
		else if (ticks == 149)
			forceTalk("So rude!");
		else if (ticks == 150) {
			setNextAnimation(new Animation(3045));
			final Player owner = getOwner();
			owner.lock();
			owner.setNextAnimation(new Animation(836));
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
	
	public static NPCClickHandler handleTalkTo = new NPCClickHandler(3022) {
		@Override
		public void handle(NPCClickEvent e) {
			if (e.getNPC() instanceof Genie) {
				Genie npc = (Genie) e.getNPC();
				if (npc.ticks >= 149)
					return;
				if (npc.getOwner() != e.getPlayer()) {
					e.getPlayer().startConversation(new Conversation(new Dialogue()
							.addNPC(3022, HeadE.CALM_TALK, "This wish is for " + npc.getOwner().getDisplayName() + ", not you!")));
					return;
				}
				if (e.getPlayer().inCombat()) {
				    if(e.getPlayer().getInventory().hasFreeSlots()) {
                        e.getPlayer().sendMessage("The genie gives you a lamp!");
                        e.getPlayer().getInventory().addItem(2528, 1);
                        npc.forceTalk("Hope that satisfies you!");
                        npc.claimed = true;
                    } else {
                        e.getPlayer().sendMessage("Your inventory is too full for a lamp!");
                        npc.claimed = true;
                    }
					npc.ticks = 152;
					return;
				}
				e.getPlayer().startConversation(new Conversation(e.getPlayer())
						.addNPC(3022, HeadE.HAPPY_TALKING, "Ah, so you are there master. I'm so glad you summoned me. Please take this lamp and make your with!")
                        .addOptions(new Options() {
                            @Override
                            public void create() {
                                option("Take the lamp", () -> {
                                    if(e.getPlayer().getInventory().hasFreeSlots()) {
                                        e.getPlayer().sendMessage("The genie gives you a lamp!");
                                        e.getPlayer().getInventory().addItem(2528, 1);
                                        npc.forceTalk("I hope you're happy with your wish.");
                                        npc.claimed = true;
                                    } else {
                                        e.getPlayer().sendMessage("Your inventory is too full for a lamp!");
                                        npc.claimed = true;
                                    }
                                    npc.ticks = 152;
                                });
                                option("Don't take it", () -> {
                                    npc.claimed = true;
                                    npc.ticks = 152;
                                });
                            }
                        }));
			}
		}
	};
}
