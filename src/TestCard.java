import static edu.uwm.cs351.Card.Rank.ACE;
import static edu.uwm.cs351.Card.Rank.DEUCE;
import static edu.uwm.cs351.Card.Rank.KING;
import static edu.uwm.cs351.Card.Rank.TEN;
import static edu.uwm.cs351.Card.Suit.CLUB;
import static edu.uwm.cs351.Card.Suit.DIAMOND;
import static edu.uwm.cs351.Card.Suit.HEART;
import static edu.uwm.cs351.Card.Suit.SPADE;

import java.util.Comparator;
import java.util.function.Supplier;

import edu.uwm.cs.junit.LockedTestCase;
import edu.uwm.cs351.Card;
import edu.uwm.cs351.Card.Rank;
import edu.uwm.cs351.Card.Suit;
import edu.uwm.cs351.CardUtil;


public class TestCard extends LockedTestCase {

	private Card[] c; // 0=null, 1=A♦, 2=2♦, 3=K♦, 4=2♣, 5=K♥, 6=10♥, 7=A♠
	
	private Card.Group g;
	
	protected int asInt(Supplier<Card> supp) {
		try {
			Card card = supp.get();
			for (int i=0; i < c.length; ++i) {
				if (c[i] == card) return i;
			}
			return c.length;
		} catch (RuntimeException e) {
			return -1;
		}
	}
	
	/**
	 * Sort using aces high, but put high cards first, not last.
	 */
	private static final Comparator<Card> reverseAcesHigh = 
			new CardUtil.ReverseComparator<Card>(CardUtil.acesHigh);
    private static final Comparator<Card> myAcesHigh =
    		new CardUtil.ReverseComparator<Card>(reverseAcesHigh);
    
	@Override
	protected void setUp() {
		c = new Card[] {null,
				new Card(ACE,DIAMOND),
				new Card(DEUCE,DIAMOND),
				new Card(KING,DIAMOND),
				new Card(DEUCE,CLUB),
				new Card(KING,HEART),
				new Card(TEN,HEART),	
				new Card(ACE,SPADE),
		};
		g = new Card.Group();
		try {
			assert 1/(c[2].getRank().asInt()-2) == 42 : "OK";
			System.err.println("Assertions must be enabled to use this test suite.");
			System.err.println("In Eclipse: add -ea in the VM Arguments box under Run>Run Configurations>Arguments");
			assertFalse("Assertions must be -ea enabled in the Run Configuration>Arguments>VM Arguments",true);
		} catch (ArithmeticException ex) {
			return;
		}
	}

    protected static void assertException(Class<? extends Throwable> c, Runnable r) {
    	try {
    		r.run();
    		assertFalse("Exception should have been thrown",true);
        } catch (RuntimeException ex) {
        	assertTrue("should throw exception of " + c + ", not of " + ex.getClass(), c.isInstance(ex));
        }	
    }	

	
	/// Locked tests
	
	public void test() {
		// Remember: 0=null, 1=A♦, 2=2♦, 3=K♦, 4=2♣, 5=K♥, 6=10♥, 7=A♠
		// cards print as [RANK] of [SUIT]S
		// The case is significant!
		assertEquals(Ts(1039364098),c[1].toString());
		assertEquals(Ts(2132967278),c[6].toString());
		assertEquals(Ts(1697572491),c[5].toString());
		testGroup(g);
	}
	
	private void testGroup(Card.Group g) {
		// group is empty
		// -1=exception, 0=null, 1=A♦, 2=2♦, 3=K♦, 4=2♣, 5=K♥, 6=10♥, 7=A♠, 8=other
		assertEquals(Ti(1008905993),asInt(() -> g.getLast()));
		assertEquals(Ti(857860171),asInt(() -> g.draw()));
		
		g.add(c[4]);
		g.add(c[7]);
		assertEquals(Ti(669468710),asInt(() -> c[7].getPrevious()));
		assertEquals(Ti(1776074497),asInt(() -> g.getLast()));
		assertEquals(Ti(101440296),asInt(() -> g.draw()));
		assertEquals(Ti(1559315561),g.count());
		assertEquals(Ti(1381188138),asInt(() -> g.draw()));
		testSort(g);
	}
	
	private void testSort(Card.Group g) {
		// group is empty:
		// We add all cards: 1=A♦, 2=2♦, 3=K♦, 4=2♣, 5=K♥, 6=10♥, 7=A♠
		for (int i=1; i < c.length; ++i) { g.add(c[i]); }
		g.sort(CardUtil.acesHigh); // ascending order!, suit ignored
		assertEquals(Ti(1196842474),asInt(() -> g.getFirst()));
		assertEquals(Ti(2030470944),asInt(() -> g.getLast()));
		// now sort with reverse comparator (this does NOT reverse the list! -- why not?)
		g.sort(new CardUtil.ReverseComparator<Card>(CardUtil.acesHigh));
		assertEquals(Ti(366962618),asInt(() -> g.getFirst()));
		assertEquals(Ti(1021459996),asInt(() -> g.getLast()));
		// remove cards illegal in sheepshead:
		g.remove(c[2]);
		g.remove(c[4]);
		Card qc = new Card(Rank.QUEEN,Suit.CLUB); // highest trump in sheepshead
		g.add(qc);
		g.sort(CardUtil.sheepshead);
		assertSame(qc,g.getLast());
		assertEquals(Ti(1117409005),asInt(() -> g.getFirst())); // lowest of all cards?
	}
	
	
	/// test0X: tests of basic card functionality.
	// These tests test code that is already given to you.  They should not fail.
	
	public void test00() {
		assertNull(c[0]);
	}
	
	public void test01() {
		assertEquals(ACE,c[7].getRank());	
	}
	
	public void test02() {
		assertEquals(DIAMOND,c[1].getSuit());		
	}
	
	public void test03() {
		assertEquals(DEUCE,c[2].getRank());		
	}
	
	public void test04() {
		assertEquals(CLUB,c[4].getSuit());
	}
	
	public void test05() {
		assertNull(c[1].getGroup());
	}
	
	public void test06() {
		assertNull(c[2].getPrevious());
	}
	
	public void test07() {
		assertNull(c[3].getNext());
	}
	
	public void test08() {
		assertEquals("DEUCE of DIAMONDS",c[2].toString());
	}
	
	public void test09() {
		assertEquals("ACE of SPADES",c[7].toString());
	}
	
	
	/// test1X: Test of the simple methods on empty groups.
	
	public void test10() {
		assertTrue(g.isEmpty());
	}
	
	public void test11() {
		assertEquals(0,g.count());
	}
	
	public void test12() {
		assertNull(g.getFirst());
	}
	
	public void test13() {
		assertNull(g.getLast());
	}
	
	
	/// test2X: Testing add (1 or two elements)
	
	public void test20() {
		g.add(c[1]);
		assertNull(c[1].getPrevious());
		assertNull(c[1].getNext());
	}
	
	public void test21() {
		g.add(c[2]);
		assertSame(g,c[2].getGroup());
	}
	
	public void test22() {
		g.add(c[3]);
		assertFalse(g.isEmpty());
		assertEquals(1,g.count());
	}
	
	public void test23() {
		g.add(c[4]);
		assertSame(c[4],g.getFirst());
	}
	
	public void test24() {
		g.add(c[5]);
		assertSame(c[5],g.getLast());
	}
	
	public void test25() {
		g.add(c[6]);
		g.add(c[7]);
		assertSame(c[7],g.getLast());
	}
	
	public void test26() {
		g.add(c[1]);
		g.add(c[2]);
		assertSame(c[1],g.getFirst());
	}
	
	public void test27() {
		g.add(c[3]);
		g.add(c[4]);
		assertNull(c[3].getPrevious());
		assertNull(c[4].getNext());
		assertSame(c[3],c[4].getPrevious());
		assertSame(c[4],c[3].getNext());
	}
	
	public void test28() {
		g.add(c[5]);
		g.add(c[6]);
		assertSame(g,c[5].getGroup());
		assertSame(g,c[6].getGroup());
	}
	
	public void test29() {
		g.add(c[7]);
		g.add(c[1]);
		assertFalse(g.isEmpty());
		assertEquals(2,g.count());
	}
	
	
	/// test3X: testing add (3 or more elements, and errors)
	
	public void test30() {
		g.add(c[1]);
		g.add(c[3]);
		g.add(c[5]);
		assertSame(c[1],g.getFirst());
		assertSame(c[5],g.getLast());
	}
	
	public void test31() {
		g.add(c[2]);
		g.add(c[4]);
		g.add(c[6]);
		assertNull(c[2].getPrevious());
		assertSame(c[4],c[2].getNext());
		assertSame(c[2],c[4].getPrevious());
		assertSame(c[6],c[4].getNext());
		assertSame(c[4],c[6].getPrevious());
		assertNull(c[6].getNext());
	}
	
	public void test32() {
		g.add(c[7]);
		g.add(c[2]);
		g.add(c[5]);
		assertEquals(3,g.count());
	}
	
	public void test34() {
		g.add(c[1]);
		g.add(c[3]);
		g.add(c[5]);
		g.add(c[7]);
		g.add(c[2]);
		g.add(c[4]);
		g.add(c[6]);
		assertEquals(7,g.count());
	}
	
	public void test35() {
		assertException(RuntimeException.class,() -> g.add(null));
	}
	
	public void test36() {
		g.add(c[4]);
		assertException(RuntimeException.class, () -> g.add(null));
	}
	
	public void test37() {
		g.add(c[1]);
		g.add(c[2]);
		g.add(c[3]);
		assertException(IllegalArgumentException.class, () -> g.add(c[1]));
		assertException(IllegalArgumentException.class, () -> g.add(c[2]));
		assertException(IllegalArgumentException.class, () -> g.add(c[3]));
	}
	
	public void test38() {
		Card c7a = new Card(c[7].getRank(),c[7].getSuit());
		Card c7b = new Card(c[7].getRank(),c[7].getSuit());
		g.add(c[7]);
		g.add(c7a);
		g.add(c7b);
		assertException(IllegalArgumentException.class, () -> g.add(c7a));
	}
	
	public void test39() {
		Card.Group g1 = new Card.Group();
		g1.add(c[4]);
		
		assertException(IllegalArgumentException.class, () -> g.add(c[4]));
		
		g1.draw();
		g.add(c[4]);
		
		assertException(IllegalArgumentException.class, () -> g1.add(c[4]));
	}
	
	
	/// test4X: testing draw
	
	public void test40() {
		g.add(c[1]);
		assertSame(c[1],g.draw());
	}
	
	public void test41() {
		g.add(c[2]);
		g.draw();
		assertEquals(0,g.count());
	}
	
	public void test42() {
		g.add(c[3]);
		g.draw();
		assertNull(c[3].getGroup());
	}
	
	public void test43() {
		g.add(c[4]);
		g.draw();
		assertNull(g.getFirst());
		assertNull(g.getLast());
	}
	
	public void test44() {
		g.add(c[5]);
		g.add(c[6]);
		assertSame(c[5],g.draw());
	}
	
	public void test45() {
		g.add(c[7]);
		g.add(c[1]);
		g.draw();
		assertEquals(1,g.count());
	}
	
	public void test46() {
		g.add(c[2]);
		g.add(c[3]);
		g.draw();
		assertNull(c[2].getGroup());
		assertSame(g,c[3].getGroup());
	}
	
	public void test47() {
		g.add(c[4]);
		g.add(c[5]);
		g.draw();
		assertSame(c[5],g.getFirst());
		assertSame(c[5],g.getLast());
	}
	
	public void test48() {
		g.add(c[6]);
		g.add(c[7]);
		g.add(c[1]);
		g.draw();
		assertNull(c[6].getNext());
		assertNull(c[7].getPrevious());
		assertSame(c[1],c[7].getNext());
	}
	
	public void test49() {
		assertException(IllegalStateException.class, () -> g.draw());	
	}
	
	
	/// test5X/6X: tests of remove(Card)
	
	public void test50() {
		g.add(c[1]);
		g.add(c[2]);
		g.add(c[3]);
		g.remove(c[2]);
		assertEquals(2,g.count());
	}
	
	public void test51() {
		g.add(c[4]);
		g.add(c[5]);
		g.add(c[6]);
		g.remove(c[5]);
		assertNull(c[5].getGroup());
	}
	
	public void test52() {
		g.add(c[7]);
		g.add(c[1]);
		g.add(c[2]);
		g.remove(c[1]);
		assertNull(c[1].getPrevious());
		assertNull(c[1].getNext());
	}
	
	public void test53() {
		g.add(c[3]);
		g.add(c[4]);
		g.add(c[5]);
		g.remove(c[4]);
		assertSame(c[3],g.getFirst());
		assertSame(c[5],g.getLast());
	}
	
	public void test54() {
		g.add(c[1]);
		g.add(c[3]);
		g.add(c[5]);
		g.remove(c[3]);
		assertSame(c[5],c[1].getNext());
		assertSame(c[1],c[5].getPrevious());
	}
	
	public void test55() {
		g.add(c[6]);
		g.add(c[7]);
		g.add(c[1]);
		g.remove(c[6]);
		assertEquals(2,g.count());
	}
	
	public void test56() {
		g.add(c[2]);
		g.add(c[3]);
		g.add(c[4]);
		g.remove(c[2]);
		assertNull(c[2].getGroup());
	}
	
	public void test57() {
		g.add(c[5]);
		g.add(c[6]);
		g.add(c[7]);
		g.remove(c[5]);
		assertNull(c[5].getPrevious());
		assertNull(c[5].getNext());
	}
	
	public void test58() {
		g.add(c[1]);
		g.add(c[2]);
		g.add(c[3]);
		g.remove(c[1]);
		assertSame(c[2],g.getFirst());
		assertSame(c[3],g.getLast());
	}
	
	public void test59() {
		g.add(c[4]);
		g.add(c[5]);
		g.add(c[6]);
		g.remove(c[4]);
		assertNull(c[5].getPrevious());
	}
	
	public void test60() {
		g.add(c[2]);
		g.add(c[4]);
		g.add(c[6]);
		g.remove(c[6]);
		assertEquals(2,g.count());
	}
	
	public void test61() {
		g.add(c[3]);
		g.add(c[5]);
		g.add(c[7]);
		g.remove(c[7]);
		assertNull(c[7].getGroup());
	}
	
	public void test62() {
		g.add(c[2]);
		g.add(c[5]);
		g.add(c[1]);
		g.remove(c[1]);
		assertNull(c[1].getPrevious());
		assertNull(c[1].getNext());
	}
	
	public void test63() {
		g.add(c[4]);
		g.add(c[7]);
		g.add(c[3]);
		g.remove(c[3]);
		assertSame(c[4],g.getFirst());
		assertSame(c[7],g.getLast());
	}
	
	public void test64() {
		g.add(c[6]);
		g.add(c[4]);
		g.add(c[7]);
		g.remove(c[7]);
		assertNull(c[4].getNext());
	}
	
	public void test65() {
		g.add(c[1]);
		g.remove(c[1]);
		assertEquals(0,g.count());
	}
	
	public void test66() {
		g.add(c[2]);
		g.remove(c[2]);
		assertNull(c[2].getGroup());
	}
	
	public void test67() {
		g.add(c[3]);
		g.remove(c[3]);
		assertNull(c[3].getPrevious());
		assertNull(c[3].getNext());
	}

	public void test68() {
		g.add(c[4]);
		g.remove(c[4]);
		assertNull(g.getFirst());
		assertNull(g.getLast());
	}
	
	public void test69() {
		g.add(c[1]);
		g.add(c[3]);
		g.add(c[5]);
		g.add(c[7]);
		g.add(c[2]);
		g.add(c[4]);
		g.add(c[6]);
		
		assertSame(c[2],c[7].getNext());
		assertSame(g,c[7].getGroup());
		
		g.remove(c[2]);
		assertSame(c[4],c[7].getNext());
		assertEquals(6,g.count());
		
		g.remove(c[1]);
		assertSame(c[3],g.getFirst());
		assertNull(c[3].getPrevious());
		
		g.remove(c[6]);
		assertEquals(4,g.count());
		assertNull(c[6].getPrevious());
		
		g.remove(c[5]);
		assertSame(c[3],c[7].getPrevious());
		
		g.remove(c[4]);
		assertEquals(2,g.count());
		assertSame(c[7],g.getLast());
		
		g.remove(c[3]);
		assertNull(c[7].getPrevious());
		
		g.remove(c[7]);
		assertEquals(0,g.count());
		assertNull(c[7].getGroup());
	}

	
	/// test7X: testing remove errors
	
	public void test70() {
		assertException(IllegalArgumentException.class,() -> g.remove(c[1]));
	}
	
	public void test71() {
		g.add(c[4]);
		assertException(RuntimeException.class,() -> g.remove(null));
	}
	
	public void test72() {
		g.add(c[6]);
		g.add(c[1]);
		assertException(IllegalArgumentException.class, () -> g.remove(c[2]));
	}
	
	public void test73() {
		g.add(c[2]);
		g.add(c[5]);
		g.add(c[1]);
		g.draw();
		assertException(IllegalArgumentException.class, () -> g.remove(c[2]));
	}
	
	public void test74() {
		g.add(c[3]);
		g.add(c[5]);
		g.add(c[7]);
		g.remove(c[5]);
		g.remove(c[3]);
		assertException(IllegalArgumentException.class, () -> g.remove(c[5]));
	}
	
	public void test75() {
		g.add(c[2]);
		g.add(c[3]);
		g.add(c[4]);
		Card c3 = new Card(c[3].getRank(),c[3].getSuit());
		assertException(IllegalArgumentException.class, () -> g.remove(c3));
	}
	

	/// test8X: sorting tests

	public void test80() {
		g.sort(CardUtil.acesHigh);
		// of course sorting an empty deck does nothing
		assertEquals(0,g.count());
	}
	
	public void test81() {
		g.add(c[2]);
		g.sort(CardUtil.acesHigh);
		// of course sorting a deck of one card does nothing
		assertEquals(1,g.count());
		assertSame(c[2],g.getFirst());
	}
	
	public void test82() {
		g.add(c[2]);
		// sorting with sheepshead should
		// not cause an error, even though 2♦
		// is not a legal sheepshead card, since
		// the comparator should never be used.
		g.sort(CardUtil.sheepshead);
		assertSame(c[2],g.getLast());
	}
	
	public void test83() {
		// Remember: 0=null, 1=A♦, 2=2♦, 3=K♦, 4=2♣, 5=K♥, 6=10♥, 7=A♠
		Card ace = c[7];
		Card deuce = c[4];
		
		g.add(ace);
		g.add(deuce);
		
		assertSame(ace,g.getFirst());
		assertSame(deuce,g.getLast());
		assertNull(ace.getPrevious());
		assertSame(deuce,ace.getNext());
		assertSame(g,ace.getGroup());
		assertSame(ace,deuce.getPrevious());
		assertNull(deuce.getNext());
		assertSame(g,deuce.getGroup());
		
		g.sort(myAcesHigh);
		
		assertEquals(deuce,g.getFirst());
		assertEquals(ace,g.getLast());
		
		assertNull(ace.getNext());
		assertSame(deuce,ace.getPrevious());
		assertSame(g,ace.getGroup());
		assertSame(ace,deuce.getNext());
		assertNull(deuce.getPrevious());
		assertSame(g,deuce.getGroup());
		
		g.sort(reverseAcesHigh);
		
		assertEquals(c[7],g.getFirst());
		assertEquals(c[4],g.getLast());

		assertNull(ace.getPrevious());
		assertSame(deuce,ace.getNext());
		assertSame(g,ace.getGroup());
		assertSame(ace,deuce.getPrevious());
		assertNull(deuce.getNext());
		assertSame(g,deuce.getGroup());

		// and again
		g.sort(reverseAcesHigh);
		
		assertEquals(c[7],g.getFirst());
		assertEquals(c[4],g.getLast());
	}

	public void test84() {
		// Remember: 0=null, 1=A♦, 2=2♦, 3=K♦, 4=2♣, 5=K♥, 6=10♥, 7=A♠
		Card ace1 = c[1];
		Card ace2 = c[7];
		
		g.add(ace1);
		g.add(ace2);
		
		assertSame(ace1,g.getFirst());
		assertSame(ace2,g.getLast());
		assertNull(ace1.getPrevious());
		assertSame(ace2,ace1.getNext());
		assertSame(g,ace1.getGroup());
		assertSame(ace1,ace2.getPrevious());
		assertNull(ace2.getNext());
		assertSame(g,ace2.getGroup());
		
		g.sort(myAcesHigh);
		
		assertSame(ace1,g.getFirst());
		assertSame(ace2,g.getLast());
		
		g.sort(reverseAcesHigh);
		
		assertSame(ace1,g.getFirst());
		assertSame(ace2,g.getLast());
	}
	
	public void test85() {
		// Remember: 0=null, 1=A♦, 2=2♦, 3=K♦, 4=2♣, 5=K♥, 6=10♥, 7=A♠
		Card ace = c[1];
		Card deuce = c[2];
		Card king = c[3];
		
		g.add(ace);
		g.add(deuce);
		g.add(king);
		
		g.sort(myAcesHigh);
		
		assertEquals(deuce,g.getFirst());
		assertEquals(king,g.getFirst().getNext());
		assertEquals(ace,g.getLast());
		
		g.sort(reverseAcesHigh);
		
		assertEquals(c[1],g.getFirst());
		assertEquals(c[3],g.getFirst().getNext());
		assertEquals(c[2],g.getLast());
		
		g.sort(CardUtil.rankThenSuit);
		
		assertEquals(c[1],g.getFirst());
		assertEquals(c[2],g.getFirst().getNext());
		assertEquals(c[3],g.getLast());
		
		assertNull(ace.getPrevious());
		assertEquals(ace,deuce.getPrevious());
		assertEquals(deuce,king.getPrevious());
		assertNull(king.getNext());
		
		assertEquals(3,g.count());
	}
	
	public void test86() {
		// Remember: 0=null, 1=A♦, 2=2♦, 3=K♦, 4=2♣, 5=K♥, 6=10♥, 7=A♠
		Card aceTrump = c[1];
		Card kingTrump = c[3];
		Card kingHeart = c[5];
		Card aceSpade = c[7];
		
		g.add(aceTrump);
		g.add(kingTrump);
		g.add(kingHeart);
		g.add(aceSpade);
		
		g.sort(CardUtil.sheepshead);
		
		assertEquals(4,g.count());
		assertSame(kingHeart,g.getFirst());
		assertSame(aceTrump,g.getLast());
		
		assertSame(g,kingHeart.getGroup());
		assertSame(g,aceSpade.getGroup());
		assertSame(g,kingTrump.getGroup());
		assertSame(g,aceTrump.getGroup());
		
		assertSame(null,kingHeart.getPrevious());
		assertSame(aceSpade,kingHeart.getNext());
		assertSame(kingHeart,aceSpade.getPrevious());
		assertSame(kingTrump,aceSpade.getNext());
		assertSame(aceSpade,kingTrump.getPrevious());
		assertSame(aceTrump,kingTrump.getNext());
		assertSame(kingTrump,aceTrump.getPrevious());
		assertSame(null,aceTrump.getNext());
	}
	
	public void test87() {
		// Remember: 0=null, 1=A♦, 2=2♦, 3=K♦, 4=2♣, 5=K♥, 6=10♥, 7=A♠
		for (int i=1; i<c.length; ++i) {
			g.add(c[i]);
		}
		g.sort(myAcesHigh);
		
		Card cd;
		cd = g.getFirst(); assertSame(c[2],cd);
		cd = cd.getNext(); assertSame(c[4],cd);
		cd = cd.getNext(); assertSame(c[6],cd);
		cd = cd.getNext(); assertSame(c[3],cd);
		cd = cd.getNext(); assertSame(c[5],cd);
		cd = cd.getNext(); assertSame(c[1],cd);
		cd = cd.getNext(); assertSame(c[7],cd);
		
		g.sort(reverseAcesHigh);

		cd = g.getFirst(); assertSame(c[1],cd);
		cd = cd.getNext(); assertSame(c[7],cd);
		cd = cd.getNext(); assertSame(c[3],cd);
		cd = cd.getNext(); assertSame(c[5],cd);
		cd = cd.getNext(); assertSame(c[6],cd);
		cd = cd.getNext(); assertSame(c[2],cd);
		cd = cd.getNext(); assertSame(c[4],cd);
	}
}
