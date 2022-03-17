//pkPanel.java
//Catherine Sun
//Asks user a question, user input an integer answer

import javax.swing.*;
import java.awt.*;

public class pkPanel extends JPanel {

    public pkPanel() {
    }

    public int ask(int max, String q) {
    	int choice = 0;
    	while(choice < 1 || choice > max) {
    		String s = JOptionPane.showInputDialog(q);		
    		if(isNum(s)) {	//Check if valid input
    			choice = Integer.parseInt(s);
    		}
    	}
    	choice -= 1;
    	return choice;

    }


    public static boolean isNum(String s) {
    	if(s.equals("")) {
    		return false;
    	}

    	for(char ch : s.toCharArray()) {
    		if(ch < 48 || ch > 57) {	
    			//not numerical
    			return false;
    		}
    	}
    	return true;

    }


}