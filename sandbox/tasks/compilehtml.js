// Modules.
var hogan = require('hogan.js'),
    fs = require('fs');

// Export.
module.exports = function (grunt) {
    'use strict';

    grunt.config.merge({
        compilehtml: {
            development: {
                options: {
                    id: 'app',
                    title: 'Web Framework',
                    description: 'Front-end web framework.',
                    author: 'Unicon, inc.',
                    environment: false,
                    environmentId: 'development',
                    dev: true
                },
                src: 'clientserver/views/app.html',
                dest: 'development/index.html'
            },
            production: {
                options: {
                    id: 'app',
                    title: 'Web Framework',
                    description: 'Front-end web framework.',
                    author: 'Unicon, inc.',
                    environment: false,
                    environmentId: 'production',
                    dev: false
                },
                src: 'clientserver/views/app.html',
                dest: 'production/index.html'
            }
        }
    });

    // Helper method that compiles templates with hogan.js.
    grunt.compileTemplates = function (filepath, filename, options, callback) {
        // Define.
        var page, context;

        // Initilize.
        page = fs.readFileSync(filepath, 'utf8');
        context = options;

        // Compile page template.
        try {
            page = hogan.compile(page);
            page = page.render(context);
            callback(null, page);
        }
        catch (err) {
            callback(err);
            return;
        }
    };

    // Registered grunt task.
    grunt.registerMultiTask('compilehtml', 'Compile templates using hogan.js.', function () {
        // Define.
        var data, src, dest, defaults, options, done;

        // Warn when the src property is missing.
        if (!this.data.hasOwnProperty('src')) {
            grunt.warn('The configuration for the compilehtml task is missing a src property.');
            return;
        }

        // Warn when the dest property is missing.
        if (!this.data.hasOwnProperty('dest')) {
            grunt.warn('The configuration for the compilehtml task is missing a dest property.');
            return;
        }

        // Capture task configuration from the Gruntfile as
        // well as src and dest information.
        data = this.data;
        src = grunt.file.expand(data.src);
        dest = grunt.template.process(data.dest);

        console.log('compile src: ' + src);

        // Define default options.
        defaults = {
            title: 'Compile Templates with Hogan.js'
        };

        // Extend defaults with passed in options.
        options = grunt.util._.extend(defaults, this.data.options || {});

        // Tell grunt this task is asynchronous.
        done = this.async();

        // Iterate over all the files in the src configuration.
        src.forEach(function (filepath) {
            // Remove the file extension.
            var filename = grunt.util._.first(filepath.match(/[^\\\/:*?"<>|\r\n]+$/i)).replace(/\.html$/, '');
            grunt.log.writeln('Compiling ' + filename.magenta);

            // Complile templates.
            grunt.compileTemplates(filepath, filename, options, function (err, result) {
                if (err) {
                    grunt.warn(err);
                    done(false);
                    return;
                }

                grunt.file.write(dest.replace('FILE', filename), result);
            });
        });

        // Tell grunt our asynchronous task has completed.
        done();
    });
};
