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
package de.fdamken.study.hangman.rest;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.fdamken.study.hangman.game.Game;
import de.fdamken.study.hangman.game.GameGenerator;
import de.fdamken.study.hangman.game.GameGenerator.Language;

/**
 * The REST controller for the game API.
 *
 */
@RestController
@RequestMapping("/api/games")
public class GameRestController {
    /**
     * Generates a game.
     *
     * @param language
     *            The language to generate a game for.
     * @return The generated game wrapped inside a {@link ResponseEntity}.
     * @throws IOException
     *             If any I/O error occurs.
     */
    @RequestMapping
    public ResponseEntity<?> generateGame(@RequestParam(name = "lang",
                                                        defaultValue = "ENG") final Language language)
            throws IOException {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.wrapGame(GameGenerator.generate(language)));
    }

    /**
     * Creates a game with the given custom word.
     *
     * @param word
     *            The custom word.
     * @return The newly created game wrapped inside a {@link ResponseEntity}.
     */
    @RequestMapping(path = "/{word}",
                    method = RequestMethod.PUT)
    public ResponseEntity<?> createGame(@PathVariable final String word) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.wrapGame(new Game(word)));
    }

    /**
     * Retrieves the game with the given ID.
     *
     * @param gameId
     *            The ID of the game to retrieve.
     * @return The game, if any, wrapped inside a {@link ResponseEntity}.
     */
    @RequestMapping(path = "/{gameId}")
    public ResponseEntity<?> retrieveGame(@PathVariable final UUID gameId) {
        final Game game = Game.getGame(gameId);
        if (game == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(this.wrapGame(game));
    }

    /**
     * Executes a guess on the game with the given ID.
     *
     * @param gameId
     *            The ID of the game to execute the guess for.
     * @param body
     *            The body of the request containing the character to guess.
     * @return The modified game erapped inside a {@link ResponseEntity}.
     */
    @RequestMapping(path = "/{gameId}",
                    method = RequestMethod.POST)
    public ResponseEntity<?> quess(@PathVariable final UUID gameId, @RequestBody final Map<String, String> body) {
        final String characterString = body.get("character");
        if (characterString == null || characterString.length() != 1) {
            return ResponseEntity.unprocessableEntity().build();
        }

        final Game game = Game.getGame(gameId);
        if (game == null) {
            return ResponseEntity.notFound().build();
        }

        final boolean correct = game.guess(characterString.charAt(0));
        final Map<String, Object> result = this.wrapGame(game);
        result.put("correct", correct);
        return ResponseEntity.ok(result);
    }

    /**
     * Wraps the given game into a map.
     *
     * @param game
     *            The game to wrap.
     * @return The map with the game inside (property <code>game</code>).
     */
    private Map<String, Object> wrapGame(final Game game) {
        final Map<String, Object> result = new HashMap<>();
        result.put("game", game);
        return result;
    }
}
