'use strict';

var Backbone = require('backbone');
var mainTmpl = require('../../templates/main.hbs');

module.exports = new Backbone.Marionette.LayoutView({
  template: mainTmpl,

  regions: {
    content: '#middle'
  }
});
