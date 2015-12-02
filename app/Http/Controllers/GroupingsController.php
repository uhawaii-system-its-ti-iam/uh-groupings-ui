<?php
/**
 * User: Jeff Sittler
 * Date: 12/2/15
 * Time: 11:51 AM
 */

namespace App\Http\Controllers;

use App\User;
use Illuminate\Http\Request;
use Faker;
//use App\Http\Controllers\Controller;

class GroupingsController extends Controller {
  protected $faker;
  protected $username = 'ckent';
  protected $password = 'root';
  protected $user = array(
    "firstName" => "Clark",
    "lastName" => "Kent",
    "username" => "ckent",
    "email" => "ckent@email.com",
    "role" => "admin"
  );

  public function __construct() {
    $this->faker = Faker\Factory::create();
  }

  public function search(Request $request) {
    if ($request->input('query')) {
      return 'got it!';
    } else {
      return 'do not got it';
    }
//    $query = $request->input('query');
//    return response()->json('search for: ' . $query);
  }

  public function getGroup($groupId) {
    return response()->json('get group id: ' .  $groupId);
  }

  public function getOwned($username) {
    //$user = $request->session()->get('user') ?: NULL;
    $status = array('active','inactive');

    $groupings = array(
        array(
          "id" => "groupings:faculty:facultyEditors",
          "displayId" => "Groupings:Faculty:FacultyEditors",
          "displayGroup" => "Faculty Editors",
          "description" => $this->faker->paragraph(3),
          "status" => $this->faker->randomElement( $status )
        ),
        array(
          "id" => "groupings:faculty:superUsers:facultyAdmin",
          "displayId" => "Groupings:Faculty:SuperUsers:FacultyAdmin",
          "displayGroup" => "Faculty Admin",
          "description" => $this->faker->paragraph(3),
          "status" => $this->faker->randomElement( $status )
        ),
        array(
          "id" => "groupings:faculty:general:facultyViewers",
          "displayId" => "Groupings:Faculty:General:FacultyViewers",
          "displayGroup" => "Faculty Viewers",
          "description" => $this->faker->paragraph(3),
          "status" => $this->faker->randomElement( $status )
        ),
        array(
          "id" => "groupings:faculty:facultyApprovers",
          "displayId" => "Groupings:Faculty:FacultyApprovers",
          "displayGroup" => "Faculty Approvers",
          "description" => $this->faker->paragraph(3),
          "status" => $this->faker->randomElement( $status )
        )
    );
    //randomElement($array = array ('groupings:faculty:facultyEditors','groupings:faculty:superUsers:facultyAdmin','c'))

//    foreach(range(1,4) as $index)
//        {
//            $group = array(
//                "id" => substr( str_replace("-", "", $this->faker->uuid), 0, 24), //"564110ea9d0dc7f212813a8c",
//                "email" => $this->faker->email,
//                "isActive" => $this->faker->boolean(50),
//                "firstName" => $this->faker->firstName,
//                "lastName" => $this->faker->lastName,
//                "permissionType" => "Admin"
//            );
//
//            array_push($orgUsers, $user);
//        }

        return response()->json($groupings);

//  {
//    "id": "groupings:faculty:facultyApprovers",
//    "displayId": "Groupings:Faculty:FacultyApprovers",
//    "displayGroup": "Faculty Approvers",
//    "description": "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam vitae justo eu est porta tempor. Vestibulum ante ipsum primis in faucibus orci luctus.",
//    "status": "inactive"
//  }

//    return response()->json($user);
    //$faker = Faker\Factory::create();
    //return 'booya - ' . $faker->name;
  }

}