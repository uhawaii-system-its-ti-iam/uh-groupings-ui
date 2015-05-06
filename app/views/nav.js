'use strict';

var Backbone = require('backbone');
var App = require('../app.js');
var navTmpl = require('../../templates/nav.hbs');

module.exports = new Backbone.Marionette.LayoutView({
  template: navTmpl,
  model: App.State
});
