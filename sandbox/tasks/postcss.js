module.exports = function (grunt) {
    'use strict';

    grunt.config.merge({
        postcss: {
            development: {
                options: {
                    processors: [
                        require('autoprefixer')({browsers: 'last 2 versions'})
                    ]
                },
                src: 'development/css/app.css',
                dest: 'development/css/app.css'

            },
            production: {
                options: {
                    map: true,
                    processors: [
                        require('autoprefixer')(),
                        require('cssnano')() //minify
                    ]
                },
                src: 'production/css/app.css',
                dest: 'production/css/app.min.css'
            }
        }
    });
};
