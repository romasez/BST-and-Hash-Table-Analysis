1. What was implemented
Unbalanced BST, iterative 
Hash table with separate chaining, resize when load factor > 0.75
Correctness tests; benchmark with n = 100, 1,000, 10,000, median of 5 runs
2. Complexity
Operation	BST best	BST avg	BST worst	Hash best	Hash avg	Hash worst
Insert	O(1)	O(log n)	O(n)	O(1)	O(1) amortized	O(n)
Search	O(1)	O(log n)	O(n)	O(1)	O(1)	O(n)
Delete	O(1)	O(log n)	O(n)	O(1)	O(1)	O(n)

BST: cost = tree height h. Worst case: sorted input, the tree becomes a linked list, h = n.
Hash: cost = chain length. Worst case: all keys in one bucket. Resize costs O(n) but happens rarely.

3. Results (n = 10000)
Scenario	Structure	Insert	Search	Delete
random	BST	12.7	8.3	11.7
random	Hash	15.9	4.5	5.0
sorted	BST	2830	1773	961
sorted	Hash	11.2	2.8	3.7

Key observations:

Random data: Hash is about 2x faster for search and delete; insertion is comparable
BST height: 28 for random data, 10,000 for sorted data
Sorted data: BST insertion is about 250x slower than Hash
BST on sorted data, n from 1,000 to 10,000: about 110x slower, which matches O(n²) total
Hash is almost insensitive to key order

4. Why performance differs in practice 
Input order affects BST but not Hash
Cache misses from pointer chasing in BST
Hash function quality and load factor

5. Conclusion

Hash tables are better for exact-match lookups; BSTs are better when ordering is needed, but only in balanced form.
