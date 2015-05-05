'use strict';

var Backbone = require('backbone');
var AdminView = require('../views/admin');
var adminsTmpl = require('../../templates/admins.hbs');

module.exports = Backbone.Marionette.CompositeView.extend({
  template: adminsTmpl,
  tagName: 'div',
  className: 'admins-container',
  childView: AdminView,
  childViewContainer: 'tbody'
});
