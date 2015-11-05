/* Mock User API.
--------------------------------------*/

// Export.
module.exports = function (environment) {
    'use strict';

    var fakeGroupingsData = require('./groupings.json');

    environment.express.route('/api/user')
        .get(function (req, res, next) {
            if (req.session && req.session.user) {
                res.status(200).send(req.session.user);
            } else {
                res.status(200).send({});
            }
        });

    /**
     * Method for getting all groupings a certain user is a member of
     */
    environment.express.route('/api/user/:userId/groupings')
        .get(function (req, res, next) {
            res.status(200).send(fakeGroupingsData);
        });

    /**
     * Method for getting all groupings owned by a certain user
     */
    environment.express.route('/api/user/:userId/groupings/owned')
        .get(function (req, res, next) {
            res.status(200).send(fakeGroupingsData);
        });

    return environment;
};
