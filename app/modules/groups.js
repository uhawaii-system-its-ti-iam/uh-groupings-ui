'use strict';

module.exports = function (App) {
  var mainView = require('../views/main');
  var GroupsView = require('../views/groups');
  var PaginatorView = require('../views/paginator');
  var GroupsCollection = require('../collections/groups');

  App.module('Groups', function (Groups, Core) {
    var groupsCollection;

    Groups.Controller = {
      listGroups: function () {
        console.debug('list groups');

        Core.request('groups:entities')
          .then(function (groups) {
            groupsCollection = new GroupsCollection(groups, {
              fullCollection: groups,
              state: {
                sortKey: 'name'
              }
            });

            var groupsView = new GroupsView({
              collection: groupsCollection
            });

            var paginatorView = new PaginatorView({
              collection: groupsCollection
            });

            mainView.content.show(groupsView);
            Core.paginator.show(paginatorView);
          })
          .fail(function () {
            console.error('Unable to get groups:entities');
          });
      }
    };
  });

  return App.module('Groups');
};
