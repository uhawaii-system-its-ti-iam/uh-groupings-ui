<?php

namespace App\Http\Controllers;

use App\User;
use Illuminate\Http\Request;
use File;

use Faker;

class UserController extends Controller {
  protected $faker;
  protected $orgUsers = array();

  public function __construct() {
    $this->getOrgUsers();
  }

  /**
   *
   * Setup the UserController by loading in the JSON data, if it exists or
   * creating it using the Faker library.
   *
   */
  private function getOrgUsers() {
    if (File::exists(\base_path() . '/sandbox/clientserver/routes/orgUsers.json')) {
      $this->orgUsers = json_decode(\File::get(\base_path() . '/sandbox/clientserver/routes/orgUsers.json'), TRUE);
    }
    else {
      $this->faker = Faker\Factory::create();

      foreach (range(1, 30) as $index) {
        $user = array(
          "userId" => substr(str_replace("-", "", $this->faker->uuid), 0, 24),
          "email" => $this->faker->email,
          "isActive" => $this->faker->boolean(50),
          "firstName" => $this->faker->firstName,
          "lastName" => $this->faker->lastName,
          "permissionType" => "Admin"
        );

        array_push($this->orgUsers, $user);
      }
    }
  }


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
  public function getUser(Request $request) {
    $user = $request->session()->get('user') ?: NULL;

    return response()->json($user);
  }

  /**
   * getUsers
   * Returns a static JSON object containing a list of users the user accesses /api/users
   *
   * If the user is logged in, then the user is retrieved from the session and returned,
   * otherwise an empty JSON object is returned.
   *
   * @param Request $request
   * @return JSON $users
   */
  public function getUsers() {
    return response()->json($this->orgUsers);
  }

}