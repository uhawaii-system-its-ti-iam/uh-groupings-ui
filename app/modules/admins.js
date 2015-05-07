'use strict';

module.exports = function (App) {
  var mainView = require('../views/main');
  var AdminsView = require('../views/admins');
  var UserCollection = require('../collections/users');

  App.module('Admins', function (Admins, Core) {
    var adminsCollection;

    Admins.Controller = {
      listAdmins: function () {
        console.debug('list admins');

        Core.request('admins:entities')
          .then(function (admins) {
            adminsCollection = new UserCollection(admins);

            var adminsView = new AdminsView({
              collection: adminsCollection
            });

            mainView.content.show(adminsView);
          })
          .fail(function () {
            console.error('Unable to get admins:entities');
          });
      },
      removeAdmin: function (user) {
        var userId = user.get('id');

        console.debug('Removing admin ' + userId);
        Core.request('admins:remove', user)
          .then(function (admin) {
            if (admin.id === userId) {
              console.debug('Removed admin ' + userId);
              adminsCollection.remove(user);
            }
          })
          .fail(function () {
            console.error('Unable to remove admin ' + userId);
          });
      }
    };
  });

  return App.module('Admins');
};
