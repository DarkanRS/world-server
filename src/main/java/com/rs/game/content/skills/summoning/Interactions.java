package com.rs.game.content.skills.summoning;

import com.rs.game.content.dialogue.Dialogue;
import com.rs.game.content.dialogue.HeadE;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.Skills;
import com.rs.lib.game.Item;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class Interactions {
	private static boolean checkIsOwner(Player player, Familiar familiar) {
		if(familiar.getOwner() == player)
			return true;
		player.sendMessage("This isn't your familiar");
		return false;
	}
	private static boolean canTalkToFamiliar(Player player, Familiar familiar) {
		return player.getSkills().getLevelForXp(Skills.SUMMONING) >= familiar.getPouch().getLevel() + 10;
	}

    public static NPCClickHandler handleSpiritWolf = new NPCClickHandler(Pouch.SPIRIT_WOLF.getIdKeys(), new String[]{"Interact"}) {
        @Override
        public void handle(NPCClickEvent e) {
			if(e.getNPC() instanceof Familiar familiar && checkIsOwner(e.getPlayer(), familiar)) {
				if(canTalkToFamiliar(e.getPlayer(), familiar)) {
					int NPC = e.getNPCId();
					if(e.getPlayer().getInventory().containsOneItem(526, 530, 532, 528, 534, 536, 2859, 2530, 3125, 4834, 4832, 6729, 18830, 18832, 6812)) {
						e.getPlayer().startConversation(new Dialogue()
								.addNPC(NPC, HeadE.CAT_EXPLAIN, "Throw the bone! I want to chase it!")
								.addPlayer(HeadE.HAPPY_TALKING, "I can't just throw bones away - I need them to train my Prayer!")
						);
						return;
					}
					switch(Utils.random(0, 4)) {
						case 0 -> {
							e.getPlayer().startConversation(new Dialogue()
									.addNPC(NPC, HeadE.CAT_EXPLAIN, "What are you doing?")
									.addPlayer(HeadE.HAPPY_TALKING, "Oh, just some...biped things. I'm sure it would bore you.")
							);
						}
						case 1 -> {
							e.getPlayer().startConversation(new Dialogue()
									.addNPC(NPC, HeadE.CAT_SHOOK, "Danger!")
									.addPlayer(HeadE.HAPPY_TALKING, "Where?!")
									.addNPC(NPC, HeadE.CAT_CALM_TALK, "False alarm...")
							);
						}
						case 2 -> {
							e.getPlayer().startConversation(new Dialogue()
									.addNPC(NPC, HeadE.CAT_CALM_TALK, "I smell something good! Hunting time!")
									.addPlayer(HeadE.HAPPY_TALKING, "We can go hunting in a moment. I just have to take care of something first.")
							);
						}
						case 3 -> {
							e.getPlayer().startConversation(new Dialogue()
									.addNPC(NPC, HeadE.CAT_EXPLAIN, "When am I going to get to chase something?")
									.addPlayer(HeadE.HAPPY_TALKING, "Oh I'm sure we'll find something for you in a bit.")
							);
						}
					}
					return;
				}
				e.getPlayer().startConversation(new Dialogue().addNPC(e.getNPCId(), HeadE.CAT_EXPLAIN, "Whurf?"));
			}
        }
    };

	public static NPCClickHandler handleDreadFowl = new NPCClickHandler(Pouch.DREADFOWL.getIdKeys(), new String[]{"Interact"}) {
		@Override
		public void handle(NPCClickEvent e) {
			if(e.getNPC() instanceof Familiar familiar && checkIsOwner(e.getPlayer(), familiar)) {
				int NPC = e.getNPCId();
				if(canTalkToFamiliar(e.getPlayer(), familiar)) {
					switch (Utils.random(0, 3)) {
						case 0 -> {
							e.getPlayer().startConversation(new Dialogue()
									.addNPC(NPC, HeadE.CAT_CALM_TALK2, "Attack! Fight! Annihilate!")
									.addPlayer(HeadE.HAPPY_TALKING, "It always worries me when you're so happy saying that.")
							);
						}
						case 1 -> {
							e.getPlayer().startConversation(new Dialogue()
									.addNPC(NPC, HeadE.CAT_CALM_TALK2, "Can it be fightin' time, please?")
									.addPlayer(HeadE.HAPPY_TALKING, "Look I'll find something for you to fight, just give me a second.")
							);
						}
						case 2 -> {
							e.getPlayer().startConversation(new Dialogue()
									.addNPC(NPC, HeadE.CAT_CALM_TALK2, "I want to fight something.")
									.addPlayer(HeadE.HAPPY_TALKING, "I'll find something for you in a minute - just be patient.")
							);
						}
					}
					return;
				}
				e.getPlayer().startConversation(new Dialogue()
						.addNPC(NPC, HeadE.CAT_CALM_TALK2, "Cock-!")
						.addNPC(NPC, HeadE.CAT_CALM_TALK2, "Cock-ledoodledoo!")
				);
			}
		}
	};

	public static NPCClickHandler handleSpiritSpider = new NPCClickHandler(Pouch.SPIRIT_SPIDER.getIdKeys(), new String[]{"Interact"}) {
		@Override
		public void handle(NPCClickEvent e) {
			if(e.getNPC() instanceof Familiar familiar && checkIsOwner(e.getPlayer(), familiar)) {
				int NPC = e.getNPCId();
				if(canTalkToFamiliar(e.getPlayer(), familiar)) {
					if(e.getPlayer().getInventory().containsOneItem(12125)) {
						e.getPlayer().startConversation(new Dialogue().addNPC(NPC, HeadE.CAT_CALM_TALK, "So, do I get any of those flies?")
								.addPlayer(HeadE.HAPPY_TALKING, "I don't know, I was saving these for a pet.")
								.addNPC(NPC, HeadE.CAT_CALM_TALK, "I see...")
								.addPlayer(HeadE.HAPPY_TALKING, "Look, you can have some if you want.")
								.addNPC(NPC, HeadE.CAT_CALM_TALK, "Oh, don't do me any favours.")
								.addPlayer(HeadE.FRUSTRATED, "Look, here, have some!")
								.addNPC(NPC, HeadE.CAT_CALM_TALK, "Don't want them now.")
								.addPlayer(HeadE.SECRETIVE, "Siiiigh...spiders.")
						);
						switch (Utils.random(0, 4)) {
							case 0 -> {
								e.getPlayer().startConversation(new Dialogue()
										.addNPC(NPC, HeadE.CAT_CALM_TALK, "Where are we going?")
										.addPlayer(HeadE.HAPPY_TALKING, "I've not decided yet.")
										.addNPC(NPC, HeadE.CAT_CALM_TALK, "Fine, don't tell me...")
										.addPlayer(HeadE.HAPPY_TALKING, "Oh, okay, well, we are going...")
										.addNPC(NPC, HeadE.CAT_CALM_TALK, "Don't want to know now.")
										.addPlayer(HeadE.SECRETIVE, "Siiiigh...spiders.")
								);
							}
							case 1 -> {
								e.getPlayer().startConversation(new Dialogue()
										.addNPC(NPC, HeadE.CAT_CALM_TALK, "Who is that?")
										.addPlayer(HeadE.HAPPY_TALKING, "Who?")
										.addNPC(NPC, HeadE.CAT_CALM_TALK, "The two-legs over there.")
										.addPlayer(HeadE.HAPPY_TALKING, "I can't see who you mean...")
										.addNPC(NPC, HeadE.CAT_CALM_TALK, "Never mind...")
										.addPlayer(HeadE.HAPPY_TALKING, "Can you describe them a little better...")
										.addNPC(NPC, HeadE.CAT_CALM_TALK, "It doesn't matter now.")
										.addPlayer(HeadE.SECRETIVE, "Siiiigh...spiders.")
								);
							}
							case 2 -> {
								e.getPlayer().startConversation(new Dialogue()
										.addNPC(NPC, HeadE.CAT_CALM_TALK, "What are you doing?")
										.addPlayer(HeadE.HAPPY_TALKING, "Nothing that you should concern yourself with.")
										.addNPC(NPC, HeadE.CAT_CALM_TALK, "I see, you don't think I'm smart enough to understand...")
										.addPlayer(HeadE.FRUSTRATED, "That's not it at all! Look, I was...")
										.addNPC(NPC, HeadE.CAT_CALM_TALK, "Don't wanna know now.")
										.addPlayer(HeadE.SECRETIVE, "Siiiigh...spiders.")
								);
							}
							case 3 -> {
								e.getPlayer().startConversation(new Dialogue()
										.addNPC(NPC, HeadE.CAT_CALM_TALK, "Sigh...")
										.addPlayer(HeadE.FRUSTRATED, "What is it now?")
										.addNPC(NPC, HeadE.CAT_CALM_TALK, "Nothing really.")
										.addPlayer(HeadE.HAPPY_TALKING, "Oh, well that's a relief.")
								);
							}
						}
						return;
					}
					return;
				}
				e.getPlayer().startConversation(new Dialogue().addNPC(NPC, HeadE.CAT_CALM_TALK, "Hisssss!"));
			}
		}
	};

	public static NPCClickHandler handleThornySnail = new NPCClickHandler(Pouch.THORNY_SNAIL.getIdKeys(), new String[]{"Interact"}) {
		@Override
		public void handle(NPCClickEvent e) {
			if(e.getNPC() instanceof Familiar familiar && checkIsOwner(e.getPlayer(), familiar)) {
				int NPC = e.getNPCId();
				if(canTalkToFamiliar(e.getPlayer(), familiar)) {
					int hat = e.getPlayer().getEquipment().getHatId();
					if(hat == 3327 || hat == 3329 || hat == 3331 || hat == 3333 || hat == 3337 || hat == 3339 || hat == 3341 || hat == 3343) {
						e.getPlayer().startConversation(new Dialogue()
								.addNPC(NPC, HeadE.CAT_CALM_TALK, "...")
								.addPlayer(HeadE.HAPPY_TALKING, "What's the matter?")
								.addNPC(NPC, HeadE.CAT_CALM_TALK, "Check your head...")
								.addPlayer(HeadE.HAPPY_TALKING, "What about it... Oh, wait! Oh, this is pretty awkward...")
								.addNPC(NPC, HeadE.CAT_CALM_TALK, "You're wearing the spine of one of my relatives as a hat...")
								.addPlayer(HeadE.HAPPY_TALKING, "Well more of a faux-pas, then.")
								.addNPC(NPC, HeadE.CAT_CALM_TALK, "Just a bit...")
						);
						return;
					}
					switch(Utils.random(0, 4)) {
						case 0-> {
							e.getPlayer().startConversation(new Dialogue()
									.addNPC(NPC, HeadE.CAT_CALM_TALK, "All this running around the place is fun!")
									.addPlayer(HeadE.HAPPY_TALKING, "I'll be it's a step up from your usually sedentary lifestyle!")
									.addNPC(NPC, HeadE.CAT_CALM_TALK, "True, but it's mostly seeing the sort of sights you don't get at home.")
									.addPlayer(HeadE.HAPPY_TALKING, "Such as?")
									.addNPC(NPC, HeadE.CAT_CALM_TALK, "Living things for a start.")
									.addPlayer(HeadE.HAPPY_TALKING, "Those are in short supply in Mort Myre, I admit.")
							);
						}
						case 1-> {
							e.getPlayer().startConversation(new Dialogue()
									.addNPC(NPC, HeadE.CAT_CALM_TALK, "I think my stomach is drying out...")
									.addPlayer(HeadE.HAPPY_TALKING, "Your stomach? How do you know how it's feeling?")
									.addNPC(NPC, HeadE.CAT_CALM_TALK, "I am walking on it, you know...")
									.addPlayer(HeadE.SECRETIVE, "Urrgh...")
							);
						}
						case 2-> {
							e.getPlayer().startConversation(new Dialogue()
									.addNPC(NPC, HeadE.CAT_CALM_TALK, "Okay, I have to ask, what are those things you people totter about on?")
									.addPlayer(HeadE.HAPPY_TALKING, "You mean my legs?")
									.addNPC(NPC, HeadE.CAT_CALM_TALK, "Yes, those. How are you supposed to eat anything through them?")
									.addPlayer(HeadE.HAPPY_TALKING, "Well, we don't. That's what our mouths are for.")
									.addNPC(NPC, HeadE.CAT_CALM_TALK, "Oh, right! I thought those were for expelling waste gas and hot air!")
									.addPlayer(HeadE.HAPPY_TALKING, "Well, for a lot of people they are.")
							);
						}
						case 3-> {
							e.getPlayer().startConversation(new Dialogue()
									.addPlayer(HeadE.HAPPY_TALKING, "Can you slow down?")
									.addNPC(NPC, HeadE.CAT_CALM_TALK, "Are we going too fast for you?")
									.addPlayer(HeadE.HAPPY_TALKING, "I bet if you had to run on your internal organs you'd want a break now and then!")
							);
						}
					}
					return;
				}
				e.getPlayer().startConversation(new Dialogue().addNPC(e.getNPCId(), HeadE.CAT_CALM_TALK, "Bloop!"));
			}
		}
	};

	public static NPCClickHandler handleGraniteCrab = new NPCClickHandler(Pouch.GRANITE_CRAB.getIdKeys(), new String[]{"Interact"}) {
		@Override
		public void handle(NPCClickEvent e) {
			if(e.getNPC() instanceof Familiar familiar && checkIsOwner(e.getPlayer(), familiar)) {
				int NPC = e.getNPCId();
				if(canTalkToFamiliar(e.getPlayer(), familiar)) {
					if(e.getPlayer().getInventory().containsOneItem(13435, 317, 321, 327, 338, 345, 335, 331, 349, 359, 371, 377, 353, 341, 363, 11328, 2148, 11330, 11332, 7944, 383, 15264, 15270)) {
						e.getPlayer().startConversation(new Dialogue()
								.addNPC(NPC, HeadE.CAT_CALM_TALK, "Can I have some fish?")
								.addPlayer(HeadE.HAPPY_TALKING, "No, I have to cook these for later.")
								.addNPC(NPC, HeadE.CAT_CALM_TALK, "Free fish, please?")
								.addPlayer(HeadE.HAPPY_TALKING, "No...I already told you you can't.")
								.addNPC(NPC, HeadE.CAT_CALM_TALK, "Can it be fish time soon?")
								.addPlayer(HeadE.FRUSTRATED, "Great...I get stuck with the only granite crab in existence that can't take no for an answer...")
						);
						return;
					}

					switch(Utils.random(0, 3)) {
						case 0-> {
							e.getPlayer().startConversation(new Dialogue()
									.addNPC(NPC, HeadE.CAT_CALM_TALK, "Rock fish now, please?")
									.addPlayer(HeadE.HAPPY_TALKING, "Not right now. I don't have any rock fish.")
							);
						}
						case 1-> {
							e.getPlayer().startConversation(new Dialogue()
									.addNPC(NPC, HeadE.CAT_CALM_TALK, "When can we go fishing? I want rock fish.")
									.addPlayer(HeadE.HAPPY_TALKING, "When I need some fish. It's not that hard to work out, right?")
							);
						}
						case 2-> {
							e.getPlayer().startConversation(new Dialogue()
									.addNPC(NPC, HeadE.CAT_CALM_TALK, "I'm stealthy!")
									.addPlayer(HeadE.HAPPY_TALKING, "Errr... of course you are.")
							);
						}
					}
				}
				e.getPlayer().startConversation(new Dialogue().addNPC(NPC, HeadE.CALM_TALK, "Click click!"));
			}
		}
	};

	public static NPCClickHandler handleSpiritMosquito = new NPCClickHandler(Pouch.SPIRIT_MOSQUITO.getIdKeys(), new String[]{"Interact"}) {
		@Override
		public void handle(NPCClickEvent e) {
			if(e.getNPC() instanceof Familiar familiar && checkIsOwner(e.getPlayer(), familiar)) {
				int NPC = e.getNPCId();
				if(canTalkToFamiliar(e.getPlayer(), familiar)) {
					switch(Utils.random(0, 4)) {
						case 0-> {
							e.getPlayer().startConversation(new Dialogue()
									.addNPC(NPC, HeadE.CAT_CALM_TALK2, "You have lovely ankles.")
									.addPlayer(HeadE.HAPPY_TALKING, "Am I meant to be pleased by that?")
									.addNPC(NPC, HeadE.CAT_CALM_TALK2, "Thin skin. Your delicious blood is easier to get too.")
									.addPlayer(HeadE.HAPPY_TALKING, "I knew I couldn't trust you.")
									.addNPC(NPC, HeadE.CAT_CALM_TALK2, "Oh come on, you won't feel a thing...")
							);
						}
						case 1-> {
							e.getPlayer().startConversation(new Dialogue()
									.addNPC(NPC, HeadE.CAT_CALM_TALK2, "How about that local sports team?")
									.addPlayer(HeadE.HAPPY_TALKING, "Which one? The gnomeball team?")
									.addNPC(NPC, HeadE.CAT_CALM_TALK2, "I must confess: I have no idea.")
									.addPlayer(HeadE.HAPPY_TALKING, "Why did you ask, then?")
									.addNPC(NPC, HeadE.CAT_CALM_TALK2, "I was just trying to be friendly.")
									.addPlayer(HeadE.HAPPY_TALKING, "Just trying to get to my veins, more like!")
							);
						}
						case 2-> {
							e.getPlayer().startConversation(new Dialogue()
									.addNPC(NPC, HeadE.CAT_CALM_TALK2, "Have you ever tasted pirate blood?")
									.addPlayer(HeadE.HAPPY_TALKING, "Why would I drink pirate blood?")
									.addNPC(NPC, HeadE.CAT_CALM_TALK2, "How about dwarf blood?")
									.addPlayer(HeadE.HAPPY_TALKING, "I don't think you quite understand...")
									.addNPC(NPC, HeadE.CAT_CALM_TALK2, "Gnome blood, then?")
							);
						}
						case 3-> {
							e.getPlayer().startConversation(new Dialogue()
									.addNPC(NPC, HeadE.CAT_CALM_TALK2, "I'm soooo hungry!")
									.addPlayer(HeadE.HAPPY_TALKING, "What would you like to eat?")
									.addNPC(NPC, HeadE.CAT_CALM_TALK2, "Well, if you're not too attached to your elbow...")
									.addPlayer(HeadE.HAPPY_TALKING, "You can't eat my elbow! You don't have teeth!")
									.addNPC(NPC, HeadE.CAT_CALM_TALK2, "Tell me about it. Cousin Nigel always makes fun of me. Calls me 'No-teeth'.")
							);
						}
					}
					return;
				}
				e.getPlayer().startConversation(new Dialogue().addNPC(NPC, HeadE.CAT_CALM_TALK, "Buzzzzz!"));
			}
		}
	};

	public static NPCClickHandler handleDesertWyrm = new NPCClickHandler(Pouch.DESERT_WYRM.getIdKeys(), new String[]{"Interact"}) {
		@Override
		public void handle(NPCClickEvent e) {
			if(e.getNPC() instanceof Familiar familiar && checkIsOwner(e.getPlayer(), familiar)) {
				int NPC = e.getNPCId();
				if(canTalkToFamiliar(e.getPlayer(), familiar)) {
					if(new Item(e.getPlayer().getEquipment().getWeaponId(), 1).getName().toLowerCase().contains("pickaxe")) {
						e.getPlayer().startConversation(new Dialogue()
								.addNPC(NPC, HeadE.CAT_CALM_TALK2, "If you have that pick, why make me dig?")
								.addPlayer(HeadE.HAPPY_TALKING, "Because it's a little quicker and easier on my arms.")
								.addNPC(NPC, HeadE.CAT_CALM_TALK2, "I should take industrial action over this...")
								.addPlayer(HeadE.HAPPY_TALKING, "You mean you won't work for me any more?")
								.addNPC(NPC, HeadE.CAT_CALM_TALK2, "No. It means me and the lads feed you legs-first into some industrial machinery, maybe the Blast Furnace.")
								.addPlayer(HeadE.HAPPY_TALKING, "I'll just be over here, digging.")
								.addNPC(NPC, HeadE.CAT_CALM_TALK2, "That's the spirit, lad!")
						);
						return;
					}
					switch(Utils.random(0, 4)) {
						case 0-> {
							e.getPlayer().startConversation(new Dialogue()
									.addNPC(NPC, HeadE.CAT_CALM_TALK2, "This is so unsafe...I should have a hard hat for this work...")
									.addPlayer(HeadE.HAPPY_TALKING, "Well, I could get you a rune helm if you like - those are pretty hard.")
									.addNPC(NPC, HeadE.CAT_CALM_TALK2, "Keep that up and you'll have the union on your back!")
							);
						}
						case 1-> {
							e.getPlayer().startConversation(new Dialogue()
									.addNPC(NPC, HeadE.CAT_CALM_TALK2, "You can't touch me, I'm part of the union!")
									.addPlayer(HeadE.HAPPY_TALKING, "Is that some official \"no-touching\" policy or something?")
									.addNPC(NPC, HeadE.CAT_CALM_TALK2, "You really don't get it, do you " + e.getPlayer().getDisplayName() + "?")
							);
						}
						case 2-> {
							e.getPlayer().startConversation(new Dialogue()
									.addNPC(NPC, HeadE.CAT_CALM_TALK2, "You know, you might want to register with the union.")
									.addPlayer(HeadE.HAPPY_TALKING, "What are the benefits?")
									.addNPC(NPC, HeadE.CAT_CALM_TALK2, "I stop bugging you to join the union.")
									.addPlayer(HeadE.HAPPY_TALKING, "Ask that again later; I'll have to consider that generous proposal.")
							);
						}
						case 3-> {
							e.getPlayer().startConversation(new Dialogue()
									.addNPC(NPC, HeadE.CAT_CALM_TALK2, "Why are you ignoring that good ore seam, " + e.getPlayer().getPronoun("mister?", "lady?"))
									.addPlayer(HeadE.HAPPY_TALKING, "Which ore seam?")
									.addNPC(NPC, HeadE.CAT_CALM_TALK2, "There's a good ore seam right underneath us at this very moment.")
									.addPlayer(HeadE.HAPPY_TALKING, "Great! How long will it take for you to get to it?")
									.addNPC(NPC, HeadE.CAT_CALM_TALK2, "Five years, give or take.")
									.addPlayer(HeadE.HAPPY_TALKING, "Five years!")
									.addNPC(NPC, HeadE.CAT_CALM_TALK2, "That's if we go opencast, mind. I could probably reach it in three if I just dug.")
									.addPlayer(HeadE.HAPPY_TALKING, "Right. I see. I think I'll skip it thanks.")
							);
						}
					}
					return;
				}
				e.getPlayer().startConversation(new Dialogue().addNPC(NPC, HeadE.CAT_CALM_TALK2, "Sssssst!"));
			}
		}
	};

	public static NPCClickHandler handleSpiritScorpion = new NPCClickHandler(Pouch.SPIRIT_SCORPION.getIdKeys(), new String[]{"Interact"}) {
		@Override
		public void handle(NPCClickEvent e) {
			if(e.getNPC() instanceof Familiar familiar && checkIsOwner(e.getPlayer(), familiar)) {
				int NPC = e.getNPCId();
				if(canTalkToFamiliar(e.getPlayer(), familiar)) {
					switch(Utils.random(0, 4)) {
						case 0-> {
							e.getPlayer().startConversation(new Dialogue()
									.addNPC(NPC, HeadE.CAT_CALM_TALK2, "Hey, boss, how about we go to the bank?")
									.addPlayer(HeadE.HAPPY_TALKING, "And do what?")
									.addNPC(NPC, HeadE.CAT_CALM_TALK2, "Well, we could open by shouting, 'Stand and deliver!'")
									.addPlayer(HeadE.HAPPY_TALKING, "Why does everything with you end with something getting held up?")
									.addNPC(NPC, HeadE.CAT_CALM_TALK2, "That isn't true! Give me one example.")
									.addPlayer(HeadE.HAPPY_TALKING, "How about the post office?")
									.addNPC(NPC, HeadE.CAT_CALM_TALK2, "How about another?")
									.addPlayer(HeadE.HAPPY_TALKING, "Those junior White Knights? The ones selling the gnome crunchies?")
									.addNPC(NPC, HeadE.CAT_CALM_TALK2, "That was self defence.")
									.addPlayer(HeadE.HAPPY_TALKING, "No! No more hold-ups, stick-ups, thefts, or heists, you got that?")
							);
						}
						case 1-> {
							e.getPlayer().startConversation(new Dialogue()
									.addPlayer(HeadE.HAPPY_TALKING, "Say hello to my little friend!")
									.addNPC(NPC, HeadE.CAT_CALM_TALK2, "What?")
									.addPlayer(HeadE.HAPPY_TALKING, "My little friend: you ignored him last time you met him.")
									.addNPC(NPC, HeadE.CAT_CALM_TALK2, "So, who is your friend?")
									.addPlayer(HeadE.HAPPY_TALKING, "If I tell you, what is the point?")
							);
						}
						case 2-> {
							e.getPlayer().startConversation(new Dialogue()
									.addNPC(NPC, HeadE.CAT_CALM_TALK2, "Hey, boss, I've been thinking.")
									.addPlayer(HeadE.HAPPY_TALKING, "That's never a good sign.")
									.addNPC(NPC, HeadE.CAT_CALM_TALK2, "See, I heard about this railway...")
									.addPlayer(HeadE.HAPPY_TALKING, "We are not robbing it!")
									.addNPC(NPC, HeadE.CAT_CALM_TALK2, "I might not have wanted to suggest that, boss...")
									.addPlayer(HeadE.HAPPY_TALKING, "Then what were you going to suggest?")
									.addNPC(NPC, HeadE.CAT_CALM_TALK2, "That isn't important right now.")
									.addPlayer(HeadE.HAPPY_TALKING, "I thought as much.")
							);
						}
						case 3-> {
							e.getPlayer().startConversation(new Dialogue()
									.addNPC(NPC, HeadE.CAT_CALM_TALK2, "Why do we never go to crossroads and rob travelers?")
									.addPlayer(HeadE.HAPPY_TALKING, "There are already highwaymen at the good spots.")
									.addNPC(NPC, HeadE.CAT_CALM_TALK2, "Maybe we need to think bigger.")
							);
						}
					}
					return;
				}
				e.getPlayer().startConversation(new Dialogue().addNPC(NPC, HeadE.CAT_CALM_TALK2, "Hsssst!"));
			}
		}
	};


}
