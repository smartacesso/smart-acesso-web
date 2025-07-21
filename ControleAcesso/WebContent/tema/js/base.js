jQuery(document).ready(function() {
	aplicaZerosEsquerda();
	
	jQuery(document).on('pfAjaxComplete', function() {
    	aplicaZerosEsquerda();
	});
	
	applySessionTimeoutMessage();
	
});


function alternarFiltrosAvançados(){
	try {
		jQuery('.filtro-input, .filtro-select-menu, .filtro-date-picker, .filtro-btConsulta').toggle();
		jQuery('.filtros-avancados').slideToggle();
	}
	catch (e) {
		console.log(e);
	}
}

function download(url){
	window.location = url;
}

function sincronizarFiltro(elem, className) {
	var newValue = elem.value;
	jQuery('.' + className).each(function() {
		jQuery(this)[0].value = newValue;
		
		if(jQuery(this).hasClass('ui-selectonemenu')){
			jQuery(this).find('select').each(function() {
				jQuery(this)[0].value = newValue;
			});
		}
	});
}

function aplicaZerosEsquerda(){
	
	jQuery('.left-zeros').focus(function (e){
		if(e.target.value == ""){
			for(var i = 0; i < e.currentTarget.maxLength; i++){
				e.target.value += '0';
			}
		}
	});
	
	jQuery('.left-zeros').keyup(function (e) {
		
		if(e.target.value.length < e.currentTarget.maxLength){
			e.target.value = e.target.value.padStart(e.currentTarget.maxLength, '0');
		
		} else if(!isNaN(e.originalEvent.key) && e.target.value[0] == "0"){
			var novo = "";
			for(var i = 1; i < e.target.value.length; i++){
				novo += e.target.value[i];
			}
			novo += e.originalEvent.key;
			e.target.value = novo;
		}
	});
}


function customExtender() {
    this.cfg.grid = {
		drawGridLines: false,
		background: '#fff',
		shadow: false,
		borderWidth: 0.0
    }
}

var startTime   = 60*120*4
var currentTime = 0
var myTimer
var myTimerSpeed = 1000 // 1 s

function resetTimer(){
    stopTimer();
    currentTime = startTime;
    $('#timer').html(secondsToMinutes(currentTime));
}

function applySessionTimeoutMessage(){
   
   stopTimer();
   resetTimer();

   myTimer = setInterval(timerTick,myTimerSpeed);
    
}

function stopTimer(){
    clearInterval(myTimer);
}

function timerTick(){
    currentTime--;
    $('#timer').html(secondsToMinutes(currentTime));
    
    if(currentTime == 0){
        stopTimer();
    }
}

function secondsToMinutes(time){
    return pad(Math.floor(time / 60 / 60),2)
    			+':'+pad(Math.floor((time / 60) % 60),2)
    			+':'+pad(Math.floor(time % 60),2);
}

function pad(num, size) {
    var s = num+"";
    while (s.length < size) s = "0" + s;
    return s;
}

function printSimpleDiv(divID) {
    //Get the HTML of div
    var divElements = document.getElementById(divID).innerHTML;
    var styleElements = "<style>@page{margin-top: 0.1cm;margin-bottom: 0.1cm;} body{zoom: 100% !important;} @media print { body{zoom: 100% !important;} }</style>";
    
    var content = "<html><head>"+styleElements+"<title></title></head><body>" + 
				divElements + "</body>";
    
    var mywindow = window.open('', 'QR Code', 'height=400,width=600');
    mywindow.document.write(content);
    
    mywindow.document.close(); // necessary for IE >= 10
    mywindow.focus(); // necessary for IE >= 10

    mywindow.print();
    mywindow.close();
}