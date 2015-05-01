'use strict';

var Backbone = require('backbone');
var mainTmpl = require('../../templates/main.hbs');

module.exports = Backbone.Marionette.LayoutView.extend({
  template: mainTmpl,

  regions: {
    main: '#main-content'
  }
});
