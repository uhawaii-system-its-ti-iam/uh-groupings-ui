<?php

namespace App\Http\Controllers;

use App\User;
use Illuminate\Http\Request;
//use App\Http\Controllers\Controller;

class UserController extends Controller
{
    /**
     * getUser
     * Returns a static JSON object when the user accesses /api/user
     *
     * If the user is logged in, then the user is retrieved from the session and returned,
     * otherwise an empty JSON object is returned.
     *
     * @param Request $request
     * @return JSON $user
     */
    public function getUser(Request $request)
    {
        $user = $request->session()->get('user')?: null;

        return response()->json($user);
    }
}