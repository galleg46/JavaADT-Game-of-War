# ADT Game of War

## The Card ADT
Each card instance represents a different physical card. Each card has a suit and rank that cannot be changed and each previous, next and group pointers for hooking
a card into a group. The class defines getters for each field, but no setters: the first two fields are final, and the last three are used by the Card.group ADT.

The Card class contains two enumarated types: Suit and Rank

## The Card.Group Adt
This ADT models a pile of cards, or a hand. This is a nested class within the Card ADT class. Each group keeps track of the first and last card in the group and
requires that the cards in the group are linked together with the previous and next pointers. This class has methods to add and remove cards from a group. It also
has a method to sort the cards using insertion sort

## The Game of war
A fast-moving version of "War" is implemented. Each player is dealt 10 cards. Each round, each player puts foward a card. The higher card wins. If there is a tie 
(Called a war), each player plays a second card, and the winner collects all the cards. If that is also a tie, the war continues.
