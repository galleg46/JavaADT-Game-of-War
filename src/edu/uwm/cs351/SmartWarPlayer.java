package edu.uwm.cs351;

import java.util.Comparator;

public class SmartWarPlayer extends PlayWar {
	
	@Override
	public Card play(Comparator<Card> cmp, int disputed) {
		if (disputed == 0 || hand.isEmpty()) {
			return super.play(cmp, disputed);
		}
		
		Card best = hand.getFirst();
		for (Card c = best.getNext(); c != null; c = c.getNext()) {
			if (cmp.compare(c, best) > 0) best = c;
		}
		
		hand.remove(best);
		return best;
	}

}
