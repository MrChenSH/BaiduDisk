if (navigator.platform != 'Android') Object.defineProperty(navigator, 'platform', {get: function () {return 'Android';}});
if (window.$) {
	// $('.welcome-banner').remove();
	/*$('body *').click(function (e) {
		var event = new TouchEvent('touchend');
		event.target = this;
		$.extend(event, e);
		event.initEvent('touchend', true, true);
		this.dispatchEvent(event);
	});*/
}
