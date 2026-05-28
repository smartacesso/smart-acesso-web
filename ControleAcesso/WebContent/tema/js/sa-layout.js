/**
 * Layout sidebar Smart Acesso (PrimeFaces 8)
 */
(function () {
	'use strict';

	var CLASS_COLLAPSED = 'sa-sidebar-collapsed';
	var STORAGE_KEY = 'sa-sidebar-collapsed-v26';
	var CLASS_CURRENT = 'sa-menuitem--current';
	var CLASS_SECTION_ACTIVE = 'sa-menu-section--active';
	var CLASS_PANEL_HAS_ACTIVE = 'sa-panelmenu-panel--has-active';

	function isCollapsed() {
		return document.body.classList.contains(CLASS_COLLAPSED);
	}

	function setCollapsed(collapsed) {
		var targets = [
			document.documentElement,
			document.body,
			document.getElementById('saLayout')
		];

		targets.forEach(function (el) {
			if (!el) {
				return;
			}
			if (collapsed) {
				el.classList.add(CLASS_COLLAPSED);
			} else {
				el.classList.remove(CLASS_COLLAPSED);
			}
		});
	}

	function initSidebarState() {
		try {
			if (window.localStorage && localStorage.getItem(STORAGE_KEY) === '1') {
				setCollapsed(true);
			}
		} catch (e) { /* ignore */ }
	}

	function updateSidebarToggleUi() {
		var collapsed = isCollapsed();
		var btn = document.getElementById('saSidebarToggle');
		if (!btn) {
			return;
		}
		btn.setAttribute('aria-expanded', collapsed ? 'false' : 'true');
		btn.setAttribute('title', collapsed ? 'Exibir menu' : 'Ocultar menu');
		btn.setAttribute('aria-label', collapsed ? 'Exibir menu' : 'Ocultar menu');
	}

	function parseMenuHref(href) {
		if (!href || href.indexOf('javascript:') === 0 || href === '#') {
			return null;
		}
		try {
			var anchor = document.createElement('a');
			anchor.href = href;
			return {
				full: (anchor.pathname + anchor.search).toLowerCase(),
				path: anchor.pathname.toLowerCase(),
				search: anchor.search.toLowerCase()
			};
		} catch (e) {
			return null;
		}
	}

	function getCurrentLocation() {
		return {
			full: (window.location.pathname + window.location.search).toLowerCase(),
			path: window.location.pathname.toLowerCase(),
			search: window.location.search.toLowerCase()
		};
	}

	function relatedListPath(path) {
		var match = path.match(/^(.+\/)(cadastro[^/]+\.xhtml)$/i);
		if (!match) {
			return null;
		}
		return (match[1] + match[2].replace(/^cadastro/i, 'pesquisa')).toLowerCase();
	}

	function scoreMenuMatch(current, candidate) {
		if (!candidate) {
			return -1;
		}
		if (current.full === candidate.full) {
			return 1000;
		}
		if (current.path === candidate.path) {
			if (candidate.search && current.search === candidate.search) {
				return 900;
			}
			if (!candidate.search) {
				return 300;
			}
			return 200;
		}
		var related = relatedListPath(current.path);
		if (related && related === candidate.path) {
			return 250;
		}
		return -1;
	}

	function expandPanelMenuSection(header) {
		if (!header || header.classList.contains('ui-state-active')) {
			return;
		}
		header.classList.add('ui-state-active');
		var content = header.nextElementSibling;
		if (content && content.classList.contains('ui-panelmenu-content')) {
			content.style.display = 'block';
		}
	}

	function clearSidebarHighlights(sidebar) {
		sidebar.querySelectorAll('.' + CLASS_CURRENT).forEach(function (el) {
			el.classList.remove(CLASS_CURRENT);
			el.removeAttribute('aria-current');
		});
		sidebar.querySelectorAll('.' + CLASS_SECTION_ACTIVE).forEach(function (el) {
			el.classList.remove(CLASS_SECTION_ACTIVE);
		});
		sidebar.querySelectorAll('.' + CLASS_PANEL_HAS_ACTIVE).forEach(function (el) {
			el.classList.remove(CLASS_PANEL_HAS_ACTIVE);
		});
	}

	function highlightSidebarMenu() {
		var sidebar = document.getElementById('saSidebar');
		if (!sidebar) {
			return;
		}

		clearSidebarHighlights(sidebar);

		var current = getCurrentLocation();
		var links = sidebar.querySelectorAll('.ui-menuitem-link[href]');
		var bestLink = null;
		var bestScore = -1;

		links.forEach(function (link) {
			var candidate = parseMenuHref(link.getAttribute('href'));
			var score = scoreMenuMatch(current, candidate);
			if (score > bestScore) {
				bestScore = score;
				bestLink = link;
			}
		});

		if (!bestLink || bestScore < 0) {
			return;
		}

		bestLink.classList.add(CLASS_CURRENT);
		bestLink.setAttribute('aria-current', 'page');

		var panel = bestLink.closest('.ui-panelmenu-panel');
		if (panel) {
			panel.classList.add(CLASS_PANEL_HAS_ACTIVE);
			var header = panel.querySelector(':scope > .ui-panelmenu-header');
			if (header) {
				header.classList.add(CLASS_SECTION_ACTIVE);
				expandPanelMenuSection(header);
			}
		}
	}

	window.saToggleSidebar = function () {
		setCollapsed(!isCollapsed());
		updateSidebarToggleUi();
		try {
			if (window.localStorage) {
				localStorage.setItem(STORAGE_KEY, isCollapsed() ? '1' : '0');
			}
		} catch (e) { /* ignore */ }
		saScheduleFitDataTables();
	};

	window.saOpenSidebarMobile = function () {
		var layout = document.getElementById('saLayout');
		if (layout) {
			layout.classList.add('sa-sidebar-open');
		}
	};

	window.saCloseSidebarMobile = function () {
		var layout = document.getElementById('saLayout');
		if (layout) {
			layout.classList.remove('sa-sidebar-open');
		}
	};

	function saQueryByIdSuffix(root, suffix) {
		if (!root) {
			return null;
		}
		return root.querySelector('[id$="' + suffix + '"]');
	}

	function saFindTabelaPanel(content, filtro) {
		var tabela = saQueryByIdSuffix(content, 'tabelaPainel');
		if (tabela) {
			return tabela;
		}

		var tables = content.querySelectorAll('.ui-datatable.sa-table');
		for (var i = 0; i < tables.length; i++) {
			var table = tables[i];
			if (filtro && filtro.contains(table)) {
				continue;
			}
			return table.closest('.sa-pesquisa-split__tabela')
				|| table.closest('.p-col-12')
				|| table;
		}
		return null;
	}

	function saInitPesquisaSplitLayout() {
		var content = document.querySelector('.sa-content.conteudo_internal');
		if (!content) {
			return;
		}

		var filtro = saQueryByIdSuffix(content, 'containerDeFiltro');
		if (!filtro) {
			return;
		}

		var split = filtro.closest('.sa-pesquisa-split') || content.querySelector('.sa-pesquisa-split');
		var tabela = split
			? (split.querySelector('.sa-pesquisa-split__tabela') || saQueryByIdSuffix(split, 'tabelaPainel'))
			: saFindTabelaPanel(content, filtro);
		if (!tabela) {
			return;
		}

		var main;
		var splitFooter;

		if (!split) {
			split = document.createElement('div');
			split.className = 'sa-pesquisa-split';
			main = document.createElement('div');
			main.className = 'sa-pesquisa-split__main';
			splitFooter = document.createElement('div');
			splitFooter.className = 'sa-pesquisa-split__footer';
			splitFooter.setAttribute('aria-label', 'Paginação');

			filtro.parentNode.insertBefore(split, filtro);
			main.appendChild(filtro);
			main.appendChild(tabela);
			split.appendChild(main);
			split.appendChild(splitFooter);
		} else {
			main = split.querySelector('.sa-pesquisa-split__main');
			splitFooter = split.querySelector('.sa-pesquisa-split__footer');
			if (!main) {
				main = document.createElement('div');
				main.className = 'sa-pesquisa-split__main';
				split.insertBefore(main, splitFooter || null);
			}
			if (!splitFooter) {
				splitFooter = document.createElement('div');
				splitFooter.className = 'sa-pesquisa-split__footer';
				splitFooter.setAttribute('aria-label', 'Paginação');
				split.appendChild(splitFooter);
			}
			if (filtro.parentNode !== main) {
				main.insertBefore(filtro, main.firstChild);
			}
			if (tabela.parentNode !== main) {
				main.appendChild(tabela);
			}
		}

		filtro.classList.add('sa-pesquisa-split__filtros');

		if (tabela.classList) {
			tabela.classList.add('sa-pesquisa-split__tabela');
		}

		var filtrosPanel = filtro.querySelector('.filtros-avancados');
		split.classList.toggle('sa-pesquisa-split--filtros-abertos', !!filtrosPanel);

		var table = tabela.querySelector('.ui-datatable.sa-table') || content.querySelector('.ui-datatable.sa-table');
		if (table && splitFooter) {
			var paginator = table.querySelector(':scope > .ui-paginator')
				|| splitFooter.querySelector('.ui-paginator')
				|| table.querySelector('.ui-paginator');
			if (paginator && paginator.parentNode !== splitFooter) {
				splitFooter.appendChild(paginator);
			}
		}

		saObserveFilterPanel();
	}

	function saGetDataTableWidget(tableEl) {
		if (!tableEl || !tableEl.id || typeof PrimeFaces === 'undefined') {
			return null;
		}
		if (typeof PrimeFaces.getWidgetById === 'function') {
			var byId = PrimeFaces.getWidgetById(tableEl.id);
			if (byId) {
				return byId;
			}
		}
		if (PrimeFaces.widgets) {
			return PrimeFaces.widgets[tableEl.id.replace(/:/g, '_')] || null;
		}
		return null;
	}

	function saClearScrollableTableWidths(tableEl) {
		var headerTable = tableEl.querySelector('.ui-datatable-scrollable-header table');
		var bodyTable = tableEl.querySelector('.ui-datatable-scrollable-body table');
		tableEl.classList.remove('sa-table--h-scroll');

		[headerTable, bodyTable].forEach(function (table) {
			if (!table) {
				return;
			}
			table.style.width = '';
			table.style.minWidth = '';
			table.style.maxWidth = '';
			table.querySelectorAll('th, td').forEach(function (cell) {
				cell.style.width = '';
				cell.style.minWidth = '';
				cell.style.maxWidth = '';
			});
		});
	}

	function saSyncScrollableTableWidths(tableEl) {
		var body = tableEl.querySelector('.ui-datatable-scrollable-body');
		var headerTable = tableEl.querySelector('.ui-datatable-scrollable-header table');
		var bodyTable = tableEl.querySelector('.ui-datatable-scrollable-body table');
		if (!body || !headerTable || !bodyTable) {
			return;
		}

		saClearScrollableTableWidths(tableEl);

		if (tableEl.getBoundingClientRect().width < 80 || body.clientWidth < 80) {
			return;
		}

		var needsHScroll = bodyTable.scrollWidth > body.clientWidth + 2;
		if (!needsHScroll) {
			return;
		}

		tableEl.classList.add('sa-table--h-scroll');

		var headerThs = headerTable.querySelectorAll('thead > tr > th');
		var bodyThs = bodyTable.querySelectorAll('thead > tr > th');
		if (!headerThs.length) {
			return;
		}

		headerThs.forEach(function (th, index) {
			var width = th.getBoundingClientRect().width;
			if (width <= 1) {
				return;
			}
			var px = Math.round(width) + 'px';
			th.style.width = px;
			th.style.minWidth = px;

			if (bodyThs[index]) {
				bodyThs[index].style.width = px;
				bodyThs[index].style.minWidth = px;
			}
		});

		var tableWidth = Math.max(headerTable.scrollWidth, bodyTable.scrollWidth);
		if (tableWidth > body.clientWidth) {
			var widthPx = tableWidth + 'px';
			headerTable.style.width = widthPx;
			headerTable.style.minWidth = widthPx;
			bodyTable.style.width = widthPx;
			bodyTable.style.minWidth = widthPx;
		}
	}

	function saFitPlainDataTableWrapper(tableEl, bottomLimit) {
		var wrapper = tableEl.querySelector('.ui-datatable-tablewrapper');
		if (!wrapper) {
			return;
		}

		var tableTop = tableEl.getBoundingClientRect().top;
		var available = Math.floor(bottomLimit - tableTop - 4);

		if (available <= 80) {
			return;
		}

		wrapper.style.maxHeight = available + 'px';

		requestAnimationFrame(function () {
			var contentHeight = wrapper.scrollHeight;
			if (contentHeight <= available) {
				wrapper.style.maxHeight = contentHeight + 'px';
				wrapper.style.overflowY = 'hidden';
			} else {
				wrapper.style.overflowY = 'auto';
			}
			wrapper.style.overflowX = wrapper.scrollWidth > wrapper.clientWidth + 1 ? 'auto' : 'hidden';
		});
	}

	function saSyncScrollableHeaderGutter(tableEl) {
		var body = tableEl.querySelector('.ui-datatable-scrollable-body');
		var headerBox = tableEl.querySelector('.ui-datatable-scrollable-header-box');
		var header = tableEl.querySelector('.ui-datatable-scrollable-header');
		if (!body || !headerBox) {
			return;
		}

		if (header) {
			header.style.paddingRight = '';
			header.style.marginRight = '';
		}

		headerBox.style.marginRight = '';
		headerBox.style.paddingRight = '';
		headerBox.style.width = '';
		headerBox.style.maxWidth = '';
	}

	function saAdjustScrollableBodyOverflow(tableEl, available) {
		var body = tableEl.querySelector('.ui-datatable-scrollable-body');
		if (!body || available <= 80) {
			return;
		}

		var table = body.querySelector('table');
		var contentHeight = table ? table.offsetHeight : body.scrollHeight;
		var needsScroll = contentHeight > available + 1;

		if (!needsScroll && contentHeight > 0) {
			body.style.height = contentHeight + 'px';
			body.style.maxHeight = contentHeight + 'px';
			body.style.overflowY = 'hidden';
		} else {
			body.style.height = available + 'px';
			body.style.maxHeight = available + 'px';
			body.style.overflowY = needsScroll ? 'auto' : 'hidden';
		}
	}

	function saAlignDataTableColumns(tableEl) {
		if (!tableEl || !tableEl.classList.contains('ui-datatable-scrollable')) {
			return;
		}

		var widget = saGetDataTableWidget(tableEl);
		if (widget) {
			if (typeof widget.setupScrolling === 'function') {
				widget.setupScrolling();
			}
		}

		saSyncScrollableTableWidths(tableEl);

		if (widget && typeof widget.alignScrollHeader === 'function') {
			widget.alignScrollHeader();
		}

		var headerBox = tableEl.querySelector('.ui-datatable-scrollable-header-box');
		var body = tableEl.querySelector('.ui-datatable-scrollable-body');
		if (!headerBox || !body) {
			return;
		}

		headerBox.scrollLeft = body.scrollLeft;

		if (!body.dataset.saScrollBound) {
			body.dataset.saScrollBound = '1';
			body.addEventListener('scroll', function () {
				headerBox.scrollLeft = body.scrollLeft;
			});
		}

		saSyncScrollableHeaderGutter(tableEl);
	}

	function saAlignAllDataTables(root) {
		var scope = root || document;
		scope.querySelectorAll('.ui-datatable.ui-datatable-scrollable').forEach(function (tableEl) {
			saAlignDataTableColumns(tableEl);
		});
	}

	function saFitScrollableDataTables() {
		saInitPesquisaSplitLayout();

		var content = document.querySelector('.sa-content.conteudo_internal');
		if (!content || !content.querySelector('.ui-datatable.sa-table')) {
			return;
		}

		var footer = document.querySelector('.sa-app-footer');
		var footerTop = footer ? footer.getBoundingClientRect().top : window.innerHeight;
		var contentBottom = Math.min(content.getBoundingClientRect().bottom, footerTop);

		content.querySelectorAll('.ui-datatable.sa-table').forEach(function (table) {
			var split = table.closest('.sa-pesquisa-split');
			var splitFooter = split && split.querySelector('.sa-pesquisa-split__footer');
			var bottomLimit = contentBottom;

			if (split) {
				bottomLimit = Math.min(bottomLimit, split.getBoundingClientRect().bottom);
			}

			if (splitFooter && splitFooter.querySelector('.ui-paginator')) {
				bottomLimit = Math.min(bottomLimit, splitFooter.getBoundingClientRect().top);
			}

			if (table.classList.contains('ui-datatable-scrollable')) {
				var header = table.querySelector('.ui-datatable-scrollable-header');
				var body = table.querySelector('.ui-datatable-scrollable-body');
				if (!body) {
					return;
				}

				var tableTop = table.getBoundingClientRect().top;
				var headerH = header ? header.offsetHeight : 0;
				var available = Math.floor(bottomLimit - tableTop - headerH - 8);

				saAdjustScrollableBodyOverflow(table, available);
				saAlignDataTableColumns(table);
				return;
			}

			saFitPlainDataTableWrapper(table, bottomLimit);
		});
	}

	var splitLayoutObserved = false;

	function saObserveFilterPanel() {
		var panel = saQueryByIdSuffix(document, 'containerDeFiltro');

		if (panel) {
			if (!panel.dataset.saFilterResizeObserved && window.ResizeObserver) {
				panel.dataset.saFilterResizeObserved = '1';
				new ResizeObserver(function () {
					saScheduleFitDataTables();
				}).observe(panel);
			}

			if (!panel.dataset.saFilterMutationObserved && window.MutationObserver) {
				panel.dataset.saFilterMutationObserved = '1';
				new MutationObserver(function () {
					var split = panel.closest('.sa-pesquisa-split');
					if (split) {
						split.classList.toggle(
							'sa-pesquisa-split--filtros-abertos',
							!!panel.querySelector('.filtros-avancados')
						);
					}
					saScheduleFitDataTables();
				}).observe(panel, { childList: true, subtree: true });
			}
		}

		if (!splitLayoutObserved && window.ResizeObserver) {
			var split = document.querySelector('.sa-pesquisa-split');
			if (split) {
				splitLayoutObserved = true;
				new ResizeObserver(function () {
					saScheduleFitDataTables();
				}).observe(split);
			}
		}
	}

	var fitDataTablesTimer;

	function saScheduleFitDataTables() {
		if (fitDataTablesTimer) {
			clearTimeout(fitDataTablesTimer);
		}
		fitDataTablesTimer = setTimeout(saFitScrollableDataTables, 60);
	}

	window.saScheduleFitDataTables = saScheduleFitDataTables;
	window.saAlignAllDataTables = saAlignAllDataTables;

	function onReady() {
		initSidebarState();
		updateSidebarToggleUi();
		highlightSidebarMenu();
		saScheduleFitDataTables();
		setTimeout(saScheduleFitDataTables, 150);
		setTimeout(saScheduleFitDataTables, 450);

		window.addEventListener('resize', saScheduleFitDataTables);

		if (window.jQuery) {
			jQuery(document).on('pfAjaxComplete', function () {
				saScheduleFitDataTables();
				setTimeout(saScheduleFitDataTables, 200);
				setTimeout(saScheduleFitDataTables, 500);
			});
		}
	}

	if (document.readyState === 'loading') {
		document.addEventListener('DOMContentLoaded', onReady);
	} else {
		onReady();
	}
})();
