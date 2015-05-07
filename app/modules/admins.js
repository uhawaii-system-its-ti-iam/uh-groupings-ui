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
      removeAdmin: function (model) {
        console.debug('Removing admin ' + model.get('id'));
        Core.request('admins:remove', model)
          .then(function (admin) {
            if (admin.id === model.get('id')) {
              console.debug('Removed admin ' + model.get('id'));
              adminsCollection.remove(model);
            }
          })
          .fail(function () {
            console.error('Unable to remove admin ' + model.get('id'));
          });
      }
    };
  });

  return App.module('Admins');
};
