package jconch.lock;

/**
 * This class should be used to implement logically equivalent locks.
 * <p>
 * Currently, Java has a major failing in its synchronization mechanism. The
 * <code>synchronize</code> keyword operates at the reference level, which
 * means that two different but logically equivalent objects can enter the same
 * synchronized block concurrently. Since POJOs are often generated such that
 * they are logically equivalent, but referentially different, this is a
 * problem.
 * <p>
 * A single instance of this class will provide the same {@link Object} instance
 * for each logically equivalent object passed in.
 * 
 * @param <OBJ_T>
 *            The type of object being processed by the lock.
 * @author Robert Fischer
 * 
 */
public class SyncLogEqLock<OBJ_T> extends AbstractLogEqLock<OBJ_T, Object> {

    /**
     * The reference to the global instance.
     */
    private static final SyncLogEqLock<Object> global = new SyncLogEqLock<Object>();

    /**
     * Default constructor.
     */
    public SyncLogEqLock() {
        // Do nothing.
    }

    /**
     * Provides the same instance of this class every time it is called. To be
     * used when a universally-valid global instance is required.
     * 
     * @return The global instance of this class.
     */
    public static SyncLogEqLock<Object> getGlobalInstance() {
        return global;
    }

    /**
     * @return A newly-instantiated {@link Object}.
     */
    @Override
    protected Object createNewLock() {
        return new Object();
    }
}
