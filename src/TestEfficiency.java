import static edu.uwm.cs351.Card.Rank.ACE;
import static edu.uwm.cs351.Card.Rank.DEUCE;
import static edu.uwm.cs351.Card.Rank.KING;
import static edu.uwm.cs351.Card.Rank.SIX;
import static edu.uwm.cs351.Card.Suit.CLUB;
import static edu.uwm.cs351.Card.Suit.DIAMOND;
import static edu.uwm.cs351.Card.Suit.HEART;
import static edu.uwm.cs351.Card.Suit.SPADE;

import java.util.Comparator;

import junit.framework.TestCase;
import edu.uwm.cs351.Card;
import edu.uwm.cs351.Card.Suit;
import edu.uwm.cs351.CardUtil;


public class TestEfficiency extends TestCase {
	private Card[] c; // 0=null, 1=A♦, 2=2♦, 3=K♦, 4=2♣, 5=K♥, 6=6♥, 7=A♠
	
	private Card.Group g;
	
	/**
	 * Sort uses aces high, but put high cards first, not last.
	 */
	private static final Comparator<Card> reverseAcesHigh = 
			new CardUtil.ReverseComparator<Card>(CardUtil.acesHigh);
    private static final Comparator<Card> myAcesHigh =
    		new CardUtil.ReverseComparator<Card>(reverseAcesHigh);
    
    private static final Comparator<Card> nullComparator = new Comparator<Card>() {

		public int compare(Card o1, Card o2) {
			return 0;
		}
    	
    };
    
	@Override
	protected void setUp() {
		c = new Card[] {null,
				new Card(ACE,DIAMOND),
				new Card(DEUCE,DIAMOND),
				new Card(KING,DIAMOND),
				new Card(DEUCE,CLUB),
				new Card(KING,HEART),
				new Card(SIX,HEART),	
				new Card(ACE,SPADE),
		};
		g = new Card.Group();
		try {
			assert 1/(c[2].getRank().asInt()-2) == 42 : "OK";
			// OK, the assertion didn't fail
		} catch (ArithmeticException ex) {
			System.err.println("Assertions must be disabled to use this test suite.");
			System.err.println("In Eclipse: remove -ea from the VM Arguments box under Run>Run Configurations>Arguments");
			assertFalse("Assertions must be disabled in the Run Configuration>Arguments>VM Arguments",true);
		}
	}
	
	private static final int SIZE = 4000000;
	
	public void testAddDraw() {
		for (int i=0; i < SIZE; ++i) {
			Card card = c[(i % 7) + 1];
			g.add(new Card(card.getRank(),card.getSuit()));
		}
		for (int i=0; i < SIZE; ++i) {
			Card card = g.draw();
			assertEquals(c[(i % 7) + 1],card);
		}
	}

	public void testAddRemove() {
		for (int i=0; i < SIZE; ++i) {
			Card card = c[(i % 7) + 1];
			g.add(new Card(card.getRank(),card.getSuit()));
		}
		Card card = g.getFirst();
		for (int i=0; i < SIZE; ++i) {
			Card n = card.getNext();
			if ((i&1) != 0) {
				g.remove(card);
			}
			card = n;
		}
	}
	
	public void testSortNull() {
		for (int i=0; i < SIZE; ++i) {
			Card card = c[(i % 7) + 1];
			g.add(new Card(card.getRank(),card.getSuit()));
		}
		g.sort(nullComparator);
	}
	
	public void testSortNew() {
		for (int i=0; i < SIZE; ++i) {
			Card card = new Card(KING,Suit.values()[i%4]);
			g.add(card);
		}
		g.sort(myAcesHigh);
		g.sort(reverseAcesHigh);
		g.add(c[2]);
		g.sort(myAcesHigh);
		Card card = g.draw();
		assertSame(c[2],card);
	}
}
