/**
 * Module definition for the groupingSearch module.
 * The groupingSearch module manages the application's
 * top-level grouping search view (i.e., /grouping-search).
 *
 * @module routes.groupingSearch
 */
angular.module('routes.groupingSearch', [
        'stack.i18n.TranslationResolver',
        'stack.authentication.ProtectResolver',
        'routes.groupingSearch.GroupingSearchViewController'
    ])
    .config(
        [
            '$stateProvider',
            function ($stateProvider) {
                'use strict';

                $stateProvider
                    .state('groupingSearch', {
                            url: '/grouping-search',
                            params: {searchPhrase: null},
                            templateUrl: 'js/src/app/routes/grouping-search/groupingSearch.html',
                            controller: 'GroupingSearchViewController',
                            controllerAs: 'groupingSearchViewCtrl',
                            resolve: {
                                translate: ['TranslationResolver', function (TranslationResolver) {
                                    return new TranslationResolver();
                                }],
                                protect: ['ProtectResolver', function (ProtectResolver) {
                                    return new ProtectResolver();
                                }]
                            }
                        }
                    );
            }
        ]
    );

