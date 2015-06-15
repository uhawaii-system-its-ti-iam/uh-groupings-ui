module.exports = function (grunt) {
    'use strict';

    grunt.config.merge({
        jscs: {
            options: {
                preset: "crockford"
            },
            files: {
                src: [
                    'development/js/src/app/**/*.js',
                    'development/js/src/stack/**/*.js',
                    'clientserver/**/*.js',
                    'tasks/*.js',
                    '*.js'
                ]
            }
        }
    });
};
