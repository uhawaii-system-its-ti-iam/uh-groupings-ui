/* global window */
'use strict';

var Backbone = require('backbone');
Backbone.$ = require('jquery');
require('backbone.marionette');
require('bootstrap');

var App = new Backbone.Marionette.Application();
var Communicator = require('./communicator');

function bootstrapModules() {
  require('./modules/admins')(App);
  require('./modules/admins-api')(App);
}
App.addRegions({
  main: '#main-wrapper'
});

App.on('before:start', bootstrapModules);

App.on('start', function () {
  Backbone.history.start();
});

var appRouter = new Backbone.Marionette.AppRouter({
  controller: {
    editAdmins: function () {
      App.module('Admins').Controller.listAdmins();
    }
  },

  appRoutes: {
    admins: 'editAdmins'
  }
});

App.addInitializer(function () {
  var mainView = require('./views/main');
  App.main.show(mainView);

  Communicator.mediator.trigger('APP:START');
});

// NOTE: This is necessary to ensure the App is exported properly
setTimeout(function () {
  App.start();
}, 1);

module.exports = App;

// NOTE: keep the following code at the bottom

// jscs:disable
// initialize Marionette Inspector (http://goo.gl/Wo3pju)
if (window.__agent) {
  window.__agent.start(Backbone, Backbone.Marionette);
}
