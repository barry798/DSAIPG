package com.phasmidsoftware.dsaipg.misc.randomwalk;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Unit tests for the RandomWalk class.
 */
public class RandomWalkTest {

    @Test
    public void testDistanceInitial() {
        RandomWalk rw = new RandomWalk();
        assertEquals(0.0, rw.distance(), 1e-7);
    }

    @Test
    public void testMove() {
        RandomWalk rw = new RandomWalk();
        rw.move(3, 4); // Move to (3, 4)
        assertEquals(5.0, rw.distance(), 1e-7); // Distance = sqrt(3^2 + 4^2)

        rw.move(-3, -4); // Move back to origin
        assertEquals(0.0, rw.distance(), 1e-7);
    }

    @Test
    public void testRandomMove() {
        RandomWalk rw = new RandomWalk();
        for (int i = 0; i < 100; i++) {
            rw.randomMove();
        }
        // Ensure the position is non-zero after 100 random moves
        assertEquals(true, rw.distance() > 0.0);
    }

    @Test
    public void testRandomWalk() {
        RandomWalk rw = new RandomWalk();
        rw.randomWalk(10); // Perform 10 steps
        // Ensure the position is non-zero after 10 steps
        assertEquals(true, rw.distance() > 0.0);
    }

    @Test
    public void testRandomWalkMulti() {
        double meanDistance = RandomWalk.randomWalkMulti(100, 10);
        // Check that the mean distance is greater than 0
        assertEquals(true, meanDistance > 0.0);
    }
}
