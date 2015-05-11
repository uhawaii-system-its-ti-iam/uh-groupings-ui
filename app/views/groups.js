'use strict';

var Backbone = require('backbone');
var GroupView = require('../views/group');
var groupsTmpl = require('../../templates/groups.hbs');

module.exports = Backbone.Marionette.CompositeView.extend({
  template: groupsTmpl,
  tagName: 'div',
  className: 'col-xs-12 groups-container',
  childView: GroupView,
  childViewContainer: 'tbody'
});
