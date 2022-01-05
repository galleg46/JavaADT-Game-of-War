package edu.uwm.cs351;

import static edu.uwm.cs351.Card.Rank.ACE;
import static edu.uwm.cs351.Card.Rank.JACK;
import static edu.uwm.cs351.Card.Rank.QUEEN;
import static edu.uwm.cs351.Card.Suit.DIAMOND;

import java.util.Comparator;

import edu.uwm.cs351.Card.Rank;
import edu.uwm.cs351.Card.Suit;

/**
 * Several card comparators.
 * Some of them return 0 when cards are equivalent even if not equal.
 * When used for sorting, such comparators indicate that the relative order 
 * of such cards is irrelevant.
 * @author boyland
 */
public class CardUtil {
	/** A comparator that sorts by suit and within the suit by rank.
	 * Useful for (say) a bridge-like game where aces are low
	 */
	public static Comparator<Card> suitThenRank  = new Comparator<Card>() {
		public int compare(Card c1, Card c2) {
			int sc = c1.getSuit().compareTo(c2.getSuit());
			if (sc != 0) return sc;
			return c1.getRank().compareTo(c2.getRank());
		}

	};

	/**
	 * First sort by rank (aces low) and then by suit.
	 */
	public static Comparator<Card> rankThenSuit = new Comparator<Card>() {
		public int compare(Card c1, Card c2) {
			int rc = c1.getRank().compareTo(c2.getRank());
			if (rc != 0) return rc;
			return c1.getSuit().compareTo(c2.getSuit());
		}
	};
	
	/**
	 * Compare rank only, with aces counting as high.
	 * Warning: this comparator does not consider suit, so two aces
	 * will be "equal" even if they are of different suits.
	 * 
	 */
	public static Comparator<Card> acesHigh = new Comparator<Card>() {
		public int compare(Card c1, Card c2) {
			int r1 = c1.getRank().asInt();
			int r2 = c2.getRank().asInt();
			if (r1 == 1) r1 = 14; // aces high
			if (r2 == 1) r2 = 14;
			return r1 - r2;
		}	  
	};

	/**
	 * A comparator for the German/Wisconsin game of Sheepshead,
	 * <p>
	 * Trumps: Q♣ Q♠ Q♥ Q♦ J♣ J♠ J♥ J♦ A♦ 10♦ K♦ 9♦ 8♦ 7♦
	 * Clubs: A♣ 10♣ K♣ 9♣ 8♣ 7♣
	 * Spades: A♠ 10♠ K♠ 9♠ 8♠ 7♠
	 * Hearts: A♥ 10♥ K♥ 9♥ 8♥ 7♥
	 * @see http://en.wikipedia.org/wiki/Sheepshead_(game)
	 */
	public static Comparator<Card> sheepshead = new Comparator<Card>() {
		private boolean isLegal(Card c) {
			return (c.getRank().asInt() >= 7 || c.getRank() == ACE);
		}
		
		private boolean isTrump(Card c) {
			return c.getSuit() == DIAMOND || c.getRank() == QUEEN || c.getRank() == JACK;
		}

		private int shSuit(Suit s) {
			switch (s) {
			case CLUB: return 3;
			case SPADE: return 2;
			case HEART: return 1;
			case DIAMOND: return 0;
			}
			throw new IllegalArgumentException("Not a sheepshead suit: " + s);
		}
		
		// NB: "fail" is Sheepshead jargon for "non trump."
		private static final int FAIL_COUNT = 6;
		
		private int shRank(Rank r) {
			// Q=7 J=6 A=5 10=4 K=3 9=2 8=1 7=0
			switch (r) {
			case QUEEN: return 7;
			case JACK: return 6;
			case ACE: return 5;
			case TEN: return 4;
			case KING: return 3;
			default: return r.asInt() - 7;
			}	
		}	
		
		/**
		 * Convert a card into a number for easier comparison
		 * first all non trumps, then diamonds, next jacks and
		 * finally queens
		 */
		private int shCard(Card c) {
			if (!isLegal(c)) throw new IllegalArgumentException("Not a legal sheepshead card: " + c);
			if (!isTrump(c)) {
				return (shSuit(c.getSuit()) - 1) * FAIL_COUNT + shRank(c.getRank());
			}
			if (c.getRank() == JACK) {
				return 4 * FAIL_COUNT + shSuit(c.getSuit());
			}
			if (c.getRank() == QUEEN) {
				return 4 * FAIL_COUNT + 4 + shSuit(c.getSuit());
			}
			return 3 * FAIL_COUNT + shRank(c.getRank());
		}

		/**
		 * Compare two sheepsheads cards.
		 * If they are in the same suit, it uses Sheepshead ranking rules.
		 * If the cards are in different suits, it ranks trumps > clubs > spades > hearts
		 * @param c1 first card, must be a sheepshead card (not null or a three, for example)
		 * @param c2 second card, must be a sheepshead card 
		 * @return comparison (negative if c1 ranks lower, equal if the same, positive if c1 ranks higher)
		 */
		public int compare(Card c1, Card c2) {
			return shCard(c1) - shCard(c2);
		}
	};

	public static class ReverseComparator<T> implements Comparator<T> {
		private final Comparator<T> base;
		
		public ReverseComparator(Comparator<T> c) {
			base = c;
		}
		
		public int compare(T o1, T o2) {
			return base.compare(o2,o1);
		}
		
	}
}
