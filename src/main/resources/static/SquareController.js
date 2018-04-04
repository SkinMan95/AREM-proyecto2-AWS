var SquareModule = (function () {
	var displayAnswer = function (number) {
		
	};

	var getSquare = function (number) {
		axios.get('/square?value=' + number)
			.then(
				displayAnswer(number);
			)
			.catch(function (error) {
				console.log(error);
				alert("There is a problem with our servers. We apologize for the inconvince, please try again later.");
			});
	};

	return {
		getSquare: getSquare
	};
})();