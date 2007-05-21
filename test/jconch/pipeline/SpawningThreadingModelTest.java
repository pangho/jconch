package jconch.pipeline;

import static org.junit.Assert.*;
import jconch.pipeline.impl.SpawningThreadingModel;

import org.apache.commons.lang.NullArgumentException;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.lang.time.DateUtils;
import org.junit.Test;

public class SpawningThreadingModelTest {

    @Test
    public void defaultConstructorDoesNotExplode() {
        new SpawningThreadingModel();
    }

    @Test(expected = IllegalArgumentException.class)
    public void longConstructorExplodesOnZero() {
        new SpawningThreadingModel(0L);
    }

    @Test(expected = IllegalArgumentException.class)
    public void longConstructorExplodesOnNegative() {
        new SpawningThreadingModel(-1L);
    }

    @Test
    public void longConstructorSetsSpawnPeriod() {
        final long next = RandomUtils.nextInt(Integer.MAX_VALUE);
        assertTrue("Next is nonpositive", next > 0);
        final SpawningThreadingModel model = new SpawningThreadingModel(next);
        assertEquals(next, model.getSpawnPeriod());
    }

    @Test(expected = NullArgumentException.class)
    public void executeExplodesOnNull() {
        new SpawningThreadingModel(1L).execute(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setSpawnPeriodExplodesOnZero() {
        new SpawningThreadingModel().setSpawnPeriod(0L);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setSpawnPeriodExplodesOnNegative() {
        new SpawningThreadingModel().setSpawnPeriod(-1L);
    }

    @Test
    public void setSpawnPeriodSetsSpawnPeriod() {
        final long next = RandomUtils.nextInt(Integer.MAX_VALUE);
        assertTrue("Next is nonpositive", next > 0);
        final SpawningThreadingModel model = new SpawningThreadingModel();
        model.setSpawnPeriod(next);
        assertEquals(next, model.getSpawnPeriod());
    }

    @Test
    public void eachExecuteIsItsOwnThread() {
        final SpawningThreadingModel fixture = new SpawningThreadingModel(1L);
        final ThreadCapturingPipelineStage stage = new ThreadCapturingPipelineStage(fixture, 100);
        stage.start();
        final long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() < startTime + DateUtils.MILLIS_PER_SECOND && !stage.isFinished()) {
            Thread.yield();
        }
        for (int i = 0; i < stage.executeThreads.size(); i++) {
            for (int k = i + 1; k < stage.executeThreads.size(); k++) {
                assertFalse("Saw the same thread at index [" + i + "] and [" + k + "]",
                        stage.executeThreads.get(i) == stage.executeThreads.get(k));
            }
        }
    }

}
