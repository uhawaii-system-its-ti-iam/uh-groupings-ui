/* Mock Groupings API.
 --------------------------------------*/

// Export.
module.exports = function (environment) {
    'use strict';

    var fakeGroupingsData = require('./groupings.json');

    /**
     * Method for querying the groupings list
     */
    environment.express.route('/api/groupings?:query')
        .get(function (req, res, next) {
            if (req.session && req.session.user) {
                //inspect 'query' param, and if not forcing zero-state return mock data
                if (req.query.query !== '!zero') {
                    res.status(200).send(fakeGroupingsData);
                } else {
                    res.status(200).send([]);
                }
            } else {
                res.status(401);
            }
        });

    return environment;
};

