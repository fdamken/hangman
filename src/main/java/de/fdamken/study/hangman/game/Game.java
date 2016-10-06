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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

/**
 * Represents a single Hangman game with one word.
 *
 */
@Getter
public class Game {
    /**
     * The pattern that is used for checking whether a word contains only
     * letters (case insensitive).
     *
     */
    private static final Pattern WORD_PATTERN = Pattern.compile("^[a-z]+$", Pattern.CASE_INSENSITIVE);

    /**
     * Contains all running games packaed by their ID.
     *
     */
    private static final Map<UUID, Game> RUNNING_GAMES = new HashMap<>();

    /**
     * The unique ID of this game.
     *
     */
    private final UUID id = UUID.randomUUID();

    /**
     * All characters that the player has guessed already.
     *
     */
    private final List<Character> guessedCharacters = new ArrayList<>();
    /**
     * The initial word.
     *
     */
    private final String word;
    /**
     * The characters of the initial word.
     *
     */
    private final List<Character> characters;

    /**
     * A mistake counter (one mistake is when the player guesses a wrong
     * character).
     *
     */
    private int mistakeCount = 0;
    /**
     * Whether this game is solved already.
     *
     */
    private boolean solved;

    /**
     * Constructor of Game.
     *
     * @param word
     *            The word to use for the game. This must only contain letters.
     */
    public Game(final String word) {
        if (!Game.WORD_PATTERN.matcher(word).matches()) {
            throw new IllegalArgumentException("Word must only contain letters!");
        }

        this.word = word.toUpperCase();
        this.characters = this.word.chars().mapToObj(x -> (char) x).collect(Collectors.toList());

        Game.RUNNING_GAMES.put(this.id, this);
    }

    /**
     * Finds a game with the given ID and returns it.
     *
     * @param gameId
     *            The ID of the game to find.
     * @return The game, if any. Otherwise <code>null</code>.
     */
    public static Game getGame(final UUID gameId) {
        return Game.RUNNING_GAMES.get(gameId);
    }

    /**
     * {@inheritDoc}
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return this.word;
    }

    /**
     * Executes a guess (case insensitive).
     *
     * <ul>
     * <li>If the player has already guessed the letter, nothing happens and
     * <code>true</code> is returned.</li>
     * <li>If the player has guessed correctly, the character is marked as
     * guessed and <code>true</code> is returned. If this finished the game,
     * {@link #solved} is set to <code>true</code> and the game is removed from
     * the {@link #RUNNING_GAMES running games}.</li>
     * <li>If the player has guessed wrong, {@link #mistakeCount} is incremented
     * and <code>false</code> is returned. No matter whether the player has
     * already tried the character.</li>
     * </ul>
     *
     * @param rawCharacter
     *            The character to guess.
     * @return Whether the guess was cotrrect or not (see above).
     */
    public boolean guess(final char rawCharacter) {
        final char character = Character.toUpperCase(rawCharacter);
        if (this.guessedCharacters.contains(character)) {
            return true;
        }
        if (this.characters.contains(character)) {
            this.guessedCharacters.add(character);
            if (!this.getMaskedWord().contains("_")) {
                Game.RUNNING_GAMES.remove(this.id);

                this.solved = true;
            }
            return true;
        }
        this.mistakeCount++;
        return false;
    }

    /**
     *
     * @return The masked word. That is all characters that where not guessed
     *         already are replaced with an underscore (e.g.
     *         <code>H _ L L _</code>). A blank is placed between all
     *         characters.
     */
    @JsonProperty
    public String getMaskedWord() {
        return this.characters.stream().map(x -> this.guessedCharacters.contains(x) ? String.valueOf(x) : "_")
                .collect(Collectors.joining(" "));
    }
}
