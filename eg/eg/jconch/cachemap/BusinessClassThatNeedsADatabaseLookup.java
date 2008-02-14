package eg.jconch.cachemap;

import static org.easymock.EasyMock.*;

import javax.persistence.EntityManager;

import jconch.cache.CacheMap;
import jconch.lock.SyncLogEqLock;

import org.apache.commons.collections.Transformer;
import org.apache.commons.lang.UnhandledException;

/**
 * Demonstrates a database look-up which is cached
 * 
 * @author Robert
 */
public class BusinessClassThatNeedsADatabaseLookup {

    private static final CacheMap<Integer, ToyEntity> entityCache = new CacheMap<Integer, ToyEntity>(new Transformer() {
        public Object transform(final Object primaryKey) {
            return getEntityManager().find(ToyEntity.class, primaryKey);
        }
    }, new SyncLogEqLock<Integer>());

    public static ToyEntity getEntity(final int primaryKey) {
        return entityCache.get(primaryKey);
    }

    /*
     * Everything past here is just stupid impl details.
     */

    static EntityManager getEntityManager() {
        try {
            final EntityManager mockEm = createMock(EntityManager.class);
            expect(mockEm.find(ToyEntity.class, anyObject())).andReturn(new ToyEntity());
            replay(mockEm);
            return mockEm;
        } catch (final Exception e) {
            throw new UnhandledException("WTF?  Shouldn't be seen.", e);
        }
    }

    static final class ToyEntity {
        // Empty class
    }
}
