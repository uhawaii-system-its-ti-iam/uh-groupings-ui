'use strict';

var Backbone = require('backbone');
var GroupView = require('../views/group');
var groupsTmpl = require('../../templates/groups.hbs');
var tableSorter = require('../table-sorter');

module.exports = Backbone.Marionette.CompositeView.extend({
  template: groupsTmpl,
  tagName: 'div',
  className: 'col-xs-12 groups-container',
  childView: GroupView,
  childViewContainer: 'tbody',

  events: {
    'click th': 'sortByColumn'
  },

  onRenderCollection: function () {
    var sortKey = this.collection.state.sortKey;
    var sortIcons = this.$el
      .find('th')
      .not('[data-property="' + sortKey + '"]')
      .find('span.fa');

    sortIcons.toggleClass('hidden', true);
  },

  sortByColumn: tableSorter
});
