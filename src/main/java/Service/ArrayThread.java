package Service;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Arnold on 15.04.2018.
 */
public class ArrayThread<E> extends ArrayList<E> {
    synchronized public boolean merge (Collection<? extends E> c) {
        return addAll(c);
    }
}
