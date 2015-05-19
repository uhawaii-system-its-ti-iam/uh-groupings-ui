'use strict';

var Backbone = require('backbone');
var memberTmpl = require('../../templates/group-member.hbs');

module.exports = Backbone.Marionette.ItemView.extend({
  template: memberTmpl,
  tagName: 'tr',
  className: 'group-item'
});
