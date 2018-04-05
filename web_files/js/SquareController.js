var SquareModule = (function () {
    const DEFAULT_ADDRESS = '';
    
    var displayAnswer = function (number) {
	console.log('displayAnswer LOG:', number);
	// $('#cnum').text(number);
	$('#cnum').attr('value', number);
    };

    var init = function () {
	$('#squareButton').click(function (){
	    var inputNumber = $('#tnum').val();
	    console.log('inputNumber:', inputNumber);
	    getSquare(inputNumber);
	});

	$('#cnum').attr('disabled', true);
	console.log('Configuracion Inicial Efectuada.');
    };
    
    var getSquare = function (number) {
	console.log('Entra al then');
	console.log('number:', number);
	axios.get(DEFAULT_ADDRESS + '/square.do?value=' + number)
	    .then(function (payload) {
		displayAnswer(payload.data);
	    })
	    .catch(function (error) {
		console.log(error);
		alert("Lo sentimos, hubo un error al procesar su solicitud");
	    });
    };

    return {
	init : init
    };
})();
