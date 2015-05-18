'use strict';

var _ = require('lodash');
var Backbone = require('backbone');
var App = require('../app');

var StateModel = Backbone.Model.extend({
  defaults: {
    authenticated: false
  },
  currentRoute: function () {
    var Router = App.Router;
    var fragment = Backbone.history.fragment;
    var routes = _.pairs(Router.options.appRoutes);

    var matched = _.find(routes, function (namedRoute) {
      var route = _.isRegExp(namedRoute[0]) ? namedRoute[0] : Router._routeToRegExp(namedRoute[0]);
      return route.test(fragment);
    });

    var params = _.zipObject(_.map(matched[0].match(/:\w+/g), function (pname) {
      var idx = matched[0].indexOf(pname);
      return [pname.slice(1), fragment.slice(idx).split('/')[0]];
    }));

    return _.zipObject(['route', 'name', 'fragment', 'params'], matched.concat(fragment, params));
  }
});

module.exports = StateModel;
