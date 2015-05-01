/* global window */
'use strict';

var Backbone = require('backbone');
Backbone.$ = require('jquery');
require('backbone.marionette');

var App = new Backbone.Marionette.Application();
var Communicator = require('./communicator');

App.addRegions({
  main: '#main-wrapper'
});

App.on('start', function () {
  Backbone.history.start();

  var MainView = require('./views/main');
  App.main.show(new MainView());
});

App.addInitializer(function () {
  Communicator.mediator.trigger('APP:START');
});

App.start();

module.exports = App;

// NOTE: keep the following code at the bottom

// jscs:disable
// initialize Marionette Inspector (http://goo.gl/Wo3pju)
if (window.__agent) {
  window.__agent.start(Backbone, Backbone.Marionette);
}
