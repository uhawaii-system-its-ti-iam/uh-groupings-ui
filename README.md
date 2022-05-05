### Badges Branch Summary
This branch utilizes Github Actions to automatically update status badges.

Implementing these files into the master branch would require users to pull them after every PR to see the badges update.

Having to re-sync with the remote repo after every PR makes the automation aspect of this badge redundant. 

Instead, we created this branch to automatically update the status of badges without having to pull after every push request to do so.  

Below are working examples of the Build and Coverage status badges:

### Example Status Badges 
[![Build and Test](https://github.com/uhawaii-system-its-ti-iam/uh-groupings-ui/actions/workflows/push_pull_testing.yml/badge.svg)](https://github.com/uhawaii-system-its-ti-iam/uh-groupings-ui/actions/workflows/push_pull_testing.yml)
[![Coverage Status](https://github.com/uhawaii-system-its-ti-iam/uh-groupings-ui/blob/badges/jacoco.svg)](https://github.com/uhawaii-system-its-ti-iam/uh-groupings-ui/actions/workflows/coverage.yml)
