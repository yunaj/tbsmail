/**
 * This jQuery plugin displays pagination links inside the selected elements.
 *
 * @author Gabriel Birke (birke *at* d-scribe *dot* de)
 * @version 1.2
 * @param {int} maxentries Number of entries to paginate
 * @param {Object} opts Several options (see README for documentation)
 * @return {Object} jQuery Object
 */
jQuery.fn.pagination = function(maxentries, opts){
	opts = jQuery.extend({
		items_per_page:10,
		num_display_entries:($(window).width() < 768) ? 5 : 10,
		current_page:0,
		num_edge_entries:0,
		link_to:"#",
		prev_text:"&lsaquo;",
		next_text:"&rsaquo;",
		ellipse_text:"...",
		prev_show_always:true,
		next_show_always:true,
		first_text:"&laquo;",
		last_text:"&raquo;",
		callback:function(){return false;}
	},opts||{});
	
	return this.each(function() {
		/**
		 * Calculate the maximum number of pages
		 */
		function numPages() {
			return Math.ceil(maxentries/opts.items_per_page);
		}
		
		/**
		 * Calculate start and end point of pagination links depending on 
		 * current_page and num_display_entries.
		 * @return {Array}
		 */
		function getInterval(btn_type)  {
			
			var np = numPages();
			var ne_half = (Math.floor(current_page/opts.num_display_entries)+1) * opts.num_display_entries;
			
			var start = Math.floor(current_page/opts.num_display_entries) * opts.num_display_entries;
			var end = start + Math.min(opts.num_display_entries, np);
			
			if(current_page > ne_half || btn_type=="next") {
				start = current_page;
				end = start+opts.num_display_entries;
			}
			if(end > np) 
				end = np;
			
			return [start,end];
		}
		
		/**
		 * This is the event handling function for the pagination links. 
		 * @param {int} page_id The new page number
		 */
		function pageSelected(page_id, evt, btn_type){
			current_page = page_id;
			var continuePropagation = opts.callback(page_id, panel);
			if (!continuePropagation) {
				if (evt.stopPropagation) {
					evt.stopPropagation();
				}
				else {
					evt.cancelBubble = true;
				}
			} else {
				drawLinks(btn_type);
			}
			return continuePropagation;
		}
		
		/**
		 * This function inserts the pagination links into the container element
		 */
		function drawLinks(btn_type) {
			panel.empty();
			var interval = getInterval(btn_type);
			var np = numPages();
			// We don't need to draw pages
			if (np == 1) return;
			// This helper function returns a handler function that calls pageSelected with the right page_id
			var getClickHandler = function(page_id, btn_type) {
				return function(evt){ return pageSelected(page_id,evt,btn_type); }
			}
			// Helper function for generating a single link (or a span tag if it's the current page)
			var appendItem = function(page_id, appendopts, btn_type){
				page_id = page_id<0?0:(page_id<np?page_id:np-1); // Normalize page id to sane value
				appendopts = jQuery.extend({text:page_id+1}, appendopts||{});
				var is_disable = ((btn_type == "prev" && interval[1] <= opts.num_display_entries) || (btn_type == "next" && interval[1] == np))? true : false;
				if(!btn_type && page_id == current_page)
					var lnk = jQuery("<li class='active'><a>"+(appendopts.text)+"</a></li>");
				else if(is_disable)
					var lnk = jQuery("<li class='disabled'><a>"+(appendopts.text)+"</a></li>");
				else {
					var a = jQuery("<a>"+(appendopts.text)+"</a>")
						.attr('href', opts.link_to.replace(/__id__/,page_id));
					var lnk = jQuery("<li></li>")
						.bind("click", getClickHandler(page_id, btn_type));
					lnk.append(a);
				}
				panel.append(lnk);
			}
			
			// appendItem(0,{text:opts.first_text}, "first");
			
			// Generate "Previous"-Link
			if(opts.prev_text && (current_page > 0 || opts.prev_show_always)){
				appendItem(interval[0]-1,{text:opts.prev_text}, "prev");
			}
			// Generate starting points
			if (interval[0] > 0 && opts.num_edge_entries > 0)
			{
				var end = Math.min(opts.num_edge_entries, interval[0]);
				for(var i=0; i<end; i++) {
					
					appendItem(i);
				}
				if(opts.num_edge_entries < interval[0] && opts.ellipse_text)
				{
					jQuery("<li class='disabled'>"+opts.ellipse_text+"</li>").appendTo(panel);
				}
			}
			// Generate interval links
			for(var i=interval[0]; i<interval[1]; i++) {
				appendItem(i);
			}
			// Generate ending points
			if (interval[1] < np && opts.num_edge_entries > 0)
			{
				if(np-opts.num_edge_entries > interval[1]&& opts.ellipse_text)
				{
					jQuery("<li class='disabled'>"+opts.ellipse_text+"</li>").appendTo(panel);
				}
				var begin = Math.max(np-opts.num_edge_entries, interval[1]);
				for(var i=begin; i<np; i++) {
					appendItem(i);
				}
			}
			// Generate "Next"-Link
			if(opts.next_text && (current_page < np-1 || opts.next_show_always)){
				appendItem(interval[1],{text:opts.next_text}, "next");
			}
			// appendItem(numPages()-1,{text:opts.last_text}, "last");
		}
		
		// Extract current_page from options
		var current_page = opts.current_page;
		// Create a sane value for maxentries and items_per_page
		maxentries = (!maxentries || maxentries < 0)?1:maxentries;
		opts.items_per_page = (!opts.items_per_page || opts.items_per_page < 0)?1:opts.items_per_page;
		// Store DOM element for easy access from all inner functions
		var panel = $( this );
		// Attach control functions to the DOM element 
		this.selectPage = function(page_id){ pageSelected(page_id);}
		this.prevPage = function(){ 
			if (current_page > 0) {
				pageSelected(current_page - 1);
				return true;
			}
			else {
				return false;
			}
		}
		this.nextPage = function(){ 
			if(current_page < numPages()-1) {
				pageSelected(current_page+1);
				return true;
			}
			else {
				return false;
			}
		}
		// When all initialisation is done, draw the links
		drawLinks();
        // call callback function
		// opts.callback(current_page, this);
	});
}

