module.exports = function (grunt) {
    'use strict';

    grunt.config.merge({
        ngtemplates: {
            app: {
                options: {
                    module: 'templates',
                    standalone: true,
                    htmlmin: {
                        collapseBooleanAttributes: true,
                        collapseWhitespace: true,
                        removeAttributeQuotes: true,
                        removeComments: true,
                        removeEmptyAttributes: true,
                        removeRedundantAttributes: true,
                        removeScriptTypeAttributes: true,
                        removeStyleLinkTypeAttributes: true
                    }
                },
                cwd: 'development/',
                src: 'js/src/**/*.html',
                dest: 'production/temp/ngtemplates.js'
            }
        }
    });
};
