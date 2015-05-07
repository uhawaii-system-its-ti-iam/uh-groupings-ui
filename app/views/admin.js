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
    console.debug('Remove Admin', this.model.id);
    App.request('admins:remove', this.model);
  }
});
