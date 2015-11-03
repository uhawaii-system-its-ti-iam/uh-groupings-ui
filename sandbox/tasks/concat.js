module.exports = function (grunt) {
    'use strict';

    grunt.config.merge({
        concat: {
            production: {
                src: ['production/temp/ngtemplates.min.js', 'production/temp/app.min.js'],
                dest: 'production/js/app.min.js'
            }
        }
    });
};
