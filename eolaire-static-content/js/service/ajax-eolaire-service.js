var ajax = require('../util/rsvp-ajax.js');
var cache = require('../util/rsvp-cache.js');
var rsvp = require('rsvp');

var DEFAULT_LIMIT = 10;

//
// Service
//

function AjaxEolaireService() {
  this.cache = {
  };
}

AjaxEolaireService.prototype.getItemsByIds = function (ids) {
  limit = limit || DEFAULT_LIMIT;

  // get items
  var promise = ajax.request("POST", "/rest/eolaire/items", {
    "ids": ids
  });

  return promise;
}

//
// exports
//

module.exports.DEFAULT_LIMIT = DEFAULT_LIMIT;

if (window.location.href.startsWith("file")) {
  var s = function StubEolaireService() {};
  s.prototype.getItemsByIds = function () {
    return new rsvp.Promise(function (resolve) { resolve({
      "items": []
    }); });
  };

  module.exports.EolaireService = s;
} else {
  module.exports.EolaireService = AjaxEolaireService;
}
