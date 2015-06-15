module.exports = function (grunt) {
    'use strict';

    grunt.config.merge({
        jshint: {
            options: {
                curly: true,
                eqeqeq: true,
                forin: true,
                freeze: true,
                latedef: true,
                noarg: true,
                nonew: true,
                notypeof: true,
                undef: true,
                unused: 'vars',
                newcap: false,
                globals: {
                    window: true,
                    document: true,
                    navigator: true,
                    ga: true,
                    angular: true,
                    require: true,
                    __dirname: true,
                    process: true,
                    module: true,
                    console: true,
                    describe: true,
                    beforeEach: true,
                    inject: true,
                    expect: true,
                    it: true,
                    Mongo: true,
                    spyOn: true,
                    _: true,
                    $: true,
                    jQuery: true,
                    setTimeout: true
                },
                ignores: [
                    ''
                ]
            },
            all: [
                'development/js/src/app/**/*.js',
                'development/js/src/stack/**/*.js',
                'clientserver/**/*.js',
                'tasks/*.js',
                '*.js'
            ]
        }
    });
};
