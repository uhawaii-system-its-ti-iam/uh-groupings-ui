/* Third-party middleware.
--------------------------------------*/

// Modules & variables.
var favicon = require('serve-favicon'),
    morgan = require('morgan'),
    bodyParser = require('body-parser'),
    methodOverride = require('method-override'),
    cookieParser = require('cookie-parser'),
    express = require('express'),
    proxy = require('proxy-middleware'),
    url = require('url');

// Export.
module.exports = function (environment) {
    'use strict';

    // Middleware for serving a favicon.
    environment.express.use(favicon(environment.express.get('STATIC_DIR') + '/favicon.ico'));

    // HTTP request logger.
    environment.express.use(morgan('dev'));

    // Enable proxy.
    environment.express.enable('trust proxy');

    // Proxy.
    environment.express.use(environment.PROXY_ROUTE, proxy(url.parse(environment.PROXY)));

    // Body parser.
    environment.express.use(bodyParser.json());
    environment.express.use(bodyParser.urlencoded({extended: false}));

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
