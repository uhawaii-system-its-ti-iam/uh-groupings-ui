/* global UHGroupingsApp */

(() => {
    /**
     * Service for data access of a logged-in user.
     * @name userService
     */
    UHGroupingsApp.service("userService", function ($q, groupingsService, $window) {

        const USER_STORAGE_KEY = "currentUserDataSession";
        let userPromise = null;

        function loadUserFromSessionStorage() {
            try {
                const userData = $window.sessionStorage.getItem(USER_STORAGE_KEY);
                return userData ? JSON.parse(userData) : null;
            } catch (e) {
                return null;
            }
        }

        let currentUser = loadUserFromSessionStorage();

        function saveUserToSessionStorage(user) {
            $window.sessionStorage.setItem(USER_STORAGE_KEY, JSON.stringify(user));
        }

        return {
            /**
             * Fetches and returns the current user.
             * Tries sessionStorage first, then makes API call and caches.
             * @return {*|Promise} A promise that resolves with current user object.
             */
            getCurrentUser() {
                if (currentUser) {
                    return $q.when(currentUser);
                }
                if (userPromise) {
                    // if a request is already in progress, return the existing promise
                    return userPromise;
                }

                // No user loaded and no request in progress, make the API call
                let deferred = $q.defer(); // Create a deferred object to turn the callback into a promise.
                userPromise = deferred.promise;

                groupingsService.getCurrentUser((res) => {
                    currentUser = res;
                    saveUserToSessionStorage(currentUser);
                    deferred.resolve(currentUser); // resolve the promise with the data.
                    userPromise = null;
                });

                return userPromise;
            },

            /**
             * Refresh information about the current user.
             */
            refresh() {
                currentUser = null;
                userPromise = null;
                $window.sessionStorage.removeItem(USER_STORAGE_KEY);
                this.getCurrentUser();
            }
        };
    });
})();