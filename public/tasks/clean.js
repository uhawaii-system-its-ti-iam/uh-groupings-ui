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
            temp: [
                'production/temp'
            ]
        }
    });
};
