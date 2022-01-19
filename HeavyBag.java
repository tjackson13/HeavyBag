import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * <P>
 * The HeavyBag class implements a Set-like collection that allows duplicates (a
 * lot of them).
 * </P>
 * <P>
 * The HeavyBag class provides Bag semantics: it represents a collection with
 * duplicates. The "Heavy" part of the class name comes from the fact that the
 * class needs to efficiently handle the case where the bag contains 100,000,000
 * copies of a particular item (e.g., don't store 100,000,000 references to the
 * item).
 * </P>
 * <P>
 * In a Bag, removing an item removes a single instance of the item. For
 * example, a Bag b could contain additional instances of the String "a" even
 * after calling b.remove("a").
 * </P>
 * <P>
 * The iterator for a heavy bag must iterate over all instances, including
 * duplicates. In other words, if a bag contains 5 instances of the String "a",
 * an iterator will generate the String "a" 5 times.
 * </P>
 * <P>
 * In addition to the methods defined in the Collection interface, the HeavyBag
 * class supports several additional methods: uniqueElements, getCount, and
 * choose.
 * </P>
 * <P>
 * The class extends AbstractCollection in order to get implementations of
 * addAll, removeAll, retainAll and containsAll.  (We will not be over-riding those).
 * All other methods defined in
 * the Collection interface will be implemented here.
 * </P>
 */
public class HeavyBag<T> extends AbstractCollection<T> implements Serializable {

	/* Needed for testing */
	private static final long serialVersionUID = 1L;

	int size = 0; // represents the total size of the bag, including all repeat items
	Map<T, Integer> bag; // the data structure for the heavy bag 


	/**
	 * Initialize a new, empty HeavyBag
	 */
	public HeavyBag() {
		bag = new HashMap<>();
	}

	/**
	 * Adds an instance of o to the Bag
	 * 
	 * @return always returns true, since added an element to a bag always
	 *         changes it
	 * 
	 */
	@Override
	public boolean add(T o) {
		if (bag.containsKey(o) == true) {  // if there is at least one instance in the heavyBag, increment and update
			int instancesOfO = bag.get(o) + 1;
			bag.put(o, instancesOfO);
		} else {                          // there are no instances of o in the bag yet
			bag.put(o, 1);
		}
		size++;
		return true;
	}

	/**
	 * Adds multiple instances of o to the Bag.  If count is 
	 * less than 0 or count is greater than 1 billion, throws
	 * an IllegalArgumentException.
	 * 
	 * @param o the element to add
	 * @param count the number of instances of o to add
	 * @return true, since addMany always modifies
	 * the HeavyBag.
	 */
	public boolean addMany(T o, int count) {
		if (count < 0 || count > 1000000000) {
			throw new IllegalArgumentException();
		} else {
			if (bag.containsKey(o) == true) {  // if already an instance of o, increment instances and replace in bag
				int instancesOfO = bag.get(o) + count;
				bag.put(o, instancesOfO);
			} else {  // if there is no instance of o, put that many instances in the bag
				bag.put(o, count);
			}
		}
		size += count;
		return true;
	}

	/**
	 * Generate a String representation of the HeavyBag. This will be useful for
	 * your own debugging purposes, but will not be tested other than to ensure that
	 * it does return a String and that two different HeavyBags return two
	 * different Strings.
	 */
	@Override
	public String toString() {
		return "Bag Contents: " + bag.keySet() ;
	}

	/**
	 * Tests if two HeavyBags are equal. Two HeavyBags are considered equal if they
	 * contain the same number of copies of the same elements.
	 * Comparing a HeavyBag to an instance of
	 * any other class should return false;
	 */
	@Override
	public boolean equals(Object o) {
		boolean allThere = true;
		if (!(o instanceof HeavyBag)) {
			return false;
		}
		HeavyBag<?> otherBag = (HeavyBag<?>) o;

		// check the keySets to see if they match
		if (!(otherBag.bag.keySet().equals(bag.keySet()))) {
			allThere = false;
			return allThere;
		}
		// check the integers in the key sets
		for(T object: bag.keySet()) {
			if (!(otherBag.bag.get(object) == bag.get(object))) {
				allThere = false;
			}
		}
		return allThere; // keySets match and all integers match
	}

	/**
	 * Return a hashCode that fulfills the requirements for hashCode (such as
	 * any two equal HeavyBags must have the same hashCode) as well as desired
	 * properties (two unequal HeavyBags will generally, but not always, have
	 * unequal hashCodes).
	 */
	@Override
	public int hashCode() {
		return bag.hashCode(); // just call the Map hashCode. Every bag (map) will have its own hashCode
	}

	/**
	 * <P>
	 * Returns an iterator over the elements in a heavy bag. Note that if a
	 * Heavy bag contains 3 a's, then the iterator must iterate over 3 a's
	 * individually.
	 * </P>
	 */
	@Override
	public Iterator<T> iterator() {
		return new Iterator<T>() {
			int currentPlace = 1;             // place in overall heavyBag
			int numberOfObjectsRemaining = 0; // number of elements in the bag of that kind remaining
			Iterator<T> keyIterator = bag.keySet().iterator(); // need to go through all items in keySet one at a time
			T nextObject = null;                               // this will be the return for the next method
			int objectsInBag = 0;

			@Override 
			public boolean hasNext() {
				return currentPlace < size();
			}

			@Override
			public T next() {
				if (numberOfObjectsRemaining > 0) { // if there are more instances of the unique object
					numberOfObjectsRemaining--;     // decrement objects remaining, and return the same current object
				
				} else {                            // if no more instances of the unique object
					nextObject = keyIterator.next(); // move on to the next unique object in the bag
					numberOfObjectsRemaining = bag.get(nextObject); // reset remaining objects count to instances of the
																	// unique object in the heavyBag
				}
				currentPlace++; // increment overall place in heavyBag
				return nextObject;
			}

			@Override
			public void remove() {
				if (bag.get(nextObject) == 1 || numberOfObjectsRemaining == 1) { // if only one instance of last object
					bag.remove(nextObject); // remove that element completely from bag
					size--;                 // decrement size
				} else {
					numberOfObjectsRemaining--; // if more than one instance of the object, decrement objects remaining
					objectsInBag = bag.get(nextObject) - 1;
					bag.put(nextObject, objectsInBag);
				}
			}
		};
	}

	/**
	 * return a Set of the elements in the Bag (since the returned value is a
	 * set, it can contain no duplicates. It will contain one value for each 
	 * UNIQUE value in the Bag).
	 * 
	 * @return A set of elements in the Bag
	 */
	public Set<T> uniqueElements() {
		return bag.keySet();
	}

	/**
	 * Return the number of instances of a particular object in the bag. Return
	 * 0 if it doesn't exist at all.
	 * 
	 * @param o
	 *            object of interest
	 * @return number of times that object occurs in the Bag
	 */
	public int getCount(Object o) {
		if (!(bag.keySet().contains(o))) {
			return 0;
		} else {
			return bag.get(o);
		}
	}

	/**
	 * Given a random number generator, randomly choose an element from the Bag
	 * according to the distribution of objects in the Bag (e.g., if a Bag
	 * contains 7 a's and 3 b's, then 70% of the time choose should return an a,
	 * and 30% of the time it should return a b.
	 * 
	 * This operation can take time proportional to the number of unique objects
	 * in the Bag, but no more.
	 * 
	 * This operation should not affect the Bag.
	 * 
	 * @param r
	 *            Random number generator
	 * @return randomly chosen element
	 */
	public T choose(Random r) {
		int randomPlace = r.nextInt(size()); // any value between 0 and size of list
		int placeInList = 0;                 // kind of like an array
		T chosenObject = null;               // will become the chosen object
		for (T object: bag.keySet()) {
			placeInList += bag.get(object);
			if (placeInList > randomPlace) { // if you've passed the placeInList with this object count then that 
				chosenObject = object;       // object is the one to be chosen
				break;
			}
		}
		return chosenObject;
	}

	/**
	 * Returns true if the Bag contains one or more instances of o
	 */
	@Override
	public boolean contains(Object o) {
		return bag.keySet().contains(o);
	}


	/**
	 * Decrements the number of instances of o in the Bag.
	 * 
	 * @return return true if and only if at least one instance of o exists in
	 *         the Bag and was removed.
	 */
	@Override
	public boolean remove(Object o) {
		if (!(bag.keySet().contains(o))) {
			return false;
		} else {
			int instancesOfO = bag.get(o); 
			if (instancesOfO == 1) { // if there is only one instance left, leave removed
				bag.remove(o); 
			} else {       // if more than one instance, subtract one from number of instances and add back to the bag
				instancesOfO--; 
				bag.put((T) o, instancesOfO);
			}
			size--;
			return true;
		}
	}

	/**
	 * Total number of instances of any object in the Bag (counting duplicates)
	 */
	@Override
	public int size() {
		return size;
	}
}
