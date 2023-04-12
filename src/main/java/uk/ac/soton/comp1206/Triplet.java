package uk.ac.soton.comp1206;

public class Triplet<F, S, T> {
    
    private F object0;
    private S object1;
    private T object2;

    public Triplet(F object0, S object1, T object2) {
        this.object0 = object0;
        this.object1 = object1;
        this.object2 = object2;
    }
    
    public F get0() {
        return object0;
    }
    
    public S get1() {
        return object1;
    }
    
    public T get2() {
        return object2;
    }
}
