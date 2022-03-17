//Pokemon.java
//Catherine Sun
//Change and access pokemon stats


import java.util.*;

public class Pokemon {

	private String name, type, weak, resist;
	private int hp, totalHp, energy = 50, numAtt;
	private ArrayList<Attack> moves = new ArrayList<Attack>();
	private boolean alive = true, disabled = false, stunned = false;

    public Pokemon(String line) {
    	//<name>,<hp>,<type>,<resistance>,<weakness>,<num attacks>,
    	String[] stats = line.split(",");
    	name = stats[0];
    	hp = Integer.parseInt(stats[1]);
    	totalHp = hp;
    	type =  stats[2];
    	resist = stats[3];
    	weak = stats[4];
    	numAtt = Integer.parseInt(stats[5]);

    	for(int i = 0; i < numAtt; i++) {
			moves.add(new Attack(stats[6+i*4], Integer.parseInt(stats[7+i*4]), Integer.parseInt(stats[8+i*4]), stats[9+i*4]));
    	}

    }
    
    
    public void heal() {
    	hp = totalHp - hp - 20 < 0 ? 50 : hp + 20;
    }


    public void recharge(int n) {
    	energy = 50 - energy - n < 0 ? 50 : energy + n;
    }
    
    
    public boolean canAfford(Attack a) {
    	if(a.getCost() > energy) {
    		return false;
    	}
    	return true;

    }
    

    public void fight(Pokemon other, Attack a) {
    	String special = a.getSpecial();
    	if(special.equals("stun")) {
    		if(rand()) {
    			other.stun();
    		}
    	} else if(special.equals("wild card")) {
    		if(rand()) {
    			System.out.printf("%s's attack missed!\n%s lost 0 HP!\n", name, other.name);
    			return;
    		}
    	} else if(special.equals("wild storm")) {
    		loopAtt(other, a);
    		return;
    	} else if(special.equals("disable")) {
    		other.disable();
    	} else if(special.equals("recharge")) {
    		recharge(20);
    	}
    	attack(other, a);	//Attack if the pokemon hasn't missed

    }


    public void stun() {
    	stunned = true;
    	System.out.printf("%s has been stunned!\n", name);
    }
    
    
    public void removeStun() {
    	stunned = false;
    }
    
    
    public void loopAtt(Pokemon other, Attack a) {
    	int i = 1;	//Counts the number of loops

    	while(rand() && other.alive) {
    		if(i == 1) {
    			System.out.printf("%s has started a wild storm!\n", name);
    		} else {
    			System.out.printf("%s attacks again(%d)!\n", name, i);
    		}
    		attack(other, a);
    		i += 1;
    	}
    	if(other.alive) {
    		System.out.printf("%s's attack missed!\n%s lost 0 HP!\n", name, other.name);
    	}
    	if(i > 1) {
    		System.out.printf("%s's wild storm had ended\n", name);
    	}
    	
    }
    

    public void disable() {
    	if(disabled) {
    		System.out.printf("%s is already disabled!\n",name);
    	} else {
    		System.out.printf("%s has been disabled! All of their attacks will do 10 less damage.\n", name);
    	}
    	disabled = true;

    }
    
    
    public void attack(Pokemon other, Attack a) {
    	int pwr = a.getDmg();
    	if(type.equals(other.weak)) {
    		pwr *= 2;
    		System.out.println("It's supereffective");
    	} else if(type.equals(other.resist)) {
    		pwr *= 0.5;
    		System.out.println("It's not very effective...");
    	}
    	if(disabled) {
    		pwr -= 10;
    	}
    	energy -= a.getCost();
    	if(pwr < 0) {	//Avoid adding to hp
    		return;
    	}
    	other.deductHp(pwr);
    	
    }
    
    
    public void deductHp(int pwr) {
    	hp -= pwr;
    	alive = hp > 0 ? true : false;
    	if(alive) {
    		System.out.printf("%s lost %d HP!\n", name, pwr);
    	} else {
    		System.out.printf("%s fainted!\n", name);
    	}
    }


    public static boolean rand(){
    	Random r = new Random();
    	return r.nextBoolean();

    }
    
    

//Accessor methods:
    public String getName() {
    	return name;
    }


    public String getType() {
    	return type;
    }
    

    public String getHp() {
    	String frac = String.format("%d/%d", hp, totalHp);
    	return frac;
    }
    
    
    public String getEnergy() {
    	return String.format("%d/50", energy);
    }
    

    public boolean getStun() {
    	return stunned;
    }
    
    
    public boolean getDisable() {
    	return disabled;
    }
    
    
    public boolean getLife() {
    	return alive;
    }


    public ArrayList<String> getMoves() {
    	ArrayList<String> arr = new ArrayList<String>();

    	for(Attack a : moves) {
    		arr.add(a.moveName());
    	}
    	return arr;
    }


    public int getNumAtt() {
    	return numAtt;
    }


    public Attack getAtt(int i) {
    	Attack a = moves.get(i);
    	return a;
    }


}
