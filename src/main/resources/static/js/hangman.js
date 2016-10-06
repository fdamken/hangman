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
'use strict';

var eng = {
	hangman: 'Hangman',
	generateGame: 'Generate Game',
	or: 'or',
	useCustomWord: 'Use a custom Word:',
	wordToGuess: 'Word to Guess',
	failure: 'Failure!',
	lost: 'You lost the game.',
	tryAgain: 'Try again!',
	win: 'Win!',
	won: 'You won the game. Congratulations!',
	playAgain: 'Play again!',
	of: 'of',
	allowedMistakes: 'allowed mistakes',
	error: 'Error',
	ajaxError: 'Failed to retrieve data from remote service!'
};
var ger = {
	hangman: 'Galgenmännchen',
	generateGame: 'Spiel Generieren',
	or: 'oder',
	useCustomWord: 'Nutze ein eigenes Wort:',
	wordToGuess: 'Wort zu raten',
	failure: 'Niederlage!',
	lost: 'Du hast das Spiel verloren.',
	tryAgain: 'Erneut versuchen!',
	win: 'Sieg!',
	won: 'Du hast das Spiel gewonnen. Herlichen Glückwunsch!',
	playAgain: 'Erneut spielen!',
	of: 'von',
	allowedMistakes: 'erlaubten Fehlversuchen',
	error: 'Fehler',
	ajaxError: 'Während des Ladens externer Inhalte ist ein Fehler aufgetreten!'
};

var hangman = angular.module('hangman', [ ]);

hangman.controller('hangmanController', ['$scope', '$http', '$window', function ($scope, $http, $window) {
	$scope.MAX_MISTAKES = 11;
	$scope.language = ($window.navigator.language || $window.navigator.userLanguage).substring(0, 2) === 'de' ? 'GER' : 'ENG';

	var ajaxError = function () {
		BootstrapDialog.show({
			type : BootstrapDialog.TYPE_WARNING,
			title : $scope.i18n.error,
			message : $scope.i18n.ajaxError
		});
	};

	var resetLetters = function () {
		var letters = ['q', 'w', 'e', 'r', 't', 'y', 'u', 'i', 'o', 'p', 'a', 's', 'd', 'f', 'g', 'h', 'j', 'k', 'l', 'z', 'x', 'c', 'v', 'b', 'n', 'm'];
		$scope.letters = letters.map(function (letter) {
			return {
				name: letter,
				displayName: letter.toUpperCase(),
				correct: null
			};
		});
	};

	var copyRelevantGameData = function (game) {
		$scope.game.maskedWord = game.maskedWord;
		$scope.game.mistakeCount = game.mistakeCount;
		$scope.game.solved = game.solved;
	};

	$scope.generateGame = function () {
		$http.get('/api/games?lang=' + encodeURIComponent($scope.language)).then(function (response) {
			$scope.game = response.data.game;

			resetLetters();
		}, ajaxError);
	};
	$scope.createGame = function () {
		$http.put('/api/games/' + encodeURIComponent($scope.customWord)).then(function (response) {
			$scope.game = response.data.game;

			resetLetters();
		}, ajaxError);
	}

	$scope.guess = function(letter) {
		$http.post('/api/games/' + encodeURIComponent($scope.game.id), {
			character: letter.name
		}).then(function (response) {
			letter.correct = response.data.correct;

			copyRelevantGameData(response.data.game);
		}, ajaxError);
	};

	$scope.$watch('game.mistakeCount', function () {
		if (($scope.game || { }).mistakeCount >= $scope.MAX_MISTAKES) {
			$scope.game.loosed = true;
		}
	});

	$scope.$watch('language', function () {
		if ($scope.language === 'GER') {
			$scope.i18n = ger;
		} else {
			$scope.i18n = eng;
		}
	});
}]);
