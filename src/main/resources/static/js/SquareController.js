var SquareModule = (function () {
    const DEFAULT_ADDRESS = '';
    
    var displayAnswer = function (number) {
	console.log('displayAnswer LOG:', number);
	// $('#cnum').text(number);
	$('#cnum').attr('value', number);
    };

    var init = function () {
	$('#squareButton').click(function (){
	    var inputNumber = Number($('#tnum').val());
	    console.log('inputNumber:', inputNumber);
	    getSquare(inputNumber);
	});
	console.log('Configuracion Inicial Efectuada.');
    };
    
    var getSquare = function (number) {
	console.log('Entra al then');
	console.log('number:', number);
	axios.get(DEFAULT_ADDRESS + '/square?value=' + number)
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
