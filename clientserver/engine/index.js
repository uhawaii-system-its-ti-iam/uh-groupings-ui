/* Template Engine.
--------------------------------------*/

// Modules & variables.
var path = require('path');

// Export.
module.exports = function (environment) {
    'use strict';

    environment.express.set('view engine', 'html');
    environment.express.engine('html', require('hjs').renderFile);
    environment.express.set('views', path.join(environment.express.get('SERVER_DIR'), environment.VIEW_DIR));

    return environment;
};
