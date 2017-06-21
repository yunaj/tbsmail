(function($) {

$.fn.bootstrapFileInput = function( title ) {

var $this = this.addClass('btn-file-wrapper');

this.each(function(i, elem) {
  var input = $(elem).find(':file'),
      buttonWord = title || 'Add...';
  input.wrap('<span class="btn btn-xs btn-file"></span>');
  $('<span>' + buttonWord + '</span>').insertBefore(input);
  $('<span class="close">&times;</span>').insertAfter(input);
}).promise().done(function() {
  $this.on('change', '.btn-file :file', function(e) {
    if (!$(this).val()) return;
    var input = $(this),
        label = input.val().replace(/\\/g, '/').replace(/.*\//, ''),
        clone = input.parent().is(':last-child') ? input.parent().clone() : null,
        files = $(e.delegateTarget).find(':file').not(this).filter(function() { 
          return $(this).val() == input.val(); 
        });
    files.each(function() { $(this).parent().remove(); });
    input.trigger('fileselect', [1, label]).prev().text(label);
    if (clone) {
      var file = clone.find(":file");
      $('<input type="file" name="' + file.attr('name') + '">').insertAfter(file);
      file.remove();
      input.parent().parent().append(clone);
    }
  }).on('click', '.btn-file > .close', function() {
    $(this).parent().remove();
  });
});

};

})(jQuery);

$(function() {

  $(window).resize(function() {
    $('#wrapper').css('min-height', $(window).height() - ($('#header').height() + $('#footer').height()) + 15);
  }).trigger('resize');
  
  $('.nav-stacked').on('click', 'li[role]', function() {
    var $this = $(this);
    if (!$this.hasClass('active')) {
      var $active = $this.siblings('.active');
      if ($active) 
        $active.removeClass('active');
      $this.addClass('active');
    }
  });

  $(document).on('click', '.dropdown-menu-form', function(e) {
    e.stopPropagation();
  });

});

var entityMap = {
    '&': '&amp;',
    '<': '&lt;',
    '>': '&gt;',
    '"': '&quot;',
    "'": '&#39;',
    '/': '&#x2F;'
  };

function escapeHtml(string) {
    return String(string).replace(/[&<>"'\/]/g, function fromEntityMap (s) {
      return entityMap[s];
    });
}

function parseBool(str) {
  return String(str).toLowerCase() === 'true';
}

function isValidMboxName(name) {
    return !/[~\.\\\/:\*\?"\<\>\|]/.test(name);
}

function formatBytes(bytes) {
    if (bytes < 1024) return bytes + " Bytes";
    else if (bytes < 1048576) return (bytes / 1024).toFixed(1) + " KB";
    else if (bytes < 1073741824) return (bytes / 1048576).toFixed(1) + " MB";
    else return (bytes / 1073741824).toFixed(1) + " GB";
}

function serializeObject(elem) {
  var arr = elem.serializeArray(),
      obj = {};
  $.each(arr, function(i, v) {
    obj[v.name] = $.trim(v.value);
  })
  return obj;
}
