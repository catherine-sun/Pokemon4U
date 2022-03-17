//PokemonArena.java
//Catherine Sun
//Pokemon battle simulator

import java.util.*;
import java.io.*;

public class PokemonArena {

    private static ArrayList<Pokemon> compPk = new ArrayList<Pokemon>();	//Opponent's pokemon
    private static ArrayList<Pokemon> userPk = new ArrayList<Pokemon>();	//Player's pokemon
    private static Scanner kb = new Scanner(System.in);
    private static pkPanel panel = new pkPanel();
    private static Pokemon user, comp;	//Pokemon on the battlefield
    private static boolean turn = rand();	//True when it's the player's turn

    public static void main(String[] args) {
		load();
		pick4();
		battle();
		kb.close();
    }


    public static void load() {	//Read from file
		try {
	    	Scanner inFile = new Scanner(new BufferedReader(new FileReader("pokemon.txt")));
	    	int n = Integer.parseInt(inFile.nextLine());

	    	for(int i = 0; i < n; i++) {
	    		compPk.add(new Pokemon(inFile.nextLine()));
	    	}
			inFile.close();
		} catch(IOException ex){
			System.out.println(ex);
		}

    }


    public static void pick4() {	//Player picks four pokemon
    	display(toString(compPk));

    	while(userPk.size() < 4) {
    		String quest = "Pick a pokemon to join your team:";
    		int choice = panel.ask(compPk.size(), quest);	//Player picks a pokemon
    		Pokemon p = compPk.get(choice);
    		if(userPk.contains(p)) {	
    			//Pokemon has already been chosen
    			System.out.printf("%s is already part of your team.\n", p.getName());
    		} else {
    			userPk.add(p);	//Add to team
    			System.out.printf("%s had joined your team!\n", p.getName());
    		}
    		
    	}
    	System.out.println("");

    	for(Pokemon p : userPk) {
    		compPk.remove(p);	//Remove player's pokemon from opponent's roster
    	}

    }


    public static Pokemon userChoose() {	//Player picks a pokemon from their team to send out
    	display(toString(userPk));
    	String quest = "Choose a pokemon to send out:";
    	int choice = panel.ask(userPk.size(), quest);
	    Pokemon p = userPk.get(choice);
	    if(p == user) {	
	    	//Pokemon is already on the battlefield
	    	System.out.printf("%s is already in battle!\n", p.getName());
	    	return userChoose();
	    } else {	
	    	//Send out pokemon
	    	System.out.printf("%s, I choose you!\n", p.getName());
	    	return p;
	    }

    }
    
    
    public static Pokemon compChoose() {	//Opponent sends out a random pokemon
    	Collections.shuffle(compPk);
    	Pokemon p = compPk.get(0);
    	System.out.printf("The opponent sends out %s!\n", p.getName());
    	return p;

    }
    
    
    public static ArrayList<String> toString(ArrayList<Pokemon> old) {	//Makes an ArrayList<String> of pokemon names
    	ArrayList<String> arr = new ArrayList<String>();

    	for(Pokemon p : old) {
    		arr.add(p.getName());
    	}
    	return arr;

    }


    public static void display(ArrayList<String> arr) {	//Prints a table
    	String table = "";
    	int i = 1;
    		
    	for(String s : arr) {
    		table += String.format("%-22s", s + "(" + i +")");
    		if(i%7 == 0) {
    			table += "\n";
    		}
    		i += 1;
    	}
    	System.out.printf("%s\n", table);

    }



    public static void rechargeAll(int energy) {	//Recharge all living pokemon

    	for(Pokemon p : compPk) {
    		p.recharge(energy);
    	}

    	for(Pokemon p : userPk) {
    		p.recharge(energy);
    	}

    }


    public static void heal() {

    	for(Pokemon p : userPk) {	
    		//Heal player's living pokemon
    		p.heal();
    	}

    }


    public static void battle() {
    	if(turn) {
    		user = userChoose();	//Player chooses pokemon first
    		comp = compChoose();
    	} else {
    		comp = compChoose();	//Opponent chooses pokemon first
    		user = userChoose();
    	}

    	int battleNum = 1, roundNum = 1;	//Counts the number of battles and rounds

    	while(running()) {
    		if(user.getLife() == false) {
    			userPk.remove(user);
    			if(running() == false) {
    				return;
    			} 
    			user = userChoose();
			}
			if(comp.getLife() == false) {	
			    compPk.remove(comp);
			    if(running() == false) {
			    	return;
			    }
			    //Start new battle
			    comp = compChoose();
			    turn = turn ? false : true;	//Alternate who goes first
			    heal();
    			rechargeAll(50);
			    battleNum += 1;
			    roundNum = 1;
			}
			System.out.printf("\n----+=Battle %d Round %d=+----\n", battleNum, roundNum);
    		round();
    		rechargeAll(10);
    		roundNum += 1;

    	}

    }


    public static void round() {
	    if(turn) {
	    	userAtt();	//Player attacks first
	    	System.out.println("");
	    	if(comp.getLife()) {	//Checks if opponent is still alive
	    		compAtt();
	    	}
	    } else {
	    	compAtt();	//Opponent attacks first
	    	System.out.println("");
	    	if(user.getLife()) {	//Checks if player is still alive
	    		userAtt();
	    	}

	    }

    }


    public static void userAtt() {
    	ArrayList<String> options = user.getMoves();
    	if(userPk.size() > 1) {	//Checks if retreating is an option
    		options.add("Retreat");
    	}
    	options.add("Pass");
    	options.add("Check Summary");
		int pass = options.indexOf("Pass"), retreat = options.indexOf("Retreat"), summary = options.indexOf("Check Summary");
		display(options);
		String quest = String.format("What should %s do?", user.getName());
		int choice = panel.ask(options.size(), quest);
    	if(choice == pass) {
			System.out.printf("%s sits there and does nothing\n", user.getName());
			return;
		} else if(user.getStun()) {
			//Player's pokemon can't attack or retreat when stunned
			if(choice == retreat) {
				System.out.printf("%s is stunned and can't retreat!\n", user.getName());
			} else {
				System.out.printf("%s is stunned and can't attack!\n", user.getName());
			}
		} else if(choice == retreat) {
			System.out.printf("%s come back!\n", user.getName());
			user = userChoose();
			return;
		} else if(choice == summary) {
			checkSummary();
		} else {
			Attack a = user.getAtt(choice);
			if(user.canAfford(a)) {
				System.out.printf("%s used %s!\n", user.getName(), a.moveName());
				user.fight(comp, a);
				return;
			}
			//Player can't afford the selected attack
			System.out.printf("%s is too tired to use %s!\n", user.getName(), a.moveName());
		}
		userAtt();	//Player can reconsider options if they haven't passed, attacked or retreated
		user.removeStun();

    }
    
    
    public static void compAtt() {
    	if(comp.getStun()) {
    		System.out.printf("%s is stunned and can't attack!\n", comp.getName());
    		comp.removeStun();
    	}
    	int choice = randint(0, comp.getNumAtt() - 1);
    	Attack a = comp.getAtt(choice);
    	if(comp.canAfford(a)) {
    		System.out.printf("%s used %s!\n", comp.getName(), a.moveName());
    		comp.fight(user, a);
    		return;
    	}
    	System.out.printf("%s sits there and does nothing\n", comp.getName());

    }


    public static void checkSummary() {	//Player selects a pokemon to summarize
		ArrayList<Pokemon> pk = new ArrayList<Pokemon>();
		pk.addAll(userPk);
		pk.add(comp);
		display(toString(pk));
		int choice = panel.ask(pk.size(), "View which pokemon?");
		summarize(pk.get(choice));

    }
    
    
    public static void summarize(Pokemon p) {	//Displays a pokemon's info
    	/*+------------------+
    	 *|Name				 |
    	 *|Health    Energy  |
    	 *|Type	     Status  |
    	 *+------------------+
    	 */
    	String health = p.getHp() + "HP";
    	String status = "healthy";
    	if(p.getDisable()) {
    		status = "disabled";
    	}
    	String ln = "+------------------+\n";
    	ln += String.format("|%-18s|\n|%-9s%-9s|\n|%-9s%-9s|\n", p.getName(), health, p.getEnergy(), p.getType(), status);
    	ln += "+------------------+";
    	System.out.println(ln);
    }


	public static boolean running() {
		if(compPk.isEmpty()) {
			System.out.println("You have defeated all of the opponent's pokemon!\nCongratulations on becoming Trainer Supreme!");
			return false;
		}
		if(userPk.isEmpty()){
			System.out.println("You have run out of pokemon that can fight...\nBetter luck next time!");
			return false;
		}
		return true;

	}
	
	
	public static int randint(int low, int high){
		return (int)(Math.random()*(high-low+1)+low);
	}


	public static boolean rand(){
    	Random r = new Random();
    	return r.nextBoolean();

    }


}