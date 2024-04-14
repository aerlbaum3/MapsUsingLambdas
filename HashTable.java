import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;
import java.util.stream.IntStream;
// HashTable class implementing MapInterface
public class HashTable implements MapInterface<String, Integer> {
    int TABLE_SIZE; // Size of the hash table
    private int size; // Number of key-value pairs
    LinkedHashEntry<String, Integer>[] table; // Array to store hash table entries
    private HashFunction hash; // Enum to specify the hash function type

    // Constructor
    public HashTable(int ts, HashFunction hash) {
        size = 0;
        TABLE_SIZE = ts;
        table = new LinkedHashEntry[TABLE_SIZE];
        this.hash = hash;
        for (int i = 0; i < TABLE_SIZE; i++)
            table[i] = null;
    }

    // Function to get number of key-value pairs
    public int getSize() {
        return size;
    }

    // Function to clear hash table
    public void makeEmpty() {
        for (int i = 0; i < TABLE_SIZE; i++)
            table[i] = null;
        size = 0;
    }

    // Function to get value of a key
    @Override
    public Integer get(String key) {
        int hashFunc;
        // Determine which hash function to use
        if (hash == HashFunction.NAIVE) {
        //	System.out.print("Set to Naive");
            hashFunc = naiveHash(key);
        } else {
        	//System.out.println("Set to Sophisticated");
            hashFunc = sophisticatedHash(key);
        }
        if (table[hashFunc] == null)
            return null;
        else {
            // Search for the key in the linked list at the calculated hash index
            LinkedHashEntry<String, Integer> entry = table[hashFunc];
            while (entry != null && !entry.key.equals(key))
                entry = entry.next;
            if (entry == null)
                return null;
            else
                return entry.value;
        }
    }

    // Function to insert a key value pair
    @Override
    public Integer put(String key, Integer value) {
        int hashFunc;
        // Determine which hash function to use
        if (hash == HashFunction.NAIVE) {
            hashFunc = naiveHash(key);
        } else {
            hashFunc = sophisticatedHash(key);
        }
        // Insert the key-value pair into the hash table
        if (table[hashFunc] == null) {
            table[hashFunc] = new LinkedHashEntry<>(key, value);
            System.out.println("Inserted key: " + key + ", value: " + value + " at index: " + hashFunc);
            size++;
        } else {
            LinkedHashEntry<String, Integer> entry = table[hashFunc];
            while (entry.next != null && !entry.key.equals(key))
                entry = entry.next;
            if (entry.key.equals(key)) {
                entry.setValue(entry.value + 1); // Increment the value if key already exists
              
            } else {
                entry.next = new LinkedHashEntry<>(key, value); // Add new entry if key doesn't exist
                System.out.println("Inserted key: " + key + ", value: " + value + " at index: " + hashFunc);
            }
        }
        size++;
        return value;
    }

    // Function to remove a key from the hash table
    @Override
    public Integer remove(String key) {
        int hashFunc;
        // Determine which hash function to use
        if (hash == HashFunction.NAIVE) {
            hashFunc = naiveHash(key);
        } else {
            hashFunc = sophisticatedHash(key);
        }
        if (table[hashFunc] != null) {
            LinkedHashEntry<String, Integer> prevEntry = null;
            LinkedHashEntry<String, Integer> entry = table[hashFunc];
            while (entry.next != null && !entry.key.equals(key)) {
                prevEntry = entry;
                entry = entry.next;
            }
            if (entry.key.equals(key)) {
                if (prevEntry == null)
                    table[hashFunc] = entry.next;
                else
                    prevEntry.next = entry.next;
                size--;
                System.out.println("Removing key: " + key + " at index: " + hashFunc);
                return entry.value;
            }
        }
        return null; // Key not found in the hash table
    }

    // Function to calculate hash using a sophisticated method
    private int sophisticatedHash(String k) {
    	 int[] hashes = IntStream.range(0, k.length())
    	            .mapToObj(i -> (int) k.charAt(i))
    	            .reduce(new int[]{0, 0},
    	                    (acc, ch) -> new int[]{31 * acc[0] + ch, 37 * acc[1] + ch},
    	                    (hashes1, hashes2) -> new int[]{hashes1[0] + hashes2[0], hashes1[1] + hashes2[1]});
    	    
    	    int hashedcode = (hashes[0] + hashes[1]) % TABLE_SIZE;
    	    if (hashedcode < 0) {
    	        hashedcode += TABLE_SIZE;
    	    }
    	    return hashedcode;
    }

    	

    // Function to calculate hash using a naive method
    private int naiveHash(String key) {
    	  return IntStream.of(key.length() % TABLE_SIZE).findFirst().orElse(0);
    }

    // Function to print hash table statistics
    public void printHashTable() {
    	for (int i = 0; i < TABLE_SIZE; i++)
        {
            System.out.print("\nBucket "+ (i + 1) +" : ");
            LinkedHashEntry<String,Integer> entry = table[i];
            while (entry != null)
            {
                System.out.print(entry.value +" ");
                entry = entry.next;
            }            
        }
    }



    // Iterator to iterate over hash table entries
    @Override
    public Iterator<MapEntry<String, Integer>> iterator() {
        return new HashTableIterator();
    }

    // Iterator implementation
    private class HashTableIterator implements Iterator<MapEntry<String, Integer>> {
        private int currentIndex = 0;
        private LinkedHashEntry<String, Integer> currentEntry = null;

        @Override
        public boolean hasNext() {
            // Check if there are more elements in the hash table
            while (currentIndex < TABLE_SIZE && (currentEntry == null || currentEntry.next == null)) {
                currentEntry = table[currentIndex++];
            }
            return currentIndex < TABLE_SIZE;
        }

        @Override
        public MapEntry<String, Integer> next() {
            if (!hasNext()) {
                throw new NoSuchElementException("No more elements in the hash table.");
            }
            MapEntry<String, Integer> entry = new MapEntry<>(currentEntry.key, currentEntry.value);
            currentEntry = currentEntry.next;
            return entry;
        }
    }

    // Function to check if the hash table contains a key
    @Override
    public boolean contains(String k) {
        int hashFunc;
        // Determine which hash function to use
        if (hash == HashFunction.NAIVE) {
            hashFunc = naiveHash(k);
        } else {
            hashFunc = sophisticatedHash(k);
        }

        // If the bucket at the calculated hash code is empty, the key is not there
        if (table[hashFunc] == null)
            return false;
        else {
            // Go through the linked list in the corresponding bucket to find the key
            LinkedHashEntry<String, Integer> entry = table[hashFunc];
            while (entry != null && !entry.key.equals(k))
                entry = entry.next;

            // Return true if the key is found, otherwise false
            return entry != null;
        }
    }

    // Function to check if the hash table is empty
    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    // Function to check if the hash table is full
    @Override
    public boolean isFull() {
        return size == TABLE_SIZE;
    }

    // Function to get the current size of the hash table
    @Override
    public int size() {
        return size;
    }

    // Function to set the hash function
    public void setHashFunction(HashFunction hashFunction) {
        this.hash = hashFunction;
    }

    // Function to view words in descending order of their counts
    public void viewWordsDescendingOrderByCount() {
        // Create a priority queue to store entries sorted by count
        PriorityQueue<MapEntry<String, Integer>> pq = new PriorityQueue<>(
                (a, b) -> Integer.compare(b.getValue(), a.getValue()));

        // Add all entries to the priority queue
        for (LinkedHashEntry<String, Integer> entry : table) {
            while (entry != null) {
                pq.offer(new MapEntry<>(entry.getKey(), entry.getValue()));
                entry = entry.next;
            }
        }

        // Print the entries in descending order of counts
        while (!pq.isEmpty()) {
            MapEntry<String, Integer> entry = pq.poll();
            System.out.println(entry.getKey() + ": " + entry.getValue());
            try {
                // Sleep for 100 milliseconds to allow for the whole list to be printed
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
