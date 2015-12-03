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
use File;

class GroupingsController extends Controller {
  protected $faker;
  protected $username = 'ckent';
  protected $password = 'root';
  protected $groupings;
  protected $orgUsers;
  protected $statusArray = array('active', 'inactive');

  public function __construct() {
    $this->faker = Faker\Factory::create();

    if (File::exists(\base_path() . '/sandbox/clientserver/routes/groupings.json')) {
      $this->groupings = json_decode(\File::get(\base_path() . '/sandbox/clientserver/routes/groupings.json'), TRUE);
    }
    else {
      $this->groupings = array(
        array(
          "id" => "groupings:faculty:facultyEditors",
          "displayId" => "Groupings:Faculty:FacultyEditors",
          "displayGroup" => "Faculty Editors",
          "description" => $this->faker->paragraph(3),
          "status" => $this->faker->randomElement($this->statusArray)
        ),
        array(
          "id" => "groupings:faculty:superUsers:facultyAdmin",
          "displayId" => "Groupings:Faculty:SuperUsers:FacultyAdmin",
          "displayGroup" => "Faculty Admin",
          "description" => $this->faker->paragraph(3),
          "status" => $this->faker->randomElement($this->statusArray)
        ),
        array(
          "id" => "groupings:faculty:general:facultyViewers",
          "displayId" => "Groupings:Faculty:General:FacultyViewers",
          "displayGroup" => "Faculty Viewers",
          "description" => $this->faker->paragraph(3),
          "status" => $this->faker->randomElement($this->statusArray)
        ),
        array(
          "id" => "groupings:faculty:facultyApprovers",
          "displayId" => "Groupings:Faculty:FacultyApprovers",
          "displayGroup" => "Faculty Approvers",
          "description" => $this->faker->paragraph(3),
          "status" => $this->faker->randomElement($this->statusArray)
        )
      );
    }


    if (File::exists(\base_path() . '/sandbox/clientserver/routes/orgUsers.json')) {
      $this->orgUsers = json_decode(\File::get(\base_path() . '/sandbox/clientserver/routes/orgUsers.json'), TRUE);
    }

  }

  public function search(Request $request) {
    if ($request->input('query')) {
      if ($request->input('query') == '!zero') {
        return response()->json([]);
      }
      else {
        return $this->groupings;
      }
    }
    else {
      return response()->json([]);
    }
  }

  public function getGroup($groupId) {
    $fakeGrouping = NULL;

    if (!$groupId) {
      // Todo: clean this up to return error
      $error = array('message' => 'Unauthorized');
      return response()->json($error, 401);
    }
    else {
      foreach ($this->groupings as $group) {
        if ($group['id'] == $groupId) {
          $fakeGrouping = $group;
        }
      }

      $fakeGrouping['basisMemberIds'] = array_slice($this->orgUsers, 0, 16);
      $fakeGrouping['ownerMemberIds'] = array_slice($this->orgUsers, 5, 5);
      $fakeGrouping['includedMemberIds'] = array_slice($this->orgUsers, 16, 6);
      $fakeGrouping['excludedMemberIds'] = array_slice($this->orgUsers, 22, 8);

      $fakeGrouping['options'] = array(
        "canAddSelf" => FALSE,
        "canRemoveSelf" => TRUE,
        "includeInListServe" => TRUE
      );

      return response()->json($fakeGrouping);

    }
  }

  public function getGroupingsOwned() {
    return response()->json($this->groupings);
  }

}