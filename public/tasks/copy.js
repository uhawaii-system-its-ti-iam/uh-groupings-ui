module.exports = function (grunt) {
    'use strict';

    grunt.config.merge({
        copy: {
            production: {
                files: [
                    {
                        src: ['favicon.ico'],
                        dest: 'production/',
                        expand: true,
                        cwd: 'development/'
                    },
                    {
                        src: ['**'],
                        dest: 'production/images/',
                        expand: true,
                        cwd: 'development/images/'
                    },
                    {
                        src: ['**'],
                        dest: 'production/fonts/',
                        expand: true,
                        cwd: 'development/fonts/'
                    },
                    {
                        src: ['**'],
                        dest: 'production/i18n/',
                        expand: true,
                        cwd: 'development/i18n/'
                    }
                ]
            }
        }
    });
};
