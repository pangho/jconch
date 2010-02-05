package jconch.lock;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Class that provides {@link ReadWriteLock} instances tagged by logically
 * equivalent objects.
 * 
 * @param <OBJ_T>
 *            The type of object compared by the logical equivalent locks.
 * @author Robert Fischer
 */
public class RWLogEqLock<OBJ_T> extends AbstractLogEqLock<OBJ_T, ReadWriteLock> {

    /**
     * The global instance.
     */
    private static final RWLogEqLock<Object> global = new RWLogEqLock<Object>();

    /**
     * Provides the same instance of this class every time.
     * 
     * @return A singleton instance.
     */
    public static RWLogEqLock<Object> getGlobalInstance() {
        return global;
    }

    /**
     * Constructor.
     */
    public RWLogEqLock() {
        // Does nothing.
    }

    @Override
    protected ReadWriteLock createNewLock() {
        return new ReentrantReadWriteLock();
    }
}
