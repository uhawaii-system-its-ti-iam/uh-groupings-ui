/* Mock Authentication API.
--------------------------------------*/

// Modules & variables.
var _ = require('underscore'),
    userData = require('./userData');

// Export.
module.exports = function (environment) {
    'use strict';

    environment.express.route('/api/login')
        .post(function (req, res, next) {
            if (req.body.username === userData.username && req.body.password === userData.password) {
                // Authenticated.
                var clone = _.clone(userData);
                delete clone.password;
                req.session.user = clone;
                res.status(200).send(clone);
            } else {
                // Not authenticated.
                req.session.reset();
                res.status(401).send({
                    message: 'Unauthorized'
                });
            }
        });

    environment.express.route('/api/logout')
        .post(function (req, res, next) {
            req.session.reset();
            res.status(200).end();
        });

    return environment;
};
