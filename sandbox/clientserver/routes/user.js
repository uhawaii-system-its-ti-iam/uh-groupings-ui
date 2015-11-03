/* Mock User API.
--------------------------------------*/

// Export.
module.exports = function (environment) {
    'use strict';

    environment.express.route('/api/user')
        .get(function (req, res, next) {
            if (req.session && req.session.user) {
                res.status(200).send(req.session.user);
            } else {
                res.status(200).send({});
            }
        });

    return environment;
};
