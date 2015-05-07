'use strict';

var Backbone = require('backbone');
var App = require('../app');
var adminTmpl = require('../../templates/admin.hbs');

module.exports = Backbone.Marionette.ItemView.extend({
  template: adminTmpl,
  tagName: 'tr',
  className: 'admin-item',

  events: {
    'click .remove': 'removeAdmin'
  },

  removeAdmin: function () {
    App.Admins.Controller.removeAdmin(this.model);
  }
});
