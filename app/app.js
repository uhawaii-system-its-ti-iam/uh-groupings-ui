/* global window, document */
'use strict';

var Backbone = require('backbone');
var $ = Backbone.$ = require('jquery');
require('backbone.marionette');
require('bootstrap');

var App = new Backbone.Marionette.Application();
var Communicator = require('./communicator');

function bootstrapModules() {
  require('./modules/admins')(App);
  require('./modules/admins-api')(App);
  require('./modules/groups')(App);
  require('./modules/groups-api')(App);
}

App.on('before:start', bootstrapModules);

App.on('start', function () {
  Backbone.history.start();
});

App.addRegions({
  nav: '#navbar-main',
  paginator: '#paginator',
  main: '#main-wrapper'
});

App.Router = new Backbone.Marionette.AppRouter({
  controller: {
    about: function () {
      var mainView = require('./views/main');
      var aboutTmpl = require('../templates/about.hbs');

      mainView.content.show(new Backbone.Marionette.LayoutView({
        template: aboutTmpl
      }));
    },
    editAdmins: function () {
      App.module('Admins').Controller.listAdmins();
    },
    listGroups: function () {
      App.module('Groups').Controller.listGroups();
    },
    notFound: function () {
      var mainView = require('./views/main');
      var notFoundTmpl = require('../templates/404.hbs');

      mainView.content.show(new Backbone.Marionette.LayoutView({
        template: notFoundTmpl
      }));
    }
  },

  appRoutes: {
    '': 'about',
    admins: 'editAdmins',
    groupings: 'listGroups',
    '*notFound': 'notFound'
  }
});

App.addInitializer(function () {
  var StateModel = require('./models/state');

  App.State = new StateModel({
  });

  var mainView = require('./views/main');
  var navView = require('./views/nav');

  App.main.show(mainView);
  App.nav.show(navView);

  Communicator.mediator.trigger('APP:START');
});

// NOTE: This is necessary to ensure the App is exported properly
setTimeout(function () {
  App.start();
}, 1);

module.exports = App;

// workaround for bootstrap nav collapse
// (https://github.com/twbs/bootstrap/issues/9013#issuecomment-24096350)
$(document).on('click.nav', '.navbar-collapse.in', function (e) {
  if ($(e.target).is('a')) {
    $(this).collapse('hide');
  }
});

// NOTE: keep the following code at the bottom

// jscs:disable
// initialize Marionette Inspector (http://goo.gl/Wo3pju)
if (window.__agent) {
  window.__agent.start(Backbone, Backbone.Marionette);
}
