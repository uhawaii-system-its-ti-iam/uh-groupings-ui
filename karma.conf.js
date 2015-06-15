module.exports = function (config) {
    'use strict';

    config.set({
        basePath: 'development/js/',
        frameworks: ['jasmine'],
        browsers: ['PhantomJS'],
        files: [
            /* libs */
            'lib/jquery/jquery.js',
            'lib/angular/angular.js',
            'lib/angular/angular-sanitize.js',
            'lib/angular/angular-animate.js',
            'lib/angular/angular-aria.js',
            'lib/angular-cache/angular-cache.js',
            'lib/angular/angular-mocks.js',
            'lib/angular-ui/ui-bootstrap-tpls.js',
            'lib/ui-router/ui-router.js',
            'lib/outside-events/outside-events.js',
            'lib/underscore/underscore.js',
            /* source */
            'src/app/**/*.js',
            'src/stack/**/*.js'
        ],
        reporters: ['progress'],
        port: 9876,
        colors: true,
        logLevel: config.LOG_WARN,
        autoWatch: true,
        singleRun: false
    });
};
