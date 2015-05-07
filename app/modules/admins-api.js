'use strict';

module.exports = function (App) {
  var $ = require('jquery');

  App.module('AdminsApi', function (AdminsApi, Core) {
    var API = {
      getAdminsEntities: function () {
        var dfd = $.Deferred();

        dfd.resolve([
          {
            id: 1,
            name: 'David Banner',
            isAdmin: true
          },
          {
            id: 2,
            name: 'Johnny Storm',
            isAdmin: true
          },
          {
            id: 3,
            name: 'Bruce Wayne',
            isAdmin: true
          },
          {
            id: 4,
            name: 'Clark Kent',
            isSuperUser: true
          }
        ]);

        return dfd.promise();
      },
      postRemoveAdmin: function (user) {
        var dfd = $.Deferred();

        dfd.resolve(JSON.parse(JSON.stringify(user)));

        return dfd.promise();
      }
    };

    Core.reqres.setHandler('admins:entities', API.getAdminsEntities);
    Core.reqres.setHandler('admins:remove', API.postRemoveAdmin);
  });
};
