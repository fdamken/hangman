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
package de.fdamken.study.hangman.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnNotWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import de.fdamken.study.hangman.game.Game;
import de.fdamken.study.hangman.game.GameGenerator;
import de.fdamken.study.hangman.game.GameGenerator.Language;

/**
 * Configuration if the CLI client is used. This is the default.
 *
 */
@Configuration
@ConditionalOnNotWebApplication
public class CliConfiguration {
    /**
     * The number of mistakes one must make in order to loose.
     *
     */
    private static final int MISTAKES_TO_LOOSE = 11;

    /**
     * Creates the {@link Runner}.
     *
     * @return The {@link Runner}.
     */
    @Bean
    public CommandLineRunner runner() {
        return new Runner();
    }

    /**
     * The {@link CommandLineRunner} that is initialized by Spring and executes
     * like a normal main-method.
     *
     */
    public static class Runner implements CommandLineRunner {
        /**
         * {@inheritDoc}
         *
         * @see org.springframework.boot.CommandLineRunner#run(java.lang.String[])
         */
        @Override
        public void run(final String... args) throws IOException {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
                final Game game = GameGenerator.generate(Language.GER);

                System.out.println("A random GERMAN game was generated.");
                System.out.println("Here is it: " + game.getMaskedWord());
                System.out.println("You are allowed to do " + CliConfiguration.MISTAKES_TO_LOOSE + " mistakes!");
                while (!game.isSolved() && game.getMistakeCount() <= CliConfiguration.MISTAKES_TO_LOOSE) {
                    System.out.println();
                    System.out.println("Word: " + game.getMaskedWord());
                    System.out.println("Total mistakes: " + game.getMistakeCount());
                    System.out.print("Please enter one character to guess: ");

                    final String line = reader.readLine();
                    if (line.length() == 1) {
                        final char character = line.charAt(0);
                        if (game.guess(character)) {
                            System.out.println("That was correct!");
                        } else {
                            System.out.println("That was not correct!");
                        }
                    } else {
                        System.out.println("Please enter exactly one character!");
                    }
                }
                System.out.println();
                if (game.isSolved()) {
                    System.out.println("You won!");
                } else {
                    System.out.println("You lost!");
                }
                System.out.println("The word was: " + game.getWord());
            }
        }
    }
}
