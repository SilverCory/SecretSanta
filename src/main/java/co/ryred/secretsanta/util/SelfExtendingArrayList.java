package co.ryred.secretsanta.util;

import java.util.ArrayList;

/**
 * Created by Beth on 14/12/2015.
 */
public class SelfExtendingArrayList<T> extends ArrayList<T> {

    @Override
    public T set(int index, T element) {

        if( size() < index + 1 && element instanceof String ) {
            System.out.println("Is string and size < index" + size() + " | " + index);
            while( size() < index + 1 ) {
                add( (T)"" );
                System.out.println( "Added one! New size: " + size() );
            }
        }

        return super.set(index, element);
    }
}
