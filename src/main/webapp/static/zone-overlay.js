Tapestry.onDOMLoaded(function () {
    function addAjaxOverlay(event, element) {

        var mgr = Tapestry.findZoneManager(element);
        var zone = mgr && mgr.element;
        if (!zone) {
            return;
        }

        zone.insert({top:"<div class='zone-ajax-overlay'/>"});
        var zoneDims = zone.getDimensions()
        var overlay = zone.down("div");

        overlay.setStyle({
            width:zoneDims.width + "px",
            height:zoneDims.height + "px" });
    }

    $(document.body).on(Tapestry.FORM_PROCESS_SUBMIT_EVENT, addAjaxOverlay);
    $(document.body).on(Tapestry.TRIGGER_ZONE_UPDATE_EVENT, addAjaxOverlay);
});