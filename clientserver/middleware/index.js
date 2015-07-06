/* Third-party middleware.
--------------------------------------*/

// Modules & variables.
var favicon = require('serve-favicon'),
    morgan = require('morgan'),
    session = require('client-sessions'),
    bodyParser = require('body-parser'),
    methodOverride = require('method-override'),
    cookieParser = require('cookie-parser'),
    express = require('express'),
    proxy = require('proxy-middleware'),
    url = require('url'),
    _ = require('underscore');

// Export.
module.exports = function (environment) {
    'use strict';

    // Middleware for serving a favicon.
    environment.express.use(favicon(environment.express.get('STATIC_DIR') + '/favicon.ico'));

    // HTTP request logger.
    environment.express.use(morgan('dev'));

    // Enable proxy.
    if (!_.isEmpty(environment.PROXY)) {
        console.log('Execute.');
        environment.express.enable('trust proxy');
        environment.express.use(environment.PROXY_ROUTE, proxy(url.parse(environment.PROXY)));
    }

    // Enable sessions.
    environment.express.use(session({
        cookieName: 'session',
        secret: 'crU8rud8uWRabR8=+s@e',
        duration: 24 * 60 * 60 * 1000,
        activeDuration: 1000 * 60 * 5,
        ephemeral: true
    }));

    // Body parser.
    environment.express.use(bodyParser.urlencoded({extended: false}));
    environment.express.use(bodyParser.json());

    // Method override.
    // Use HTTP verbs such as PUT or DELETE in places where the client doesn't support it.
    environment.express.use(methodOverride());

    // Cookie parser.
    // Parse Cookie header and populate req.cookies with an object keyed by the cookie names.
    environment.express.use(cookieParser());

    // Serve static content from the 'public' directory.
    environment.express.use(express.static(environment.express.get('STATIC_DIR')));

    return environment;
};
