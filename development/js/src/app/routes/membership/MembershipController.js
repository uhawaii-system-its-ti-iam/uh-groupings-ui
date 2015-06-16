angular.module('routes.membership.MembershipController', [])

/**
 * The MembershipController manages view logic rendered to the
 * membership.html template for the /membership route.
 *
 * @class MembershipController
 * @constructor
 * @module routes.membership.MembershipController
 **/
.controller('MembershipController', [
    '$timeout',
    function ($timeout) {
        'use strict';

        // Define.
        var membershipCtrl;

        /**
         * Property houses a reference to the membership controller.
         *
         * @property membershipCtrl
         * @type {Object}
         */
        membershipCtrl = this;

        /**
         * Method executes initialization process.
         *
         * @method initialize
         * @private
         */
        function initialize() {
            var t = $timeout(function () {
                $timeout.cancel(t);
            }, 0);
        }
        initialize();
    }
]);
