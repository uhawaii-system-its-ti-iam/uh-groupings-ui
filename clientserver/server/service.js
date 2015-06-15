/* Server Service.
--------------------------------------*/
var service = {
    /**
     * Method handles access errors, outputs a friendly message
     * and stops the server.
     *
     * @method accessException
     * @param {Number} port Port value
     */
    accessException: function (port) {
        'use strict';
        console.error('Port ' + port + ' requires elevated privileges.');
        process.exit(1);
    },

    /**
     * Method handles the common 'address in use' error,
     * outputs a friendly message and stops the server.
     *
     * @method addressInUseException
     * @param {Number} port Port value
     */
    addressInUseException: function (port) {
        'use strict';
        console.error('Port ' + port + ' is already in use.');
        process.exit(1);
    },

    /**
     * Method throws an error when an invalid port value is encountered.
     * It outputs a friendly message and stops the server.
     *
     * @method portException
     * @param {Number} port Port value
     */
    portException: function (port) {
        'use strict';
        console.error(port + ' port value is not valid. Port must be a number and greater then 0.');
        process.exit(1);
    },

    /**
     * Method examines the value parameter and throws a port exception when the value
     * encountered is not a valid positive number.
     *
     * @method normalizePort
     * @param {Number} value Port value
     * @return {Number} Valid port
     */
    normalizePort: function (value) {
        'use strict';
        var port = parseInt(value, 10);

        if (isNaN(port)) {
            service.portException(value);
        } else if (port <= 0) {
            service.portException(value);
        }

        return port;
    }
};

module.exports = service;
