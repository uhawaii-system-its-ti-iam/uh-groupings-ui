/* Server configuration.
--------------------------------------*/

// Modules & variables.
var http = require('http'),
    serverService = require('./service'),
    server;

module.exports = function (environment) {
    'use strict';

    // Create HTTP server.
    server = http.createServer(environment.express);
    server.listen(environment.express.get('PORT'));

    // Server 'error' handler.
    server.on('error', function (error) {
        if (error.syscall !== 'listen') {
            throw error;
        }

        // Listen errors with friendly messages.
        switch (error.code) {
        case 'EACCES':
            serverService.accessException(environment.express.get('PORT'));
            break;
        case 'EADDRINUSE':
            serverService.addressInUseException(environment.express.get('PORT'));
            break;
        default:
            throw error;
        }
    });

    // Server 'listening' handler.
    server.on('listening', function () {
        console.log('Listening on port: ', environment.express.get('PORT'));
    });

    return environment;
};
