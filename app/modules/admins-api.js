'use strict';

module.exports = function (App) {
  var UserCollection = require('../collections/users');
  var $ = require('jquery');

  App.module('AdminsApi', function (AdminsApi, Core) {
    var dfd = $.Deferred();

    var API = {
      getAdminsEntities: function () {
        dfd.resolve(new UserCollection([
          {
            name: 'David Banner',
            isAdmin: true
          },
          {
            name: 'Johnny Storm',
            isAdmin: true
          },
          {
            name: 'Bruce Wayne',
            isAdmin: true
          },
          {
            name: 'Clark Kent',
            isSuperUser: true
          }
        ]));

        return dfd.promise();
      }
    };

    Core.reqres.setHandler('admins:entities', API.getAdminsEntities);
  });
};
