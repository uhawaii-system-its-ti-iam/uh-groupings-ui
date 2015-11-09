module.exports = function (grunt) {
    'use strict';

    grunt.config.merge({
        less: {
            development: {
                files: {
                    'development/css/app.css': 'development/less/app.less'
                }
            },
            production: {
                files: {
                    'production/css/app.css': 'development/less/app.less'
                }
            }
        }
    });
};
