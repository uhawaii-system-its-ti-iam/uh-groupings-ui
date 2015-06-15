/**
 * The global module houses high-level, global constants
 * for the application.
 *
 * @module global
 */
angular.module('global', ['app.development'])
    .config([
        '$provide',
        'DEVELOP',
        function ($provide, develop) {
            'use strict';

            // Define.
            var global;

            // Set defaults.
            global = {
                application: {
                    base: ((develop) ? '' : ''),
                    api: ((develop) ? '/api' : '/api')
                },
                routes: {
                    login: 'login'
                }
            };

            /**
             * Defines high-level global constants.
             *
             * @attribute GLOBAL
             * @type String
             * @readOnly
             */
            $provide.constant('GLOBAL', global);
        }
    ]
);
