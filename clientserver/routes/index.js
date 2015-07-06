/* Route middleware.
 * Map routes to separate module files.
--------------------------------------*/

// Modules & variables.
var _ = require('underscore');

// Export.
module.exports = function (environment) {
    'use strict';

    // Page.
    require('./page')(environment);

    // Mock API.
    if (_.isEmpty(environment.PROXY)) {
        require('./authentication')(environment);
        require('./user')(environment);
    }

    return environment;
};
