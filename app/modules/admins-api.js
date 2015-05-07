'use strict';

module.exports = function (App) {
  var UserCollection = require('../collections/users');
  var $ = require('jquery');

  App.module('AdminsApi', function (AdminsApi, Core) {
    var adminCollection = new UserCollection([
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

    var API = {
      getAdminsEntities: function () {
        var dfd = $.Deferred();

        dfd.resolve(adminCollection);

        return dfd.promise();
      },
      postRemoveAdmin: function (model) {
        var dfd = $.Deferred();

        dfd.resolve(adminCollection.remove(model));

        return dfd.promise();
      }
    };

    Core.reqres.setHandler('admins:entities', API.getAdminsEntities);
    Core.reqres.setHandler('admins:remove', API.postRemoveAdmin);
  });
};
