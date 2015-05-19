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
            name: 'example-group-2'
          },
          {
            id: 1,
            name: 'example-group-1'
          },
          {
            id: 4,
            name: 'example-group-3'
          },
          {
            id: 2,
            name: 'example-group-4'
          },
          {
            id: 7,
            name: 'example-group-6'
          },
          {
            id: 9,
            name: 'example-group-5'
          },
          {
            id: 6,
            name: 'example-group-7'
          },
          {
            id: 8,
            name: 'example-group-8'
          },
          {
            id: 5,
            name: 'example-group-9'
          }
        ]);

        return dfd.promise();
      },

      getGroupsMembers: function () {
        var dfd = $.Deferred();

        dfd.resolve([
          {
            id: 1,
            firstName: 'David',
            lastName: 'Banner',
            username: 'hulk',
            email: 'dbanner@justiceleague.us',
            isAdmin: true
          },
          {
            id: 2,
            firstName: 'Johnny',
            lastName: 'Storm',
            username: 'humantorch',
            email: 'jstorm@justiceleague.us',
            isAdmin: true
          },
          {
            id: 3,
            firstName: 'Bruce',
            lastName: 'Wayne',
            username: 'batman',
            email: 'bruce@waynemanor.com',
            isAdmin: true
          },
          {
            id: 4,
            firstName: 'Clark',
            lastName: 'Kent',
            username: 'superman',
            email: 'clark@justiceleague.us',
            isSuperUser: true
          },
          {
            id: 5,
            firstName: 'Cain',
            lastName: 'Marko',
            username: 'juggernaut',
            email: 'dontyouknowwhoiam@juggernaut.com'
          },
          {
            id: 6,
            firstName: 'Peter',
            lastName: 'Parker',
            username: 'spiderman',
            email: 'peter@justiceleague.us'
          },
          {
            id: 7,
            firstName: 'Diana',
            lastName: 'Prince',
            username: 'wonderwoman',
            email: 'mswonderful36@yahoo.com'
          }
        ]);

        return dfd.promise();
      }
    };

    Core.reqres.setHandler('groups:entities', API.getGroupsEntities);
    Core.reqres.setHandler('groups:members', API.getGroupsMembers);
  });
};
