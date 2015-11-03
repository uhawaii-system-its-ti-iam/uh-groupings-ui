module.exports = function (grunt) {
    'use strict';

    grunt.config.merge({
        karma: {
            unit: {
                configFile: 'karma.conf.js',
                singleRun: true
            }
        }
    });
};
