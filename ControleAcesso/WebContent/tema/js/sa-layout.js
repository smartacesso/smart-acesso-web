/**
 * Layout sidebar Smart Acesso (PrimeFaces 8)
 */
(function () {
	'use strict';

	var CLASS_COLLAPSED = 'sa-sidebar-collapsed';

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
			if (window.localStorage && localStorage.getItem('sa-sidebar-collapsed') === '1') {
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

	window.saToggleSidebar = function () {
		setCollapsed(!isCollapsed());
		updateSidebarToggleUi();
		try {
			if (window.localStorage) {
				localStorage.setItem('sa-sidebar-collapsed', isCollapsed() ? '1' : '0');
			}
		} catch (e) { /* ignore */ }
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

	function onReady() {
		initSidebarState();
		updateSidebarToggleUi();
	}

	if (document.readyState === 'loading') {
		document.addEventListener('DOMContentLoaded', onReady);
	} else {
		onReady();
	}
})();
