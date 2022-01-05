package edu.uwm.cs351;

import static edu.uwm.cs351.Card.Rank.ACE;
import static edu.uwm.cs351.Card.Rank.DEUCE;
import static edu.uwm.cs351.Card.Rank.KING;
import static edu.uwm.cs351.Card.Rank.SIX;
import static edu.uwm.cs351.Card.Suit.CLUB;
import static edu.uwm.cs351.Card.Suit.DIAMOND;
import static edu.uwm.cs351.Card.Suit.HEART;
import static edu.uwm.cs351.Card.Suit.SPADE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import edu.uwm.cs.junit.LockedTestCase;

/**
 * Traditional playing cards.
 * @author boyland
 */
public class Card {
	public enum Suit { CLUB, DIAMOND, HEART, SPADE };
	public enum Rank {
		ACE(1), DEUCE(2), THREE(3), FOUR(4), FIVE(5), SIX(6), SEVEN(7), EIGHT (8), NINE(9), TEN(10),
		JACK(11), QUEEN(12), KING(13);

		private final int rank;
		private Rank(int r) {
			rank = r;
		}

		public int asInt() {
			return rank;
		}
	}

	private final Suit suit;
	private final Rank rank;
	private Card prev, next;
	private Group group;

	public Card(Rank r, Suit s) {
		rank = r;
		suit = s;
	}

	// getters for all fields:

	public Suit getSuit() {
		return suit;
	}

	public Rank getRank() {
		return rank;
	}

	public Card getPrevious() {
		return prev;
	}

	public Card getNext() {
		return next;
	}

	public Group getGroup() {
		return group;
	}

	// no setters!

	@Override
	/** Return true if the suit and rank are the same.
	 * Caution: do not use this method to check if you have the same card!
	 */
	public boolean equals(Object x) {
		if (!(x instanceof Card)) return false;
		Card other = (Card)x;
		return suit == other.suit && rank == other.rank;
	}

	@Override
	public String toString() {
		return rank + " of " + suit + "S";
	}


	/**
	 * An endogenous DLL of card objects.
	 */
	public static class Group {
		private Card first, last;
		private int size;

		/**
		 * Create an empty group.
		 */
		public Group() {
			first = last = null;
			size = 0;
		}

		public Card getFirst() { return first; }
		public Card getLast() { return last; }

		private static boolean reportErrors = true; // do not change
		private boolean report(String s) {
			if (reportErrors) System.err.println("Invariant error: " + s);
			return false;
		}
		private boolean wellFormed() {
			// - The cards must be properly linked up in a doubly-linked list.
			// - All cards must have their group set to this.
			// This code must terminate and must not crash even if there are problems.
			Card p = null;
			int count = 0;
			for(Card t = first; t != null; t = t.next)
			{
				if(t.prev != p) return report("prev wrong for " + t);
				if(t.group != this) return report("Card has wrong group: " + t);
				p = t;
				++count;
				
			}
			
			if(count != size) return report("Size = " +size +"This doesn't match the number of cards " +count);
			if(last != p) return report("last should be " +p);
			
			return true;
		}

		/**
		 * Return true if there are no cards,
		 * that is, if and only if getFirst() == null. O(1)
		 */
		public boolean isEmpty() {
			assert wellFormed() : "invariant false on entry to isEmpty()";
			return first == null;
		}

		/**
		 * Return the number of cards in this pile. O(1)
		 */
		public int count() {
			assert wellFormed() : "invariant false on entry to count()";
			return size;
		}

		/**
		 * Add a card to the end of this pile/hand. O(1)
		 * @param c card to add, must not be null or in a group already.
		 * @throws IllegalArgumentException if the card is in a group already.
		 */
		public void add(Card c) {
			// No loops allowed!
			// Make sure to test invariant at start and before returning.
			assert wellFormed() : "Invariant failed at the beginning of add";
			
			if(c.group != null) throw new IllegalArgumentException("Already in group" +c);
			if(c.prev != null || c.next != null) throw new IllegalArgumentException("Card has existing prev/next" +c);
			
			if(last == null)
			{
				first = last = c;
			}
			else
			{
				last.next = c;
				c.prev = last;
				last = c;
			}
			
			c.group = this;
			++size;
			
			assert wellFormed() : "Invariant failed at the end of add";
		}

		/**
		 * Remove the first card and return it.
		 * The group must not be empty.  The resulting card
		 * will not belong to any group afterwards. O(1)
		 *@throws IllegalStateException if group empty
		 */
		public Card draw() {
			
			assert wellFormed() : "Invariant failed at the beginning of draw";
			
			Card result = first;
			if(result == null) throw new IllegalStateException("Cannot draw from empty deck");
			
			first = result.next;
			result.next = null;
			if(first == null)
			{
				last = null;
			}
			else
			{
				first.prev = null;
			}
			
			result.group = null;
			--size;
			
			assert wellFormed() : "Invariant failed at the end of draw";
			
			return result;
			// No loops allowed!
			// Make sure to test invariant at start and before returning.
		}

		/**
		 * Remove the given card from this group.
		 * Afterwards the card is not in this group. O(1)
		 * @param c, card in this group, must not be null
		 * @throws IllegalArgumentException if c is not in this group
		 */
		public void remove(Card c) {  
			// No loops allowed!
			// Make sure to test invariant!
			assert wellFormed() : "invariant failed at the start of remove()";
			
			if(c.group != this) throw new IllegalArgumentException("Not part of this group");
			
			if(c.prev == null)
			{
				first = c.next;
			}
			else
			{
				c.prev.next = c.next;
			}
			
			if(c.next == null)
			{
				last = c.prev;
			}
			else
			{
				c.next.prev = c.prev;
			}
			
			c.prev = null;
			c.next = null;
			c.group = null;
			--size;
			
			assert wellFormed() : "invariant failed at the end of remove()";
		}

		/**
		 * Sort the cards using the given comparison, so that
		 * after sorting for all cards c in the group that is not last
		 * <code>cmp.compare(c,c.next)</code> is never positive.
		 * This code must use insertion sort so that it is efficient
		 * on (mostly) sorted lists.
		 * @param cmp comparator to use for sorting.  Must not be null.
		 * The comparator should work correctly, or the final result is undefined.
		 */
		public void sort(Comparator<Card> cmp) {
			assert wellFormed() : "invariant false on entry to sort()";
			
			if(first == null) return;
			for(Card c = first.next; c != null;)
			{
				Card prev = c.prev; // the card after which to insert c
				
				while(prev != null && cmp.compare(prev, c) > 0)
				{
					prev = prev.prev;
				}
				
				if(c.prev != prev)
				{
					// we have to swap
					remove(c); // easiest way to remove it
					c.group = this; // start replacing it
					c.prev = prev;
					++size;
					
					if(prev == null)
					{
						// need to go to the beginning
						c.next = first;
						first.prev = c;
						first = c;
					}
					else
					{
						c.next = prev.next;
						prev.next.prev = c;
						prev.next = c;
					}
				}
				
				c = c.next;
			}
			
			assert wellFormed() : "invariant false on exit to sort()";
		}

		/**
		 * Randomize the order of the cards in this group.
		 */
		public void shuffle() {
			/*
			 * This is very different from the sort method because:
			 * @ we decant the cards into an array list;
			 * @ we use a library function to do the work;
			 * The implementation you write for the sort method should
			 * have *neither* of these characteristics.
			 */
			List<Card> cards = new ArrayList<Card>();
			while (!isEmpty()) {
				cards.add(draw());
			}
			Collections.shuffle(cards);
			for (Card c: cards) {
				add(c);
			}
		}
	}
	
	// Do not change this code!
	public static class TestInvariant extends LockedTestCase {
		private Group self;
		private Card c[] = { null,
				new Card(ACE,DIAMOND),
				new Card(DEUCE,DIAMOND),
				new Card(KING,DIAMOND),
				new Card(DEUCE,CLUB),
				new Card(KING,HEART),
				new Card(SIX,HEART),	
				new Card(ACE,SPADE),
		};

		@Override
		protected void setUp() {
			Group.reportErrors = false;
			self = new Group();
		}

		public void test0() {
			// self starts empty
			assertEquals(Tb(1664353579),self.wellFormed());
			self.size = 1;
			assertEquals(Tb(1773780887),self.wellFormed());
			self.first = c[1];
			assertEquals(Tb(1258588040),self.wellFormed());
			self.first = null;
			self.last = c[2];
			self.size = 0;
			assertEquals(Tb(1588849059),self.wellFormed());
			self.last = null;
			assertEquals(Tb(1369135158),self.wellFormed());
		}

		public void test1() {
			// self starts empty
			self.first = c[2];
			assertEquals(Tb(1456599228),self.wellFormed());
			self.last = c[2];
			assertEquals(Tb(702186087),self.wellFormed());
			self.size = 1;
			assertEquals(false,self.wellFormed());
			c[2].group = self;
			assertEquals(Tb(498442860),self.wellFormed());
			self.first.prev = self.last;
			self.last.next = self.first;
			assertEquals(Tb(62398800),self.wellFormed());
			self.first = new Card(KING,SPADE);
			self.last = new Card(KING,SPADE);
			self.first.group = self;
			self.last.group = self;
			assertEquals(Tb(1545236604),self.wellFormed());
			self.last = self.first;
			assertEquals(Tb(2115154626),self.wellFormed());
		}

		public void test2() {
			// self starts empty
			self.first = c[3];
			self.last = c[4];
			c[3].group = self;
			c[4].group = self;
			assertEquals(Tb(1695835262),self.wellFormed());
			self.size = 2;
			assertEquals(Tb(1659916700),self.wellFormed());
			self.first.next = self.last;
			self.last.prev = self.first;
			assertEquals(Tb(1197524895),self.wellFormed());
			self.size = 1;
			assertEquals(Tb(2050626739),self.wellFormed());
		}

		public void test2Cycle() {
			self.first = c[3];
			self.last = c[4];
			c[3].group = self;
			c[4].group = self;
			c[3].next = c[4];
			c[4].prev = c[3];
			self.size = 2;
			assertEquals(true,self.wellFormed());
			c[4].next = c[4];
			assertEquals(false,self.wellFormed());
			c[4].next = c[3];
			assertEquals(false,self.wellFormed());
			c[4].next = null;
			c[3].next = c[3];
			assertEquals(false,self.wellFormed());
		}

		public void test3() {
			self.first = c[1];
			c[1].next = c[2]; 
			c[2].next = c[3]; 
			self.last = c[3];
			c[1].group = self;
			c[2].group = self;
			c[3].group = self;
			self.size = 3;
			assertEquals(false,self.wellFormed());
			c[2].prev = c[1]; c[3].prev = c[2];
			assertEquals(true,self.wellFormed());
			self.size = 2;
			assertEquals(false,self.wellFormed());
			self.size = 3;
			c[3].next = c[4];
			c[4].prev = c[3];
			assertEquals(false,self.wellFormed());
			c[3].next = null;
			assertEquals(true,self.wellFormed());
			c[3].prev = c[1];
			assertEquals(false,self.wellFormed());
		}

		public void test3Cycle21() {
			self.first = c[1];
			c[1].next = c[2]; c[2].prev = c[1]; 
			c[2].next = c[3]; c[3].prev = c[2];
			self.last = c[3];
			c[1].group = self;
			c[2].group = self;
			c[3].group = self;
			self.size = 3;
			assertEquals(true,self.wellFormed());
			c[2].next = c[1];
			assertEquals(false,self.wellFormed());
		}

		public void test3Cycle22() {
			self.first = c[1];
			c[1].next = c[2]; c[2].prev = c[1]; 
			c[2].next = c[3]; c[3].prev = c[2];
			self.last = c[3];
			c[1].group = self;
			c[2].group = self;
			c[3].group = self;
			self.size = 3;
			assertEquals(true,self.wellFormed());
			c[2].next = c[2];
			assertEquals(false,self.wellFormed());
		}

		public void test3Cycle31() {
			self.first = c[1];
			c[1].next = c[2]; c[2].prev = c[1]; 
			c[2].next = c[3]; c[3].prev = c[2];
			self.last = c[3];
			c[1].group = self;
			c[2].group = self;
			c[3].group = self;
			self.size = 3;
			assertEquals(true,self.wellFormed());
			c[3].next = c[1];
			assertEquals(false,self.wellFormed());
		}

		public void test3Cycle32() {
			self.first = c[1];
			c[1].next = c[2]; c[2].prev = c[1]; 
			c[2].next = c[3]; c[3].prev = c[2];
			self.last = c[3];
			c[1].group = self;
			c[2].group = self;
			c[3].group = self;
			self.size = 3;
			assertEquals(true,self.wellFormed());
			c[3].next = c[2];
			assertEquals(false,self.wellFormed());
		}

		public void test3Cycle33() {
			self.first = c[1];
			c[1].next = c[2]; c[2].prev = c[1]; 
			c[2].next = c[3]; c[3].prev = c[2];
			self.last = c[3];
			c[1].group = self;
			c[2].group = self;
			c[3].group = self;
			self.size = 3;
			assertEquals(true,self.wellFormed());
			c[3].next = c[3];
			assertEquals(false,self.wellFormed());
		}
	}


	/** Create and return a fresh pack of cards.
	 * A "static" method is a class method.  It is invoked using
	 * the class, not an instance.
	 * @return a fresh pack of 52 cards
	 */
	public static Group newDeck() {
		Group g = new Group();
		for (Suit s : Suit.values()) {
			for (Rank r : Rank.values()) {
				Card c = new Card(r,s);
				g.add(c);
			}
		}
		return g;
	}
}
