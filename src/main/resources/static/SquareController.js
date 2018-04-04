var SquareModule = (function () {
	var displayAnswer = function (number) {
		console.log(number);
	};

	var init = function () {
		$('#squareButton').click(function (){
			var inputNumber = Number($('#tnum').val());
			getSquare(inputNumber);
		});
		console.log('Configuracion Inicial Efectuada.');
	};

	var getSquare = function (number) {
		console.log('Entrea al then');
		axios.get('http://localhost:8080/square?value=' + number)
			.then(
				displayAnswer
			)
			.catch(function (error) {
				console.log(error);
				alert("There is a problem with our servers. We apologize for the inconvince, please try again later.");
			});
	};

	return {
		init : init
	};
})();