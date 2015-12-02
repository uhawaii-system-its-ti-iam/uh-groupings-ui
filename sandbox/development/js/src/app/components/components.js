/**
 * Module definition houses all application-specific components.
 * Application components consist of global widgets or implementations
 * that can be applied to multiple locations within the application.
 *
 * @module components
 */
angular.module('components', [
    'components.header',
    'components.listNavigation',
    'components.footer',
    'components.groupingSearch',
    'components.groupingsServices.GroupingsService'
]);
