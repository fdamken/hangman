/*-
 * #%L
 * hangman
 * %%
 * Copyright (C) 2016 Fabian Damken
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */
package de.fdamken.study.hangman;

import org.junit.Assert;
import org.junit.Test;

import de.fdamken.study.hangman.game.Game;

/**
 * Tests {@link Game}.
 *
 */
@SuppressWarnings("javadoc")
public class GameTest {
    @Test
    public void testWithoutGuesses() {
        final Game game = new Game("HeLlO");

        this.expectGame(game, 0, "_ _ _ _ _");
    }

    @Test
    public void testWithOneGuess() {
        final Game game = new Game("HeLlO");

        Assert.assertTrue(game.guess('e'));

        this.expectGame(game, 0, "_ E _ _ _");
    }

    @Test
    public void testWithOneMultiGuess() {
        final Game game = new Game("HeLlO");

        Assert.assertTrue(game.guess('l'));

        this.expectGame(game, 0, "_ _ L L _");
    }

    @Test
    public void testWithMultipleGuesses() {
        final Game game = new Game("HeLlO");

        Assert.assertTrue(game.guess('H'));
        Assert.assertTrue(game.guess('l'));

        this.expectGame(game, 0, "H _ L L _");
    }

    @Test
    public void testWithOneWrongGuess() {
        final Game game = new Game("HeLlO");

        Assert.assertFalse(game.guess('a'));

        this.expectGame(game, 1, "_ _ _ _ _");
    }

    @Test
    public void testWithMultipleWrongGuesses() {
        final Game game = new Game("HeLlO");

        Assert.assertFalse(game.guess('a'));
        Assert.assertFalse(game.guess('i'));

        this.expectGame(game, 2, "_ _ _ _ _");
    }

    @Test
    public void testWithMixedWrongAndCorrectGuesses() {
        final Game game = new Game("HeLlO");

        Assert.assertTrue(game.guess('e'));
        Assert.assertTrue(game.guess('l'));

        Assert.assertFalse(game.guess('a'));
        Assert.assertFalse(game.guess('i'));

        this.expectGame(game, 2, "_ E L L _");
    }

    @Test
    public void testRepeatedGuesses() {
        final Game game = new Game("HeLlO");

        Assert.assertTrue(game.guess('e'));
        Assert.assertTrue(game.guess('e'));

        this.expectGame(game, 0, "_ E _ _ _");
    }

    @Test
    public void testRepeatedWrongGuesses() {
        final Game game = new Game("HeLlO");

        Assert.assertFalse(game.guess('a'));
        Assert.assertFalse(game.guess('a'));

        this.expectGame(game, 2, "_ _ _ _ _");
    }

    /**
     * Executes all required asserts on the given game.
     *
     * @param game
     *            The game to test.
     * @param expectedMistakeCount
     *            The expected mistake count.
     * @param expectedMaskedWord
     *            The expected result of {@link Game#getMaskedWord()}
     */
    private void expectGame(final Game game, final int expectedMistakeCount, final String expectedMaskedWord) {
        Assert.assertTrue(expectedMistakeCount >= 0);
        Assert.assertNotNull(game);
        Assert.assertNotNull(expectedMaskedWord);

        Assert.assertEquals(expectedMistakeCount, game.getMistakeCount());
        Assert.assertEquals(expectedMaskedWord, game.getMaskedWord());
    }
}
