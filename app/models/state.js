'use strict';

var Backbone = require('backbone');

var StateModel = Backbone.Model.extend({
  defaults: {
    authenticated: false
  }
});

module.exports = StateModel;

