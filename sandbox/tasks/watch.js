module.exports = function (grunt) {
    'use strict';

    grunt.config.merge({
        watch: {
            js: {
                files: [
                    'clientserver/**/*.js',
                    'development/js/src/app/**/*.js',
                    'development/js/src/stack/**/*.js',
                    '!development/js/src/app/**/*.spec.js',
                    '!development/js/src/stack/**/*.spec.js',
                    'tasks/**/*.js',
                    '*.js'
                ],
                tasks: [
                    'jshint',
                    'jscs'
                ]
            },

            less: {
                files: [
                    'development/js/src/app/**/*.less',
                    'development/js/src/stack/**/*.less',
                    'development/less/**/*.less'
                ],
                tasks: [
                    'less:development',
                    'postcss:development'
                ]
            },

            index: {
                files: [
                    'clientserver/views/*.html'
                ],
                tasks: [
                    'compilehtml:development'
                ]
            }
        }
    });
};
