module.exports = function (grunt) {
    'use strict';

    grunt.config.merge({
        uglify: {
            options: {
                compress: true,
                mangle: false,
                preserveComments: false,
                preserveLicenseComments: true
            },
            lib: {
                files: {
                    'production/js/lib.min.js': [
                        'development/js/lib/jquery/jquery.js',
                        'development/js/lib/angular/angular.js',
                        'development/js/lib/angular/angular-sanitize.js',
                        'development/js/lib/angular/angular-animate.js',
                        'development/js/lib/angular/angular-aria.js',
                        'development/js/lib/angular-cache/angular-cache.js',
                        'development/js/lib/angular-ui/ui-bootstrap-tpls.js',
                        'development/js/lib/ui-router/ui-router.js',
                        'development/js/lib/outside-events/outside-events.js',
                        'development/js/lib/underscore/underscore.js'
                    ]
                }
            },
            app: {
                files: {
                    'production/temp/app.min.js': [
                        'development/js/src/app/**/*.js',
                        '!development/js/src/app/**/*.spec.js'
                    ]
                }
            },
            stack: {
                files: {
                    'production/js/stack.min.js': [
                        'development/js/src/stack/**/*.js',
                        '!development/js/src/stack/**/*.spec.js'
                    ]
                }
            },
            templates: {
                files: {
                    'production/temp/ngtemplates.min.js': [
                        'production/temp/ngtemplates.js'
                    ]
                }
            }
        }
    });
};
