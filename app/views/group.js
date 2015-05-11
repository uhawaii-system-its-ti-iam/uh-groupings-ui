'use strict';

var Backbone = require('backbone');
var groupTmpl = require('../../templates/group.hbs');

module.exports = Backbone.Marionette.ItemView.extend({
  template: groupTmpl,
  tagName: 'tr',
  className: 'group-item'
});
