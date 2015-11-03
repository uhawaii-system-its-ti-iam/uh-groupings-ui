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
    /**
     * Check to see if there is an index.html file, meaning they have setup
     * the Angular app, and return it.  Otherwise display a welcome page.
     */
    if ( File::exists( \public_path().'/index.html') ) {
        return \File::get(public_path().'/index.html');
    } else {
        return View::make('welcome');
    }
}]);

Route::any( '{catchall}', function () {
    return redirect()->route('root');
} )->where('catchall', '(.*)');
