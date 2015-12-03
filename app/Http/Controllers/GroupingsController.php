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

    $this->getGroupings();

    $this->getOrgUsers();
  }


  /**
   * getGroupings
   * Check to see if the groupings.json file exists, if it does then load it,
   * otherwise create fake data using the Faker library.
   */
  private function getGroupings() {

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
  }

  /**
   * getOrgUsers()
   * Check to see if the orgUsers.json file exists, if it does then load it,
   * otherwise create the fake data using the Faker library.
   */
  private function getOrgUsers() {
    /**
     * Check if file exists
     */
    if (File::exists(\base_path() . '/sandbox/clientserver/routes/orgUsers.json')) {
      $this->orgUsers = json_decode(\File::get(\base_path() . '/sandbox/clientserver/routes/orgUsers.json'), TRUE);
    }
    else {
      /**
       * Populate using Faker data
       */
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
   * search
   * Checked the request object for a query parameter called "query" and then
   * performs a pseudo-search and returns the object.  If the "query" value is
   * !zero, then an empty array is returned.
   *
   * @param Request $request
   * @return JSON $grouping || empty array
   */
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

  /**
   * getGroup
   * Takes a passed in group id and returns that group from the collection. It
   * also appends some additional data such as users and options prior to
   * returning the data.
   *
   * @param $groupId
   * @return JSON $fakeGrouping
   */
  public function getGroup($groupId) {
    $fakeGrouping = NULL;

    if (!$groupId) {
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

  /**
   * getGroupingsOwned
   * This just returns the whole groupings object since we are not doing real
   * filtering in this mock-up
   * @return JSON $groupings
   */
  public function getGroupingsOwned() {
    return response()->json($this->groupings);
  }

}