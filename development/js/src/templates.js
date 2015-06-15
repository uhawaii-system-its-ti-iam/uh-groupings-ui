/**
 * The templates module houses all of the application's templates
 * using the $templateCache service. The $templateCache service is
 * leveraged to reduce the number of http requests this application
 * must make when retrieving templates. If a template is stored in
 * the $templateCache the application will not make a request for
 * the template in question.
 *
 * @module templates
 */
angular.module('templates', []);
