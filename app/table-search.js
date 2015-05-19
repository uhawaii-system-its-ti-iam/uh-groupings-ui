'use strict';

var _ = require('lodash');
var $ = require('jquery');

// (optional) options = {
//  (optional) keys: Array|String,
//  (optional) omit: Array|String
// }
//
// return handler function
module.exports = function (App, options) {
  options = options || {};

  return function (e) {
    var re = new RegExp($(e.currentTarget).val(), 'i');
    var set = App.Groups.Controller
      .getCachedGroupMembers(App.State.currentRoute().params.id);

    var results = _.filter(set, function (member) {
      return re.test(_.values(
        _[options.keys ? 'pick' : 'omit'](
          member,
          options.keys ? options.keys : options.omit || []
        )
      ));
    });

    this.collection.fullCollection.reset(results);

    return false;
  };
};
