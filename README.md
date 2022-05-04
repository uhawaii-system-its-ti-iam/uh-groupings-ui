### Badges Branch Summary
This branch utilizes Github Actions in order to automatically update status badges

Implementing these files into the master branch would require users to rebase ater every PR in order to see the badges update.

Instead we created this branch in order to automatically update the status of badges without having to pull after every push request to do so.  

Below are working examples of the Build and Coverage status badges

### Example Status Badges 
[![Build and Test](https://github.com/uhawaii-system-its-ti-iam/uh-groupings-ui/actions/workflows/push_pull_testing.yml/badge.svg)](https://github.com/uhawaii-system-its-ti-iam/uh-groupings-ui/actions/workflows/push_pull_testing.yml)
[![Coverage Status](https://github.com/uhawaii-system-its-ti-iam/uh-groupings-ui/blob/badges/jacoco.svg)](https://github.com/uhawaii-system-its-ti-iam/uh-groupings-ui/actions/workflows/coverage.yml)
