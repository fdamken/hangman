<#--
 #%L
 hangman
 %%
 Copyright (C) 2016 Fabian Damken
 %%
 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:
 
 The above copyright notice and this permission notice shall be included in
 all copies or substantial portions of the Software.
 
 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 THE SOFTWARE.
 #L%
-->
<#import "hangman.ftl" as hangman>

<!DOCTYPE html>
<html>
<head>
	<@hangman.head />

	<title>Hangman</title>
</head>
<body ng-app="hangman" ng-cloak>
	<@hangman.body />

	<div class="container">
		<div class="panel panel-default" ng-controller="hangmanController">
			<div class="panel-heading">
				<h3 class="heading inline">{{ i18n.hangman }}</h3>
				<form id="language-select" class="form-inline inline pull-right">
					<select class="form-control" ng-model="language">
						<option value="ENG" selected>English</option>
						<option value="GER">Deutsch</option>
					</select>
				</form>
			</div>
			<div class="panel-body">
				<button type="button" class="btn btn-primary" ng-click="generateGame()">{{ i18n.generateGame }}</button>
				{{ i18n.or }}
				<form class="form-inline inline" ng-submit="createGame()">
					<button type="submit" class="btn btn-default">{{ i18n.useCustomWord }}</button>
					<input type="text" id="form-word" class="form-control" placeholder="{{ i18n.wordToGuess }}" ng-model="customWord">
				</form>

				<div class="game" ng-show="game">
					<hr />

					<div class="alert alert-danger" ng-show="game.loosed">
						<strong>{{ i18n.failure }}</strong> {{ i18n.lost }} <a href ng-click="generateGame()">{{ i18n.tryAgain }}</a>
					</div>
					<div class="alert alert-success" ng-show="game.solved">
						<strong>{{ i18n.win }}</strong> {{ i18n.won }} <a href ng-click="generateGame()">{{ i18n.playAgain }}</a>
					</div>

					<center class="guess">
						<div class="data">
							<div class="word monospace">{{ (game.solved || game.loosed) ? game.word : game.maskedWord }}</div>
							<div class="mistakes">{{ game.mistakeCount }} {{ i18n.of }} {{ MAX_MISTAKES }} {{ i18n.allowedMistakes }}</div>
						</div>

						<div class="keyboard" ng-show="!game.solved &amp;&amp; !game.loosed">
							<div class="top">
								<button class="btn letter" ng-repeat="letter in letters | limitTo : 10 : 0" ng-disabled="letter.correct !== null" ng-class="{ 'btn-default' : letter.correct === null, 'btn-success' : letter.correct === true, 'btn-danger' : letter.correct === false }" ng-click="guess(letter)">{{ letter.displayName }}</button>
							</div>
							<div class="middle">
								<button class="btn letter" ng-repeat="letter in letters | limitTo : 9 : 10" ng-disabled="letter.correct !== null" ng-class="{ 'btn-default' : letter.correct === null, 'btn-success' : letter.correct === true, 'btn-danger' : letter.correct === false }" ng-click="guess(letter)">{{ letter.displayName }}</button>
							</div>
							<div class="bottom">
								<button class="btn letter" ng-repeat="letter in letters | limitTo : 7 : 19" ng-disabled="letter.correct !== null" ng-class="{ 'btn-default' : letter.correct === null, 'btn-success' : letter.correct === true, 'btn-danger' : letter.correct === false }" ng-click="guess(letter)">{{ letter.displayName }}</button>
							</div>
						</div>
					</center>
				</div>
			</div>
		</div>
	</div>
</body>
</html>
