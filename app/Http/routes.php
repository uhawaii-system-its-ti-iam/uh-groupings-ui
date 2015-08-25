<?php

/*
|--------------------------------------------------------------------------
| Application Routes
|--------------------------------------------------------------------------
|
| Here is where you can register all of the routes for an application.
| It's a breeze. Simply tell Laravel the URIs it should respond to
| and give it the controller to call when that URI is requested.
|
*/

/**
 * API ROUTES
 * These routes are handled via Laravel
 * ==================================
 */

Route::group(['prefix' => 'api'], function () {
    Route::post('login', 'MockAuthController@login');

    Route::match(['get', 'post'], 'logout' ,'MockAuthController@logout');

    Route::get('session', 'MockAuthController@getSession');

    /**
     * User API Routes
     */
    Route::get('user', 'UserController@getUser');
});

Route::get('/', ['as' => 'root', function()
{
    $myEnv = env( 'APP_ENV' );

    View::addNamespace( $myEnv, $myEnv);
    View::addExtension('html', 'php');
        return View::make($myEnv.'::index');

}]);

Route::any( '{catchall}', function () {
    return redirect()->route('root');
} )->where('catchall', '(.*)');
