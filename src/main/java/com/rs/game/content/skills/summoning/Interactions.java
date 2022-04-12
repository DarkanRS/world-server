package com.rs.game.content.skills.summoning;

import java.util.Arrays;

import com.rs.game.content.controllers.DamonheimController;
import com.rs.game.content.controllers.UndergroundDungeonController;
import com.rs.game.content.dialogue.Dialogue;
import com.rs.game.content.dialogue.HeadE;
import com.rs.game.content.dialogue.Options;
import com.rs.game.content.skills.magic.Magic;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.Skills;
import com.rs.lib.game.Item;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class Interactions {
	
	public static NPCClickHandler handleInteract = new NPCClickHandler(Pouch.getAllNPCIdKeys(), new String[] { "Interact" }) {
		@Override
		public void handle(NPCClickEvent e) {
			if (!(e.getNPC() instanceof Familiar familiar))
				return;
			if (familiar.getOwner() != e.getPlayer()) {
				e.getPlayer().sendMessage("This isn't your familiar");
				return;
			}
			e.getPlayer().startConversation(new Dialogue().addOptions("What would you like to do?", ops -> {
				if (familiar.getInventory() != null)
					ops.add("Open Familiar Inventory");
				ops.add("Talk-to", getTalkToDialogue(e.getPlayer(), familiar));
				addExtraOps(e.getPlayer(), ops, familiar);
			}));
		}
	};
	
	private static Dialogue getTalkToDialogue(Player player, Familiar familiar) {
		boolean canTalk = player.getSkills().getLevelForXp(Skills.SUMMONING) >= familiar.getPouch().getLevel() + 10;
		
		return switch(familiar.getPouch()) {
			case SPIRIT_WOLF -> {
				if (!canTalk)
					yield new Dialogue().addNPC(familiar.getId(), HeadE.CAT_EXPLAIN, "Whurf?");

				yield random(
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_EXPLAIN, "What are you doing?")
								.addPlayer(HeadE.HAPPY_TALKING, "Oh, just some...biped things. I'm sure it would bore you."),

						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_SHOOK, "Danger!")
								.addPlayer(HeadE.HAPPY_TALKING, "Where?!")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK, "False alarm..."),

						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK, "I smell something good! Hunting time!")
								.addPlayer(HeadE.HAPPY_TALKING, "We can go hunting in a moment. I just have to take care of something first."),

						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_EXPLAIN, "When am I going to get to chase something?")
								.addPlayer(HeadE.HAPPY_TALKING, "Oh I'm sure we'll find something for you in a bit.")
				);
			}
			case DREADFOWL -> {
				if (!canTalk)
					yield new Dialogue().addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Cock-!")
							.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Cock-ledoodledoo!");

				yield random(
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Attack! Fight! Annihilate!")
								.addPlayer(HeadE.HAPPY_TALKING, "It always worries me when you're so happy saying that."),

						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Can it be fightin' time, please?")
								.addPlayer(HeadE.HAPPY_TALKING, "Look I'll find something for you to fight, just give me a second."),

						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "I want to fight something.")
								.addPlayer(HeadE.HAPPY_TALKING, "I'll find something for you in a minute - just be patient.")
				);
			}
			case SPIRIT_SPIDER -> {
				if (!canTalk)
					yield new Dialogue().addNPC(familiar.getId(), HeadE.CAT_CALM_TALK, "Hisssss!");

				if(player.getInventory().containsOneItem(12125)) {
					yield new Dialogue().addNPC(familiar.getId(), HeadE.CAT_CALM_TALK, "So, do I get any of those flies?")
							.addPlayer(HeadE.HAPPY_TALKING, "I don't know, I was saving these for a pet.")
							.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK, "I see...")
							.addPlayer(HeadE.HAPPY_TALKING, "Look, you can have some if you want.")
							.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK, "Oh, don't do me any favours.")
							.addPlayer(HeadE.FRUSTRATED, "Look, here, have some!")
							.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK, "Don't want them now.")
							.addPlayer(HeadE.SECRETIVE, "Siiiigh...spiders.");
				}

				yield random(
					new Dialogue()
							.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK, "Where are we going?")
							.addPlayer(HeadE.HAPPY_TALKING, "I've not decided yet.")
							.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK, "Fine, don't tell me...")
							.addPlayer(HeadE.HAPPY_TALKING, "Oh, okay, well, we are going...")
							.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK, "Don't want to know now.")
							.addPlayer(HeadE.SECRETIVE, "Siiiigh...spiders."),
					new Dialogue()
							.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK, "Who is that?")
							.addPlayer(HeadE.HAPPY_TALKING, "Who?")
							.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK, "The two-legs over there.")
							.addPlayer(HeadE.HAPPY_TALKING, "I can't see who you mean...")
							.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK, "Never mind...")
							.addPlayer(HeadE.HAPPY_TALKING, "Can you describe them a little better...")
							.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK, "It doesn't matter now.")
							.addPlayer(HeadE.SECRETIVE, "Siiiigh...spiders."),
					new Dialogue()
							.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK, "What are you doing?")
							.addPlayer(HeadE.HAPPY_TALKING, "Nothing that you should concern yourself with.")
							.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK, "I see, you don't think I'm smart enough to understand...")
							.addPlayer(HeadE.FRUSTRATED, "That's not it at all! Look, I was...")
							.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK, "Don't wanna know now.")
							.addPlayer(HeadE.SECRETIVE, "Siiiigh...spiders."),
					new Dialogue()
							.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK, "Sigh...")
							.addPlayer(HeadE.FRUSTRATED, "What is it now?")
							.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK, "Nothing really.")
							.addPlayer(HeadE.HAPPY_TALKING, "Oh, well that's a relief.")
				);
			}
			case THORNY_SNAIL -> {
				if(!canTalk)
					yield new Dialogue().addNPC(familiar.getId(), HeadE.CAT_CALM_TALK, "Bloop!");
				int hat = player.getEquipment().getHatId();
				if(hat == 3327 || hat == 3329 || hat == 3331 || hat == 3333 || hat == 3337 || hat == 3339 || hat == 3341 || hat == 3343) {
					yield new Dialogue()
							.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK, "...")
							.addPlayer(HeadE.HAPPY_TALKING, "What's the matter?")
							.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK, "Check your head...")
							.addPlayer(HeadE.HAPPY_TALKING, "What about it... Oh, wait! Oh, this is pretty awkward...")
							.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK, "You're wearing the spine of one of my relatives as a hat...")
							.addPlayer(HeadE.HAPPY_TALKING, "Well more of a faux-pas, then.")
							.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK, "Just a bit...");
				}
				yield random(
					new Dialogue()
							.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK, "All this running around the place is fun!")
							.addPlayer(HeadE.HAPPY_TALKING, "I'll be it's a step up from your usually sedentary lifestyle!")
							.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK, "True, but it's mostly seeing the sort of sights you don't get at home.")
							.addPlayer(HeadE.HAPPY_TALKING, "Such as?")
							.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK, "Living things for a start.")
							.addPlayer(HeadE.HAPPY_TALKING, "Those are in short supply in Mort Myre, I admit."),
					
					new Dialogue()
							.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK, "I think my stomach is drying out...")
							.addPlayer(HeadE.HAPPY_TALKING, "Your stomach? How do you know how it's feeling?")
							.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK, "I am walking on it, you know...")
							.addPlayer(HeadE.SECRETIVE, "Urrgh..."),
					new Dialogue()
							.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK, "Okay, I have to ask, what are those things you people totter about on?")
							.addPlayer(HeadE.HAPPY_TALKING, "You mean my legs?")
							.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK, "Yes, those. How are you supposed to eat anything through them?")
							.addPlayer(HeadE.HAPPY_TALKING, "Well, we don't. That's what our mouths are for.")
							.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK, "Oh, right! I thought those were for expelling waste gas and hot air!")
							.addPlayer(HeadE.HAPPY_TALKING, "Well, for a lot of people they are."),
					new Dialogue()
							.addPlayer(HeadE.HAPPY_TALKING, "Can you slow down?")
							.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK, "Are we going too fast for you?")
							.addPlayer(HeadE.HAPPY_TALKING, "I bet if you had to run on your internal organs you'd want a break now and then!")
				);
			}
			case GRANITE_CRAB -> {
				if(!canTalk)
					yield new Dialogue().addNPC(familiar.getId(), HeadE.CALM_TALK, "Click click!");
				if(player.getInventory().containsOneItem(13435, 317, 321, 327, 338, 345, 335, 331, 349, 359, 371, 377, 353, 341, 363, 11328, 2148, 11330, 11332, 7944, 383, 15264, 15270)) {
					yield new Dialogue()
							.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK, "Can I have some fish?")
							.addPlayer(HeadE.HAPPY_TALKING, "No, I have to cook these for later.")
							.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK, "Free fish, please?")
							.addPlayer(HeadE.HAPPY_TALKING, "No...I already told you you can't.")
							.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK, "Can it be fish time soon?")
							.addPlayer(HeadE.FRUSTRATED, "Great...I get stuck with the only granite crab in existence that can't take no for an answer...");
				}
				yield random(
					new Dialogue()
							.addNPC(familiar.getId(),HeadE.CAT_CALM_TALK, "Rock fish now, please?")
							.addPlayer(HeadE.HAPPY_TALKING, "Not right now. I don't have any rock fish."),
					new Dialogue()
							.addNPC(familiar.getId(),HeadE.CAT_CALM_TALK, "When can we go fishing? I want rock fish.")
							.addPlayer(HeadE.HAPPY_TALKING, "When I need some fish. It's not that hard to work out, right?"),				
					new Dialogue()
							.addNPC(familiar.getId(),HeadE.CAT_CALM_TALK, "I'm stealthy!")
							.addPlayer(HeadE.HAPPY_TALKING, "Errr... of course you are.")
				);
			}
			case SPIRIT_MOSQUITO -> {
				if(!canTalk)
					yield new Dialogue().addNPC(familiar.getId(),HeadE.CAT_CALM_TALK, "Buzzzzz!");
				yield random(
					new Dialogue()
							.addNPC(familiar.getId(),HeadE.CAT_CALM_TALK2, "You have lovely ankles.")
							.addPlayer(HeadE.HAPPY_TALKING, "Am I meant to be pleased by that?")
							.addNPC(familiar.getId(),HeadE.CAT_CALM_TALK2, "Thin skin. Your delicious blood is easier to get too.")
							.addPlayer(HeadE.HAPPY_TALKING, "I knew I couldn't trust you.")
							.addNPC(familiar.getId(),HeadE.CAT_CALM_TALK2, "Oh come on, you won't feel a thing..."),
					new Dialogue()
							.addNPC(familiar.getId(),HeadE.CAT_CALM_TALK2, "How about that local sports team?")
							.addPlayer(HeadE.HAPPY_TALKING, "Which one? The gnomeball team?")
							.addNPC(familiar.getId(),HeadE.CAT_CALM_TALK2, "I must confess: I have no idea.")
							.addPlayer(HeadE.HAPPY_TALKING, "Why did you ask, then?")
							.addNPC(familiar.getId(),HeadE.CAT_CALM_TALK2, "I was just trying to be friendly.")
							.addPlayer(HeadE.HAPPY_TALKING, "Just trying to get to my veins, more like!"),
					new Dialogue()
							.addNPC(familiar.getId(),HeadE.CAT_CALM_TALK2, "Have you ever tasted pirate blood?")
							.addPlayer(HeadE.HAPPY_TALKING, "Why would I drink pirate blood?")
							.addNPC(familiar.getId(),HeadE.CAT_CALM_TALK2, "How about dwarf blood?")
							.addPlayer(HeadE.HAPPY_TALKING, "I don't think you quite understand...")
							.addNPC(familiar.getId(),HeadE.CAT_CALM_TALK2, "Gnome blood, then?"),
					new Dialogue()
							.addNPC(familiar.getId(),HeadE.CAT_CALM_TALK2, "I'm soooo hungry!")
							.addPlayer(HeadE.HAPPY_TALKING, "What would you like to eat?")
							.addNPC(familiar.getId(),HeadE.CAT_CALM_TALK2, "Well, if you're not too attached to your elbow...")
							.addPlayer(HeadE.HAPPY_TALKING, "You can't eat my elbow! You don't have teeth!")
							.addNPC(familiar.getId(),HeadE.CAT_CALM_TALK2, "Tell me about it. Cousin Nigel always makes fun of me. Calls me 'No-teeth'.")
				);
			}
			case DESERT_WYRM -> {
				if(!canTalk)
					yield new Dialogue().addNPC(familiar.getId(),HeadE.CAT_CALM_TALK2, "Sssssst!");
				if(new Item(player.getEquipment().getWeaponId(), 1).getName().toLowerCase().contains("pickaxe")) {
					yield new Dialogue()
							.addNPC(familiar.getId(),HeadE.CAT_CALM_TALK2, "If you have that pick, why make me dig?")
							.addPlayer(HeadE.HAPPY_TALKING, "Because it's a little quicker and easier on my arms.")
							.addNPC(familiar.getId(),HeadE.CAT_CALM_TALK2, "I should take industrial action over this...")
							.addPlayer(HeadE.HAPPY_TALKING, "You mean you won't work for me any more?")
							.addNPC(familiar.getId(),HeadE.CAT_CALM_TALK2, "No. It means me and the lads feed you legs-first into some industrial machinery, maybe the Blast Furnace.")
							.addPlayer(HeadE.HAPPY_TALKING, "I'll just be over here, digging.")
							.addNPC(familiar.getId(),HeadE.CAT_CALM_TALK2, "That's the spirit, lad!");
				}
				yield random(
					new Dialogue()
							.addNPC(familiar.getId(),HeadE.CAT_CALM_TALK2, "This is so unsafe...I should have a hard hat for this work...")
							.addPlayer(HeadE.HAPPY_TALKING, "Well, I could get you a rune helm if you like - those are pretty hard.")
							.addNPC(familiar.getId(),HeadE.CAT_CALM_TALK2, "Keep that up and you'll have the union on your back!"),
					new Dialogue()
							.addNPC(familiar.getId(),HeadE.CAT_CALM_TALK2, "You can't touch me, I'm part of the union!")
							.addPlayer(HeadE.HAPPY_TALKING, "Is that some official \"no-touching\" policy or something?")
							.addNPC(familiar.getId(),HeadE.CAT_CALM_TALK2, "You really don't get it, do you " + player.getDisplayName() + "?"),
					new Dialogue()
							.addNPC(familiar.getId(),HeadE.CAT_CALM_TALK2, "You know, you might want to register with the union.")
							.addPlayer(HeadE.HAPPY_TALKING, "What are the benefits?")
							.addNPC(familiar.getId(),HeadE.CAT_CALM_TALK2, "I stop bugging you to join the union.")
							.addPlayer(HeadE.HAPPY_TALKING, "Ask that again later; I'll have to consider that generous proposal."),
					new Dialogue()
							.addNPC(familiar.getId(),HeadE.CAT_CALM_TALK2, "Why are you ignoring that good ore seam, " + player.getPronoun("mister?", "lady?"))
							.addPlayer(HeadE.HAPPY_TALKING, "Which ore seam?")
							.addNPC(familiar.getId(),HeadE.CAT_CALM_TALK2, "There's a good ore seam right underneath us at this very moment.")
							.addPlayer(HeadE.HAPPY_TALKING, "Great! How long will it take for you to get to it?")
							.addNPC(familiar.getId(),HeadE.CAT_CALM_TALK2, "Five years, give or take.")
							.addPlayer(HeadE.HAPPY_TALKING, "Five years!")
							.addNPC(familiar.getId(),HeadE.CAT_CALM_TALK2, "That's if we go opencast, mind. I could probably reach it in three if I just dug.")
							.addPlayer(HeadE.HAPPY_TALKING, "Right. I see. I think I'll skip it thanks.")
				);
			}
			case SPIRIT_SCORPION -> {
				if(!canTalk)
					yield new Dialogue().addNPC(familiar.getId(),HeadE.CAT_CALM_TALK2, "Hsssst!");
				yield random(
					new Dialogue()
							.addNPC(familiar.getId(),HeadE.CAT_CALM_TALK2, "Hey, boss, how about we go to the bank?")
							.addPlayer(HeadE.HAPPY_TALKING, "And do what?")
							.addNPC(familiar.getId(),HeadE.CAT_CALM_TALK2, "Well, we could open by shouting, 'Stand and deliver!'")
							.addPlayer(HeadE.HAPPY_TALKING, "Why does everything with you end with something getting held up?")
							.addNPC(familiar.getId(),HeadE.CAT_CALM_TALK2, "That isn't true! Give me one example.")
							.addPlayer(HeadE.HAPPY_TALKING, "How about the post office?")
							.addNPC(familiar.getId(),HeadE.CAT_CALM_TALK2, "How about another?")
							.addPlayer(HeadE.HAPPY_TALKING, "Those junior White Knights? The ones selling the gnome crunchies?")
							.addNPC(familiar.getId(),HeadE.CAT_CALM_TALK2, "That was self defence.")
							.addPlayer(HeadE.HAPPY_TALKING, "No! No more hold-ups, stick-ups, thefts, or heists, you got that?"),
					new Dialogue()
							.addPlayer(HeadE.HAPPY_TALKING, "Say hello to my little friend!")
							.addNPC(familiar.getId(),HeadE.CAT_CALM_TALK2, "What?")
							.addPlayer(HeadE.HAPPY_TALKING, "My little friend: you ignored him last time you met him.")
							.addNPC(familiar.getId(),HeadE.CAT_CALM_TALK2, "So, who is your friend?")
							.addPlayer(HeadE.HAPPY_TALKING, "If I tell you, what is the point?"),
					new Dialogue()
							.addNPC(familiar.getId(),HeadE.CAT_CALM_TALK2, "Hey, boss, I've been thinking.")
							.addPlayer(HeadE.HAPPY_TALKING, "That's never a good sign.")
							.addNPC(familiar.getId(),HeadE.CAT_CALM_TALK2, "See, I heard about this railway...")
							.addPlayer(HeadE.HAPPY_TALKING, "We are not robbing it!")
							.addNPC(familiar.getId(),HeadE.CAT_CALM_TALK2, "I might not have wanted to suggest that, boss...")
							.addPlayer(HeadE.HAPPY_TALKING, "Then what were you going to suggest?")
							.addNPC(familiar.getId(),HeadE.CAT_CALM_TALK2, "That isn't important right now.")
							.addPlayer(HeadE.HAPPY_TALKING, "I thought as much."),
					new Dialogue()
							.addNPC(familiar.getId(),HeadE.CAT_CALM_TALK2, "Why do we never go to crossroads and rob travelers?")
							.addPlayer(HeadE.HAPPY_TALKING, "There are already highwaymen at the good spots.")
							.addNPC(familiar.getId(),HeadE.CAT_CALM_TALK2, "Maybe we need to think bigger.")
				);
			}
			case SPIRIT_TZ_KIH -> {
				if(!canTalk)
					new Dialogue().addNPC(familiar.getId(),HeadE.CAT_CALM_TALK2, "Chirp chirp!");
				if(player.containsOneItem(139, 141, 143, 2434)) {
					yield new Dialogue()
							.addNPC(familiar.getId(),HeadE.CAT_CALM_TALK2, "You drink pray, me drink pray.")
							.addPlayer(HeadE.HAPPY_TALKING, "What's that, Tz-Kih?")
							.addNPC(familiar.getId(),HeadE.CAT_CALM_TALK2, "You got pray pray pot. Tz-Kih drink pray pray you, you drink pray pray pot.")
							.addPlayer(HeadE.HAPPY_TALKING, "You want to drink my Prayer points?")
							.addNPC(familiar.getId(),HeadE.CAT_CALM_TALK2, "Yes. Pray pray.")
							.addPlayer(HeadE.HAPPY_TALKING, "Err, not right now, Tz-Kih. I, er, need them myself.")
							.addPlayer(HeadE.HAPPY_TALKING, "Sorry.")
							.addNPC(familiar.getId(),HeadE.CAT_CALM_TALK2, "But, pray praaaay...?");
				}
				yield random(
					new Dialogue()
							.addNPC(familiar.getId(),HeadE.CAT_CALM_TALK2, "How's it going, Tz-kih?")
							.addPlayer(HeadE.HAPPY_TALKING, "Pray pray?")
							.addNPC(familiar.getId(),HeadE.CAT_CALM_TALK2, "Don't start with all that again.")
							.addPlayer(HeadE.HAPPY_TALKING, "Hmph, silly JalYt."),
					new Dialogue()
							.addNPC(familiar.getId(),HeadE.CAT_CALM_TALK2, "Does JalYt think Tz-kih as strong as Jad Jad?")
							.addPlayer(HeadE.HAPPY_TALKING, "Are you as strong as TzTok-Jad? Yeah, sure, why not.")
							.addNPC(familiar.getId(),HeadE.CAT_CALM_TALK2, "Really? Thanks, JalYt. Tz-Kih strong and happy."),
					new Dialogue()
							.addNPC(familiar.getId(),HeadE.CAT_CALM_TALK2, "Have you heard of blood bat, JalYt?")
							.addPlayer(HeadE.HAPPY_TALKING, "Blood bats? You mean vampire bats?")
							.addNPC(familiar.getId(),HeadE.CAT_CALM_TALK2, "Yes. Blood bat.")
							.addPlayer(HeadE.HAPPY_TALKING, "Yes, I've heard of them. What about them?")
							.addNPC(familiar.getId(),HeadE.CAT_CALM_TALK2, "Tz-Kih like blood bat, but drink pray pray not blood blood. Blood blood is yuck.")
							.addPlayer(HeadE.HAPPY_TALKING, "Thanks, Tz-Kih, that's nice to know."),
					new Dialogue()
							.addNPC(familiar.getId(),HeadE.CAT_CALM_TALK2, "Pray pray pray pray pray pray pray pray!")
							.addPlayer(HeadE.HAPPY_TALKING, "Calm down, Tz-Kih, we'll find you something to drink soon.")
							.addNPC(familiar.getId(),HeadE.CAT_CALM_TALK2, "Pray praaaaaaaaaaaaaay!")
							.addPlayer(HeadE.HAPPY_TALKING, "Okay, okay. Calm down!")
				);
			}
			case ALBINO_RAT -> {
				if(!canTalk)
					yield new Dialogue().addNPC(familiar.getId(),HeadE.CAT_CALM_TALK2, "Reeeeee!");
				yield random(
					new Dialogue()
							.addNPC(familiar.getId(),HeadE.CAT_CALM_TALK2, "Hey boss, we going to do anything wicked today?")
							.addPlayer(HeadE.HAPPY_TALKING, "Well, I don't know why we would: I tend not to go around being wicked.")
							.addNPC(familiar.getId(),HeadE.CAT_CALM_TALK2, "Not even a little?")
							.addPlayer(HeadE.HAPPY_TALKING, "Well there was that one time... I'm sorry, no wickedness today.")
							.addNPC(familiar.getId(),HeadE.CAT_CALM_TALK2, "Awwwwww..."),
					new Dialogue()
							.addNPC(familiar.getId(),HeadE.CAT_CALM_TALK2, "Hey boss, can we go and loot something now?")
							.addPlayer(HeadE.HAPPY_TALKING, "Well, what did you have in mind?")
							.addNPC(familiar.getId(),HeadE.CAT_CALM_TALK2, "I dunno - where are we headed?")
							.addPlayer(HeadE.HAPPY_TALKING, "I hadn't decided yet.")
							.addNPC(familiar.getId(),HeadE.CAT_CALM_TALK2, "When we get there, let's loot something nearby!")
							.addPlayer(HeadE.HAPPY_TALKING, "Sounds like a plan, certainly."),
					new Dialogue()
							.addNPC(familiar.getId(),HeadE.CAT_CALM_TALK2, "So what we up to today, boss?")
							.addPlayer(HeadE.HAPPY_TALKING, "Oh I'm sure we'll find something to occupy our time.")
							.addNPC(familiar.getId(),HeadE.CAT_CALM_TALK2, "Let's go robbin' graves again!")
							.addPlayer(HeadE.HAPPY_TALKING, "What do you mean 'again'?")
							.addNPC(familiar.getId(),HeadE.CAT_CALM_TALK2, "Nuffin'..."),
					new Dialogue()
							.addNPC(familiar.getId(),HeadE.CAT_CALM_TALK2, "You know, boss, I don't think you're totally into this whole 'evil' thing.")
							.addPlayer(HeadE.HAPPY_TALKING, "I wonder what gave you that impression?")
							.addNPC(familiar.getId(),HeadE.CAT_CALM_TALK2, "Well, I worked with a lot of evil people; some of the best.")
							.addPlayer(HeadE.HAPPY_TALKING, "Such as?")
							.addNPC(familiar.getId(),HeadE.CAT_CALM_TALK2, "I'm not telling! I've got my principles to uphold.")
							.addPlayer(HeadE.HAPPY_TALKING, "There is honour amongst thieves, it would seem.")
				);
			}
			case SPIRIT_KALPHITE -> {
				int weaponID = player.getEquipment().getWeaponId();
				if(player.getInventory().containsOneItem(10581, 10582, 10583, 10584) || weaponID == 10581 || weaponID == 10582
						|| weaponID == 10583 || weaponID == 10584)
					yield new Dialogue().addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "How dare you!")
							.addPlayer(HeadE.HAPPY_TALKING, "How dare I what?")
							.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "That weapon offends us!")
							.addPlayer(HeadE.HAPPY_TALKING, "How dare you!")
							.addPlayer(HeadE.HAPPY_TALKING, "What weapon?")
							.addPlayer(HeadE.HAPPY_TALKING, "That weapon offends us!");
				yield random(
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "This activity is not optimal for us.")
								.addPlayer(HeadE.HAPPY_TALKING, "Well, you'll just have to put up with it for now.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "We would not have to 'put up' with this in the hive."),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "We are growing infuriated. What is our goal?")
								.addPlayer(HeadE.HAPPY_TALKING, "Well, I haven't quite decided yet.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "There is no indecision in the hive.")
								.addPlayer(HeadE.HAPPY_TALKING, "Or a sense of humour or patience, it seems."),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "We find this to be wasteful of our time.")
								.addPlayer(HeadE.HAPPY_TALKING, "Maybe I find you wasteful...")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "We would not face this form of abuse in the hive."),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "We grow tired of your antics, biped.")
								.addPlayer(HeadE.HAPPY_TALKING, "What antics? I'm just getting on with my day.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "In an inefficient way. In the hive, you would be replaced.")
								.addPlayer(HeadE.HAPPY_TALKING, "In the hive this, in the hive that...")
				);
			}
			case COMPOST_MOUND -> {
				if(!canTalk)
					yield new Dialogue().addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Mwap mwap!");
				yield random(
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Oi've gotta braand new comboine 'aarvester!")
								.addPlayer(HeadE.HAPPY_TALKING, "A what?")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Well, it's a flat bit a metal wi' a 'andle that I can use ta 'aarvest all combintions o' plaants.")
								.addPlayer(HeadE.HAPPY_TALKING, "You mean a spade?")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Aye, 'aat'll be it.")
						,
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "What we be doin' 'ere, zur?")
								.addPlayer(HeadE.HAPPY_TALKING, "Oh, I have a few things to take care of here, is all.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Aye, right ye are, zur. Oi'll be roight there.")
						,
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Errr...are ye gonna eat that?")
								.addPlayer(HeadE.HAPPY_TALKING, "Eat what?")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Y've got summat on yer, goin' wastin'.")
								.addPlayer(HeadE.HAPPY_TALKING, "Ewwww!")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "So ye don' want it then?")
								.addPlayer(HeadE.HAPPY_TALKING, "No I do not want it! Nor do I want to put my boot in your mouth for you to clean it off.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "An' why not?")
								.addPlayer(HeadE.HAPPY_TALKING, "It'll likely come out dirtier than when I put it in!")
						,
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Sigh...")
								.addPlayer(HeadE.HAPPY_TALKING, "What's the matter?")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Oi'm not 'appy carryin' round these young'uns where we're going.")
								.addPlayer(HeadE.HAPPY_TALKING, "Young'uns? Oh, the buckets of compost! Well, those wooden containers will keep them safe.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "'Aah, that be a mighty good point, zur.")
						,
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Oi wus just a-wonderin'...")
								.addPlayer(HeadE.HAPPY_TALKING, "Oh! What have you been eating! Your breath is making my eyes water!")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Oi! Oi'm 'urt by thaat.")
								.addPlayer(HeadE.HAPPY_TALKING, "Sorry.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Oi mean, oi even et some mints earlier.")
								.addPlayer(HeadE.HAPPY_TALKING, "You did?")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "'At's roight. Oi found some mint plaants in a big pile o' muck, and oi 'ad 'em fer me breakfast.")
								.addPlayer(HeadE.HAPPY_TALKING, "The mystery resolves itself.")
				);
			}
			case GIANT_CHINCHOMPA -> {
				if(!canTalk)
					yield new Dialogue().addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Reep reeeep!");
				if(player.getInventory().containsOneItem(9976, 9977, 10033, 10034))
					yield new Dialogue()
							.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Woah, woah, woah - hold up there.")
							.addPlayer(HeadE.HAPPY_TALKING, "What is it, ratty?")
							.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "You got something in your backpack that you'd like to tell me about?")
							.addPlayer(HeadE.HAPPY_TALKING, "I was wondering when you were going to bring up the chinchompa. I'm sure they like it in my inventory.")
							.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Did they not teach you anything in school? Chinchompas die in hot bags. You know what happens when chinchompas die. Are you attached to your back?")
							.addPlayer(HeadE.HAPPY_TALKING, "Medically, yes. And I kind of like it too. I get the point.");
				yield random(
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Half a pound of tuppenny rice, half a pound of treacle...")
								.addPlayer(HeadE.HAPPY_TALKING, "I hate it when you sing that song.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "...that's the way the money goes...")
								.addPlayer(HeadE.HAPPY_TALKING, "Couldn't you sing 'Kumbaya' or something?")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "...BANG, goes the chinchompa!")
								.addPlayer(HeadE.HAPPY_TALKING, "Sheesh.")
						,
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "What's small, brown and blows up?")
								.addPlayer(HeadE.HAPPY_TALKING, "A brown balloon?")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "A chinchompa! Pull my finger.")
								.addPlayer(HeadE.HAPPY_TALKING, "I'm not pulling your finger.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Nothing will happen. Truuuuust meeeeee.")
								.addPlayer(HeadE.HAPPY_TALKING, "Oh, go away."),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "I spy, with my little eye, something beginning with 'B'.")
								.addPlayer(HeadE.HAPPY_TALKING, "Bomb? Bang? Boom? Blowing-up-little-chipmunk?")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "No. Body odour. You should wash a bit more.")
								.addPlayer(HeadE.HAPPY_TALKING, "Well, that was pleasant. You don't smell all that great either, you know.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Stop talking, stop talking! Your breath stinks!")
								.addPlayer(HeadE.HAPPY_TALKING, "We're never going to get on, are we?"),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "I seem to have found a paper bag.")
								.addPlayer(HeadE.HAPPY_TALKING, "Well done. Anything in it?")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Hmmm. Let me see. It seems to be full of some highly sought after, very expensive...chinchompa breath!")
								.addPlayer(HeadE.HAPPY_TALKING, "No, don't pop it!")
								.addSimple("*BANG!!*")
								.addPlayer(HeadE.HAPPY_TALKING, "You just cannot help yourself, can you?")
				);
			}
			case VAMPYRE_BAT -> {
				if(!canTalk)
					yield new Dialogue().addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Ree! Ree! Reeeee!");
				//less than half hp is this dialogue
				if(player.getHitpoints() < (player.getMaxHitpoints()/2))
					yield new Dialogue()
							.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "You're vasting all that blood, can I have some?")
							.addPlayer(HeadE.HAPPY_TALKING, "No!");
				//darkness
				if((player.getControllerManager().getController() != null && player.getControllerManager().getController() instanceof UndergroundDungeonController))
					yield new Dialogue()
							.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Ze creatures ov ze dark; vat vonderful music zey make.")
							.addPlayer(HeadE.HAPPY_TALKING, "Riiight.")
							.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "I like it down here. Let's stay and eat moths!")
							.addPlayer(HeadE.HAPPY_TALKING, "I think I'll pass, thanks.");
				yield random(
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Ven are you going to feed me?")
								.addPlayer(HeadE.HAPPY_TALKING, "Well for a start, I'm not giving you any of my blood."),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "I want to eat something.")
								.addPlayer(HeadE.HAPPY_TALKING, "I'm sure you do; let's go see what we can find."),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Ven can I eat something?")
								.addPlayer(HeadE.HAPPY_TALKING, "Just as soon as I find something to attack.")
				);
			}
			case HONEY_BADGER -> {
				if(!canTalk)
					yield new Dialogue().addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Flop flop!");
				yield random(
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "*An outpouring of sanity-straining abuse*")
								.addPlayer(HeadE.HAPPY_TALKING, "Why do I talk to you again?"),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "*An outpouring of spittal-flecked insults.*")
								.addPlayer(HeadE.HAPPY_TALKING, "Why do I talk to you again?"),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "*A lambasting of visibly illustrated obscenities.*")
								.addPlayer(HeadE.HAPPY_TALKING, "Why do I talk to you again?"),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "*A tirade of biologically questionable threats*")
								.addPlayer(HeadE.HAPPY_TALKING, "Why do I talk to you again?"),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "*A stream of eye-watering crudities*")
								.addPlayer(HeadE.HAPPY_TALKING, "Why do I talk to you again?")
				);
			}
			case BEAVER -> {
				if(!canTalk)
					yield new Dialogue().addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Chip chip!");
				//logs in inventory
				if(player.getInventory().containsOneItem(1511, 1513, 1515, 1517, 1519, 1521, 2862, 3438, 3440, 3442, 3444, 3446, 3448, 4445, 5211, 5213, 6332, 6333))
					yield new Dialogue()
							.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "'Ere, you 'ave ze logs, now form zem into a mighty dam!")
							.addPlayer(HeadE.HAPPY_TALKING, "Well, I was thinking of burning, selling, or fletching them.")
							.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Sacre bleu! Such a waste.");
				yield random(
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Vot are you doing 'ere when we could be logging and building mighty dams, alors?")
								.addPlayer(HeadE.HAPPY_TALKING, "Why would I want to build a dam again?")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Why vouldn't you want to build a dam again?")
								.addPlayer(HeadE.HAPPY_TALKING, "I can't argue with that logic."),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Pardonnez-moi - you call yourself a lumberjack?")
								.addPlayer(HeadE.HAPPY_TALKING, "No")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Carry on zen."),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Paul Bunyan 'as nothing on moi!")
								.addPlayer(HeadE.HAPPY_TALKING, "Except several feet in height, a better beard, and opposable thumbs.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "What was zat?")
								.addPlayer(HeadE.HAPPY_TALKING, "Nothing."),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Zis is a fine day make some lumber.")
								.addPlayer(HeadE.HAPPY_TALKING, "That it is!")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "So why are you talking to moi? Get chopping!")
				);
			}
			case VOID_RAVAGER -> {
				if(!canTalk)
					yield new Dialogue().addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Raaaaa!");
				yield random(
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "You look delicious!")
								.addPlayer(HeadE.HAPPY_TALKING, "Don't make me dismiss you!"),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Take me to the rift!")
								.addPlayer(HeadE.HAPPY_TALKING, "I'm not taking you there! Goodness knows what you'd get up to.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "I promise not to destroy your world...")
								.addPlayer(HeadE.HAPPY_TALKING, "If only I could believe you..."),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Pardon me. Could I trouble you for a moment?")
								.addPlayer(HeadE.HAPPY_TALKING, "Yeah, sure.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Oh, it's just a trifling thing. Mmm, trifle...you look like trifle...So, will you help?")
								.addPlayer(HeadE.HAPPY_TALKING, "Pardon me. Could I trouble you for a moment?")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Yeah, sure.")
								.addPlayer(HeadE.HAPPY_TALKING, "Oh, just be honest. I just want a second opinion...Is this me? Mmm trifle...")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Pardon me. Could I trouble you for a moment?")
								.addPlayer(HeadE.HAPPY_TALKING, "Erm...why yes...of course. It definitely reflects the inner you.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Oh, just be honest. I just want a second opinion...Is this me? Mmm trifle..."),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "How do you bear life without ravaging?")
								.addPlayer(HeadE.HAPPY_TALKING, "It's not always easy.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "I could show you how to ravage, if you like...")
				);
			}
			case VOID_SPINNER -> {
				if(!canTalk)
					yield new Dialogue().addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Psssst!");
				//purple sweets
				if(player.getInventory().containsOneItem(10476, 4561))
					yield new Dialogue()
							.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "You have sweeties for spinner?")
							.addPlayer(HeadE.HAPPY_TALKING, "Sweeties? No sweeties here.")
							.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "You do! You do! Gimmie sweeties!")
							.addPlayer(HeadE.HAPPY_TALKING, "I don't have any sweeties!")
							.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "What you hiding in your backpack, then?")
							.addPlayer(HeadE.HAPPY_TALKING, "That? Oh, that's...erm...worms! Yes, worms. Purple worms.")
							.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Yucky!");
				yield random(
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Let's go play hide an' seek!")
								.addPlayer(HeadE.HAPPY_TALKING, "Okay, you hide and I'll come find you.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "You'll never find me!")
								.addPlayer(HeadE.HAPPY_TALKING, "What a disaster that would be..."),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "My mummy told me I was clever.")
								.addPlayer(HeadE.HAPPY_TALKING, "Aren't you meant to be the essence of a spinner? How do you have a mother?")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "What you mean, 'essence'?")
								.addPlayer(HeadE.HAPPY_TALKING, "Never mind, I don't think it matters.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "My logimical powers has proved me smarterer than you!"),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "I'm coming to tickle you!")
								.addPlayer(HeadE.HAPPY_TALKING, "No! You've got so many tentacles!")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "I'm coming to tickle you!")
								.addPlayer(HeadE.HAPPY_TALKING, "Aieee!"),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Where's the sweeties?")
								.addPlayer(HeadE.HAPPY_TALKING, "They are wherever good spinners go.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Yay for me!")
				);
			}
			case VOID_TORCHER -> {
				if(!canTalk)
					yield new Dialogue().addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Reeeeee!");
				yield random(
						new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "You okay there, spinner?")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "I not spinner!")
								.addPlayer(HeadE.HAPPY_TALKING, "Sorry, splatter?")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "I not splatter either!")
								.addPlayer(HeadE.HAPPY_TALKING, "No, wait, I meant defiler.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "I torcher!")
								.addPlayer(HeadE.HAPPY_TALKING, "Hehe, I know. I was just messing with you.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Grr. Don't be such a pest."),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "'T' is for torcher, that's good enough for me... 'T' is for torcher, I'm happy you can see.")
								.addPlayer(HeadE.HAPPY_TALKING, "You're just a bit weird, aren't you?"),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Burn, baby, burn! Torcher inferno!")
								.addPlayer(HeadE.HAPPY_TALKING, "*Wibble*"),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "So hungry... must devour...")
								.addPlayer(HeadE.HAPPY_TALKING, "*Gulp* Er, yeah, I'll find you something to eat in a minute.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Is flesh-bag scared of torcher?")
								.addPlayer(HeadE.HAPPY_TALKING, "No, no. I, er, always look like this... honest.")
				);
			}
			case VOID_SHIFTER -> {
				if(!canTalk)
					yield new Dialogue().addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Screee!");
				yield random(
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "What a splendid day, " + player.getPronoun("sir!", "madam!"))
								.addPlayer(HeadE.HAPPY_TALKING, "Yes, it is!")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "It could only be marginally improved, perhaps, by tea and biscuits.")
								.addPlayer(HeadE.HAPPY_TALKING, "What a marvellous idea!"),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "I'm sorry to bother you, but could you assist me briefly?")
								.addPlayer(HeadE.HAPPY_TALKING, "I suppose so.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "I was wondering, briefly, if perchance you might care to dance?")
								.addPlayer(HeadE.HAPPY_TALKING, "Dance? With a pest?")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Well, you see, I'm dreadfully out of practice and now I can barely leap, let alone teleport.")
								.addPlayer(HeadE.HAPPY_TALKING, "I'm not going to help you remember how to destroy the world!")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "What a beastly world we live in where one gentleman/lady will not aid a pest in need..."),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "How do you do?")
								.addPlayer(HeadE.HAPPY_TALKING, "Okay, I suppose.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Marvellous, simply marvellous!"),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Lets go and see to those cads and bounders!")
								.addPlayer(HeadE.HAPPY_TALKING, "Which 'cads and bounders' did you mean, exactly?")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Why, the ones with no honour, of course.")
								.addPlayer(HeadE.HAPPY_TALKING, "I don't think he knows what pests do...")
				);
			}
			case BRONZE_MINOTAUR, IRON_MINOTAUR, STEEL_MINOTAUR, MITHRIL_MINOTAUR, ADAMANT_MINOTAUR, RUNE_MINOTAUR -> {
				if(!canTalk)
					yield new Dialogue().addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Rawr!");
				String hat = new Item(player.getEquipment().getHatId(), 1).getName().toLowerCase();
				if(hat.contains("guthan") || hat.contains("bandos helmet") || hat.contains("berserker") || hat.contains("archer")
						|| hat.contains("dragon med helm"))
					yield new Dialogue()
							.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "...")
							.addPlayer(HeadE.HAPPY_TALKING, "What?")
							.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Are you having a laugh?")
							.addPlayer(HeadE.HAPPY_TALKING, "I'm not sure I know what you-")
							.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Listen, no-horns, you have two choices: take off the horns yourself or I'll headbutt you until they fall off.")
							.addPlayer(HeadE.HAPPY_TALKING, "Yessir.")
							.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Good, no-horns. Let's not have this conversation again.");
				yield random(
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "All this walking about is making me angry.")
								.addPlayer(HeadE.HAPPY_TALKING, "You seem to be quite happy about that.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Yeah! There's nothing like getting a good rage on and then working it out on some no-horns.")
								.addPlayer(HeadE.HAPPY_TALKING, "I can't say I know what you mean.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Well I didn't think a no-horns like you would get it!"),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Can you tell me why we're not fighting yet?")
								.addPlayer(HeadE.HAPPY_TALKING, "Buck up; I'll find you something to hit soon.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "You'd better, no-horns, because that round head of yours is looking mighty axeable."),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Hey, no-horns?")
								.addPlayer(HeadE.HAPPY_TALKING, "Why do you keep calling me no-horns?")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Do I really have to explain that?")
								.addPlayer(HeadE.HAPPY_TALKING, "No, thinking about it, it's pretty self-evident.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Glad we're on the same page, no-horns.")
								.addPlayer(HeadE.HAPPY_TALKING, "So, what did you want?")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "I've forgotten, now. I'm sure it'll come to me later."),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Hey no-horns!")
								.addPlayer(HeadE.HAPPY_TALKING, "Yes?")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Oh, I don't have anything to say, I was just yelling at you.")
								.addPlayer(HeadE.HAPPY_TALKING, "Why?")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "No reason. I do like to mess with the no-horns, though.")
				);
			}
			case BULL_ANT -> {
				if(!canTalk)
					yield new Dialogue().addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Snip snip!");
				if(player.getRunEnergy() < 12.0)
					yield new Dialogue()
							.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "What's the matter, Private? Not enjoying the run?")
							.addPlayer(HeadE.HAPPY_TALKING, "Sir...wheeze...yes Sir!")
							.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Not enjoying the run? You need more training biped?")
							.addPlayer(HeadE.HAPPY_TALKING, "Sir, no Sir! Sir, I'm enjoying the run a great deal, Sir!")
							.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Then hop to, Private!")
							.addPlayer(HeadE.HAPPY_TALKING, "Sir, yes Sir!");
				yield random(
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "All right you worthless biped, fall in!")
								.addPlayer(HeadE.HAPPY_TALKING, "Sir, yes Sir!")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "We're going to work you so hard your boots fall off, understood?")
								.addPlayer(HeadE.HAPPY_TALKING, "Sir, yes Sir!")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Carry on Private!"),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Aten...hut!")
								.addPlayer(HeadE.HAPPY_TALKING, "Sir, Private Player reporting for immediate active duty, Sir!")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "As you were, Private!"),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "I can't believe they stuck me with you...")
								.addPlayer(HeadE.HAPPY_TALKING, "Buck up, Sir, it's not that bad.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Stow that, Private, and get back to work!")
								.addPlayer(HeadE.HAPPY_TALKING, "Sir, yes Sir!"),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "What in the name of all the layers of the abyss do you think you're doing, biped?")
								.addPlayer(HeadE.HAPPY_TALKING, "Sir, nothing Sir!")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Well double-time it, Private, whatever it is!")
								.addPlayer(HeadE.HAPPY_TALKING, "Sir, yes Sir!")
				);
			}
			case MACAW -> {
				if(!canTalk)
					yield new Dialogue().addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Ca-caw! Ca-caw!");
				yield random(
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Awk! Gimme the rum! Gimme the rum!")
								.addPlayer(HeadE.HAPPY_TALKING, "I don't think you'll like the stuff. Besides, I think there is a law about feeding birds alcohol."),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Awk! I'm a pirate! Awk! Yo, ho ho!")
								.addPlayer(HeadE.HAPPY_TALKING, "I'd best not keep you around any customs officers!"),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Awk! Caw! Shiver me timbers!")
								.addPlayer(HeadE.HAPPY_TALKING, "I wonder where you picked up all these phrases?")
				);
			}
			case EVIL_TURNIP -> {
				if(!canTalk)
					yield new Dialogue().addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Yeeeeee!");
				yield random(
						new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "So, how are you feeling?")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "My roots feel hurty. I thinking it be someone I eated.")
								.addPlayer(HeadE.HAPPY_TALKING, "You mean someTHING you ate?")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Hur hur hur. Yah, sure, why not."),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Hur hur hur...")
								.addPlayer(HeadE.HAPPY_TALKING, "Well, as sinister as it's chuckling is, at least it's happy. That's a good thing, right?"),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "When we gonna fighting things, boss?")
								.addPlayer(HeadE.HAPPY_TALKING, "Soon enough.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Hur hur hur. I gets the fighting."),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "I are turnip hear me roar! I too deadly to ignore.")
								.addPlayer(HeadE.HAPPY_TALKING, "I'm glad it's on my side... and not behind me.")
				);
			}
			case SPIRIT_COCKATRICE, SPIRIT_GUTHATRICE, SPIRIT_SARATRICE, SPIRIT_ZAMATRICE, SPIRIT_PENGATRICE, SPIRIT_CORAXATRICE, SPIRIT_VULATRICE -> {
				if(!canTalk)
					yield new Dialogue().addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Cock-ledoodledoooo!");
				if(player.getEquipment().getShieldId() == 4156)
					yield new Dialogue()
							.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "You know, I'm sensing some trust issues here.")
							.addPlayer(HeadE.HAPPY_TALKING, "I'm not sure I know what you are talking about.")
							.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "What are you holding?")
							.addPlayer(HeadE.HAPPY_TALKING, "A mirror shield.")
							.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "And what do those do?")
							.addPlayer(HeadE.HAPPY_TALKING, "Mumblemumble...")
							.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "What was that?")
							.addPlayer(HeadE.HAPPY_TALKING, "It protects me from your gaze attack.")
							.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "See! Why would you need one unless you didn't trust me?")
							.addPlayer(HeadE.HAPPY_TALKING, "Who keeps demanding that we stop and have staring contests?")
							.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "How about we drop this and call it even?")
							.addPlayer(HeadE.HAPPY_TALKING, "Fine by me.");
				yield random(
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Is this what you do for fun?")
								.addPlayer(HeadE.HAPPY_TALKING, "Sometimes. Why, what do you do for fun?")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "I find things and glare at them until they die!")
								.addPlayer(HeadE.HAPPY_TALKING, "Well...everyone needs a hobby, I suppose."),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "You know, I think I might train as a hypnotist.")
								.addPlayer(HeadE.HAPPY_TALKING, "Isn't that an odd profession for a cockatrice?")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Not at all! I've already been practicing!")
								.addPlayer(HeadE.HAPPY_TALKING, "Oh, really? How is that going?")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Not good. I tell them to look in my eyes and that they are feeling sleepy.")
								.addPlayer(HeadE.HAPPY_TALKING, "I think I can see where this is headed.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "And then they just lie there and stop moving.")
								.addPlayer(HeadE.HAPPY_TALKING, "I hate being right sometimes."),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Come on, lets have a staring contest!")
								.addPlayer(HeadE.HAPPY_TALKING, "You win!")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Yay! I win again!")
								.addPlayer(HeadE.HAPPY_TALKING, "Oh, it's no contest alright."),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "You know, sometimes I don't think we're good friends.")
								.addPlayer(HeadE.HAPPY_TALKING, "What do you mean?")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Well, you never make eye contact with me for a start.")
								.addPlayer(HeadE.HAPPY_TALKING, "What happened the last time someone made eye contact with you?")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Oh, I petrified them really good! Ooooh...okay, point taken.")
								.addPlayer(HeadE.HAPPY_TALKING, "I'm glad we had this chat.")
				);
			}
			case PYRELORD -> {
				if(!canTalk)
					yield new Dialogue().addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Buuuuurrrrrr!");
				yield random(
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "What are we doing here?")
								.addPlayer(HeadE.HAPPY_TALKING, "Whatever I feel like doing.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "I was summoned by a greater demon once you know.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "He said we'd see the world...")
								.addPlayer(HeadE.HAPPY_TALKING, "What happened?")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "He was slain; it was hilarious!"),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "I used to be feared across five planes...")
								.addPlayer(HeadE.HAPPY_TALKING, "Oh dear, now you're going to be sad all day!")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "At least I won't be the only one."),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "I could teach you to smite your enemies with flames.")
								.addPlayer(HeadE.HAPPY_TALKING, "You're not the only one: we have runes to do that.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Runes? Oh, that's so cute!")
								.addPlayer(HeadE.HAPPY_TALKING, "Cute?")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Well, not cute so much as tragic. I could teach you to do it without runes.")
								.addPlayer(HeadE.HAPPY_TALKING, "Really?")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "No."),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Have you never been on fire?")
								.addPlayer(HeadE.HAPPY_TALKING, "You say that like it's a bad thing.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Isn't it? It gives me the heebie-jeebies!")
								.addPlayer(HeadE.HAPPY_TALKING, "You're afraid of something?")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Yes: I'm afraid of being you.")
								.addPlayer(HeadE.HAPPY_TALKING, "I don't think he likes me...")
				);
			}
			case MAGPIE -> {
				if(!canTalk)
					yield new Dialogue().addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Breeeee! Breeeeee!");
				yield random(
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "There's nowt gannin on here...")
								.addPlayer(HeadE.HAPPY_TALKING, "Err...sure? Maybe?")
								.addPlayer(HeadE.HAPPY_TALKING, "It seems upset, but what is it saying?"),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Howway, let's gaan see what's happenin' in toon.")
								.addPlayer(HeadE.HAPPY_TALKING, "What? I can't understand what you're saying."),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Are we gaan oot soon? I'm up fer a good walk me.")
								.addPlayer(HeadE.HAPPY_TALKING, "That...that was just noise. What does that mean?"),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Ye' been plowdin' i' the claarts aall day.")
								.addPlayer(HeadE.HAPPY_TALKING, "What? That made no sense.")
				);
			}
			case BLOATED_LEECH -> {
				if(!canTalk)
					yield new Dialogue().addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Sssssssst-t-t-t!");
				yield random(
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "I'm afraid it's going to have to come off, " + player.getDisplayName() + ".")
								.addPlayer(HeadE.HAPPY_TALKING, "What is?")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Never mind. Trust me, I'm almost a doctor.")
								.addPlayer(HeadE.HAPPY_TALKING, "I think I'll get a second opinion."),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "You're in a critical condition.")
								.addPlayer(HeadE.HAPPY_TALKING, "Is it terminal?")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Not yet. Let me get a better look and I'll see what I can do about it.")
								.addPlayer(HeadE.HAPPY_TALKING, "There are two ways to take that...and I think I'll err on the side of caution."),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Let's get a look at that brain of yours.")
								.addPlayer(HeadE.HAPPY_TALKING, "What? My brains stay inside my head, thanks.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "That's ok, I can just drill a hole.")
								.addPlayer(HeadE.HAPPY_TALKING, "How about you don't and pretend you did?"),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "I think we're going to need to operate.")
								.addPlayer(HeadE.HAPPY_TALKING, "I think we can skip that for now.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Who's the doctor here?")
								.addPlayer(HeadE.HAPPY_TALKING, "Not you.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "I may not be a doctor, but I'm keen. Does that not count?")
								.addPlayer(HeadE.HAPPY_TALKING, "In most other fields, yes; in medicine, no.")
				);
			}
//			case SPIRIT_TERRORBIRD -> {
//				if(!canTalk)
//					new Dialogue().addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Reeeeee!");
//
//			}
			default -> new Dialogue().addSimple("This familiar can't talk yet.");
		};
	}

	
	private static void addExtraOps(Player player, Options ops, Familiar familiar) {
		switch(familiar.getPouch()) {
		case LAVA_TITAN:
			ops.add("Teleport to Lava Maze", new Dialogue().addOptions("Are you sure you want to teleport here? It's very high wilderness.", yesNo -> {
				yesNo.add("Yes. I'm sure.", () -> Magic.sendNormalTeleportSpell(player, new WorldTile(3030, 3838, 0)));
				yesNo.add("Nevermind. That sounds dangerous.");
			}));
			break;
		default:
			break;
		}
	}
	
	private static Dialogue random(Dialogue... options) {
		return options[Utils.random(options.length)];
	}
}
