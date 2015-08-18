// The purpose of this file is to setup the use of a configuration
// file for our node development server.

// Modules.
var nconf = require('nconf'),
    configFile = 'config.json',
    config = {},
    NODE_ENV;

// If an argument is passed containing a specific app, then
// use that arg to pull a specific config file.
process.argv.forEach(function (arg) {
    'use strict';

    if (arg.indexOf('--config=') === 0) {
        configFile = arg.split('=')[1];
    }
});

// NODE_ENV is an environment variable that can be defined and set
// when the server is started (i.e., NODE_ENV=production node app.js).
NODE_ENV = process.env.NODE_ENV || 'development';

// Configure nconf to use (in-order) command-line arguments,
// environment variables and configuration file.
nconf.argv().env().file({file: __dirname + '/' + configFile});

// Set environment option.
config.env = NODE_ENV;

// Export module.
module.exports = config;
