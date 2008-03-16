package eg.jconch.cachemap;

import static org.easymock.EasyMock.*;

import javax.persistence.EntityManager;

import jconch.cache.CacheMap;
import jconch.functor.Transformer5;

/**
 * Demonstrates a database entity look-up which is cached.
 * 
 * @author Robert
 */
public class NeedsADatabaseEntityLookup {

    private static final CacheMap<Integer, ToyEntity> entityCache = new CacheMap<Integer, ToyEntity>(
            new Transformer5<Integer, ToyEntity>() {
                public ToyEntity transform(final Integer primaryKey) {
                    return getEntityManager().find(ToyEntity.class, primaryKey);
                }
            });

    public static ToyEntity getEntity(final int primaryKey) {
        return entityCache.get(primaryKey);
    }

    /*
     * Everything past here is just stupid impl details.
     */

    static EntityManager getEntityManager() {
        final EntityManager mockEm = createMock(EntityManager.class);
        expect(mockEm.find(ToyEntity.class, anyObject())).andReturn(new ToyEntity());
        replay(mockEm);
        return mockEm;
    }

    static final class ToyEntity {
        // Empty class
    }
}
