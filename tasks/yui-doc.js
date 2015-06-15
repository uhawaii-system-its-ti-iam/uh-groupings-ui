module.exports = function (grunt) {
    'use strict';

    grunt.config.merge({
        yuidoc: {
            development: {
                name: '<%= pkg.name %>',
                description: '<%= pkg.description %>',
                version: '<%= pkg.version %>',
                options: {
                    paths: 'development/js/src/',
                    outdir: 'development/docs'
                }
            }
        }
    });
};
