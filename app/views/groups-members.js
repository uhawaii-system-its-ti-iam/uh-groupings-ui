'use strict';

var Backbone = require('backbone');
var App = require('../app');
var MemberView = require('../views/group-member');
var groupsMembersTmpl = require('../../templates/groups-members.hbs');
var tableSorter = require('../table-sorter');
var tableSearch = require('../table-search');

module.exports = Backbone.Marionette.CompositeView.extend({
  template: groupsMembersTmpl,
  tagName: 'div',
  className: 'col-xs-12 groups-members-container',
  childView: MemberView,
  childViewContainer: 'tbody',

  events: {
    'click th': 'sortByColumn',
    'keyup .table-search input': 'searchTable'
  },

  onRenderCollection: function () {
    var sortKey = this.collection.state.sortKey;
    var sortIcons = this.$el
      .find('th')
      .not('[data-property="' + sortKey + '"]')
      .find('span.fa');

    sortIcons.toggleClass('hidden', true);
  },

  onDestroy: function () {
    App.paginator.empty();
  },

  searchTable: tableSearch(App),

  sortByColumn: tableSorter
});
