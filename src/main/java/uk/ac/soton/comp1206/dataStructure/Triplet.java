package uk.ac.soton.comp1206.dataStructure;

/**
 * A Tuple data structure which can hold any 3 objects
 * @param <F> The type of the first Object
 * @param <S> The type of the second Object
 * @param <T> The type of the third Object
 */
public class Triplet<F, S, T> {
    
    private F object0;
    private S object1;
    private T object2;
    
    /**
     * Constructor to create a new Triplet with 3 objects
     * @param object0 the first object
     * @param object1 the second object
     * @param object2 the third object
     */
    public Triplet(F object0, S object1, T object2) {
        this.object0 = object0;
        this.object1 = object1;
        this.object2 = object2;
    }
    
    /**
     * Returns the first object
     * @return the first object
     */
    public F get0() {
        return object0;
    }
    
    /**
     * Returns the second object
     * @return the second object
     */
    public S get1() {
        return object1;
    }
    
    /**
     * Returns the third object
     * @return the third object
     */
    public T get2() {
        return object2;
    }
}
