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
package de.fdamken.study.hangman.game;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.SecureRandom;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;

/**
 * A game generator is used to parse the dictionaries on the classpath and
 * chooses a random word from them and creates a {@link Game}.
 *
 */
@UtilityClass
public class GameGenerator {
    /**
     * The RNG.
     *
     */
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * Generates a {@link Game} with a random word in the given
     * {@link Language}.
     *
     * @param language
     *            The {@link Language} to generate a {@link Game} for.
     * @return The generated {@link Game}.
     * @throws IOException
     *             If any I/O error occurs.
     */
    public Game generate(final Language language) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                GameGenerator.class.getClassLoader().getResourceAsStream(language.getLocale() + ".dict")))) {
            // This chooses a random word from the word list by using reservoir
            // sampling (https://en.wikipedia.org/wiki/Reservoir_sampling).
            //
            // The result is initialized as null.
            // The 1. line has a chance of 1/1 = 100% to replace the result.
            // The 2. line has a chance of 1/2 = 50% to replace the result.
            // The 3. line has a chance of 1/3 ≈ 33.3% to replace the result.
            // The 4. line has a chance of 1/4 = 25% to replace the result.
            // The 5. line has a chance of 1/5 = 12.5% to replace the result.
            // ...
            // The n. line has a chance of 1/n to replace the result.

            String result = null;
            int n = 0;
            String line = null;
            while ((line = reader.readLine()) != null) {
                n++;
                if (GameGenerator.RANDOM.nextInt(n) == 0) {
                    result = line;
                }
            }
            return new Game(result);
        }
    }

    /**
     * Represents a simple language.
     *
     */
    @RequiredArgsConstructor
    @Getter
    public static enum Language {
        /**
         * English (USA).
         *
         */
        ENG("en_US"),
        /**
         * German (Germany).
         *
         */
        GER("de_DE");

        /**
         * The locale of the language. This is the name of the dictionary file.
         *
         */
        private final String locale;
    }
}
