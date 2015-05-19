'use strict';

var Backbone = require('backbone');
var UserModel = require('../models/user');

require('backbone.paginator/lib/backbone.paginator');

module.exports = Backbone.PageableCollection.extend({
  mode: 'client',
  state: {
    pageSize: 2
  },
  model: UserModel,
  comparator: 'username'
});
