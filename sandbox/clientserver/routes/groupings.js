/* Mock Groupings API.
 --------------------------------------*/

// Export.
module.exports = function (environment) {
    'use strict';

    var fakeGroupingsData = require('./groupings.json'),
        fakeOrgUsersData = require('./orgUsers.json');

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

    environment.express.route('/api/grouping/:groupingId')
        .get(function (req, res, next) {
            var fakeGrouping = fakeGroupingsData.filter(function (g) {
                return g.id === req.params.groupingId;
            }).slice()[0];

            if (!fakeGrouping) {
                res.status(200);
            } else {
                fakeGrouping.basisMemberIds = fakeOrgUsersData.slice(0, 10);
                fakeGrouping.includedMemberIds = fakeOrgUsersData.slice(10, 4);
                fakeGrouping.excludedmemberIds = fakeOrgUsersData.slice(14, 2);

                res.status(200).send(fakeGrouping);
            }
        });

    return environment;
};

