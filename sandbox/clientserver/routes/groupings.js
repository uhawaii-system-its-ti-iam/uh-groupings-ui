/* Mock Groupings API.
 --------------------------------------*/

// Export.
module.exports = function (environment) {
    'use strict';
    
    var fakeGroupingsData = [
        {
            "id": "groupings:faculty:facultyEditors",
            "displayId": "Groupings:Faculty:FacultyEditors",
            "displayGroup": "Faculty Editors",
            "description": "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam vitae justo eu est porta tempor. Vestibulum ante ipsum primis in faucibus orci luctus.",
            "status": "active"
        },
        {
            "id": "groupings:faculty:superUsers:facultyAdmin",
            "displayId": "Groupings:Faculty:SuperUsers:FacultyAdmin",
            "displayGroup": "Faculty Admin",
            "description": "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam vitae justo eu est porta tempor. Vestibulum ante ipsum primis in faucibus orci luctus.",
            "status": "active"
        },
        {
            "id": "groupings:faculty:general:facultyViewers",
            "displayId": "Groupings:Faculty:General:FacultyViewers",
            "displayGroup": "Faculty Viewers",
            "description": "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam vitae justo eu est porta tempor. Vestibulum ante ipsum primis in faucibus orci luctus.",
            "status": "active"
        },
        {
            "id": "groupings:faculty:facultyApprovers",
            "displayId": "Groupings:Faculty:FacultyApprovers",
            "displayGroup": "Faculty Approvers",
            "description": "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam vitae justo eu est porta tempor. Vestibulum ante ipsum primis in faucibus orci luctus.",
            "status": "inactive"
        }
    ];

    /**
     * Method for querying the groupings list
     */
    environment.express.route('/api/groupings?:query')
        .get(function (req, res, next) {
            //inspect 'query' param, and if not forcing zero-state return mock data
            if (req.query.query !== '!zero') {
                res.status(200).send(fakeGroupingsData);
            } else {
                res.status(200).send([]);
            }
        });

    /**
     * Method for getting all groupings owned by a certain user
     */
    environment.express.route('/api/groupings/:userId/owned')
        .get(function (req, res, next) {
            res.status(200).send(fakeGroupingsData);
        });

    /**
     * Method for getting all groupings a certain user is a member of
     */
    environment.express.route('/api/groupings/:userId')
        .get(function (req, res, next) {
            res.status(200).send(fakeGroupingsData);
        });

    return environment;
};

