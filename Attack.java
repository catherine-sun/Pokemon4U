//Attack.java
//Catherine Sun
//Attack accessor methods

public class Attack {
	
	private String name, special;
	private int cost, dmg;

    public Attack(String name, int cost, int dmg, String special) {
    	this.name = name;
    	this.cost = cost;
    	this.dmg = dmg;
    	this.special = special;
    }
    

    public String moveName() {
    	return name;
    }
    

    public int getCost() {
    	return cost;
    }
    
    
    public int getDmg() {
    	return dmg;
    }
    

    public String getSpecial() {
    	return special;
    }
    

}