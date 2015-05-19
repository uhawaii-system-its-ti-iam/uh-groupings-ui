'use strict';

module.exports = function (App) {
  var mainView = require('../views/main');
  var GroupsView = require('../views/groups');
  var GroupsMembersView = require('../views/groups-members');
  var PaginatorView = require('../views/paginator');
  var GroupsCollection = require('../collections/groups');
  var MembersCollection = require('../collections/group-members');

  App.module('Groups', function (Groups, Core) {
    var groupsCollection;
    var usersCollection;

    var memberHash = {};

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
      },

      listGroupMembers: function (groupId) {
        console.debug('list group members %s', groupId);

        Core.request('groups:members', groupId)
          .then(function (users) {
            memberHash[groupId] = users;

            usersCollection = new MembersCollection(users, {
              fullCollection: users,
              state: {
                sortKey: 'name'
              }
            });

            var groupsMembersView = new GroupsMembersView({
              collection: usersCollection
            });

            mainView.content.show(groupsMembersView);

            var paginatorView = new PaginatorView({
              collection: usersCollection
            });

            Core.paginator.show(paginatorView);
          })
          .fail(function () {
            console.error('Unable to get groups:members');
          });
      },

      getCachedGroupMembers: function (groupId) {
        return memberHash[groupId];
      }
    };
  });

  return App.module('Groups');
};
