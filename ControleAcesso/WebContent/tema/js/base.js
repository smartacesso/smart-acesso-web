jQuery(document).ready(function() {
	aplicaZerosEsquerda();
	saInitSearchFilters();

	jQuery(document).on('pfAjaxComplete', function() {
		aplicaZerosEsquerda();
		saRestoreFilterFields();
		saSyncFilterMirrors();
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

function saResolveFilterElement(node) {
	if (!node) {
		return null;
	}

	if (node.tagName === 'INPUT' || node.tagName === 'SELECT' || node.tagName === 'TEXTAREA') {
		return node;
	}

	var calendar = jQuery(node).closest('.ui-calendar');
	if (calendar.length) {
		return calendar.find('input').get(0) || null;
	}

	var menu = jQuery(node).closest('.ui-selectonemenu');
	if (menu.length) {
		return menu.find('select').get(0) || menu.find('input').get(0) || null;
	}

	return jQuery(node).find('input, select, textarea').first().get(0) || null;
}

function saGetFieldValue(field) {
	if (!field) {
		return '';
	}
	if (field.tagName === 'SELECT') {
		return field.value;
	}
	return field.value != null ? field.value : '';
}

function saSetFieldValue(field, value) {
	if (!field) {
		return;
	}

	var val = value != null ? value : '';

	if (field.tagName === 'SELECT') {
		field.value = val;
		jQuery(field).trigger('change');
		return;
	}

	field.value = val;
	jQuery(field).trigger('input').trigger('change');

	var calendar = jQuery(field).closest('.ui-calendar');
	if (calendar.length) {
		calendar.find('input').val(val);
	}
}

function saSetFieldDisabled(field, disabled) {
	if (!field) {
		return;
	}

	field.disabled = !!disabled;

	var calendar = jQuery(field).closest('.ui-calendar');
	if (calendar.length) {
		calendar.find('input, button').prop('disabled', !!disabled);
	}

	var menu = jQuery(field).closest('.ui-selectonemenu');
	if (menu.length) {
		menu.find('select, input').prop('disabled', !!disabled);
		if (disabled) {
			menu.addClass('ui-state-disabled');
		} else {
			menu.removeClass('ui-state-disabled');
		}
	}
}

function sincronizarFiltro(elem, className) {
	var source = saResolveFilterElement(elem) || elem;
	var newValue = saGetFieldValue(source);

	jQuery('.' + className).each(function() {
		var target = saResolveFilterElement(this) || this;
		saSetFieldValue(target, newValue);
	});
}

function saCollectSyncGroups(form) {
	var groups = {};

	jQuery(form).find('[class*="filtro-"][class*="-sync"]').each(function() {
		var matches = (this.className || '').match(/filtro-[a-z0-9-]+-sync/gi) || [];
		var field = saResolveFilterElement(this);

		if (!field) {
			return;
		}

		matches.forEach(function(rawClass) {
			var syncClass = rawClass.toLowerCase();
			if (!groups[syncClass]) {
				groups[syncClass] = [];
			}
			if (groups[syncClass].indexOf(field) === -1) {
				groups[syncClass].push(field);
			}
		});
	});

	return groups;
}

function saPickFilterValue(fields) {
	var active = document.activeElement;
	var i;

	for (i = 0; i < fields.length; i++) {
		if (fields[i] === active) {
			return saGetFieldValue(fields[i]);
		}
		if (jQuery(fields[i]).closest('.ui-calendar, .ui-selectonemenu').find(active).length) {
			return saGetFieldValue(fields[i]);
		}
	}

	var merged = '';
	for (i = 0; i < fields.length; i++) {
		var value = saGetFieldValue(fields[i]);
		if (value !== null && value !== undefined && String(value).trim() !== '') {
			merged = value;
		}
	}

	if (merged !== '') {
		return merged;
	}

	return fields.length ? saGetFieldValue(fields[0]) : '';
}

function saChooseActiveFilterField(fields) {
	var advancedVisible = jQuery('.filtros-avancados:visible').length > 0;
	var i;

	if (advancedVisible) {
		for (i = 0; i < fields.length; i++) {
			if (jQuery(fields[i]).closest('.filtros-avancados').length) {
				return fields[i];
			}
		}
	}

	for (i = 0; i < fields.length; i++) {
		if (!jQuery(fields[i]).closest('.filtros-avancados').length) {
			return fields[i];
		}
	}

	return fields[fields.length - 1];
}

function saResolveSyncGroup(fields) {
	if (!fields || fields.length <= 1) {
		return;
	}

	var merged = saPickFilterValue(fields);
	var activeField = saChooseActiveFilterField(fields);

	fields.forEach(function(field) {
		saSetFieldValue(field, merged);
		saSetFieldDisabled(field, field !== activeField);
	});
}

function saPrepareSearch(form) {
	var targetForm = form;

	if (!targetForm) {
		targetForm = document.querySelector('form[id*="form"]') || document.querySelector('form');
	}

	if (!targetForm) {
		return;
	}

	var groups = saCollectSyncGroups(targetForm);
	Object.keys(groups).forEach(function(syncClass) {
		saResolveSyncGroup(groups[syncClass]);
	});
}

function saRestoreFilterFields(form) {
	var targetForm = form || document.querySelector('form[id*="form"]') || document.querySelector('form');
	if (!targetForm) {
		return;
	}

	jQuery(targetForm).find('input, select, textarea').each(function() {
		this.disabled = false;
		jQuery(this).closest('.ui-selectonemenu').removeClass('ui-state-disabled');
		jQuery(this).closest('.ui-calendar').find('input, button').prop('disabled', false);
	});
}

function saSyncFilterMirrors(form) {
	var targetForm = form || document.querySelector('form[id*="form"]') || document.querySelector('form');
	if (!targetForm) {
		return;
	}

	var groups = saCollectSyncGroups(targetForm);
	Object.keys(groups).forEach(function(syncClass) {
		var fields = groups[syncClass];
		if (!fields || fields.length <= 1) {
			return;
		}
		var activeField = saChooseActiveFilterField(fields);
		var value = saGetFieldValue(activeField);
		fields.forEach(function(field) {
			saSetFieldValue(field, value);
		});
	});
}

function saClickSearchButton(form) {
	var targetForm = form || document.querySelector('form[id*="form"]') || document.querySelector('form');
	if (!targetForm) {
		return;
	}

	var button = targetForm.querySelector('[id$="acaoBuscar"]');
	if (button) {
		button.click();
	}
}

function saInitSearchFilters() {
	jQuery(document).on('mousedown', '[id$="acaoBuscar"]', function() {
		saPrepareSearch(this.closest('form'));
	});

	jQuery(document).on('keydown', '.sa-search-input, .sa-toolbar-search input, .sa-toolbar-filters input, .filtros-avancados input[type="text"], .filtros-avancados .ui-inputtext input', function(e) {
		if (e.key !== 'Enter') {
			return;
		}

		e.preventDefault();

		var form = this.closest('form');
		var syncMatch = (this.className || '').match(/filtro-[a-z0-9-]+-sync/i);
		if (syncMatch) {
			sincronizarFiltro(this, syncMatch[0].toLowerCase());
		}

		saPrepareSearch(form);
		saClickSearchButton(form);
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
