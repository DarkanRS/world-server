package com.rs.game.content.skills.summoning;

import java.util.Arrays;

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
	
	public static NPCClickHandler handleInteract = new NPCClickHandler(Arrays.stream(Pouch.values()).map(p -> p.getIdKeys()).toArray(), new String[] { "Interact" }) {
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
