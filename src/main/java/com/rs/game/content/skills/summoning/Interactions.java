package com.rs.game.content.skills.summoning;

import com.rs.game.content.dialogue.Dialogue;
import com.rs.game.content.dialogue.HeadE;
import com.rs.game.content.dialogue.Options;
import com.rs.game.content.quests.Quest;
import com.rs.game.content.skills.firemaking.Firemaking;
import com.rs.game.content.skills.firemaking.Firemaking.Fire;
import com.rs.game.content.skills.magic.Magic;
import com.rs.game.content.world.areas.dungeons.UndergroundDungeonController;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.Skills;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ItemOnNPCEvent;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.handlers.ItemOnNPCHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class Interactions {
	
	//spotanim 1575 mining boost
	//TODO 8320 8321 anim interaction to search for fruit fruit bat
	
	public static NPCClickHandler handleInteract = new NPCClickHandler(Pouch.getAllNPCIdKeys(), new String[] { "Interact" }) {
		@Override
		public void handle(NPCClickEvent e) {
			if (!(e.getNPC() instanceof Familiar familiar))
				return;
			if (familiar.getOwner() != e.getPlayer()) {
				e.getPlayer().sendMessage("This isn't your familiar");
				return;
			}
			familiar.interact();
		}
	};
	
	public static ItemOnNPCHandler pyrelordFire = new ItemOnNPCHandler(Pouch.PYRELORD.getIdKeys()) {
		@Override
		public void handle(ItemOnNPCEvent e) {
			Fire fire = Fire.forId(e.getItem().getId());
			if (fire == null) {
				e.getPlayer().sendMessage("The pyrelord only burns logs.");
				return;
			}
			if (e.getPlayer().getSkills().getLevel(Skills.FIREMAKING) < fire.getLevel()) {
				e.getPlayer().sendMessage("You need " + fire.getLevel() + " firemaking to burn this log.");
				return;
			}
			e.getNPC().getActionManager().setAction(new Firemaking(fire));
		}
	};
	
	public static Dialogue getTalkToDialogue(Player player, Familiar familiar) {
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
			case SPIRIT_TERRORBIRD -> {
				if(!canTalk)
					new Dialogue().addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Breeeeee!");
				int itemsInBird = familiar.getInventory().getUsedSlots();
				if(itemsInBird <= 8) {
					yield new Dialogue()
							.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "This is a fun little walk.")
							.addPlayer(HeadE.HAPPY_TALKING, "Why do I get the feeling you'll change your tune when I start loading you up with items?");
				}
				if(itemsInBird == 9) {
					yield new Dialogue()
							.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "I can keep this up for hours.")
							.addPlayer(HeadE.HAPPY_TALKING, "I'm glad, as we still have plenty of time to go.");
				}
				if(itemsInBird == 10) {
					yield new Dialogue()
							.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Are we going to visit a bank soon?")
							.addPlayer(HeadE.HAPPY_TALKING, "I'm not sure, you still have plenty of room for more stuff.")
							.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Just don't leave it too long, okay?");
				}
				if(itemsInBird == 11) {
					yield new Dialogue()
							.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Can we go to a bank now?")
							.addPlayer(HeadE.HAPPY_TALKING, "Just give me a little longer, okay?")
							.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "That's what you said last time!")
							.addPlayer(HeadE.HAPPY_TALKING, "Did I?")
							.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Yes!")
							.addPlayer(HeadE.HAPPY_TALKING, "Well, I mean it this time, promise.");
				}
				yield new Dialogue()
						.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "So...heavy...")
						.addPlayer(HeadE.HAPPY_TALKING, "I knew you'd change your tune once you started carrying things.")
						.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Can we go bank this stuff now?")
						.addPlayer(HeadE.HAPPY_TALKING, "Sure. You do look like you're about to collapse.");

			}
			case ABYSSAL_PARASITE -> {
				if(!canTalk)
					yield new Dialogue()
							.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Reeeeeee!")
							.addPlayer(HeadE.SECRETIVE, "What?")
							.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Reeeeeee!");
				yield random(
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Ongk n'hd?")
								.addPlayer(HeadE.HAPPY_TALKING, "Oh, I'm not feeling so well.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Uge f't es?")
								.addPlayer(HeadE.HAPPY_TALKING, "Please have mercy!")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "F'tp ohl't?")
								.addPlayer(HeadE.HAPPY_TALKING, "I shouldn't have eaten that kebab. Please stop talking!"),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Noslr'rh...")
								.addPlayer(HeadE.HAPPY_TALKING, "What's the matter?")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Kdso Seo...")
								.addPlayer(HeadE.HAPPY_TALKING, "Could you...could you mime what the problem is?")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Yiao itl!")
								.addPlayer(HeadE.HAPPY_TALKING, "I want to help it but, aside from the language gap its noises make me retch!"),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Ace'e e ur'y!")
								.addPlayer(HeadE.HAPPY_TALKING, "I think I'm going to be sick... The noises! Oh, the terrifying noises."),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Tdsa tukk!")
								.addPlayer(HeadE.HAPPY_TALKING, "Oh, the noises again.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Hem s'htee?")
								.addPlayer(HeadE.HAPPY_TALKING, "Please, just stop talking!")
				);
			}
			case SPIRIT_JELLY -> {
				if(!canTalk)
					yield new Dialogue().addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Bloop!");
				yield random(
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Play play play play!")
								.addPlayer(HeadE.HAPPY_TALKING, "The only game I have time to play is the 'Staying Very Still' game.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "But that game is soooooo booooooring...")
								.addPlayer(HeadE.HAPPY_TALKING, "How about we use the extra house rule, that makes it the 'Staying Very Still and Very Quiet' game.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Happy happy! I love new games!"),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "It's playtime now!")
								.addPlayer(HeadE.HAPPY_TALKING, "Okay, how about we play the 'Staying Very Still' game.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "But that game is booooooring...")
								.addPlayer(HeadE.HAPPY_TALKING, "If you win then you can pick the next game, how about that?")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Happy happy!"),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Can we go over there now, please please please pleeeeeease?")
								.addPlayer(HeadE.HAPPY_TALKING, "Go over where?")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "I dunno, someplace fun, please please please!")
								.addPlayer(HeadE.HAPPY_TALKING, "Okay, but first, let's play the 'Sitting Very Still' game.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "But that game is booooooring...")
								.addPlayer(HeadE.HAPPY_TALKING, "Well, if you win we can go somewhere else, okay?")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Happy happy!"),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "What game are we playing now?")
								.addPlayer(HeadE.HAPPY_TALKING, "It's called the 'Staying Very Still' game.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "This game is booooooring...")
								.addPlayer(HeadE.HAPPY_TALKING, "Hey, all that moping doesn't look very still to me.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "I never win at this game...")
								.addPlayer(HeadE.HAPPY_TALKING, "You know what? I think I'll not count it this one time")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Happy happy! You're the best friend ever!")
				);
			}
			case IBIS -> {
				if(!canTalk)
					yield new Dialogue().addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Reeeeep!");
				if(player.getInventory().getAmountOf(383) >= 2)
					yield new Dialogue()
							.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Can I look after those sharks for you?")
							.addPlayer(HeadE.HAPPY_TALKING, "I don't know. Would you eat them?")
							.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Yes! Ooops...")
							.addPlayer(HeadE.HAPPY_TALKING, "I think I'll hang onto them myself for now.");
				yield random(
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "I'm the best fisherman ever!")
								.addPlayer(HeadE.HAPPY_TALKING, "Where is your skillcape to prove it, then?")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "At home...")
								.addPlayer(HeadE.HAPPY_TALKING, "I'll bet it is."),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "I'll bet it is.")
								.addPlayer(HeadE.HAPPY_TALKING, "I like to fish!"),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "I'll bet it is.")
								.addPlayer(HeadE.HAPPY_TALKING, "I like to fish!"),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Hey, where are we?")
								.addPlayer(HeadE.HAPPY_TALKING, "What do you mean?")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "I just noticed we weren't fishing.")
								.addPlayer(HeadE.HAPPY_TALKING, "Well, we can't fish all the time.")
				);
			}
			case SPIRIT_KYATT -> {
				if(!canTalk)
					yield new Dialogue().addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Rawr!");
				if(player.getInventory().containsOneItem(1759, 15416))
					yield new Dialogue()
							.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Human, hand me that ball of wool.")
							.addPlayer(HeadE.HAPPY_TALKING, "Aww...do you want to play with it?")
							.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "I do not 'play', human.")
							.addPlayer(HeadE.HAPPY_TALKING, "If you say so, kitty! Alright, you can have it.")
							.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Aha! Ball of wool: you are mine now. I will destroy you!")
							.addPlayer(HeadE.HAPPY_TALKING, "Well I'm not giving it to you, now! I'll never get it back.")
							.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Then you leave me no choice but to destroy YOU, human!")
							.addPlayer(HeadE.HAPPY_TALKING, "Bad kitty!");
				yield random(
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Guess who wants a belly rub, human.")
								.addPlayer(HeadE.HAPPY_TALKING, "Umm...is it me?")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "No, human, it is not you. Guess again.")
								.addPlayer(HeadE.HAPPY_TALKING, "Is it the Duke of Lumbridge?")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "You try my patience, human!")
								.addPlayer(HeadE.HAPPY_TALKING, "Is it Zamorak? That would explain why he's so cranky.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Please do not make me destroy you before I get my belly rub!"),
						new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "Here, kitty!")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "What do you want, human?")
								.addPlayer(HeadE.HAPPY_TALKING, "I just thought I would see how you were.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "I do not have time for your distractions. Leave me be!")
								.addPlayer(HeadE.HAPPY_TALKING, "Well, sorry! Would a ball of wool cheer you up?")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "How dare you insult my intelli- what colour wool?")
								.addPlayer(HeadE.HAPPY_TALKING, "Umm...white?")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "I will end you!"),
						new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "Hello, kitty cat!")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Human, leave me be. I'm far too busy to deal with your nonsense.")
								.addPlayer(HeadE.HAPPY_TALKING, "What are you up to?")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "I am engaged in an intricate dirt-purging operation!")
								.addPlayer(HeadE.HAPPY_TALKING, "Aww, kitty's cleaning his paws! How cute!")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Know this, human. Once I finish cleaning my paws...")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "I will destroy you!"),
						new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "Here, kitty!")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Do not toy with me, human!")
								.addPlayer(HeadE.HAPPY_TALKING, "What about under your chin?")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "I am not one of your playful kittens, human. I eat playful kittens for breakfast!")
								.addPlayer(HeadE.HAPPY_TALKING, "Not even behind your ears?")
								.addSimple("You lean down and tickle the kyatt behind the ears.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "I will...purrrrr...ooh that's quite nice...destroy...purrrrrrr...you.")
				);
			}
			case SPIRIT_LARUPIA -> {
				if(!canTalk)
					yield new Dialogue().addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Rawr!");
				yield random(
						new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "Kitty cat!")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "What is your wish master?")
								.addPlayer(HeadE.HAPPY_TALKING, "Have you ever thought about doing something other than hunting and serving me?")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "You mean, like stand-up comedy, master?")
								.addPlayer(HeadE.HAPPY_TALKING, "Umm...yes, like that.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "No, master."),
						new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "Hello friend!")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "'Friend', master? I do not understand this word.")
								.addPlayer(HeadE.HAPPY_TALKING, "Friends are people, or animals, who like one another. I think we are friends.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Ah, I think I understand friends, master.")
								.addPlayer(HeadE.HAPPY_TALKING, "Great!")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "A friend is someone who looks tasty, but you don't eat.")
								.addPlayer(HeadE.SCARED, "!"),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "What are we doing today, master?")
								.addPlayer(HeadE.HAPPY_TALKING, "I don't know, what do you want to do?")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "I desire only to hunt and to serve my master.")
								.addPlayer(HeadE.HAPPY_TALKING, "Err...great! I guess I'll decide then."),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Master, do you ever worry that I might eat you?")
								.addPlayer(HeadE.HAPPY_TALKING, "No, of course not! We're pals.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "That is good, master.")
								.addPlayer(HeadE.HAPPY_TALKING, "Should I?")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Of course not, master.")
								.addPlayer(HeadE.HAPPY_TALKING, "Oh. Good.")
				);
			}
			case SPIRIT_GRAAHK -> {
				if(!canTalk)
					yield new Dialogue().addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Grrrrr...");
				yield random(
						new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "Your spikes are looking particularly spiky today.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Really, you think so?")
								.addPlayer(HeadE.HAPPY_TALKING, "Yes. Most pointy, indeed.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "That's really kind of you to say. I was going to spike you but I won't now...")
								.addPlayer(HeadE.HAPPY_TALKING, "Thanks?")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "...I'll do it later instead.")
								.addPlayer(HeadE.HAPPY_TALKING, "*sigh!*"),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "My spikes hurt, could you pet them for me?")
								.addPlayer(HeadE.HAPPY_TALKING, "Aww, of course I can I'll just... Oww! I think you drew blood that time."),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Hi!")
								.addPlayer(HeadE.HAPPY_TALKING, "Hello. Are you going to spike me again?")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "No, I got a present to apologise for last time.")
								.addPlayer(HeadE.HAPPY_TALKING, "That's really sweet, thank you.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Here you go, it's a special cushion to make you comfortable.")
								.addPlayer(HeadE.HAPPY_TALKING, "It's made of spikes!")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Yes, but they're therapeutic spikes.")
								.addPlayer(HeadE.HAPPY_TALKING, "..."),
						new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "How's your day going?")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "It's great! Actually I've got something to show you!")
								.addPlayer(HeadE.HAPPY_TALKING, "Oh? What's that?")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "You'll need to get closer!")
								.addPlayer(HeadE.HAPPY_TALKING, "I can't see anything...")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "It's really small - even closer.")
								.addPlayer(HeadE.HAPPY_TALKING, "Oww! I'm going to have your spikes trimmed!")
				);
			}
			case KARAMTHULU_OVERLORD -> {
				if(!canTalk)
					yield new Dialogue().addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Bloooooob.... *burp");
				yield random(
						new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "Do you want-")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Silence!")
								.addPlayer(HeadE.FRUSTRATED, "But I only...")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Silence!")
								.addPlayer(HeadE.FRUSTRATED, "Now, listen here...")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "SIIIIIILLLLLEEEEENCE!")
								.addPlayer(HeadE.FRUSTRATED, "Fine!")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Good!")
								.addPlayer(HeadE.FRUSTRATED, "Maybe I'll be so silent you'll think I never existed")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Oh, how I long for that day..."),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Kneel before my awesome might!")
								.addPlayer(HeadE.HAPPY_TALKING, "I would, but I have a bad knee you see...")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Your feeble prattlings matter not, air-breather! Kneel or face my wrath!")
								.addPlayer(HeadE.HAPPY_TALKING, "I'm not afraid of you. You're only a squid in a bowl!")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Only? I, radiant in my awesomeness, am 'only' a squid in a bowl? Clearly you need to be shown in your place, lung-user!")
								.addSimple("*The Karamthulhu overlord narrows its eye and you find yourself unable to breathe!")
								.addPlayer(HeadE.SCARED, "Gaak! Wheeeze!")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Who rules?")
								.addPlayer(HeadE.SCARED, "You rule!")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "And don't forget it!"),
						new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "...")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "The answer 'be silent'!")
								.addPlayer(HeadE.HAPPY_TALKING, "You have no idea what I was going to ask you.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Yes I do; I know all!")
								.addPlayer(HeadE.HAPPY_TALKING, "Then you will not be surprised to know I was going to ask you what you wanted to do today.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "You dare doubt me!")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "The answer 'be silent' because your puny compressed brain could not even begin to comprehend my needs!")
								.addPlayer(HeadE.HAPPY_TALKING, "Well, how about I dismiss you so you can go and do what you like?")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Well, how about I topple your nations into the ocean and dance my tentacle-waving victory dance upon your watery graves?")
								.addPlayer(HeadE.HAPPY_TALKING, "Yeah...well...")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Silence! Your burbling vexes me greatly!"),
						new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "Errr...Have you calmed down yet?")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Calmed down? Why would I need to calm down?")
								.addPlayer(HeadE.HAPPY_TALKING, "Well there is that whole 'god complex' thing...")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Complex? What 'complex' are you drooling about this time, minion?")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "It is a sad thing indeed when a god as powerful as I cannot gain recognition from the foolish mewling sheep of this 'surface' place.")
								.addPlayer(HeadE.SECRETIVE, "I don't really think sheep really make mewling noises...")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Silence!")
				);
			}
			case SMOKE_DEVIL -> {
				if(!canTalk)
					yield new Dialogue().addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Roooo.... rooooo!");
				yield random(
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "When are you going to be done with that?")
								.addPlayer(HeadE.HAPPY_TALKING, "Soon, I hope.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Good, because this place is too breezy.")
								.addPlayer(HeadE.HAPPY_TALKING, "What do you mean?")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "I mean, it's tricky to keep hovering in this draft.")
								.addPlayer(HeadE.HAPPY_TALKING, "Ok, we'll move around a little if you like.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Yes please!"),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Hey!")
								.addPlayer(HeadE.HAPPY_TALKING, "Yes?")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Where are we going again?")
								.addPlayer(HeadE.HAPPY_TALKING, "Well, I have a lot of things to do today, so we might go a lot of places.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Are we there yet?")
								.addPlayer(HeadE.HAPPY_TALKING, "No, not yet.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "How about now?")
								.addPlayer(HeadE.FRUSTRATED, "No.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Are we still not there?")
								.addPlayer(HeadE.ANGRY, "NO!")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Okay, just checking."),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Ah, this is the life!")
								.addPlayer(HeadE.HAPPY_TALKING, "Having a good time up there?")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Yeah! It's great to feel the wind in your tentacles.")
								.addPlayer(HeadE.HAPPY_TALKING, "Sadly, I don't know what that feels like.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Why not?")
								.addPlayer(HeadE.HAPPY_TALKING, "No tentacles for a start.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Well, nobody's perfect."),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Why is it always so cold here?")
								.addPlayer(HeadE.HAPPY_TALKING, "I don't think it's that cold.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "It is compared to back home.")
								.addPlayer(HeadE.HAPPY_TALKING, "How hot is it where you are from?")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "I can never remember. What is the vaporisation point of steel again?")
								.addPlayer(HeadE.HAPPY_TALKING, "Pretty high.")
								.addPlayer(HeadE.HAPPY_TALKING, "No wonder you feel cold here...")
				);
			}
			case ABYSSAL_LURKER -> {
				if(!canTalk)
					yield new Dialogue().addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Reeeee!");
				yield random(
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Djrej gf'ig sgshe...")
								.addPlayer(HeadE.HAPPY_TALKING, "What? Are we in danger, or something?"),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "To poshi v'kaa!")
								.addPlayer(HeadE.HAPPY_TALKING, "What? Is that even a language?"),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "G-harrve shelmie?")
								.addPlayer(HeadE.HAPPY_TALKING, "What? Do you want something?"),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Jehifk i'ekfh skjd.")
								.addPlayer(HeadE.HAPPY_TALKING, "What? Is there somebody down an old well, or something?")
				);
			}
			case SPIRIT_COBRA -> {
				if(!canTalk)
					yield new Dialogue().addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Sssssst!");
				int ringID = player.getEquipment().getRingId();
				if(ringID == 4202 || ringID == 6465 || ringID == 15016)
					yield new Dialogue()
							.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "You are under my power!")
							.addPlayer(HeadE.HAPPY_TALKING, "No, you are under my power!")
							.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "No, you are under my power!")
							.addPlayer(HeadE.HAPPY_TALKING, "No, my power is greater!")
							.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Your power is the greater...")
							.addPlayer(HeadE.HAPPY_TALKING, "Your powers are no match for mine!")
							.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "You are convinced you have won this argument...")
							.addPlayer(HeadE.HAPPY_TALKING, "I won the argument...yay!")
							.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "*Manic serpentine laughter*");
				yield random(
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Do we have to do thissss right now?")
								.addPlayer(HeadE.HAPPY_TALKING, "Yes, I'm afraid so.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "You are under my sssspell...")
								.addPlayer(HeadE.HAPPY_TALKING, "I will do as you ask...")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Do we have to do thissss right now?")
								.addPlayer(HeadE.HAPPY_TALKING, "Not at all, I had just finished!"),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "You are feeling ssssleepy...")
								.addPlayer(HeadE.HAPPY_TALKING, "I am feeling sssso ssssleepy...")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "You will bring me lotssss of sssstuff!")
								.addPlayer(HeadE.HAPPY_TALKING, "What ssssort of sssstuff?")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "What ssssort of sssstuff have you got?")
								.addPlayer(HeadE.HAPPY_TALKING, "All kindsss of sssstuff.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Then just keep bringing sssstuff until I'm ssssatissssfied!"),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "I'm bored, do ssssomething to entertain me...")
								.addPlayer(HeadE.HAPPY_TALKING, "Errr, I'm not here to entertain you, you know.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "You will do as I assssk...")
								.addPlayer(HeadE.HAPPY_TALKING, "Your will is my command...")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "I'm bored, do ssssomething to entertain me...")
								.addPlayer(HeadE.HAPPY_TALKING, "I'll dance for you!", ()->{
									player.setNextAnimation(new Animation(866));
								}),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "I am king of the world!")
								.addPlayer(HeadE.HAPPY_TALKING, "You know, I think there is a law against snakes being the king.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "My will is your command...")
								.addPlayer(HeadE.HAPPY_TALKING, "I am yours to command...")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "I am king of the world!")
								.addPlayer(HeadE.HAPPY_TALKING, "All hail King Serpentor!")
				);
			}
			case STRANGER_PLANT -> {
				if(!canTalk)
					yield new Dialogue().addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "...");
				yield random(
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "I'M STRANGER PLANT!")
								.addPlayer(HeadE.HAPPY_TALKING, "I know you are.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "I KNOW! I'M JUST SAYING!")
								.addPlayer(HeadE.HAPPY_TALKING, "Do you have to shout like that all of the time?")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "WHO'S SHOUTING?")
								.addPlayer(HeadE.HAPPY_TALKING, "If this is you speaking normally, I'd hate to hear you shouting.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "OH, SNAP!"),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "WILL WE HAVE TO BE HERE LONG?")
								.addPlayer(HeadE.HAPPY_TALKING, "We'll be here until I am finished.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "BUT THERE'S NO DRAMA HERE!")
								.addPlayer(HeadE.HAPPY_TALKING, "Well, how about you pretend to be an undercover agent.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "WONDERFUL! WHAT'S MY MOTIVATION?")
								.addPlayer(HeadE.HAPPY_TALKING, "You're trying to remain stealthy and secretive, while looking out for clues.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "I'LL JUST GET INTO CHARACTER! AHEM!")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "PAPER! PAPER! VARROCK HERALD FOR SALE!")
								.addPlayer(HeadE.HAPPY_TALKING, "What kind of spy yells loudly like that?")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "ONE WHOSE COVER IDENTITY IS A PAPER-SELLER, OF COURSE!")
								.addPlayer(HeadE.HAPPY_TALKING, "Ask a silly question..."),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "DIIIIVE!")
								.addPlayer(HeadE.HAPPY_TALKING, "What? Help! Why dive?")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "OH, DON'T WORRY! I JUST LIKE TO YELL THAT FROM TIME TO TIME!")
								.addPlayer(HeadE.HAPPY_TALKING, "Well, can you give me a little warning next time?")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "WHAT, AND TAKE ALL THE FUN OUT OF LIFE?")
								.addPlayer(HeadE.HAPPY_TALKING, "If by 'fun' you mean 'sudden heart attacks', then yes, please take them out of my life!"),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "I THINK I'M WILTING!")
								.addPlayer(HeadE.HAPPY_TALKING, "Do you need some water?")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "DON'T BE SILLY! I CAN PULL THAT OUT OF THE GROUND!")
								.addPlayer(HeadE.HAPPY_TALKING, "Then why are you wilting?")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "IT'S SIMPLE: THERE'S A DISTINCT LACK OF DRAMA!")
								.addPlayer(HeadE.HAPPY_TALKING, "Drama?")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "YES, DRAMA!")
								.addPlayer(HeadE.HAPPY_TALKING, "Okay...")
								.addPlayer(HeadE.HAPPY_TALKING, "Let's see if we can find some for you.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "LEAD ON!")
				);
			}
			case BARKER_TOAD -> {
				if(!canTalk)
					yield new Dialogue().addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Riiibit!");
				if(player.getInventory().containsOneItem(2150))
					yield new Dialogue()
							.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Bwaaarp graaaawk? (What's that croaking in your inventory?)")
							.addPlayer(HeadE.HAPPY_TALKING, "Ah, you mean that toad?")
							.addPlayer(HeadE.HAPPY_TALKING, "Oh, I'm guessing you're not going to like me carrying a toad about.")
							.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Craaawk, croak. (I might not be all that happy, no.)")
							.addPlayer(HeadE.HAPPY_TALKING, "I'm not going to eat it.")
							.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Craaaaawk braaap croak. (Weeeeell, I'd hope not! Reminds me of my mama toad. She was inflated and fed to a jubbly, you know. A sad, demeaning way to die.)");
				yield random(
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Ladies and gentlemen, for my next trick, I shall swallow this fly!")
								.addPlayer(HeadE.HAPPY_TALKING, "Seen it.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Ah, but last time was the frog...on fire?")
								.addPlayer(HeadE.HAPPY_TALKING, "No! That would be a good trick.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Well, it won't be this time either.")
								.addPlayer(HeadE.HAPPY_TALKING, "Awwwww..."),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Roll up, roll up, roll up! See the greatest show on Gielinor!")
								.addPlayer(HeadE.HAPPY_TALKING, "Where?")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Well, it's kind of...you.")
								.addPlayer(HeadE.HAPPY_TALKING, "Me?")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Roll up, roll up, roll up! See the greatest freakshow on Gielinor!")
								.addPlayer(HeadE.HAPPY_TALKING, "Don't make me smack you, slimy."),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "We need to set up the big top somewhere near here. The locals look friendly enough.")
								.addPlayer(HeadE.HAPPY_TALKING, "Are you kidding?")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Your problem is that you never see opportunities."),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Braaaaaaaaaaaaaaaaaaaaaaap! (*Burp!*)")
								.addPlayer(HeadE.HAPPY_TALKING, "That's disgusting behaviour!")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Braap craaaaawk craaaawk. (That, my dear boy, was my world-renowned belching.)")
								.addPlayer(HeadE.HAPPY_TALKING, "I got that part. Why are you so happy about it?")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Braaaaaaap craaaaaawk craaaaaaaawk. (My displays have bedazzled the crowned heads of Gielinor.)")
								.addPlayer(HeadE.HAPPY_TALKING, "I'd give you a standing ovation, but I have my hands full.")
				);
			}
			case WAR_TORTOISE -> {
				if(!canTalk)
					yield new Dialogue().addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Roop!");
				yield random(
						new Dialogue()
								.addSimple("*The tortoise waggles its head about*")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "What are we doing in this dump?")
								.addPlayer(HeadE.HAPPY_TALKING, "Well, I was just going to take care of a few things.")
								.addSimple("*The tortoise shakes its head*")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "I don't believe it. Stuck here with this young whippersnapper running around having fun.")
								.addPlayer(HeadE.HAPPY_TALKING, "You know, I'm sure you would enjoy it if you gave it a chance.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Oh, you would say that, wouldn't you?"),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Hold up a minute, there.")
								.addPlayer(HeadE.HAPPY_TALKING, "What do you want?")
								.addSimple("*The tortoise bobs its head*")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "For you to slow down!")
								.addPlayer(HeadE.HAPPY_TALKING, "Well, I've stopped now.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Yes, but you'll soon start up again, won't you?")
								.addPlayer(HeadE.HAPPY_TALKING, "Probably.")
								.addSimple("* The tortoise waggles its head despondently.*")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, " I don't believe it...."),
						new Dialogue()
								.addSimple("* The tortoise bobs its head around energetically.*")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Oh, so now you're paying attention to me, are you?")
								.addPlayer(HeadE.HAPPY_TALKING, "I pay you plenty of attention!")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Only when you want me to carry those heavy things of yours.")
								.addPlayer(HeadE.HAPPY_TALKING, "I don't ask you to carry anything heavy.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "What about those lead ingots?")
								.addPlayer(HeadE.HAPPY_TALKING, "What lead ingots?")
								.addSimple("*The tortoise droops its head*")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Well, that's what it felt like....")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "*grumble grumble*"),
						new Dialogue()
								.addSimple("*The tortoise exudes an air of reproach*")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Are you going to keep rushing around all day?")
								.addPlayer(HeadE.HAPPY_TALKING, "Only for as long as I have the energy to.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Oh. I'm glad that my not being able to keep up with you brings you such great amusement.")
								.addPlayer(HeadE.HAPPY_TALKING, "I didn't mean it like that.")
								.addSimple("*The tortoise waggles its head disapprovingly.*")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Well, when you are QUITE finished laughing at my expense, how about you pick up a rock larger than your body and go crawling about with it?")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "We'll see how energetic you are after an hour or two of that.")
				);
			}
			case BUNYIP -> {
				if(!canTalk)
					yield new Dialogue();
				//has fish
				if(player.getInventory().containsOneItem(13435, 317, 321, 327, 338, 345, 335, 331, 349, 359, 371, 377, 353, 341, 363, 11328, 2148, 11330, 11332, 7944, 383, 15264, 15270)) {
					yield new Dialogue()
							.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "I see you've got some fish there, mate.")
							.addPlayer(HeadE.HAPPY_TALKING, "Yeah, but I might cook them up before I give them to you!")
							.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Humans...always ruining good fishes.")
							.addPlayer(HeadE.HAPPY_TALKING, "You know, some people prefer them cooked.")
							.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Yeah. We call 'em freaks.");
				}
				yield random(
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Where are we going and why is it not to the beach?")
								.addPlayer(HeadE.HAPPY_TALKING, "Well, we have a fair few places to go, but I suppose we could go to the beach if we get time.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Bonza! I'll get my board ready!")
								.addPlayer(HeadE.HAPPY_TALKING, "Well, even if we do go to the beach I don't know if we'll have time for that.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Awww, that's a drag..."),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Hey Bruce, can we go down to the beach t'day?")
								.addPlayer(HeadE.HAPPY_TALKING, "Well, I have a lot of things to do today but maybe later.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Bonza!"),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Pass me another bunch of shrimps, mate!")
								.addPlayer(HeadE.HAPPY_TALKING, "I don't know if I want any more water runes.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Righty, but I do know that I want some shrimps!")
								.addPlayer(HeadE.HAPPY_TALKING, "A fair point."),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Sigh...")
								.addPlayer(HeadE.HAPPY_TALKING, "What's the matter?")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "I'm dryin' out in this sun, mate.")
								.addPlayer(HeadE.HAPPY_TALKING, "Well, what can I do to help?")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Well, fish oil is bonza for the skin, ya know.")
								.addPlayer(HeadE.HAPPY_TALKING, "Oh, right, I think I see where this is going.")
				);
			}
			case FRUIT_BAT -> {
				if(!canTalk)
					yield new Dialogue().addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Squeeeeeek!");
				if(player.getInventory().getAmountOf(5972) > 3)
					yield new Dialogue()
							.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Squeek squeek-a-squeek squeeek? (Can I have a papaya?)")
							.addPlayer(HeadE.HAPPY_TALKING, "No, I have a very specific plan for them.")
							.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Squeek? (What?)")
							.addPlayer(HeadE.HAPPY_TALKING, "I was just going to grate it over some other vegetables and eat it. Yum.");
				yield random(
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Squeek-a-squeek squeek? (How much longer do you want me for?)")
								.addPlayer(HeadE.HAPPY_TALKING, "I don't really know at the moment, it all depends what I want to do today."),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Squeak squeek-a-squeak. (This place is fun!)")
								.addPlayer(HeadE.HAPPY_TALKING, "Glad you think so!"),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Squeek squeek squeek-a-squeek? (Where are we going?)")
								.addPlayer(HeadE.HAPPY_TALKING, "Oh, we're likely to go to a lot of places today."),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Squeek squeek-a-squeek squeek? (Can you smell lemons?)")
								.addPlayer(HeadE.HAPPY_TALKING, "No, why do you ask?")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Squeak-a-squeak squeek. (Must just be thinking about them.)")
				);
			}
			case RAVENOUS_LOCUST -> {
				if(!canTalk)
					yield new Dialogue().addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Whiiiiiiiiiine!");
				yield random(
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Click whiiine whiiiiine click click? (Hey, man, can you spare some lentils?)")
								.addPlayer(HeadE.HAPPY_TALKING, "What would you want with lentils?")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Whiiiiiinewhiiiiiiine click whiiiiiiiine. (I was going to make a casserole.)")
								.addPlayer(HeadE.HAPPY_TALKING, "How? You don't have a fire, pans or thumbs.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Whiiiiiiiiiiiiine! (Stop hassling me, man.)"),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Whiiiiiine click click! (Man, it's a totally groovy day.)")
								.addPlayer(HeadE.HAPPY_TALKING, "That it is.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Whiiiiine whiiiiine whinewhiiiine. (Now, if only I wasn't being held down by 'The Man'.)")
								.addPlayer(HeadE.HAPPY_TALKING, "Which man?")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Clickclack whiiiiiine whiiiiinewhiiine. ('The Man'; the one that keeps harshing my mellow.)")
								.addPlayer(HeadE.HAPPY_TALKING, "'Harshing your mellow'? Okay, I don't want to know any more."),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Whiiiiiine... (Siiiiigh...)")
								.addPlayer(HeadE.HAPPY_TALKING, "What's the matter?")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Whiiiine whiiiineclickwhiiiiine whine... (I was just thinking about how meat is murder...)")
								.addPlayer(HeadE.HAPPY_TALKING, "But it isn't. Killing someone is murder.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Click click! (Good point.)"),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Whiiiiine whinewhiiiine? (Man, how about time?)")
								.addPlayer(HeadE.HAPPY_TALKING, "I think it's about midday.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Clickwhiiiiine whiiiiiiiiiiiiine... (No, man. Isn't time, like, massive?)")
								.addPlayer(HeadE.HAPPY_TALKING, "I don't think an abstract concept can have mass...")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Whineclick click! (Oh, man, that's heavy.)")
				);
			}
			case ARCTIC_BEAR -> {
				if(!canTalk)
					yield new Dialogue().addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Rawr!");
				yield random(
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Crikey! We’re tracking ourselves a real live one here. I call ’em ‘Brighteyes’.")
								.addPlayer(HeadE.FRUSTRATED, "Will you stop stalking me like that?")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Lookit that! Something’s riled this one up good and proper.")
								.addPlayer(HeadE.HAPPY_TALKING, "Who are you talking to anyway?")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Looks like I’ve been spotted.")
								.addPlayer(HeadE.HAPPY_TALKING, "Did you think you didn’t stand out here or something?"),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Crikey! Something seems to have startled Brighteyes, here.")
								.addPlayer(HeadE.HAPPY_TALKING, "What? What’s happening?")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Maybe " + player.getPronoun("he's", "she's") + " scented a rival.")
								.addPlayer(HeadE.HAPPY_TALKING, "I smell something, but it’s not a rival."),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "We’re tracking Brighteyes here as " + player.getPronoun("he", "she") + " goes about " + player.getPronoun("his", "her") + " daily routine.")
								.addPlayer(HeadE.HAPPY_TALKING, "My name is " + player.getDisplayName() + ", not Brighteyes!")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Looks like the little critter’s upset about something.")
								.addPlayer(HeadE.HAPPY_TALKING, "I wonder if " + player.getPronoun("he", "she") + "’d be quiet if I just did really boring stuff."),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "These little guys get riled up real easy.")
								.addPlayer(HeadE.HAPPY_TALKING, "Who wouldn’t be upset with a huge bear tracking along behind them, commenting on everything they do?")
				);
			}
			case PHOENIX -> {
				if(!canTalk)
					yield new Dialogue().addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Skreee!");
				yield random(
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Skreee skree skrooo skrooooouuu. (I want to burn something.)")
								.addPlayer(HeadE.HAPPY_TALKING, "Why are you looking at me like that?")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Skeeeeooouoou! Skree skrooo, skrooouuee skreee! (Please! It won't hurt that much, and I'll bring you back straight away!)")
								.addPlayer(HeadE.HAPPY_TALKING, "Maybe later. Much later. When I'm dead from natural causes already. And medicine has failed to bring me back.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Skreee skreeeooouu skroou! (I'll hold you to it!)"),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "May I ask you a question?")
								.addPlayer(HeadE.HAPPY_TALKING, "Skreeoooouuu, skreeee skreeeeoooo. (Yes, but you have already asked me a question.)")
								.addPlayer(HeadE.HAPPY_TALKING, "Skreeeooo, skreee skreeeeee skreeoooo. (You should have said 'May I ask you two questions?'.)")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Erm, may I ask you two questions?")
								.addPlayer(HeadE.HAPPY_TALKING, "Skroo. (No.)")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "..."),
						new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "May I ask you... TWO questions?")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Skree ree ree! Skree, skreee skrooou skreeeoou. (Heh heh heh. The answer to your first is yes. You may ask your second.)")
								.addPlayer(HeadE.HAPPY_TALKING, "What was RuneScape like in the distant past?")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Skreee skreeeeout skreeou. Skreee skree. (It was like it is now, only younger.)")
								.addPlayer(HeadE.HAPPY_TALKING, "...")
								.addPlayer(HeadE.HAPPY_TALKING, "You, madam, are the most pestiferous poultry I have ever met.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Skree ree ree! (Heh heh heh!)"),
						new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "Skreeee, skree skrooo. Skrooooou skreee!")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Skreee skroooue, skreeee skreeeeeeeou. (Either you need to practice your phoenixspeak, or I should burn you where you stand.)")
								.addPlayer(HeadE.HAPPY_TALKING, "So that didn't mean 'How are you feeling today?'")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Skroo. Skroo, skreee skreou. (No, it didn't.")
				);
			}
			case OBSIDIAN_GOLEM -> {
				if(!canTalk)
					yield new Dialogue().addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Brrrr!");
				int capeID = player.getEquipment().getCapeId();
				if(player.getInventory().containsOneItem(6570, 23659) || capeID == 6570 || capeID == 23659)
					yield new Dialogue()
							.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Truly, you are a powerful warrior, Master!")
							.addPlayer(HeadE.HAPPY_TALKING, "I'm pleased you think so.")
							.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "It is my duty to respect you, Master.")
							.addPlayer(HeadE.HAPPY_TALKING, "Oh, So you're just saying that to make me happy...")
							.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "I obey all orders, Master.");
				yield random(
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Let us go forth and prove our strength, Master!")
								.addPlayer(HeadE.HAPPY_TALKING, "Where would you like to prove it?")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "The caves of the TzHaar are filled with monsters for us to defeat, Master! TzTok-Jad shall quake in his slippers!")
								.addPlayer(HeadE.HAPPY_TALKING, "Have you ever met TzTok-Jad?")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Alas, Master, I have not. No Master has ever taken me to see him."),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "How many foes have you defeated, Master?")
								.addPlayer(HeadE.HAPPY_TALKING, "Quite a few, I should think.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Was your first foe as mighty as the volcano, Master?")
								.addPlayer(HeadE.HAPPY_TALKING, "Um, not quite.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "I am sure it must have been a deadly opponent, Master!")
								.addPlayer(HeadE.HAPPY_TALKING, "*Cough* It might have been a chicken. *Cough*"),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Master! We are truly a mighty duo!")
								.addPlayer(HeadE.HAPPY_TALKING, "Do you think so?")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Of course, Master! I am programmed to believe so.")
								.addPlayer(HeadE.HAPPY_TALKING, "Do you do anything you're not programmed to?")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "No, Master.")
								.addPlayer(HeadE.HAPPY_TALKING, "I guess that makes things simple for you..."),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Do you ever doubt your programming, Master?")
								.addPlayer(HeadE.HAPPY_TALKING, "I don't have programming. I can think about whatever I like.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "What do you think about, Master?")
								.addPlayer(HeadE.HAPPY_TALKING, "Oh, simple things: the sound of one hand clapping, where the gods come from...Simple things.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Paradox check = positive. Error. Reboot.")
				);
			}
			case GRANITE_LOBSTER -> {
				if(!canTalk)
					yield new Dialogue().addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "CLomp! Clap Clamp!");
				if(player.isQuestComplete(Quest.FREMENNIK_TRIALS))
					yield random(
							new Dialogue()
									.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Ho, my Fremennik brother, shall we go raiding?")
									.addPlayer(HeadE.HAPPY_TALKING, "Well, I suppose we could when I'm done with this.")
									.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Yes! To the looting and the plunder!"),
							new Dialogue()
									.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "We shall heap the helmets of the fallen into a mountain!")
									.addPlayer(HeadE.HAPPY_TALKING, "The outerlanders have insulted our heritage for the last time!")
									.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "The longhall will resound with our celebration!")
					);
				yield new Dialogue()
						.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Clonkclonk clonk grind clonk. (Keep walking, outerlander. We have nothing to discuss.)")
						.addPlayer(HeadE.HAPPY_TALKING, "Fair enough.")
						.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Clonkclonkclonk grind clonk grind? (It's nothing personal, you're just an outerlander, you know?)");
			}
			case PRAYING_MANTIS -> {
				if(!canTalk)
					yield new Dialogue().addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Clatter click chitter!");
				if(player.getInventory().containsOneItem(10010) || player.getEquipment().getWeaponId() == 10010)
					yield new Dialogue()
							.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Clatter click chitter click? (Wouldn't you learn focus better if you used chopsticks?)")
							.addPlayer(HeadE.HAPPY_TALKING, "Huh?")
							.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Clicker chirrpchirrup. (For catching the butterflies, grasshopper.)")
							.addPlayer(HeadE.HAPPY_TALKING, "Oh, right! Well, if I use anything but the net I squash them.")
							.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Chirrupchirrup click! (Then, I could have them!)");
				yield random(
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Chitter chirrup chirrup? (Have you been following your training, grasshopper?)")
								.addPlayer(HeadE.HAPPY_TALKING, "Yes, almost every day.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Chirrupchirrup chirrup. ('Almost' is not good enough.)")
								.addPlayer(HeadE.HAPPY_TALKING, "Well, I'm trying as hard as I can.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Chirrup chitter chitter chirrup? (How do you expect to achieve enlightenment at this rate, grasshopper?)")
								.addPlayer(HeadE.HAPPY_TALKING, "Spontaneously."),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Chitterchitter chirrup clatter. (Today, grasshopper, I will teach you to walk on rice paper.)")
								.addPlayer(HeadE.HAPPY_TALKING, "What if I can't find any?")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Clatter chitter click chitter... (Then we will wander about and punch monsters in the head...)")
								.addPlayer(HeadE.HAPPY_TALKING, "I could do in an enlightened way if you want?")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Chirrupchitter! (That will do!)"),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Clatter chirrup chirp chirrup clatter clatter. (A wise man once said; 'Feed your mantis and it will be happy'.)")
								.addPlayer(HeadE.HAPPY_TALKING, "Is there any point to that saying?")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Clatter chirrupchirrup chirp. (I find that a happy mantis is its own point.)"),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Clatter chirrupchirp- (Today, grasshopper, we will-)")
								.addPlayer(HeadE.HAPPY_TALKING, "You know, I'd rather you call me something other than grasshopper.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Clitterchirp? (Is there a reason for this?)")
								.addPlayer(HeadE.HAPPY_TALKING, "You drool when you say it.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Clickclatter! Chirrup chirpchirp click chitter... (I do not! Why would I drool when I cann you a juicy...)")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "...clickclick chitter clickchitter click... (...succulent, nourishing, crunchy...)")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "*Drooool*")
								.addPlayer(HeadE.HAPPY_TALKING, "You're doing it again!")
				);
			}
			case FORGE_REGENT -> {
				if(!canTalk)
					yield new Dialogue().addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Crackley spit crack sizzle...");
				yield random(
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Crackley spit crack sizzle? (Can we go Smithing?)")
								.addPlayer(HeadE.HAPPY_TALKING, "Maybe.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Hiss? (Can we go smelt something?)")
								.addPlayer(HeadE.HAPPY_TALKING, "Maybe.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Flicker crackle sizzle? (Can we go mine something to smelt?)")
								.addPlayer(HeadE.HAPPY_TALKING, "Maybe.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Sizzle flicker! (Yay! I like doing that!)")
								.addPlayer(HeadE.HAPPY_TALKING, "..."),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Hiss. (I'm happy.)")
								.addPlayer(HeadE.HAPPY_TALKING, "Good.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Crackle. (Now I'm sad.)")
								.addPlayer(HeadE.HAPPY_TALKING, "Oh dear, why?")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Hiss-hiss. (Happy again.)")
								.addPlayer(HeadE.HAPPY_TALKING, "Glad to hear it.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Crackley-crick. (Sad now.)")
								.addPlayer(HeadE.HAPPY_TALKING, "Um.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Hiss. (Happy.)")
								.addPlayer(HeadE.HAPPY_TALKING, "Right...")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Crackle. (Sad.)")
								.addPlayer(HeadE.HAPPY_TALKING, "You're very strange.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Sizzle hiss? (What makes you say that?)")
								.addPlayer(HeadE.HAPPY_TALKING, "Oh...nothing in particular."),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Sizzle! (I like logs.)")
								.addPlayer(HeadE.HAPPY_TALKING, "They are useful for making planks.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Sizzley crack hiss spit. (No, I just like walking on them. They burst into flames.)")
								.addPlayer(HeadE.HAPPY_TALKING, "It's a good job I can use you as a firelighter really!"),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Sizzle... (I'm bored.)")
								.addPlayer(HeadE.HAPPY_TALKING, "Are you not enjoying what we're doing?")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Crackley crickle sizzle. (Oh yes, but I'm still bored.)")
								.addPlayer(HeadE.HAPPY_TALKING, "Oh, I see.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Sizzle hiss? (What's that over there?)")
								.addPlayer(HeadE.HAPPY_TALKING, "I don't know. Should we go and look?")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Hiss crackle spit sizzle crack? (Nah, that's old news - I'm bored of it now.)")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Sizzle hiss? (What's that over there?)")
								.addPlayer(HeadE.HAPPY_TALKING, "But...wha...where now?")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Sizzle crack crickle. (Oh no matter, it no longer interests me.)")
								.addPlayer(HeadE.HAPPY_TALKING, "You're hard work.")
				);
			}
			case TALON_BEAST -> {
				if(!canTalk)
					yield new Dialogue().addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Screeee!");
				yield random(
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Is this all you apes do all day, then?")
								.addPlayer(HeadE.HAPPY_TALKING, "Well, we do a lot of other things, too.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "That’s dull. Lets go find something and bite it.")
								.addPlayer(HeadE.HAPPY_TALKING, "I wouldn’t want to spoil my dinner.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "So, I have to watch you trudge about again? Talk about boring."),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "This place smells odd…")
								.addPlayer(HeadE.HAPPY_TALKING, "Odd?")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Yes, not enough is rotting…")
								.addPlayer(HeadE.HAPPY_TALKING, "For which I am extremely grateful."),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Hey!")
								.addPlayer(HeadE.HAPPY_TALKING, "Aaaargh!")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Why d’you always do that?")
								.addPlayer(HeadE.HAPPY_TALKING, "I don’t think I’ll ever get used to having a huge, ravenous feline sneaking around behind me all the time.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "That’s okay, I doubt I’ll get used to following an edible, furless monkey prancing in front of me all the time either."),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "C’mon! Lets go fight stuff!")
								.addPlayer(HeadE.HAPPY_TALKING, "What sort of stuff?")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "I dunno? Giants, monsters, vaguely-defined philosophical concepts. You know: stuff.")
								.addPlayer(HeadE.HAPPY_TALKING, "How are we supposed to fight a philosophical concept?")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "With subtle arguments and pointy sticks!")
								.addPlayer(HeadE.HAPPY_TALKING, "Well, I can see you’re going to go far in debates.")
				);
			}
			case GIANT_ENT -> {
				if(!canTalk)
					yield new Dialogue().addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Creeeeeeeek....");
				if(player.getTempAttribs().getI("ent_fam_dial") == 0) {
					player.getTempAttribs().setI("ent_fam_dial", 1);
					yield new Dialogue()
							.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Creeeeeeeeeeeak..... (I.....)")
							.addPlayer(HeadE.HAPPY_TALKING, "Yes?")
							.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, ".....")
							.addSimple("After a while you realise that the ent has finished speaking for the moment.");
				}
				if(player.getTempAttribs().getI("ent_fam_dial") == 1) {
					player.getTempAttribs().setI("ent_fam_dial", 2);
					yield new Dialogue()
							.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Creak..... Creaaaaaaaaak..... (Am.....)")
							.addPlayer(HeadE.HAPPY_TALKING, "Yes?")
							.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, ".....")
							.addSimple("After a while you realise that the ent has finished speaking for the moment.");
				}
				if(player.getTempAttribs().getI("ent_fam_dial") == 2) {
					player.getTempAttribs().setI("ent_fam_dial", 3);
					yield new Dialogue()
							.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Grooooooooan..... (Feeling.....)")
							.addPlayer(HeadE.HAPPY_TALKING, "Yes? We almost have a full sentence now - the suspense is killing me!")
							.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, ".....")
							.addSimple("After a while you realise that the ent has finished speaking for the moment.");
				}
				player.getTempAttribs().setI("ent_fam_dial", 0);
				yield random(
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Groooooooooan..... (Sleepy.....)")
								.addPlayer(HeadE.HAPPY_TALKING, "I'm not sure if that was worth all the waiting."),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Grooooooan.....creeeeeeeak (Restful.....)")
								.addPlayer(HeadE.HAPPY_TALKING, "I'm not sure if that was worth all the waiting."),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Grrrrooooooooooooooan..... (Achey.....)")
								.addPlayer(HeadE.HAPPY_TALKING, "I'm not sure if that was worth all the waiting."),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Creeeeeeeegroooooooan..... (Goood.....)")
								.addPlayer(HeadE.HAPPY_TALKING, "I'm not sure if that was worth all the waiting."),
						new Dialogue()
							.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Creeeeeeeeeeeeeaaaaaak..... (Tired.....)")
							.addPlayer(HeadE.HAPPY_TALKING, "I'm not sure if that was worth all the waiting.")
				);
			}
			case HYDRA -> {
				if(!canTalk)
					yield new Dialogue().addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Raaaasp!");
				yield random(
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Raaaspraaasp? (Isn't it hard to get things done with just one head?)")
								.addPlayer(HeadE.HAPPY_TALKING, "Not really!")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Raaasp raaaaap raaaasp? (Well I suppose you work with what you got, right?")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Raaaaaasp raaaasp raaaasp. (At least he doesn't have someone whittering in their ear all the time.)")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Raaaaaaasp! (Quiet, you!)"),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Raaaasp raaaasp! (Man, I feel good!)")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Raaasp ssssss raaaasp. (That's easy for you to say.)")
								.addPlayer(HeadE.HAPPY_TALKING, "What's up?")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Raaa.... (well...)")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Raaaaasp sss rassssp. (Don't pay any attention, they are just feeling whiny.)")
								.addPlayer(HeadE.HAPPY_TALKING, "But they're you, aren't they?")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Raaaasp raasp rasssp! (Don't remind me!)"),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Rassssp rasssssp! (You know, two heads are better than one!)")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Raaaasp rassssp sssssp.... (Unless you're the one doing all the heavy thinking....)")
								.addPlayer(HeadE.HAPPY_TALKING, "I think I'll stick to one for now, thanks."),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Raaaaaaasp. (Siiiigh.)")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Raasp raasp raaaaasp? (What's up this time?)")
								.addPlayer(HeadE.HAPPY_TALKING, "Can I help?")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Rasssp ssssssp? raaaaasp raaaasp. (Do you mind? This is a private conversation.)")
								.addPlayer(HeadE.HAPPY_TALKING, "Well, excu-u-use me.")
				);
			}
			case SPIRIT_DAGANNOTH -> {
				if(!canTalk)
					yield new Dialogue().addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Grooooowl");
				yield random(
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Grooooooowl graaaaawl raaaawl? (Are you ready to surrender to the power of the Deep Waters?)")
								.addPlayer(HeadE.HAPPY_TALKING, "Err, not really.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Rooooowl? (How about now?)")
								.addPlayer(HeadE.HAPPY_TALKING, "No, sorry.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Rooooowl? (How about now?)")
								.addPlayer(HeadE.HAPPY_TALKING, "No, sorry. You might want to try again a little later."),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Groooooowl. Hsssssssssssssss! (The Deeps will swallow the lands. None will stand before us!)")
								.addPlayer(HeadE.HAPPY_TALKING, "What if we build boats?")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Hsssssssss groooooowl? Hssssshsss grrooooooowl? (What are boats? The tasty wooden containers full of meat?)")
								.addPlayer(HeadE.HAPPY_TALKING, "I suppose they could be described as such, yes."),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Hssssss graaaawl grooooowl, growwwwwwwwwl! (Oh how the bleak gulfs hunger for the Day of Rising.)")
								.addPlayer(HeadE.HAPPY_TALKING, "My brain hurts when I listen to you talk...")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Raaaaawl groooowl grrrrawl! (That's the truth biting into your clouded mind!)")
								.addPlayer(HeadE.HAPPY_TALKING, "Could you try using a little less truth please?"),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Raaaawl! (Submit!)")
								.addPlayer(HeadE.HAPPY_TALKING, "Submit to what?")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Hssssssssss rawwwwwl graaaawl! (To the inevitable defeat of all life on the Surface!)")
								.addPlayer(HeadE.HAPPY_TALKING, "I think I'll wait a little longer before I just keep over and submit, thanks")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Hsssss, grooooowl, raaaaawl. (Well, it's your choice, but those that submit first will be eaten first.)")
								.addPlayer(HeadE.HAPPY_TALKING, "I'll pass on that one, thanks.")
				);
			}
			case UNICORN_STALLION -> {
				if(!canTalk)
					yield new Dialogue().addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Neigh .... neigh!");
				if(player.getHitpoints() < player.getMaxHitpoints()*0.6)
					yield new Dialogue()
							.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Whicker snort! Whinny whinny whinny. (You're hurt! Let me try to heal you.)")
							.addPlayer(HeadE.HAPPY_TALKING, "Yes, please do!")
							.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Snuffle whicker whicker neigh neigh... (Okay, we'll begin with acupuncture and some reiki, then I'll get my crystals...)")
							.addPlayer(HeadE.HAPPY_TALKING, "Or you could use some sort of magic...like the other unicorns...")
							.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Whicker whinny whinny neigh. (Yes, but I believe in alternative medicine.)")
							.addPlayer(HeadE.HAPPY_TALKING, "Riiight. Don't worry about it, then; I'll be fine.");
				yield random(
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Neigh neigh neighneigh snort? (Isn't everything so awesomely wonderful?)")
								.addPlayer(HeadE.HAPPY_TALKING, "Err...yes?")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Whicker whicker snuffle. (I can see you're not tuning in, " + player.getDisplayName() + ".)")
								.addPlayer(HeadE.HAPPY_TALKING, "No, no, I'm completely at one with...you know...everything.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Whicker! (Cosmic.)"),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Whicker whicker. Neigh, neigh, whinny. (I feel so, like, enlightened. Let's meditate and enhance our auras.)")
								.addPlayer(HeadE.HAPPY_TALKING, "I can't do that! I barely even know you.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Whicker... (Bipeds...)"),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Whinny whinny whinny. (I think I'm astrally projecting.)")
								.addPlayer(HeadE.HAPPY_TALKING, "Okay... Hang on. Seeing as I summoned you here, wouldn't that mean you are physically projecting instead?")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Whicker whicker whicker. (You're, like, no fun at all, man.)"),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Whinny, neigh! (Oh, happy day!)")
								.addPlayer(HeadE.HAPPY_TALKING, "Happy day? Is that some sort of holiday or something?")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Snuggle whicker (Man, you're totally, like, uncosmic, " + player.getDisplayName() + ".)")
				);
			}
			case WOLPERTINGER -> {
				yield new Dialogue().addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Rawr!");
			}
			case PACK_YAK -> {
				yield new Dialogue().addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Barroobaroooo baaaaaaaaarooo!");
			}
			case FIRE_TITAN -> {
				if(!canTalk)
					yield new Dialogue().addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Brrrr....");
				if(player.getInventory().containsOneItem(590))
					yield new Dialogue()
							.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Relight my fire.")
							.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "A tinderbox is my only desire.")
							.addPlayer(HeadE.HAPPY_TALKING, "What are you singing?")
							.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Just a song I heard a while ago.")
							.addPlayer(HeadE.HAPPY_TALKING, "A tinderbox is my only desire.")
							.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "You're just jealous of my singing voice.")
							.addPlayer(HeadE.HAPPY_TALKING, "Where did you hear this again?")
							.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Oh, you know, just with some other fire titans. Out for a night on the pyres.")
							.addPlayer(HeadE.HAPPY_TALKING, "Hmm. Come on then. We have stuff to do.");
				yield random(
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Pick flax.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Jump to it.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "If you want to get to Fletching level 99.")
								.addPlayer(HeadE.HAPPY_TALKING, "That song...is terrible.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Sorry."),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "You're fanning my flame with your wind spells.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "I'm singeing the curtains with my heat.")
								.addPlayer(HeadE.HAPPY_TALKING, "Oooh, very mellow."),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "I'm burning up.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "I want the world to know.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "I got to let it show.")
								.addPlayer(HeadE.HAPPY_TALKING, "Catchy."),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "It's raining flame!")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Huzzah!")
								.addPlayer(HeadE.HAPPY_TALKING, "You have a...powerful voice.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Thanks"),
						new Dialogue()
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Let's go fireside.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "I think I've roasted the sofa.")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "I think I've burnt down the hall.")
								.addPlayer(HeadE.HAPPY_TALKING, "Can't you sing quietly?")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Sorry.")
				);
			}
			case MOSS_TITAN -> {
				if(!canTalk)
					yield new Dialogue().addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Brrr...");
				yield new Dialogue()
						.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Oh, look! A bug!")
						.addPlayer(HeadE.HAPPY_TALKING, "It's quite a large bug.")
						.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "He's so cute! I wanna keep him.")
						.addPlayer(HeadE.HAPPY_TALKING, "Well, be careful.")
						.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "I'm gonna call him Buggie and I'm gonna keep him in a box.")
						.addPlayer(HeadE.HAPPY_TALKING, "Don't get overexcited.")
						.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "I'm gonna feed him and we're gonna be so happy together!")
						.addSimple("The Moss titan begins to bounce up and down.")
						.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Aww...Buggie went squish.")
						.addPlayer(HeadE.HAPPY_TALKING, "Sigh.");
			}
			case ICE_TITAN -> {
				if(!canTalk)
					yield new Dialogue().addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Brrrrrr...");
				//in kharidian desert
				if(player.getX() > 3130 && player.getX() < 3520 && player.getY() < 3130 && player.getY() > 2755)
					yield new Dialogue()
							.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "I'm melting!")
							.addPlayer(HeadE.HAPPY_TALKING, "I have to admit, I am rather on the hot side myself.")
							.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "No, I mean I'm actually melting! My legs have gone dribbly.")
							.addPlayer(HeadE.HAPPY_TALKING, "Urk! Well, try hold it together.");
				yield new Dialogue()
						.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "It's too hot here.")
						.addPlayer(HeadE.HAPPY_TALKING, "It's really not that hot. I think it's rather pleasant.")
						.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Well, it's alright for some. Some of us don't like the heat. I burn easily - well, okay, melt.")
						.addPlayer(HeadE.HAPPY_TALKING, "Well, at least I know where to get a nice cold drink if I need one.")
						.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "What was that?")
						.addPlayer(HeadE.HAPPY_TALKING, "Nothing. Hehehehe");
			}
			case LAVA_TITAN -> {
				if(!canTalk)
					yield new Dialogue().addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Brrrrr....");
				yield new Dialogue()
						.addPlayer(HeadE.HAPPY_TALKING, "Isn’t it a lovely day, Titan?")
						.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "It is quite beautiful. The perfect sort of day for a limerick. Perhaps, I could tell you one?")
						.addPlayer(HeadE.HAPPY_TALKING, "That sounds splendid.")
						.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "There once was a bard of Edgeville,")
						.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Whose limericks were quite a thrill,")
						.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "He wrote this one here,")
						.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "His best? Nowhere near,")
						.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "But at least half a page it did fill.");
			}
			case SWAMP_TITAN -> {
				if(!canTalk)
					yield new Dialogue().addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Brrrrr....");
				yield new Dialogue()
						.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "I’m alone, all alone I say.")
						.addPlayer(HeadE.HAPPY_TALKING, "Oh, stop being so melodramatic.")
						.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "It’s not easy being greenery…well, decomposing greenery.")
						.addPlayer(HeadE.HAPPY_TALKING, "Surely, you’re not the only swamp…thing in the world? What about the other swamp titans?")
						.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "They’re not my friends…they pick on me…they’re so mean…")
						.addPlayer(HeadE.HAPPY_TALKING, "Why would they do that?")
						.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "They think I DON’T smell.")
						.addPlayer(HeadE.HAPPY_TALKING, "Oh, yes. That is, er, mean…");
			}
			case GEYSER_TITAN -> {
				if(!canTalk)
					yield new Dialogue().addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Brrrrr....");
				yield new Dialogue()
						.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Did you know a snail can sleep up to three years?")
						.addPlayer(HeadE.HAPPY_TALKING, "I wish I could do that. Ah...sleep.");
			}
			case ABYSSAL_TITAN -> {
				yield new Dialogue().addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Scruunt, scraaan.....");
			}
			case IRON_TITAN, STEEL_TITAN -> {
				yield new Dialogue().addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Brrrrrr...");
			}
			case MEERKATS -> {
				if(!canTalk)
					yield new Dialogue().addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Chatter Chatter");
				yield new Dialogue()
						.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Chatter Chatter. Chatter chatter chatter chatter chatter. Chatter! (We're pretty unlucky. Often, we hit a box when we try to burrow where you tell us. Very suspicious!)")
						.addPlayer(HeadE.HAPPY_TALKING, "Well, if we remove all the boxes, you'll be able to burrow anywhere!")
						.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Chatter chatter chatter! (Then the boxes must be removed!)")
						.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Chatter! (Agreed!)")
						.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Chatter chatter chatter! (Let's dig out those boxes!)")
						.addPlayer(HeadE.HAPPY_TALKING, "That's the spirit!");
			}
			case GHAST -> {
				yield new Dialogue().addNPC(familiar.getId(), HeadE.FRUSTRATED, "Woooooo woooo!");
			}
			case BLOODRAGER_1, BLOODRAGER_2, BLOODRAGER_3, BLOODRAGER_4, BLOODRAGER_5, BLOODRAGER_6, BLOODRAGER_7, BLOODRAGER_8, BLOODRAGER_9, BLOODRAGER_10 ->{
				if(!canTalk)
					yield new Dialogue().addNPC(familiar.getId(), HeadE.FRUSTRATED, "Yahadahalyonih dahdikad, bebehsha.");
				if(!player.getTempAttribs().getB("talked_to_bloograger")) {
					player.getTempAttribs().setB("talked_to_bloograger", true);
					yield new Dialogue().addNPC(familiar.getId(), HeadE.FRUSTRATED, "Brother, you are always welcome to talk with me.");
				}
				yield random(
						new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "Are all gorajo as cheery as you?")
								.addNPC(familiar.getId(), HeadE.FRUSTRATED, "Come to the gorajo plane and find out, brother! In the clan fringes, you will find bloodragers, and there are none more welcoming. You would be treated like a sachem.")
								.addPlayer(HeadE.HAPPY_TALKING, "I would love to! Are the other gorajo as friendly?")
								.addNPC(familiar.getId(), HeadE.FRUSTRATED, "Their lives are more complicated, brother. They must bear burdens, teach, guide and lead. Although we must protect the clan and serve Challem, we have nothing else to cloud our minds.")
								.addPlayer(HeadE.HAPPY_TALKING, "Well, I'll hold you to that invite. If we ever get out of here, of course."),
						new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "How do you like it in Daemonheim?")
								.addNPC(familiar.getId(), HeadE.FRUSTRATED, "It is a place, as any other. I am just happy to be alive, taking sharp rukhs full of air-")
								.addPlayer(HeadE.HAPPY_TALKING, "Im not sure I have a rukh.")
								.addNPC(familiar.getId(), HeadE.FRUSTRATED, "Sure you do! Or how else would you grebbit? I am just happy to be alive, breathing the air and completing the task that has been asked of me. Challem be praised."),
						new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "I don't have any more questions.")
								.addNPC(familiar.getId(), HeadE.FRUSTRATED, "Shame. I feel that we are pollen on the same wind, friend.")
				);
			}
			case STORMBRINGER_1, STORMBRINGER_2, STORMBRINGER_3, STORMBRINGER_4, STORMBRINGER_5, STORMBRINGER_6, STORMBRINGER_7, STORMBRINGER_8, STORMBRINGER_9, STORMBRINGER_10 -> {
				if(!canTalk)
					yield new Dialogue().addNPC(familiar.getId(), HeadE.FRUSTRATED, "Rooooooo!");
				if(!player.getTempAttribs().getB("talked_to_stormbringer")){
					player.getTempAttribs().setB("talked_to_stormbringer", true);
					yield new Dialogue().addNPC(familiar.getId(), HeadE.FRUSTRATED, "Little cub, I see you have questions for me.");
				}
				yield random(
						new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "How do you cast magic? You don't seem to carry any runes.")
								.addNPC(familiar.getId(), HeadE.FRUSTRATED, "So, how can I possibly form magic?")
								.addPlayer(HeadE.HAPPY_TALKING, "Yes, I guess.")
								.addNPC(familiar.getId(), HeadE.FRUSTRATED, "You are right, we have no runes, but some of us are born with currents of magic rippling over our bodies. Such is the power generated by a stormbringer that we render our mothers blind and infertile during childbirth.")
								.addNPC(familiar.getId(), HeadE.FRUSTRATED, "One day I hope to be a Blind Mother. They are highly esteemed and carried on the back of their own worldbearer."),
						new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "How powerful is a stormbringer? Have you killed a dragon?")
								.addNPC(familiar.getId(), HeadE.FRUSTRATED, "A drah-gon? Is that like the triple-bellied wolflok?")
								.addPlayer(HeadE.HAPPY_TALKING, "Probably not. Twenty-feet high, firebreathing?")
								.addNPC(familiar.getId(), HeadE.FRUSTRATED, "Oh, it sounds much like! Seven heads? Large bellows on its sides, pumping out acid?")
								.addPlayer(HeadE.HAPPY_TALKING, "Bellows? No, definitely no bellows.")
								.addNPC(familiar.getId(), HeadE.FRUSTRATED, "Then it is a shame. The creatures on our plane sound far more dangerous."),
						new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "How do you like it in Daemonheim?")
								.addNPC(familiar.getId(), HeadE.FRUSTRATED, "I like it very little. I grow tired of this place.")
								.addPlayer(HeadE.HAPPY_TALKING, "Then why don't you just leave? If you're so tired of floating about behind me, why don't you just go?")
								.addNPC(familiar.getId(), HeadE.FRUSTRATED, "Do not tempt me. You are like a tiny pup, nibbling and barking at my heel. I am here because the gorajos greatest duty is to hinder the one who created this place.")
								.addNPC(familiar.getId(), HeadE.FRUSTRATED, "We sense that great headway has been made; that we have dropped over a waterfall and risen, coughing and spluttering to the surface. Yet, there are many turns and falls ahead.")
								.addNPC(familiar.getId(), HeadE.FRUSTRATED, "I follow you because you have shown a remarkable habit of stumbling into trouble, young fawn. When that trouble comes, we shall push you aside and finish this.")
								.addPlayer(HeadE.HAPPY_TALKING, "Charming."),
						new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "I don't have any more questions.")
								.addNPC(familiar.getId(), HeadE.FRUSTRATED, "That is a good thing. We must prepare for the next battle.")
				);
			}
			case HOARDSTALKER_1, HOARDSTALKER_2, HOARDSTALKER_3, HOARDSTALKER_4, HOARDSTALKER_5, HOARDSTALKER_6, HOARDSTALKER_7, HOARDSTALKER_8, HOARDSTALKER_9, HOARDSTALKER_10 -> {
				if(!canTalk)
					yield new Dialogue().addNPC(familiar.getId(), HeadE.FRUSTRATED, "Nabe!");
				if(!player.getTempAttribs().getB("talked_to_hoardstalker")){
					player.getTempAttribs().setB("talked_to_hoardstalker", true);
					yield new Dialogue().addNPC(familiar.getId(), HeadE.FRUSTRATED, "Are you sure we can stop, naabe? Aren't there creatures about?");
				}
				yield random(
						new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "You're a little timid for a gorajo, aren't you?")
								.addNPC(familiar.getId(), HeadE.FRUSTRATED, "Naabe, a hoardstalker has little or no experience of combat. I am told that I fight like a cat on hind legs.")
								.addOptions("Choose an option:", new Options() {
									@Override
									public void create() {
										option("Don't worry, I have your back.", new Dialogue()
												.addPlayer(HeadE.HAPPY_TALKING, "Don't worry, I have your back.")
												.addNPC(familiar.getId(), HeadE.FRUSTRATED, "Not just my back, I hope. I find a foe more terrifying when " +
														"they run at me from the front. Although we do not die in our spirit form, we must rest for a week due to " +
														"our spirit wounds, so others would have to perform my role for me.")
												.addNPC(familiar.getId(), HeadE.FRUSTRATED, "They would be like a woodpecket attempting to feed from a tortoise. " +
														"I'd come back to find the crops irrigated with milk, " +
														"weapons dipped in water, " +
														"and babies drinking poison. Churra, it is too terrible to think on.")
										);
										option("Then what use are you?", new Dialogue()
												.addPlayer(HeadE.HAPPY_TALKING, "Then what use are you?")
												.addNPC(familiar.getId(), HeadE.FRUSTRATED, "Churra! You have a closed mind on those shoulders. I may not be a" +
														" towering bloodrager or a graceful deathslinger, but I can be useful where they cannot")
												.addNPC(familiar.getId(), HeadE.FRUSTRATED, "Let me scavenge in these ruins, naabe. I will bring you such " +
														"items that you would never question my place here.")
										);
									}
								}),
						new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "Do you like it in Daemonheim? I can't imagine how anyone could.")
								.addNPC(familiar.getId(), HeadE.FRUSTRATED, "I see this place in a way that you do not, naabe. It amazes me how something can " +
										"go down so deep, yet still be strong and broad. I cannot help but applaud the mind behind these dungeons.")
								.addNPC(familiar.getId(), HeadE.FRUSTRATED, "The workmanship, too...it makes me want to put down my tools and reincarnate " +
										"as a bloodrager. I feel like a sparrowhawk who has been chased off his kill by a dragon.")
								.addPlayer(HeadE.HAPPY_TALKING, "But this place is evil, and thousands died building it. I doubt you've murdered anyone to make a dagger, you know.")
								.addNPC(familiar.getId(), HeadE.FRUSTRATED, "And that is some comfort. I feel I must be careful about what I learn and study " +
										"on this plane. There are poisoned thorns among the flowering wonders"),
						new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "Why are you called a hoardstalker? It seems a strange choice for a...blacksmith and scavenger, I guess.")
								.addNPC(familiar.getId(), HeadE.FRUSTRATED, "We are not just required to make the tools of our clansmen, naabe, We must " +
										"protect the tools from those who would take them.")
								.addPlayer(HeadE.HAPPY_TALKING, "Still, hoardstalker is a silly name.")
								.addNPC(familiar.getId(), HeadE.FRUSTRATED, "Naabe, I have held this back from you until now, but the term " +
										player.getDisplayName() + ", in our tongue, means 'One-Who-Juggles-Piglets'. A less-mature gorajo would find that amusing."),
						new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "I don't have any more questions.")
								.addNPC(familiar.getId(), HeadE.FRUSTRATED, "No problem, naabe. Just make sure nothing sneaks past and hurts me.")
				);
			}
			case SKINWEAVER_1, SKINWEAVER_2, SKINWEAVER_3, SKINWEAVER_4, SKINWEAVER_5, SKINWEAVER_6, SKINWEAVER_7, SKINWEAVER_8, SKINWEAVER_9,  SKINWEAVER_10 -> {
				if(!canTalk)
					yield new Dialogue().addNPC(familiar.getId(), HeadE.FRUSTRATED, "Naaabe!");
				if(!player.getTempAttribs().getB("talked_to_skinweaver")){
					player.getTempAttribs().setB("talked_to_skinweaver", true);
					yield new Dialogue().addNPC(familiar.getId(), HeadE.FRUSTRATED, "This is my first time on this plane, naabe. I hope I can serve you well.");
				}
				yield random(
						new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "What is your name? My name is "+ player.getDisplayName() + ", by the way.")
								.addNPC(familiar.getId(), HeadE.FRUSTRATED, "I get confused with these titles you humans take pride in. Fremennik names," +
										" first names, last names... Gorajo have no need to be individual - to have a name that no-one else has.")
								.addPlayer(HeadE.HAPPY_TALKING, "How can you not have a name?")
								.addNPC(familiar.getId(), HeadE.FRUSTRATED, "Everything we do is for the clan. If a problem arises, a role will be required, " +
										"not an individual. The individual has no place among the gorajo.")
								.addNPC(familiar.getId(), HeadE.FRUSTRATED, "Please do not take it as rudeness, but I cannot understand how your world " +
										"functions with names and individuals, naabe."),
						new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "What does a skinweaver do, exactly?")
								.addNPC(familiar.getId(), HeadE.FRUSTRATED, "We are healers of livestock, crops and other gorajo. Which reminds me, " +
										"naabe, do you mind if I ask you a question?")
								.addPlayer(HeadE.HAPPY_TALKING, "Sure.")
								.addNPC(familiar.getId(), HeadE.FRUSTRATED, "Do you find that human organs feel like slippery fish? And that your skin is " +
										"stretchy like the dried sap of an utuku?")
								.addPlayer(HeadE.HAPPY_TALKING, "Uh. I'm feeling a little faint.")
								.addNPC(familiar.getId(), HeadE.FRUSTRATED, "Don't worry! I have cat spittle for your head should you fall. And I am on hand to suck any blood clots from your brain."),
						new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "How do you like it in Daemonheim?.")
								.addNPC(familiar.getId(), HeadE.FRUSTRATED, "Naabe, let me tell you something. When I was a few years younger than I am now, " +
										"I helped to heal a nustukh: a creature as big as three floors of this place.")
								.addNPC(familiar.getId(), HeadE.FRUSTRATED, "The nustukh do not benefit the gorajo in any way, but they are the " +
										"reincarnations of our greatest leaders. They have great significance to our people.")
								.addNPC(familiar.getId(), HeadE.FRUSTRATED, "This nustukh was ravaged by a corruption that ate at every one of its organs. " +
										"A skinweaver was required to crawl in through an open lesion and heal it: I volunteered immediately. I spent two weeks inside.")
								.addNPC(familiar.getId(), HeadE.FRUSTRATED, "I cannot help but be reminded of the nustukh in Daemonheim. The dungeons are as rank and unwholesome, and I feel that my powers are just as ineffective inside."),
						new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "I don't have any more questions.")
								.addNPC(familiar.getId(), HeadE.FRUSTRATED, "I can understand your curiosity, naabe. Feel free to talk whenever you need.")
				);
			}
			case WORLDBEARER_1, WORLDBEARER_2, WORLDBEARER_3, WORLDBEARER_4, WORLDBEARER_5, WORLDBEARER_6, WORLDBEARER_7, WORLDBEARER_8, WORLDBEARER_9, WORLDBEARER_10 -> {
				if(!canTalk)
					yield new Dialogue().addNPC(familiar.getId(), HeadE.FRUSTRATED, "Naaabe!");
				if(!player.getTempAttribs().getB("talked_to_worldbearer")){
					player.getTempAttribs().setB("talked_to_worldbearer", true);
					yield new Dialogue().addNPC(familiar.getId(), HeadE.FRUSTRATED, "I am not a great talker, little cub. For my sake, make it quick.");
				}
				yield random(
						new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "Do you need help carrying?")
								.addNPC(familiar.getId(), HeadE.FRUSTRATED, "Little cub, there is no greater insult to a worldbearer. Shall I rub your belly so you can digest? Shall I move your feet so you can walk?")
								.addNPC(familiar.getId(), HeadE.FRUSTRATED, "Garra! I know your culture: you call us pack mules and servants. There is no indignity in what I do! There is only honour in bearing another's burdens.")
								.addPlayer(HeadE.HAPPY_TALKING, "I didn't mean to insult you.")
								.addNPC(familiar.getId(), HeadE.FRUSTRATED, "Bah, you are right. I must have woken on the wrong side of the stream today, little cub. Ignore me.")
								.addPlayer(HeadE.HAPPY_TALKING, "You're carrying a few burdens of your own.")
								.addNPC(familiar.getId(), HeadE.FRUSTRATED, "Grrrr! Although my role is to carry - and I carry for you now - I like it as much as I like your stale odour. Can we press on before I do something that I will get exiled for?"),
						new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "You shouldn't complain so much.")
								.addNPC(familiar.getId(), HeadE.FRUSTRATED, "Do not pretend that we are in the servant and master role, little cub! Our alliance is a delicate one and will end some day, through good means or bad.")
								.addNPC(familiar.getId(), HeadE.FRUSTRATED, "When that day comes, you may not find that I am so helpful. Our goal is to blast this dungeon apart, but I have no issue with leaving you behind."),
						new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "What is it like to be a worldbearer?")
								.addNPC(familiar.getId(), HeadE.FRUSTRATED, "The worldbearers are the legs and back of the gorajo; we bear the provisions, tents and tools from one destination to another.")
								.addNPC(familiar.getId(), HeadE.FRUSTRATED, "And when the gorajo are not moving, the worldbearers prepare and serve the food. It is an important role, and one that is esteemed among my clansmen.")
								.addPlayer(HeadE.HAPPY_TALKING, "It sounds like hard work. There can't be much time to enjoy yourself.")
								.addNPC(familiar.getId(), HeadE.FRUSTRATED, "It is rewarding in its own way. When a worldbearer is put to rest, they are stripped of all belongings, to be reincarnated as a creature without burden: a sparrowhawk or a wildcat, perhaps.")
								.addNPC(familiar.getId(), HeadE.FRUSTRATED, "Although we face trials in this life, our next is free and joyful.")
								.addPlayer(HeadE.HAPPY_TALKING, "You carry everything and have to serve it too?")
								.addNPC(familiar.getId(), HeadE.FRUSTRATED, "The clan sees the worldbearer as the nurturing mother wolf: proudly defending her pack while carrying the food and delivering it to her pups.")
								.addNPC(familiar.getId(), HeadE.FRUSTRATED, "I would prefer a more masculine comparison; never mind, it has been so for centuries."),
						new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "I don't have any more questions.")
								.addNPC(familiar.getId(), HeadE.FRUSTRATED, "That is good to hear. I am not one to talk.")
				);
			}
			case DEATHSLINGER_1, DEATHSLINGER_2, DEATHSLINGER_3, DEATHSLINGER_4, DEATHSLINGER_5, DEATHSLINGER_6, DEATHSLINGER_7, DEATHSLINGER_8, DEATHSLINGER_9, DEATHSLINGER_10 -> {
				if(!canTalk)
					yield new Dialogue().addNPC(familiar.getId(), HeadE.FRUSTRATED, "Naaabe!");
				if(!player.getTempAttribs().getB("talked_to_deathslinger")){
					player.getTempAttribs().setB("talked_to_deathslinger", true);
					yield new Dialogue().addNPC(familiar.getId(), HeadE.FRUSTRATED, "We have much work to do, but I could stop for a moment.");
				}
				yield random(
						new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "What is the biggest creature you have killed?")
								.addNPC(familiar.getId(), HeadE.FRUSTRATED, "Haha, you sound like my cubs! What did you kill, mama? Did it have " +
										"ten heads, mama? Did it fire magic bolts from its eyes?")
								.addPlayer(HeadE.HAPPY_TALKING, "Alright, I get it. I was just making conversation.")
								.addNPC(familiar.getId(), HeadE.FRUSTRATED, "Do not sulk, naabe. You simply reminded me of better times. To answer your question, " +
										"it was most likely a sinkhole. They are huge and flat, shaped something like an open palm. ")
								.addNPC(familiar.getId(), HeadE.FRUSTRATED, "They burrow beneath the ground, and then fold themselves into a fist, " +
										"storing the land and people within them to be digested when required.")
								.addPlayer(HeadE.HAPPY_TALKING, "That's horrible!")
								.addNPC(familiar.getId(), HeadE.FRUSTRATED, "And so much worse to be inside one. This place is nothing in comparison to a " +
										"sinkhole, naabe. I relish every day outside of that thing."),
						new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "How much do you know about Daemonheim?")
								.addNPC(familiar.getId(), HeadE.FRUSTRATED, "I know that it has been here for far longer than you or I have been alive, " +
										"beyond the lifetimes of our parents, grandparents and any relatives they knew.")
								.addNPC(familiar.getId(), HeadE.FRUSTRATED, "Many, from so many different races, have been born here. And many have died here, " +
										"filling the holes they helped to dig. It is not a life they deserved, but they knew no other.")
								.addPlayer(HeadE.HAPPY_TALKING, "It must have been a terrible life.")
								.addNPC(familiar.getId(), HeadE.FRUSTRATED, "It is best to not consider it a life, naabe. They would have burrowed without " +
										"question, knowing no life better than this. Like blind moles, churra. ")
								.addNPC(familiar.getId(), HeadE.FRUSTRATED, "They believed that they were burrowing to an exit. It is hateful to think that " +
										"their leader may have played upon this fact, encouraging them downward to their escape."),
						new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "Why do the gorajo have only one role? You can't be a deathslinger all the time, can you?")
								.addNPC(familiar.getId(), HeadE.FRUSTRATED, "A gorajo needs but one role. How do you humans say it? We...specialise.")
								.addPlayer(HeadE.HAPPY_TALKING, "I guess that would make you a pure. I mean, adventurers who specialise in one skill are often called pures.")
								.addNPC(familiar.getId(), HeadE.FRUSTRATED, "A pure? I like this. The goraju are pure of action, pure of purpose... Yes, I will accept this term."),
						new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "I don't have any more questions.")
								.addNPC(familiar.getId(), HeadE.FRUSTRATED, "Fly fast on the wind, young naabe.")
				);
			}
			case CLAY_POUCH_1, CLAY_POUCH_2, CLAY_POUCH_3, CLAY_POUCH_4, CLAY_POUCH_5 -> {
				if(!canTalk)
					yield new Dialogue().addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Rumble Rumble.");
				if(familiar.getInventory().getUsedSlots() > 0)
					yield new Dialogue()
							.addPlayer(HeadE.HAPPY_TALKING, "How are you getting on with the load?")
							.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Rumble. (Just fine, master.)")
							.addPlayer(HeadE.HAPPY_TALKING, "Don't go dropping it, okay?")
							.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Rumble. (I'll try my very best, master.)");

				yield random(
						new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "What is it like to be made out of sacred clay?")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Rumble... (I do not understand the question...)")
								.addPlayer(HeadE.HAPPY_TALKING, "Can you at least tell me how you feel?")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Rumble! (I am happy as long as I can serve you, master!)"),
						new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "They're attacking!")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Rumble! (Fear not, master, for I'll protect you!)")
								.addPlayer(HeadE.HAPPY_TALKING, "I'm glad you're here!"),
						new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "Hey!")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Rumble? (Yes, master?)")
								.addPlayer(HeadE.HAPPY_TALKING, "Actually, I probably don't want to be talking to you. It's kind of dangerous here...")
								.addNPC(familiar.getId(), HeadE.CAT_CALM_TALK2, "Rumble... (As You wish...)")
				);
			}
			default -> new Dialogue().addSimple("This familiar can't talk yet.");
		};
	}

	
	public static void addExtraOps(Player player, Options ops, Familiar familiar) {
		switch(familiar.getPouch()) {
			case LAVA_TITAN:
				ops.add("Teleport to Lava Maze", new Dialogue().addOptions("Are you sure you want to teleport here? It's very high wilderness.", yesNo -> {
					yesNo.add("Yes. I'm sure.", () -> Magic.sendNormalTeleportSpell(player, new WorldTile(3030, 3838, 0)));
					yesNo.add("Nevermind. That sounds dangerous.");
				}));
				break;
			case DREADFOWL:
				ops.add("Boost Farming", new Dialogue().addNext(()->{
					if(player.getSkills().getLevel(Skills.FARMING) <= player.getSkills().getLevelForXp(Skills.FARMING))
						player.getSkills().set(Skills.FARMING, player.getSkills().getLevel(Skills.FARMING) + 1);
				}));
				break;
			case COMPOST_MOUND:
				ops.add("Boost Farming", new Dialogue().addNext(()->{
					int boostAmount = (int)Math.ceil(1 + player.getSkills().getLevelForXp(Skills.FARMING) * 0.02);
					if(player.getSkills().getLevel(Skills.FARMING) <= player.getSkills().getLevelForXp(Skills.FARMING))
						player.getSkills().set(Skills.FARMING, player.getSkills().getLevel(Skills.FARMING) + boostAmount);
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
