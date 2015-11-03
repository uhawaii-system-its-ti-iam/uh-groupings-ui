<?php

namespace App\Http\Controllers;

use App\User;
use Illuminate\Http\Request;
//use App\Http\Controllers\Controller;

class MockAuthController extends Controller
{
    protected $username = 'ckent';
    protected $password = 'root';
    protected $user = array(
        "firstName" => "Clark",
        "lastName" => "Kent",
        "username" => "ckent",
        "email" => "ckent@email.com",
        "role" => "admin"
    );

    private function createSession(Request $request, $key) {
        if ($request->session()->has($key)) {
            return $request->session()->get($key);
        } else {
            // Create a session, and stuff the user data into it.
            $request->session()->put($key, $this->user);

            // Return the user object from the session
            return $request->session()->get('user');
        }
    }

    public function login(Request $request) {

        if (
            $request->input('username') == $this->username &&
            $request->input('password') == $this->password
        ) {
            $user = $this->createSession($request, 'user');

            return response()->json($user);
        } else {
            $error = array('message' => 'Unauthorized');

            return response()->json($error, 401);
        }
    }

    public function logout(Request $request) {
        $request->session()->forget('user');

        $request->session()->flush();

        return;
    }

    public function getToken() {
        return csrf_token();
    }

    public function getSession(Request $request) {
        return $request->session()->all();
    }

}