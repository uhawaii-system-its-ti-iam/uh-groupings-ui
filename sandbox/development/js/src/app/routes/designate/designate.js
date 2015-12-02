/**
 * Module definition for the designate module. The designate module manages
 * the application's top-level designate view (i.e., /designate).
 *
 * @module routes.designate
 */
angular.module('routes.designate', [
    'stack.i18n.TranslationResolver',
    'stack.authentication.ProtectResolver',
    'routes.designate.DesignateController'
])
.config(
    [
        '$stateProvider',
        function ($stateProvider) {
            'use strict';

            $stateProvider
                .state('designate', {
                    url: '/designate',
                    templateUrl: 'js/src/app/routes/designate/designate.html',
                    controller: 'DesignateController',
                    controllerAs: 'designateCtrl',
                    resolve: {
                        translate: ['TranslationResolver', function (TranslationResolver) {
                            return new TranslationResolver();
                        }],
                        protect: ['ProtectResolver', function (ProtectResolver) {
                            return new ProtectResolver({permissions: ['owner']});
                        }]
                    }
                }
            );
        }
    ]
);
