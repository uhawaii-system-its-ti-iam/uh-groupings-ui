/**
 * Module definition for the membership module. The membership module manages
 * the application's top-level membership view (i.e., /membership).
 *
 * @module routes.membership
 */
angular.module('routes.membership', [
    'stack.i18n',
    'routes.membership.MembershipController'
])
.config(
    [
        '$stateProvider',
        function ($stateProvider) {
            'use strict';

            $stateProvider
                .state('membership', {
                    url: '/membership',
                    templateUrl: 'js/src/app/routes/membership/membership.html',
                    controller: 'MembershipController',
                    controllerAs: 'membershipCtrl',
                    resolve: {
                        translate: ['TranslationResolver', function (TranslationResolver) {
                            return new TranslationResolver();
                        }]
                    }
                }
            );
        }
    ]
);
