// Grunt.
module.exports = function (grunt) {
    'use strict';

    // Configuration.
    grunt.initConfig({
        pkg: grunt.file.readJSON('package.json')
    });

    // Load plugins/tasks.
    grunt.loadNpmTasks('grunt-contrib-clean');
    grunt.loadNpmTasks('grunt-contrib-copy');
    grunt.loadNpmTasks('grunt-contrib-less');
    grunt.loadNpmTasks('grunt-contrib-jshint');
    grunt.loadNpmTasks('grunt-contrib-uglify');
    grunt.loadNpmTasks('grunt-angular-templates');
    grunt.loadNpmTasks('grunt-contrib-concat');
    grunt.loadNpmTasks('grunt-contrib-watch');
    grunt.loadNpmTasks('grunt-contrib-yuidoc');
    grunt.loadNpmTasks('grunt-karma');
    grunt.loadNpmTasks('grunt-jscs');
    grunt.task.loadTasks('tasks');

    // grunt dev
    // Execute tasks that prepare code base for use in the sandbox development environment.
    grunt.registerTask('dev', ['clean:development', 'less:development', 'jshint', 'jscs', 'compilehtml:development']);

    // grunt prod
    // Execute tasks that prepare code base for use in the sandbox production environment.
    grunt.registerTask('prod', ['clean:production', 'copy:production', 'less:production', 'prod-templates', 'prod-js', 'concat:production', 'compilehtml:production', 'clean:temp']);

    // grunt laravel
    // Execute tasks that prepare code base for use in the laravel production environment.
    grunt.registerTask('laravel', ['prod', 'clean:laravel', 'copy:laravel']);

    // grunt prod-templates
    // Execute tasks that cache HTML templates for a production environment.
    grunt.registerTask('prod-templates', ['ngtemplates', 'uglify:templates']);

    // grunt prod-js
    // Execute tasks that prepare JavaScript resources for a use in a production environment.
    grunt.registerTask('prod-js', ['jshint', 'jscs', 'test', 'uglify:lib', 'uglify:app', 'uglify:stack']);

    // grunt docs
    // Generate application documentation.
    grunt.registerTask('docs', ['yuidoc']);

    // grunt test
    // Execute JavaScript unit tests.
    grunt.registerTask('test', ['karma']);

    // grunt js.watcher, less.watcher, index.watcher
    // Execute tasks when JavaScript, Less or Templates change.
    grunt.registerTask('js.watcher', ['watch:js']);
    grunt.registerTask('less.watcher', ['watch:less']);
    grunt.registerTask('index.watcher', ['watch:index']);

    // grunt
    // Default tasks that execute with the grunt command.
    grunt.registerTask('default', ['dev', 'laravel']);
};
