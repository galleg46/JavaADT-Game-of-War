package edu.uwm.cs351;

import java.util.Comparator;

/**
 * A player in the children's card game of war.
 * @author boyland
 */
public class PlayWar {
	protected Card.Group hand = new Card.Group();
	private Card.Group captured = new Card.Group();

	/**
	 * Return all cards back to the deck.
	 * @param pile pile of cards to return out cards too.
	 */
	public final void flush(Card.Group pile)
	{
		while (!hand.isEmpty()) {
			pile.add(hand.draw());
		}
		while (!captured.isEmpty()) {
			pile.add(captured.draw());
		}
	}

	/** Accept a card dealt.
	 * @param c card received
	 */
	public void receive(Card c)
	{
		hand.add(c);
	}

	/**
	 * Play a card given that there are a particular number of
	 * cards on top from previous ties.
	 * @param disputed number of disputed cards on table
	 * @return card removed from hand
	 */
	public Card play(Comparator<Card> cmp, int disputed)
	{
		// dumb
		return hand.draw();
	}

	/**
	 * Take all these cards to our captured pile.
	 * @param pile cards to take to our pile.
	 */
	public final void capture(Card.Group pile)
	{
		while (!pile.isEmpty()) {
			captured.add(pile.draw());
		}
	}

	/**
	 * Return how many cards have been captured.
	 * @return number of cards this player has captured.
	 */
	public final int countCaptured()
	{
		return captured.count();
	}

	public static final int CARDS = 10; // cards dealt for game of war

	/**
	 * Compare rank only, with aces counting as high.
	 * Warning: this comparator does not consider suit, so two aces
	 * will be "equal" even if they are of different suits.
	 * 
	 */
	protected static Comparator<Card> acesHigh = new Comparator<Card>() {
		public int compare(Card c1, Card c2) {
			int r1 = c1.getRank().asInt();
			int r2 = c2.getRank().asInt();
			if (r1 == 1) r1 = 14; // aces high
			if (r2 == 1) r2 = 14;
			return r1 - r2;
		}	  
	};

	/**
	 * Play the card game of "war" between two players.
	 * Return the relative score for the first player.
	 * Each player gets to choose what card they play each time
	 * without seeing what the other player played.  The winner gets both cards.  
	 * If there is a tie, the cards remain on the table to be captured by
	 * the next winner.  A sequences of ties can lead to a win getting
	 * a large number of cards.
	 * The game ends when each player has played all their cards.
	 * @param deck cards to use.  It must be big enough to give each player {@link #CARDS}
	 * cards.
	 * @param p1 first player, must not be null
	 * @param p2 second player, must not be null
	 * @param comparator how to compare cards.
	 * @return the difference in the number of captured cards between the two players.
	 * A positive number means the first play wins,
	 * a negative number means the second play wins.
	 */
	public static int playGame(Card.Group deck, PlayWar p1, PlayWar p2, Comparator<Card> comparator) {
		// make sure everything was cleaned up from last time.
		p1.flush(deck);
		p2.flush(deck);

		if (deck.count() < CARDS*2) {
			System.out.println("Too few cards to play game");
			return 0;
		}

		// shuffle deck thoroughly
		System.out.println("Shuffling.");
		deck.shuffle();

		System.out.println("Dealing.");

		for (int i=0; i < CARDS; ++i) {
			Card c1 = deck.draw();
			Card c2 = deck.draw();
			System.out.println("Player #1 gets " + c1 + ", Player #2 gets " + c2);
			p1.receive(c1);
			p2.receive(c2);
		}

		Card.Group table = new Card.Group();

		// go until game over
		try {
			for (int round = 1; true ;++round) {
				Card c1 = p1.play(comparator,table.count());
				Card c2 = p2.play(comparator,table.count());

				System.out.print("Round " + round
						+ ": Player #1 plays " + c1
						+ ", Player #2 plays " + c2 + "\t");

				table.add(c1);
				table.add(c2);

				PlayWar winner;
				int comparison = comparator.compare(c1,c2);
				if (comparison < 0) {
					System.out.println("Player #2 wins");
					winner = p2;
				} else if (comparison > 0){
					System.out.println("Player #1 wins");
					winner = p1;
				} else {
					System.out.println("WAR!");
					continue;
				}

				winner.capture(table);
			}
		} catch (IllegalStateException ex) {
			System.out.println("At end, Player #1 has " + p1.countCaptured() + " cards, "
					+ " and Player #2 has " + p2.countCaptured() + " cards.");
			return p1.countCaptured() - p2.countCaptured();
		} finally {
			p1.flush(deck);
			p2.flush(deck);
		}

	}
	
	public static void main(String[] args) {
		playGame(Card.newDeck(),new SmartWarPlayer(), new PlayWar(), acesHigh);
	}
}
