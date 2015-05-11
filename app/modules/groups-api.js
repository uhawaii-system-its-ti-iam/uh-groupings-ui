'use strict';

module.exports = function (App) {
  var $ = require('jquery');

  App.module('GroupsApi', function (GroupsApi, Core) {
    var API = {
      getGroupsEntities: function () {
        var dfd = $.Deferred();

        dfd.resolve([
          {
            id: 3,
            name: 'example-group-1'
          },
          {
            id: 1,
            name: 'example-group-2'
          },
          {
            id: 4,
            name: 'example-group-3'
          },
          {
            id: 2,
            name: 'example-group-4'
          }
        ]);

        return dfd.promise();
      }
    };

    Core.reqres.setHandler('groups:entities', API.getGroupsEntities);
  });
};
