'use strict';

var Backbone = require('backbone');
var adminTmpl = require('../../templates/admin.hbs');

module.exports = Backbone.Marionette.ItemView.extend({
  template: adminTmpl,
  tagName: 'tr',
  className: 'admin-item',

  events: {
    'click td': 'logName'
  },

  logName: function () {
    console.log(this.model.escape('name'));
  }
});
