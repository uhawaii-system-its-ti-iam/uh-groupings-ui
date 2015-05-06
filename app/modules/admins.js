'use strict';

module.exports = function (App) {
  var mainView = require('../views/main');
  var AdminsView = require('../views/admins');

  App.module('Admins', function (Admins, Core) {
    Admins.Controller = {
      listAdmins: function () {
        console.debug('list admins');

        Core.request('admins:entities')
          .then(function (admins) {
            var adminsView = new AdminsView({
              collection: admins
            });

            mainView.content.show(adminsView);
          })
          .fail(function () {
            console.error('Unable to get admins:entities');
          });
      }
    };
  });

  return App.module('Admins');
};
