var ajax = require('rsvp-ajax');
var cache = require('rsvp-cache');
var rsvp = require('rsvp');
var DEFAULT_LIMIT = require('../util/constants.js').DEFAULT_LIMIT;

function prepareRequestWithOffsetAndLimit(offsetToken, limit) {
  limit = limit || DEFAULT_LIMIT;

  var request = {
    "limit": limit
  };

  if (offsetToken) {
    request["offsetToken"] = offsetToken;
  }

  return request;
}

//
// Service
//

function AjaxEolaireService() {
  this.cache = {
  };
}

AjaxEolaireService.prototype.getItemsByIds = function (ids) {
  return ajax.request("POST", "/rest/eolaire/item/list", {"itemIds": ids});
}

AjaxEolaireService.prototype.getAllEntities = function (offsetToken, limit) {
  var request = prepareRequestWithOffsetAndLimit(offsetToken, limit);
  return ajax.request("POST", "/rest/eolaire/entity/list", request);
}

AjaxEolaireService.prototype.getItemListByType = function (type, offsetToken, limit) {
  var request = prepareRequestWithOffsetAndLimit(offsetToken, limit);
  request["itemTypeId"] = type;

  var promise = ajax.request("POST", "/rest/eolaire/item/query/by-type", request);

  promise = promise.then(function (response) {
    var inner = this.getItemsByIds(response["itemIds"]);
    return inner.then(function (innerResponse) {
      return {
        "offsetToken": response["offsetToken"],
        "items": innerResponse["items"]
      }
    });
  }.bind(this));

  return promise;
}

//
// exports
//

if (window.location.href.startsWith("file")) {
  var s = function StubEolaireService() {};
  s.prototype.getItemsByIds = function () {
    return new rsvp.Promise(function (resolve) {
      resolve({"items": []});
    });
  };

  s.prototype.getAllEntities = function () {
    return new rsvp.Promise(function (resolve) {
      resolve({"types": []});
    });
  };

  s.prototype.getItemListByType = function () {
    return new rsvp.Promise(function (resolve) {
      resolve({"items": []});
    });
  }

  module.exports.EolaireService = s;
} else {
  module.exports.EolaireService = AjaxEolaireService;
}
