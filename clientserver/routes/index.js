/* Route middleware.
 * Map routes to separate module files.
--------------------------------------*/
module.exports = function (environment) {
    'use strict';

    require('./page')(environment);
    return environment;
};
