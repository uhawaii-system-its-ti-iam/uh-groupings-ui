module.exports = function (grunt) {
    'use strict';

    // Configuration.
    grunt.config.merge({
        clean: {
            development: [
                'development/index.html'
            ],
            production: [
                'production/'
            ],
            laravel: [
                '../public/css',
                '../public/fonts',
                '../public/i18n',
                '../public/images',
                '../public/js',
                '../public/favicon.ico',
                '../public/index.html'
            ],
            temp: [
                'production/temp'
            ]
        }
    });
};
