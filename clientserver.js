/* jshint unused: false */

// Modules & variables.
var express = require('express'),
    nconf = require('nconf'),
    _ = require('underscore'),
    config = require('./clientserver/config'),
    engine = require('./clientserver/engine'),
    middleware = require('./clientserver/middleware'),
    routes = require('./clientserver/routes'),
    server = require('./clientserver/server'),
    serverService = require('./clientserver/server/service'),
    exec = require('child_process').exec,
    applications = [];

// Iterate over all applications.
applications = nconf.get('APPLICATIONS');
_.each(applications, function (app) {
    'use strict';

    // Iterate over application environments.
    var environments = app.ENVIRONMENTS;
    _.each(environments, function (environment) {
        // Express instantiation.
        environment.express = express();

        // Cache.
        environment.express.set('ID', app.ID);
        environment.express.set('TITLE', app.TITLE);
        environment.express.set('DESCRIPTION', app.DESCRIPTION);
        environment.express.set('ROOT', __dirname);
        environment.express.set('SERVER_DIR', __dirname + environment.SERVER_DIR);
        environment.express.set('STATIC_DIR', __dirname + environment.STATIC_DIR);
        environment.express.set('PORT', serverService.normalizePort(process.env.PORT || environment.PORT));

        // View engine configuration.
        environment = engine(environment);

        // Middleware configuration.
        environment = middleware(environment);

        // Route configuration.
        environment = routes(environment);

        // Server configuration.
        environment = server(environment);

        // Build environment.
        exec(environment.GRUNT_COMMAND, function (error, stdout, stderr) {
            console.log(stdout);
            if (stderr) {
                console.log(stderr);
            }
        });
    });
});
