'use strict';

var Backbone = require('backbone');
var GroupModel = require('../models/group');

require('backbone.paginator/lib/backbone.paginator');

module.exports = Backbone.PageableCollection.extend({
  mode: 'client',
  state: {
    pageSize: 2
  },
  model: GroupModel,
  comparator: 'name'
});
