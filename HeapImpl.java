class HeapImpl<T extends Comparable<? super T>> implements Heap<T> {
	private static final int INITIAL_CAPACITY = 128;
	private T[] _storage;
	private int _numElements;

	@SuppressWarnings("unchecked")
	public HeapImpl() {
		_storage = (T[]) new Comparable[INITIAL_CAPACITY];
		_numElements = 0;
	}

	@SuppressWarnings("unchecked")
	public void add(T data) {
		if (_numElements < INITIAL_CAPACITY) {
			_storage[_numElements] = data;
			_numElements++;
		}

		// "trickle up" method
		trickleUp(data, _numElements - 1);
	}

	/**
	 * makes sure that the largest data is always on top, swaps them if not
	 * 
	 * @param data  the variable that we added
	 * @param index the current index of the data
	 */
	public void trickleUp(T data, int index) {
		int parent = (index - 1) / 2;

		if (parent >= 0) {
			if (data.compareTo(_storage[parent]) > 0) {
				_storage[index] = _storage[parent];
				_storage[parent] = data;

				trickleUp(data, parent);
			}
		}
	}

	public T removeFirst() {
		T largest = _storage[0];
		_storage[0] = _storage[_numElements - 1];
		_storage[_numElements - 1] = null;

		_numElements--;
		trickleDown(0);

		return largest;
	}

	/**
	 * makes sure the largest data is always at top even after removing the root
	 * 
	 * @param index the current index of the data
	 */
	public void trickleDown(int index) {
		int leftNodeIndex = (2 * index) + 1;
		int rightNodeIndex = (2 * index) + 2;
		int largest = index;

		// checks if it's not greater than the size, sees if the left is larger than the
		// root
		if (leftNodeIndex < _numElements && _storage[leftNodeIndex].compareTo(_storage[largest]) > 0) {
			largest = leftNodeIndex;
		}

		// checks if its not greater than the size, sees if the right is larger than the
		// largest so far
		if (rightNodeIndex < _numElements && _storage[rightNodeIndex].compareTo(_storage[largest]) > 0) {
			largest = rightNodeIndex;
		}

		// swaps the largest, calls the function recursively
		if (largest != index) {
			T tempData = _storage[largest];
			_storage[largest] = _storage[index];
			_storage[index] = tempData;

			trickleDown(largest);
		}
	}

	public int size() {
		return _numElements;
	}
}
